package com.qubole.tenali.metastore;

/*
 * Copyright (c) 2015. Qubole Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *    limitations under the License.
 */


import org.apache.commons.lang.NotImplementedException;
import org.apache.hadoop.hive.common.ObjectPair;
import org.apache.hadoop.hive.common.ValidTxnList;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.PartitionDropOptions;
import org.apache.hadoop.hive.metastore.api.AggrStats;
import org.apache.hadoop.hive.metastore.api.AlreadyExistsException;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.CompactionType;
import org.apache.hadoop.hive.metastore.api.ConfigValSecurityException;
import org.apache.hadoop.hive.metastore.api.CurrentNotificationEventId;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.FireEventRequest;
import org.apache.hadoop.hive.metastore.api.FireEventResponse;
import org.apache.hadoop.hive.metastore.api.Function;
import org.apache.hadoop.hive.metastore.api.GetOpenTxnsInfoResponse;
import org.apache.hadoop.hive.metastore.api.GetPrincipalsInRoleRequest;
import org.apache.hadoop.hive.metastore.api.GetPrincipalsInRoleResponse;
import org.apache.hadoop.hive.metastore.api.GetRoleGrantsForPrincipalRequest;
import org.apache.hadoop.hive.metastore.api.GetRoleGrantsForPrincipalResponse;
import org.apache.hadoop.hive.metastore.api.HeartbeatTxnRangeResponse;
import org.apache.hadoop.hive.metastore.api.HiveObjectPrivilege;
import org.apache.hadoop.hive.metastore.api.HiveObjectRef;
import org.apache.hadoop.hive.metastore.api.Index;
import org.apache.hadoop.hive.metastore.api.InvalidInputException;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.hadoop.hive.metastore.api.InvalidPartitionException;
import org.apache.hadoop.hive.metastore.api.LockRequest;
import org.apache.hadoop.hive.metastore.api.LockResponse;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchLockException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.NoSuchTxnException;
import org.apache.hadoop.hive.metastore.api.NotificationEventResponse;
import org.apache.hadoop.hive.metastore.api.OpenTxnsResponse;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PartitionEventType;
import org.apache.hadoop.hive.metastore.api.PrincipalPrivilegeSet;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import org.apache.hadoop.hive.metastore.api.PrivilegeBag;
import org.apache.hadoop.hive.metastore.api.Role;
import org.apache.hadoop.hive.metastore.api.SetPartitionsStatsRequest;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponse;
import org.apache.hadoop.hive.metastore.api.ShowLocksResponse;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.TxnAbortedException;
import org.apache.hadoop.hive.metastore.api.TxnOpenException;
import org.apache.hadoop.hive.metastore.api.UnknownDBException;
import org.apache.hadoop.hive.metastore.api.UnknownPartitionException;
import org.apache.hadoop.hive.metastore.api.UnknownTableException;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecProxy;
import org.apache.thrift.TException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;

import static com.google.common.base.Objects.toStringHelper;


/**
 * Created by sakshibansal on 20/04/17.
 */
public class CachingMetastoreClient implements IMetaStoreClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachingMetastoreClient.class);

    private final LoadingCache<String, List<String>> tableNamesCache;
    private final LoadingCache<HiveTableName, Table> tableCache;

    // A single Jedis instance is not threadsafe. Also, creating lots of Jedis instances
    // creates lots of sockets and connections. JedisPool is a threadsafe pool of network
    // connections. It reliably creates several Jedis instances.
    private static JedisPool redisPool;

    public CachingMetastoreClient(String redis, String prefix, int cacheTtlMinutes,
                                  final IMetaStoreClient sourceClient, int missingCacheTTlMin,
                                  boolean enableMissingCache) throws Exception {
        int cacheTtl = cacheTtlMinutes * 60;


        redisPool = new JedisPool(new JedisPoolConfig(),
                redis, 6379, Protocol.DEFAULT_TIMEOUT);


        String tableCachePrefix = prefix + ".tableCache.";
        String tableNamesCachePrefix = prefix + ".tableNamesCache.";

        tableCache = new RedisCache(redisPool, tableCachePrefix.getBytes(),
                cacheTtl, missingCacheTTlMin * 60,
                new CacheLoader<HiveTableName, Table>() {
                    
                    public Table load(HiveTableName hiveTableName) throws Exception {
                        return sourceClient.getTable(hiveTableName.getDatabaseName(),
                                hiveTableName.getTableName());
                    }
                }, enableMissingCache);

        tableNamesCache = new RedisCache(redisPool, tableNamesCachePrefix.getBytes(),
                cacheTtl, missingCacheTTlMin * 60,
                new CacheLoader<String, List<String>>() {
                    
                    public List<String> load(String databaseName) throws Exception {
                        return sourceClient.getAllTables(databaseName);
                    }
                }, enableMissingCache);
    }

    /*protected CacheStats getCachedStats() throws Exception {
        Jedis redis = null;
        try {
            redis = redisPool.getResource();
            Properties properties = new Properties();
            String stats = redis.info("Stats");
            properties.load(new StringReader(stats));
            Integer keyspace_hits = Integer.
                    valueOf((String) properties.get("keyspace_hits"));
            Integer keyspace_misses = Integer.
                    valueOf((String) properties.get("keyspace_misses"));
            CacheStats cacheStats = new CacheStats(keyspace_hits, keyspace_misses);
            return cacheStats;
        } catch (JedisConnectionException e) {
            if (redis != null) {
                redisPool.returnBrokenResource(redis);
                redis = null;
            }
            throw e;
        } finally {
            if (redis != null) {
                redisPool.returnResource(redis);
            }
        }
    }*/

    
    public boolean isCompatibleWith(HiveConf hiveConf) {
        throw new NotImplementedException();
    }

    
    public void setHiveAddedJars(String s) {
        throw new NotImplementedException();
    }

    
    public void reconnect() throws MetaException {
        throw new NotImplementedException();
    }

    
    public void close() {
        throw new NotImplementedException();
    }

    
    public void setMetaConf(String s, String s1) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public String getMetaConf(String s) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<String> getDatabases(String s) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<String> getAllDatabases() throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<String> getTables(String dbname, String tablePattern) throws MetaException,
            TException, UnknownDBException {

        List<String> tableNames = Lists.newArrayList();
        List<String> allTableNames = getAllTables(dbname);

        for (String tableName : allTableNames) {
            if (tableName.matches(tablePattern)) {
                tableNames.add(tableName);
            }
        }
        return tableNames;
    }

    
    public List<String> getAllTables(String dbName)
            throws MetaException, TException, UnknownDBException {
        try {
            return tableNamesCache.get(dbName);
        } catch (ExecutionException e) {
            LOGGER.error("Error while fetching table list: " + e.getMessage(), e);
            throw new CachingMetastoreException("Unable to fetch table list for db: " + dbName);
        }
    }

    
    public List<String> listTableNamesByFilter(String s, String s1, short i) throws MetaException,
            TException, InvalidOperationException, UnknownDBException {
        throw new NotImplementedException();
    }

    
    public void dropTable(String s, String s1, boolean b, boolean b1) throws MetaException,
            TException, NoSuchObjectException {
        throw new NotImplementedException();
    }

    
    public void dropTable(String s, String s1, boolean b, boolean b1, boolean b2)
            throws MetaException, TException, NoSuchObjectException {
        throw new NotImplementedException();
    }

    
    public void dropTable(String s, boolean b) throws MetaException, UnknownTableException,
            TException, NoSuchObjectException {
        throw new NotImplementedException();
    }

    
    public void dropTable(String s, String s1) throws MetaException,
            TException, NoSuchObjectException {
        throw new NotImplementedException();
    }

    
    public boolean tableExists(String s, String s1) throws MetaException,
            TException, UnknownDBException {
        throw new NotImplementedException();
    }

    
    public boolean tableExists(String s) throws MetaException, TException, UnknownDBException {
        throw new NotImplementedException();
    }

    
    public Table getTable(String s) throws MetaException, TException, NoSuchObjectException {
        throw new NotImplementedException();
    }

    
    public Database getDatabase(String s) throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public Table getTable(String dbName, String tableName) throws MetaException,
            TException, NoSuchObjectException {
        try {
            HiveTableName hiveTableName = new HiveTableName(dbName, tableName);
            return tableCache.get(hiveTableName);
        } catch (ExecutionException e) {
            LOGGER.error("Error while fetching table metadata: " + e.getMessage(), e);
            throw new CachingMetastoreException(
                    "Unable to fetch table metadata for table: " + dbName + "." + tableName);
        }
    }

    
    public List<Table> getTableObjectsByName(String s, List<String> list) throws MetaException,
            InvalidOperationException, UnknownDBException, TException {
        throw new NotImplementedException();
    }

    
    public Partition appendPartition(String s, String s1, List<String> list)
            throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public Partition appendPartition(String s, String s1, String s2) throws InvalidObjectException,
            AlreadyExistsException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public Partition add_partition(Partition partition) throws InvalidObjectException,
            AlreadyExistsException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public int add_partitions(List<Partition> list) throws InvalidObjectException,
            AlreadyExistsException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public int add_partitions_pspec(PartitionSpecProxy partitionSpecProxy)
            throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<Partition> add_partitions(List<Partition> list, boolean b, boolean b1)
            throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public Partition getPartition(String s, String s1, List<String> list)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public Partition exchange_partition(Map<String, String> map, String s,
                                        String s1, String s2, String s3)
            throws MetaException, NoSuchObjectException, InvalidObjectException, TException {
        throw new NotImplementedException();
    }

    
    public Partition getPartition(String s, String s1, String s2)
            throws MetaException, UnknownTableException, NoSuchObjectException, TException {
        throw new NotImplementedException();
    }

    
    public Partition getPartitionWithAuthInfo(String s, String s1,
                                              List<String> list, String s2, List<String> list1)
            throws MetaException, UnknownTableException, NoSuchObjectException, TException {
        throw new NotImplementedException();
    }

    
    public List<Partition> listPartitions(String s, String s1, short i)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public PartitionSpecProxy listPartitionSpecs(String s, String s1, int i) throws TException {
        throw new NotImplementedException();
    }

    
    public List<Partition> listPartitions(String s, String s1, List<String> list, short i)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<String> listPartitionNames(String s, String s1, short i)
            throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<String> listPartitionNames(String s, String s1, List<String> list, short i)
            throws MetaException, TException, NoSuchObjectException {
        throw new NotImplementedException();
    }

    
    public List<Partition> listPartitionsByFilter(String s, String s1, String s2, short i)
            throws MetaException, NoSuchObjectException, TException {
        throw new NotImplementedException();
    }

    
    public PartitionSpecProxy listPartitionSpecsByFilter(String s, String s1, String s2, int i)
            throws MetaException, NoSuchObjectException, TException {
        throw new NotImplementedException();
    }

    
    public boolean listPartitionsByExpr(String s, String s1, byte[] bytes,
                                        String s2, short i, List<Partition> list) throws TException {
        throw new NotImplementedException();
    }

    
    public List<Partition> listPartitionsWithAuthInfo(String s, String s1,
                                                      short i, String s2, List<String> list)
            throws MetaException, TException, NoSuchObjectException {
        throw new NotImplementedException();
    }

    
    public List<Partition> getPartitionsByNames(String s, String s1, List<String> list)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<Partition> listPartitionsWithAuthInfo(String s, String s1, List<String> list,
                                                      short i, String s2, List<String> list1)
            throws MetaException, TException, NoSuchObjectException {
        throw new NotImplementedException();
    }

    
    public void markPartitionForEvent(String s, String s1, Map<String, String> map,
                                      PartitionEventType partitionEventType)
            throws MetaException, NoSuchObjectException, TException, UnknownTableException,
            UnknownDBException, UnknownPartitionException, InvalidPartitionException {
        throw new NotImplementedException();
    }

    
    public boolean isPartitionMarkedForEvent(String s, String s1, Map<String, String> map,
                                             PartitionEventType partitionEventType)
            throws MetaException, NoSuchObjectException, TException, UnknownTableException,
            UnknownDBException, UnknownPartitionException, InvalidPartitionException {
        throw new NotImplementedException();
    }

    
    public void validatePartitionNameCharacters(List<String> list) throws TException, MetaException {
        throw new NotImplementedException();
    }

    
    public void createTable(Table table) throws AlreadyExistsException,
            InvalidObjectException, MetaException, NoSuchObjectException, TException {
        throw new NotImplementedException();
    }

    
    public void alter_table(String s, String s1, Table table)
            throws InvalidOperationException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void alter_table(String s, String s1, Table table, boolean b)
            throws InvalidOperationException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void createDatabase(Database database)
            throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void dropDatabase(String s) throws NoSuchObjectException,
            InvalidOperationException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void dropDatabase(String s, boolean b, boolean b1)
            throws NoSuchObjectException, InvalidOperationException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void dropDatabase(String s, boolean b, boolean b1, boolean b2)
            throws NoSuchObjectException, InvalidOperationException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void alterDatabase(String s, Database database)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public boolean dropPartition(String s, String s1, List<String> list, boolean b)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public boolean dropPartition(String s, String s1, List<String> list,
                                 PartitionDropOptions partitionDropOptions) throws TException {
        throw new NotImplementedException();
    }

    
    public List<Partition> dropPartitions(String s, String s1,
                                          List<ObjectPair<Integer, byte[]>> list,
                                          boolean b, boolean b1, boolean b2)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<Partition> dropPartitions(String s, String s1,
                                          List<ObjectPair<Integer, byte[]>> list, boolean b,
                                          boolean b1, boolean b2, boolean b3)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<Partition> dropPartitions(String s, String s1,
                                          List<ObjectPair<Integer, byte[]>> list,
                                          PartitionDropOptions partitionDropOptions)
            throws TException {
        throw new NotImplementedException();
    }

    
    public boolean dropPartition(String s, String s1, String s2, boolean b)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void alter_partition(String s, String s1, Partition partition)
            throws InvalidOperationException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void alter_partitions(String s, String s1, List<Partition> list)
            throws InvalidOperationException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void renamePartition(String s, String s1, List<String> list, Partition partition)
            throws InvalidOperationException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<FieldSchema> getFields(String db, String tableName)
            throws MetaException, TException, UnknownTableException, UnknownDBException {
        Table table = getTable(db, tableName);
        List<FieldSchema> columns = Lists.newArrayList();
        columns.addAll(table.getSd().getCols());
        columns.addAll(table.getPartitionKeys());
        return columns;
    }

    
    public List<FieldSchema> getSchema(String s, String s1)
            throws MetaException, TException, UnknownTableException, UnknownDBException {
        throw new NotImplementedException();
    }

    
    public String getConfigValue(String s, String s1) throws TException, ConfigValSecurityException {
        throw new NotImplementedException();
    }

    
    public List<String> partitionNameToVals(String s) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public Map<String, String> partitionNameToSpec(String s) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void createIndex(Index index, Table table)
            throws InvalidObjectException, MetaException, NoSuchObjectException,
            TException, AlreadyExistsException {
        throw new NotImplementedException();
    }

    
    public void alter_index(String s, String s1, String s2, Index index)
            throws InvalidOperationException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public Index getIndex(String s, String s1, String s2) throws MetaException,
            UnknownTableException, NoSuchObjectException, TException {
        throw new NotImplementedException();
    }

    
    public List<Index> listIndexes(String s, String s1, short i)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<String> listIndexNames(String s, String s1, short i)
            throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public boolean dropIndex(String s, String s1, String s2, boolean b)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public boolean updateTableColumnStatistics(ColumnStatistics columnStatistics)
            throws NoSuchObjectException, InvalidObjectException, MetaException,
            TException, InvalidInputException {
        throw new NotImplementedException();
    }

    
    public boolean updatePartitionColumnStatistics(ColumnStatistics columnStatistics)
            throws NoSuchObjectException, InvalidObjectException,
            MetaException, TException, InvalidInputException {
        throw new NotImplementedException();
    }

    
    public List<ColumnStatisticsObj> getTableColumnStatistics(String s, String s1, List<String> list)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public Map<String, List<ColumnStatisticsObj>> getPartitionColumnStatistics(
            String s, String s1, List<String> list, List<String> list1)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public boolean deletePartitionColumnStatistics(String s, String s1, String s2, String s3)
            throws NoSuchObjectException, MetaException,
            InvalidObjectException, TException, InvalidInputException {
        throw new NotImplementedException();
    }

    
    public boolean deleteTableColumnStatistics(String s, String s1, String s2)
            throws NoSuchObjectException, MetaException, InvalidObjectException,
            TException, InvalidInputException {
        throw new NotImplementedException();
    }

    
    public boolean create_role(Role role) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public boolean drop_role(String s) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<String> listRoleNames() throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public boolean grant_role(String s, String s1, PrincipalType principalType,
                              String s2, PrincipalType principalType1, boolean b)
            throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public boolean revoke_role(String s, String s1, PrincipalType principalType, boolean b)
            throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<Role> list_roles(String s, PrincipalType principalType)
            throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public PrincipalPrivilegeSet get_privilege_set(HiveObjectRef hiveObjectRef,
                                                   String s, List<String> list)
            throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<HiveObjectPrivilege> list_privileges(
            String s, PrincipalType principalType, HiveObjectRef hiveObjectRef)
            throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public boolean grant_privileges(PrivilegeBag privilegeBag) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public boolean revoke_privileges(PrivilegeBag privilegeBag, boolean b)
            throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public String getDelegationToken(String s, String s1) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public long renewDelegationToken(String s) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void cancelDelegationToken(String s) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public String getTokenStrForm() throws IOException {
        throw new NotImplementedException();
    }

    
    public void createFunction(Function function)
            throws InvalidObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void alterFunction(String s, String s1, Function function)
            throws InvalidObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public void dropFunction(String s, String s1)
            throws MetaException, NoSuchObjectException,
            InvalidObjectException, InvalidInputException, TException {
        throw new NotImplementedException();
    }

    
    public Function getFunction(String s, String s1) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public List<String> getFunctions(String s, String s1) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public ValidTxnList getValidTxns() throws TException {
        throw new NotImplementedException();
    }

    
    public ValidTxnList getValidTxns(long l) throws TException {
        throw new NotImplementedException();
    }

    
    public long openTxn(String s) throws TException {
        throw new NotImplementedException();
    }

    
    public OpenTxnsResponse openTxns(String s, int i) throws TException {
        throw new NotImplementedException();
    }

    
    public void rollbackTxn(long l) throws NoSuchTxnException, TException {
        throw new NotImplementedException();
    }

    
    public void commitTxn(long l) throws NoSuchTxnException, TxnAbortedException, TException {
        throw new NotImplementedException();
    }

    
    public GetOpenTxnsInfoResponse showTxns() throws TException {
        throw new NotImplementedException();
    }

    
    public LockResponse lock(LockRequest lockRequest)
            throws NoSuchTxnException, TxnAbortedException, TException {
        throw new NotImplementedException();
    }

    
    public LockResponse checkLock(long l)
            throws NoSuchTxnException, TxnAbortedException, NoSuchLockException, TException {
        throw new NotImplementedException();
    }

    
    public void unlock(long l) throws NoSuchLockException, TxnOpenException, TException {
        throw new NotImplementedException();
    }

    
    public ShowLocksResponse showLocks() throws TException {
        throw new NotImplementedException();
    }

    
    public void heartbeat(long l, long l1)
            throws NoSuchLockException, NoSuchTxnException, TxnAbortedException, TException {
        throw new NotImplementedException();
    }

    
    public HeartbeatTxnRangeResponse heartbeatTxnRange(long l, long l1) throws TException {
        throw new NotImplementedException();
    }

    
    public void compact(String s, String s1, String s2, CompactionType compactionType)
            throws TException {
        throw new NotImplementedException();
    }

    
    public ShowCompactResponse showCompactions() throws TException {
        throw new NotImplementedException();
    }

    
    public void addDynamicPartitions(long l, String s, String s1, List<String> list)
            throws TException {
        throw new NotImplementedException();
    }

    
    public NotificationEventResponse getNextNotification(
            long l, int i, NotificationFilter notificationFilter) throws TException {
        throw new NotImplementedException();
    }

    
    public CurrentNotificationEventId getCurrentNotificationEventId() throws TException {
        throw new NotImplementedException();
    }

    
    public FireEventResponse fireListenerEvent(FireEventRequest fireEventRequest) throws TException {
        throw new NotImplementedException();
    }

    
    public GetPrincipalsInRoleResponse get_principals_in_role(
            GetPrincipalsInRoleRequest getPrincipalsInRoleRequest) throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public GetRoleGrantsForPrincipalResponse get_role_grants_for_principal(
            GetRoleGrantsForPrincipalRequest getRoleGrantsForPrincipalRequest)
            throws MetaException, TException {
        throw new NotImplementedException();
    }

    
    public AggrStats getAggrColStatsFor(String s, String s1, List<String> list, List<String> list1)
            throws NoSuchObjectException, MetaException, TException {
        throw new NotImplementedException();
    }

    
    public boolean setPartitionColumnStatistics(SetPartitionsStatsRequest setPartitionsStatsRequest)
            throws NoSuchObjectException, InvalidObjectException,
            MetaException, TException, InvalidInputException {
        throw new NotImplementedException();
    }

    /**
     * Created by sakshibansal on 02/05/17.
     */
    protected static class HiveTableName implements Serializable {
        private final String databaseName;
        private final String tableName;

        protected HiveTableName(String databaseName, String tableName) {
            this.databaseName = databaseName;
            this.tableName = tableName;
        }

        protected static HiveTableName table(String databaseName, String tableName) {
            return new HiveTableName(databaseName, tableName);
        }

        protected String getDatabaseName() {
            return databaseName;
        }

        protected String getTableName() {
            return tableName;
        }

        
        public String toString() {
            return toStringHelper(this)
                    .add("databaseName", databaseName)
                    .add("tableName", tableName)
                    .toString();
        }

    }

    /**
     * Created by sakshibansal on 02/05/17.
     */

    protected class CacheStats {

        @JsonProperty
        private Integer keyspaceHits;
        @JsonProperty
        private Integer keyspaceMisses;

        private CacheStats(Integer keyspaceHits, Integer keyspaceMisses) {
            this.keyspaceHits = keyspaceHits;
            this.keyspaceMisses = keyspaceMisses;
        }

        protected Integer getKeyspaceHits() {
            return keyspaceHits;
        }

        protected Integer getKeyspaceMisses() {
            return keyspaceMisses;
        }
    }

}

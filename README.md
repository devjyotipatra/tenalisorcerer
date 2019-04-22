# tenalisorcerer

Parsing and analysis of Hive, Spark and Presto commands.

Currently, The project works only with SQL dialects, but we are adding other command types that are currently 
supported by Qubole.

Each command type implements its own tokenization and parsing logic. The resultant abstract syntax trees (AST) 
are then converted into a single tree based model (TenaliAst) for representing commands of any type. The model 
for TenaliAst is defined in  com.qubole.tenali.parse.sql.datamodel.

Once the commands are parsed, we can apply a number of transformations on these TenaliAst's. Following are some of
the transformer functions that have been implemented.
1. HiveAstTransformer .  Transforms a Hive AST (AstNode) into TenaliAst
2. CalciteAstTransformer  .  Transforms a Calcite AST (SqlNode) into TenaliAst
3. TenaliAstAliasResolver .  Transforms the TenaliAst with raw names to an TenaliAst over resolved names. It requires 
as input the full list of columns in every table and the full list of tables in every schema, 
otherwise known as "catalog information". This is currently available through Qubole's Metastore API's. But we will be adding 
support for reading catalog through local filesystem soon. 


Usage
```
import com.qubole.tenali.parse.sql.SqlCommandHandler;
import com.qubole.tenali.parse.catalog.Catalog;
import com.qubole.tenali.parse.catalog.CatalogResolver;
import com.qubole.tenali.parse.sql.*;
import com.qubole.tenali.parse.config.CommandContext;
import com.qubole.tenali.parse.lexer.HiveCommandLexer;
import com.qubole.tenali.parse.sql.visitor.TenaliAstAliasResolver;
import com.qubole.tenali.parse.util.CachingMetastore;

public static CommandContext parseHive(String command) throws IOException {
        CommandContext ctx = null;
        try {
            Catalog catalog = new CachingMetastore(5911, "api.qubole.com",
                    "lkdwnfdlewknflwekfqpdnwfnwklfnwelkfnwelnf",
                    "mojave-redis.9qcbtf.0001.use1.cache.amazonaws.com");
            ctx =  new SqlCommandHandler(CommandType.HIVE)
                    .setLexer(new HiveCommandLexer())
                    .setParser(new HiveSqlParser())
                    .setTransformer(new HiveAstTransformer())
                    .setTransformer(new TenaliAstAliasResolver(new CatalogResolver(catalog)))
                    .build(command);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return ctx;
    }
```



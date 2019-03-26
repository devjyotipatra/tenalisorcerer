package com.qubole.tenali.parse.config;


import java.util.Objects;

/**
 * Created by devjyotip on 5/30/18.
 */
public class CommandContext {

    String stmt;

    QueryContext qCtx;

    QueryType queryType;

    CommandContext parent;

    CommandContext child;

    boolean isRootNode = false;

    boolean isDDLQuery;

    boolean isSupportedDDLQuery;


    public CommandContext() {  }

    public CommandContext(QueryType type) {
        setQueryType(type);
    }


    public boolean hasParent() {
        return parent != null;
    }

    public CommandContext getParent() {
        return parent;
    }

    public void setParent(CommandContext parent) {
        this.parent = parent;
    }

    public boolean hasChild() {
        return child != null;
    }

    public CommandContext getChild() {
        return child;
    }

    public void setChild(CommandContext child) {
        this.child = child;
    }

    public String getStmt() {
        return stmt;
    }

    public void setStmt(String stmt) {
        this.stmt = stmt;
    }



    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }


    public boolean isRootNode() {
        return isRootNode;
    }

    public void setAsRootNode() {
        isRootNode = true;
    }


    public void setQueryContext(QueryContext qCtx) {
        this.qCtx = qCtx;
    }

    public QueryContext getQueryContext() {
        return this.qCtx;
    }


    public CommandContext cloneContext() {
        CommandContext ctx = new CommandContext(queryType);
        ctx.setStmt(stmt);
        return ctx;
    }


    public CommandContext getCurrentContext() {
        CommandContext qctx = this;

        while (qctx.hasChild()) {
            qctx = qctx.getChild();
        }

        return qctx;
    }

    public void appendNewContext(CommandContext cctx) {
        setChild(cctx);
        cctx.setParent(this);
    }



    public void setIsDDLQuery() {
        isDDLQuery = true;
    }

    public void setIsSupportedDDLQuery() {
        isSupportedDDLQuery = true;
    }

    public boolean getIsDDLQuery() {
        return isDDLQuery;
    }

    public boolean getIsSupportedDDLQuery() {
        return isSupportedDDLQuery;
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof CommandContext)) {
            return false;
        }
        CommandContext context = (CommandContext) other;
        return getQueryType() == context.getQueryType() &&
                Objects.equals(parent, context.parent);
    }

    @Override
    public int hashCode() {
        long parentHashCode = (parent == null) ? -1 : parent.hashCode();
        return Objects.hash(parentHashCode);
    }


    /*public CommandContextIterator iterator() {
        return new CommandContextIterator(this);
    }


    public class CommandContextIterator {

        CommandContext rootCtx = null;

        private CommandContextIterator() { }

        private CommandContextIterator(CommandContext rootCtx) {
            this.rootCtx = rootCtx;
        }

        public boolean hasNext() {
            if(rootCtx == null) {
                return false;
            }

            if(rootCtx.isRootNode) {
                return true;
            }

            if(rootCtx.hasChild()) {
                CommandContext ctx = rootCtx.getChild();

                if (ctx != null) {
                    return true;
                }
            }
            return false;
        }


        public CommandContext next() {
            CommandContext ctx = rootCtx;
            rootCtx = rootCtx.getChild();
            return ctx;
        }

    }*/
}

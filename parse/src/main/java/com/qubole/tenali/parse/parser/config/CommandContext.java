package com.qubole.tenali.parse.parser.config;


/**
 * Created by devjyotip on 5/30/18.
 */
public class CommandContext {

    TenaliType type;

    String stmt;

    CommandContext parent;

    CommandContext child;

    public CommandContext() { }


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


    public TenaliType getType() {
        return type;
    }

    public void setType(TenaliType type) {
        this.type = type;
    }


    public CommandContext cloneContext() {
        CommandContext ctx = new CommandContext();
        return ctx;
    }


    public CommandContext getCurrentContext() {
        CommandContext qctx = this;

        while (qctx.hasChild()) {
            qctx = qctx.getChild();
        }

        return qctx;
    }

    public void appendNewContext(CommandContext qctx) {
        CommandContext cctx = this;
        appendNewContext(cctx, qctx);
    }

    private void appendNewContext(CommandContext cctx, CommandContext qctx) {
        qctx.setChild(cctx);
        cctx.setParent(qctx);
    }
}

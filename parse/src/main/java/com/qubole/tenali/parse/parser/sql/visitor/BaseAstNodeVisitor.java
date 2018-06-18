package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.*;

public abstract class BaseAstNodeVisitor {

    public abstract void visit(BaseAstNode node);

}

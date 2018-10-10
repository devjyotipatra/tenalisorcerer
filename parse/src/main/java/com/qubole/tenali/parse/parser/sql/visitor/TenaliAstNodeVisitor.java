package com.qubole.tenali.parse.parser.sql.visitor;

import com.qubole.tenali.parse.parser.sql.datamodel.*;

public abstract class TenaliAstNodeVisitor {

    public abstract void visit(TenaliAstNode node);

}

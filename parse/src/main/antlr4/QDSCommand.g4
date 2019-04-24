grammar QDSCommand;

/*
* Parser rules
*/


parse
 : SPACES* sql_stmt EOF
 ;


sql_stmt
  : op=(Q_SET
  |  Q_ADD_JAR
  |  Q_USE
  |  Q_CTE
  |  Q_CREATE_FUNCTION
  |  Q_INSERT_INTO
  |  Q_INSERT_OVERWRITE
  |  Q_SELECT
  |  Q_DROP_TABLE
  |  Q_DROP_VIEW
  |  Q_ALTER_TABLE
  |  Q_CREATE_TABLE
  |  Q_CREATE_EXTERNAL_TABLE
  |  Q_CREATE_VIEW
  |  Q_CREATE_DATABASE)
  ;


/*
* Lexer rules
*/

Q_SET
 :  S E T  SPACES  TEXT+
 ;

Q_ADD_JAR
 :  A D D  SPACES  J A R  SPACES  TEXT+
 ;

Q_USE
 :  U S E  SPACES  TEXT+
 ;

Q_CREATE_FUNCTION
 :  C R E A T E  SPACES  T E M P O R A R Y  SPACES  TEXT+
 ;

Q_INSERT_INTO
 :  I N S E R T  SPACES  I N T O SPACES TEXT+
 ;

Q_INSERT_OVERWRITE
 :  I N S E R T  SPACES  O V E R W R I T E SPACES TEXT+
 ;

Q_SELECT
 :  S E L E C T  SPACES  TEXT+
 ;


Q_DROP_TABLE
 : D R O P  SPACES  T A B L E  SPACES  TEXT+
 ;

Q_DROP_VIEW
 : D R O P  SPACES  V I E W  SPACES  TEXT+
 ;

Q_ALTER_TABLE
 :  A L T E R  SPACES  T A B L E  SPACES+  TEXT+
 ;

Q_CREATE_TABLE
 :  C R E A T E  SPACES  T A B L E  SPACES  TEXT+
 ;


Q_CREATE_DATABASE
 :  C R E A T E  SPACES  D A T A B A S E  SPACES  TEXT+
 ;


Q_CREATE_EXTERNAL_TABLE
  :  C R E A T E  SPACES  E X T E R N A L  SPACES  T A B L E  SPACES  TEXT+
  ;

Q_CREATE_VIEW
 :  C R E A T E  SPACES  V I E W  SPACES  TEXT+
 ;

Q_CTE
 :  W I T H  SPACES  TEXT+
 ;


TEXT
 :  ~[;]
 ;


Q_SEMI
 : [ ;]+
 ;


SIMPLE_COMMENT
    : '--' ~[\r\n]* '\r'* '\n'* -> channel(HIDDEN)
    ;

BRACKETED_COMMENT
    : '/*' .*? '*/' -> channel(HIDDEN)
    ;


SPACES
 : [ \t\r\n]+
 ;



fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];

package com.tonbeller.jpivot.olap.mdxparse;
import java_cup.runtime.Symbol;

%%

%eofval{
  return new Symbol(sym.EOF, null);
%eofval}

AND=[Aa][Nn][Dd]
AS=[Aa][Ss]
CASE=[Cc][Aa][Ss][Ee]
CELL=[Cc][Ee][Ll][Ll]
DIMENSION=[Dd][Ii][Mm][Ee][Nn][Ss][Ii][Oo][Nn]
DRILLTHROUGH=[Dd][Rr][Ii][Ll][Ll][Tt][Hh][Rr][Oo][Uu][Gg][Hh]
ELSE=[Ee][Ll][Ss][Ee]
EMPTY=[Ee][Mm][Pp][Tt][Yy]
END=[Ee][Nn][Dd]
FROM=[Ff][Rr][Oo][Mm]
MEMBER=[Mm][Ee][Mm][Bb][Ee][Rr]
NON=[Nn][Oo][Nn]
NOT=[Nn][Oo][Tt]
ON=[Oo][Nn]
OR=[Oe][Rr]
PROPERTIES=[Pp][Rr][Oo][Pp][Ee][Rr][Tt][Ii][Ee][Ss]
SELECT=[Ss][Ee][Ll][Ee][Cc][Tt]
SET=[Ss][Ee][Tt]
THEN=[Tt][Hh][Ee][Nn]
WHEN=[Ww][Hh][Ee][Nn]
WITH=[Ww][Ii][Tt][Hh]
WHERE=[Ww][Hh][Ee][Rr][Ee]
XOR=[Xx][Oo][Rr]
WHITESPACE=[\ \t\r\n\f]
ALPHA = [a-zA-Z]
ALNUM = [a-zA-Z_0-9]
IDENT = {ALPHA}{ALNUM}*
BRACKETID = \[[^\]\n\f\r\t]*\]
INSTRING = [^\"\n\f\r\t]
INSTRING2 = [^'\n\f\r\t]
DIGIT=[0-9]
INTEGER={DIGIT}+
EXP = ([eE](\+|\-)?{DIGIT}+)
FLOAT = ({DIGIT}+\.{DIGIT}*{EXP}?|{DIGIT}*\.{DIGIT}+{EXP}?|{DIGIT}+{EXP})

%cup
%unicode

%state AFTER_AS1
%state AFTER_AS2

%%
{AND} { return new Symbol(sym.AND, "AND"); }
{AS} { yybegin(AFTER_AS1); return new Symbol(sym.AS, "AS"); }
{CASE} { return new Symbol(sym.CASE, "CASE"); }
{CELL} { return new Symbol(sym.CELL, "CELL"); }
{DIMENSION} { return new Symbol(sym.DIMENSION, "DIMENSION"); }
{DRILLTHROUGH} { return new Symbol(sym.DRILLTHROUGH, "DRILLTHROUGH"); }
{ELSE} { return new Symbol(sym.ELSE, "ELSE"); }
{EMPTY} { return new Symbol(sym.EMPTY, "EMPTY"); }
{END} { return new Symbol(sym.END, "END"); }
{FROM} { return new Symbol(sym.FROM, "FROM"); }
{MEMBER} { return new Symbol(sym.MEMBER, "MEMBER"); }
{NON} { return new Symbol(sym.NON, "NON"); }
{NOT} { return new Symbol(sym.NOT, "NOT"); }
{ON} { return new Symbol(sym.ON, "ON"); }
{OR} { return new Symbol(sym.OR, "OR"); }
{PROPERTIES} { return new Symbol(sym.PROPERTIES, "PROPERTIES"); }
{SELECT} { return new Symbol(sym.SELECT, "SELECT"); }
{SET} { return new Symbol(sym.SET, "SET"); }
{THEN} { return new Symbol(sym.THEN, "THEN"); }
{WHEN} { return new Symbol(sym.WHEN, "WHEN"); }
{WITH} { return new Symbol(sym.WITH, "WITH"); }
{WHERE} { return new Symbol(sym.WHERE, "WHERE"); }
{XOR} { return new Symbol(sym.XOR, "XOR"); }
":" { return new Symbol(sym.COLON, ":"); }
"," { return new Symbol(sym.COMMA, ","); }
"=" { return new Symbol(sym.EQ, "="); }
"<>" { return new Symbol(sym.NE, "<>"); }
"<=" { return new Symbol(sym.LE, "<="); }
"<" { return new Symbol(sym.LT, "<"); }
">=" { return new Symbol(sym.GE, ">="); }
">" { return new Symbol(sym.GT, ">"); }
"{" { return new Symbol(sym.LBRACE, "{"); }
"(" { return new Symbol(sym.LPAREN, "("); }
"}" { return new Symbol(sym.RBRACE, "}"); }
")" { return new Symbol(sym.RPAREN, ")"); }
"+" { return new Symbol(sym.PLUS, "+"); }
"-" { return new Symbol(sym.MINUS, "-"); }
"*" { return new Symbol(sym.ASTERISK, "*"); }
"/" { return new Symbol(sym.SOLIDUS, "/"); }
"||" { return new Symbol(sym.CONCAT, "||"); }
"." { return new Symbol(sym.DOT, "."); }
<AFTER_AS1> "'" {yybegin(AFTER_AS2); return new Symbol(sym.QUOTE, "'"); }
<AFTER_AS2> "'" {yybegin(YYINITIAL); return new Symbol(sym.QUOTE, "'"); }
{INTEGER} { Double d = new Double(yytext()); return new Symbol(sym.NUMBER, d); }
{FLOAT} { Double d = new Double(yytext()); return new Symbol(sym.NUMBER, d); }
{IDENT} { return new Symbol(sym.ID, yytext()); }
{BRACKETID} { return new Symbol(sym.QUOTED_ID, yytext()); }
"&"{BRACKETID} { return new Symbol(sym.AMP_QUOTED_ID, yytext()); }
"\""{INSTRING}*"\"" { return new Symbol(sym.STRING, yytext()); }
<YYINITIAL> "'"{INSTRING2}*"'" { return new Symbol(sym.STRING, yytext()); }
{WHITESPACE} { /* ignore white space. */ }
. { System.out.println("Unmatched input: " + yytext()); return new Symbol(sym.UNKNOWN, yytext()); }

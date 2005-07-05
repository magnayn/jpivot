/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 *
 * 
 */
package com.tonbeller.jpivot.olap.model;

import java.util.HashSet;
import java.util.Set;

/**
 * prints an expression for testing purposes
 * 
 * @author av
 */
public class ExpressionPrinter extends VisitorSupportStrict {
  StringBuffer sb = new StringBuffer();

  static final Set infix = new HashSet();
  static final Set prefix = new HashSet();
  static {
    String[] s = new String[] { "*", "/", "+", "-", "%", "<", ">", "<=", ">=", "<>", "=", "and", "or", "xor" };
    for (int i = 0; i < s.length; i++)
      infix.add(s[i]);
    prefix.add("not");
    prefix.add("-");
  };

  public String print(Visitable e) {
    if (e == null)
      return "(null)";
    sb = new StringBuffer();
    e.accept(this);
    return sb.toString();
  }

  public String toString() {
    return sb.toString();
  }

  void append(String s) {
    sb.append(s);
  }

  public void visitBooleanExpr(BooleanExpr v) {
    sb.append(v.getValue());
  }
  public void visitDimension(Dimension v) {
    sb.append("[" + v.getLabel() + "]");
  }
  public void visitDoubleExpr(DoubleExpr v) {
    sb.append(v.getValue());
  }
  public void visitFunCallExpr(FunCallExpr v) {
    ExpressionPrinter ep = new ExpressionPrinter();
    Expression[] args = v.getArgs();

    // binary infix operator (not including unary minus)
    String name = v.getName();
    if (infix.contains(name) && args.length == 2) {
      String left = ep.print(args[0]);
      String right = ep.print(args[1]);
      sb.append(left).append(name).append(right);
      return;
    }

    for (int i = 0; i < args.length; i++) {
      if (i > 0)
        ep.append(", ");
      args[i].accept(ep);
    }
    if ("()".equals(name))
      sb.append("(").append(ep).append(")");
    else if ("{}".equals(name))
      sb.append("{").append(ep).append("}");
    else if (prefix.contains(name))
      sb.append(name).append(ep); // unary minus, not
    else if (name.startsWith(".")) {
      if (args.length > 1) {
        // syntax is "object.method(arg, arg)"
        ep = new ExpressionPrinter();
        args[0].accept(ep);
        sb.append(ep).append(name).append("(");
        ep = new ExpressionPrinter();
        for (int i = 1; i < args.length; i++) {
          if (i > 1)
            ep.append(", ");
          args[i].accept(ep);
        }
        sb.append(ep).append(")");
      }
      else
        sb.append(ep).append(name);
    }
    else
      sb.append(name).append("(").append(ep).append(")");
  }
  public void visitHierarchy(Hierarchy v) {
    sb.append("[" + v.getLabel() + "]");
  }
  public void visitIntegerExpr(IntegerExpr v) {
    sb.append(v.getValue());
  }
  public void visitLevel(Level v) {
    sb.append("[" + v.getLabel() + "]");
  }
  public void visitMember(Member v) {
    sb.append("[" + v.getLabel() + "]");
  }
  public void visitParameterExpr(ParameterExpr v) {
    // sb.append("Parameter(\"").append(v.getName()).append("\"");
    // v.getName() is random, dont show
    sb.append("Parameter(\"RandomID\"");
    switch (v.getType()) {
      case ParameterExpr.MEMBER :
        sb.append(", MEMBER");
        break;
      case ParameterExpr.NUMBER :
        sb.append(", NUMBER");
        break;
      case ParameterExpr.STRING :
        sb.append(", STRING");
        break;
      default :
        throw new IllegalArgumentException();
    }
    sb.append(", \"").append(v.getLabel()).append("\"");
    sb.append(", ");
    v.getValue().accept(this);
    sb.append(")");
  }
  public void visitStringExpr(StringExpr v) {
    sb.append("\"" + v.getValue() + "\"");
  }

  public void visitPropertyExpr(PropertyExpr v) {
    sb.append(v.getName()).append("=");
    v.getValueExpr().accept(this);
    sb.append(";");
    
  }

}

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
package com.tonbeller.jpivot.olap.query;

import java.util.HashSet;
import java.util.Set;

import com.tonbeller.jpivot.olap.model.BooleanExpr;
import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.DoubleExpr;
import com.tonbeller.jpivot.olap.model.Expression;
import com.tonbeller.jpivot.olap.model.FunCallExpr;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.IntegerExpr;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.ParameterExpr;
import com.tonbeller.jpivot.olap.model.StringExpr;
import com.tonbeller.jpivot.olap.model.VisitorSupportStrict;


/**
 * Expression to String converter
 *
 */
public class ExprStringConverter extends VisitorSupportStrict {
  static final Set infix = new HashSet();
  static final Set prefix = new HashSet();
  static {
    String[] s =
      new String[] { "and", "or", "xor", "*", "/", "+", "-", "%", "<", ">", "<=", ">=", "<>", "=" };
    for (int i = 0; i < s.length; i++)
      infix.add(s[i]);

    prefix.add("-");
    prefix.add("not");
  };

  protected StringBuffer strbuf = null;

  public String exprToString(Expression e) {
    strbuf = new StringBuffer();
    e.accept(this);
    return strbuf.toString();
  }

  public void visitBooleanExpr(BooleanExpr v) {
    if (v.getValue())
      strbuf.append("true");
    else
      strbuf.append("false");
  }
  public void visitDimension(Dimension v) {
    strbuf.append(((MDXElement) v).getUniqueName());
  }

  public void visitDoubleExpr(DoubleExpr v) {
    strbuf.append(Double.toString(v.getValue()));
  }

  public void visitFunCallExpr(FunCallExpr v) {
    String name = v.getName();
    //ExprStringConverter strcnv = new ExprStringConverter();
    Expression[] args = v.getArgs();

    // infix operator (not including unary minus)
    if (infix.contains(name) && args.length == 2) {

      //argstr = strcnv.exprToString(args[0]);
      //strbuf.append(argstr);
      args[0].accept(this);
      strbuf.append(' ');
      strbuf.append(name);
      strbuf.append(' ');
      args[1].accept(this);
      //argstr = strcnv.exprToString(args[1]);
      //strbuf.append(argstr);
      return;
    } else if (v.getName().startsWith(".")) {
      name = name.substring(1);
      if (args.length == 1) {
        args[0].accept(this);
        //argstr = strcnv.exprToString(args[0]);
        //strbuf.append(argstr);
        strbuf.append('.');
        strbuf.append(name);
        return;
      }
    } else if (prefix.contains(name)) {
      strbuf.append(name);
      strbuf.append(' ');
      args[0].accept(this);
      //argstr = strcnv.exprToString(args[0]);
      //strbuf.append(argstr);
      return;
    } else if ("{}".equals(name)) {
      strbuf.append('{');
      for (int i = 0; i < args.length; i++) {
        if (i > 0)
          strbuf.append(',');
        args[i].accept(this);
        //argstr = strcnv.exprToString(args[i]);
        //strbuf.append(argstr);
      }
      strbuf.append('}');
      return;
    } else if ("()".equals(name)) {
      strbuf.append('(');
      for (int i = 0; i < args.length; i++) {
        if (i > 0)
          strbuf.append(',');
        args[i].accept(this);          
        //argstr = strcnv.exprToString(args[i]);
        //strbuf.append(argstr);
      }
      strbuf.append(')');
      return;
    }
    strbuf.append(name);
    strbuf.append('(');
    for (int i = 0; i < args.length; i++) {
      if (i > 0)
        strbuf.append(',');
      args[i].accept(this);   
      //argstr = strcnv.exprToString(args[i]);
      //strbuf.append(argstr);
    }
    strbuf.append(')');
  }

  public void visitHierarchy(Hierarchy v) {
    strbuf.append(((MDXElement) v).getUniqueName());
  }

  public void visitIntegerExpr(IntegerExpr v) {
    strbuf.append(Integer.toString(v.getValue()));
  }

  public void visitLevel(Level v) {
    strbuf.append(((MDXElement) v).getUniqueName());
  }

  public void visitMember(Member v) {
    strbuf.append(((MDXElement) v).getUniqueName());
  }

  public void visitParameterExpr(ParameterExpr v) {
    Expression val = v.getValue();
    ExprStringConverter strcnv = new ExprStringConverter();
    String valstr = strcnv.exprToString(val);
    strbuf.append(valstr);
  }

  public void visitStringExpr(StringExpr v) {
    strbuf.append(v.getValue());
  }

} // ExprStringConverter

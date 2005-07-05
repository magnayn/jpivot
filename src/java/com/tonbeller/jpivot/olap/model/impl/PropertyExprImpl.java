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
package com.tonbeller.jpivot.olap.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.tonbeller.jpivot.olap.model.Expression;
import com.tonbeller.jpivot.olap.model.FunCallExpr;
import com.tonbeller.jpivot.olap.model.PropertyExpr;
import com.tonbeller.jpivot.olap.model.StringExpr;
import com.tonbeller.jpivot.olap.model.Visitor;
import com.tonbeller.jpivot.olap.model.VisitorSupportSloppy;

/**
 * PropertyExpr implementation
 */
public class PropertyExprImpl implements PropertyExpr {

  private Expression valueExpr;
  private String name;

  /**
   * c'tor
   * @param name
   */
  public PropertyExprImpl(String name, Expression valueExpr) {
    this.name = name;
    this.valueExpr = valueExpr;
  }
  
  /**
   * @return the value expression
   * @see com.tonbeller.jpivot.olap.model.PropertyExpr#getValueExpr()
   */
  public Expression getValueExpr() {
    return valueExpr;
  }

  /**
   * @return the property name
   * @see com.tonbeller.jpivot.olap.model.PropertyExpr#getName()
   */
  public String getName() {
    return name;
  }

  /**
   * walk expression tree and find possible choices
   * @see com.tonbeller.jpivot.olap.model.PropertyExpr#getChoices()
   */
  public String[] getChoices() {

    final List choices = new ArrayList();

    this.accept(new VisitorSupportSloppy() {
      // ParameterExpr not supported
      public void visitStringExpr(StringExpr v) {
        choices.add(v.getValue());
      }

      public void visitFunCallExpr(FunCallExpr v) {
        Expression[] args = v.getArgs();
        for (int i = 0; i < args.length; i++) {
          args[i].accept(this);
        }
      }
      
      public void visitPropertyExpr(PropertyExpr v) {
        Expression exp = v.getValueExpr();
        exp.accept(this);
      }
      
    });

    return (String[]) choices.toArray(new String[0]);
  }

  /**
   * visitor implementation
   * @see com.tonbeller.jpivot.olap.model.Visitable#accept
   */
  public void accept(Visitor visitor) {
    visitor.visitPropertyExpr(this);
  }

} // PropertyExpr

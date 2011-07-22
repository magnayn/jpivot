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

import com.tonbeller.jpivot.olap.model.Expression;
import com.tonbeller.jpivot.olap.model.ParameterExpr;
import com.tonbeller.jpivot.olap.model.Visitor;

public class ParameterExprImpl implements ParameterExpr {

  private String name;
  private String label;
  private int type;
  private Expression value;

  public ParameterExprImpl() {
  }

  public ParameterExprImpl(String name, String label, int type, Expression value) {
    this.name = name;
    this.label = label;
    this.type = type;
    this.value = value;
  }

  public void accept(Visitor visitor) {
    visitor.visitParameterExpr(this);
  }

  public String getLabel() {
    return label;
  }

  public Expression getValue() {
    return value;
  }

  public void setLabel(String string) {
    label = string;
  }

  public void setValue(Expression expression) {
    value = expression;
  }

  public String getName() {
    return name;
  }

  public int getType() {
    return type;
  }

  public void setName(String string) {
    name = string;
  }

  public void setType(int i) {
    type = i;
  }

}

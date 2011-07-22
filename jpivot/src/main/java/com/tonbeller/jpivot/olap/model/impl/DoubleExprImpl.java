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

import com.tonbeller.jpivot.olap.model.DoubleExpr;
import com.tonbeller.jpivot.olap.model.Visitor;

public class DoubleExprImpl implements DoubleExpr {

  private double value;
  public DoubleExprImpl(){
  }

  public DoubleExprImpl(double value){
    this.value=value;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double d) {
    value = d;
  }
  public void accept(Visitor visitor) {
    visitor.visitDoubleExpr(this);
  }

}

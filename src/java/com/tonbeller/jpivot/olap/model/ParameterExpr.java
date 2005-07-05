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

public interface ParameterExpr extends Expression {
  public static final int STRING = 0;
  public static final int NUMBER = 1;
  public static final int MEMBER = 2;
  
  /** internal name that identifies this parameter */
  String getName();
  
  /** label to display to the user */
  String getLabel();
  
  /** type of parameter */
  int getType();
  
  /** value of the parameter */
  Expression getValue();
}

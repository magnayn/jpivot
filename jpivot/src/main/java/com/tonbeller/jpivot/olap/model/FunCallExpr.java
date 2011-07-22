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

/**
 * An MDX expression.
 * <p>
 * 
 * Function names are all lowercase:
 * <ul>
 *   <li>()</li> for Tupel
 *   <li>{}</li> for Set
 *   <li>.ident</li> for property ident, e.g. <code>.children</code> 
 *   <li>ident</li> for function ident, e.g. topcount()</code>
 *   <li>*,/,+,- for infix operators
 * </ul>
 * 
 * @author av
 */
public interface FunCallExpr extends Expression {
  /**
   * returns the function name
   */
  String getName();
  Expression[] getArgs();
}

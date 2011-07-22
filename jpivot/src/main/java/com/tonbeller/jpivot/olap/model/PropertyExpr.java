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
 * expression defining a calculated member's property
 */
public interface PropertyExpr extends Visitable {
  
  /**
   * @return the Value expression
   */
  public Expression getValueExpr();
  
  /**
   * @return the name of this property
   */
  public String getName();
  
  /**
   * @return the possible values
   */
  public String[] getChoices();

} // PropertyExpr

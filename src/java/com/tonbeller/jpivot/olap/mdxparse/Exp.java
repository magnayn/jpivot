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
package com.tonbeller.jpivot.olap.mdxparse;

/**
 * Expression node for an MDX parse tree
 */
public interface Exp {
  
  public String toMdx();
  
  public Object clone();
  
  /**
   * Exp is visitable
   */
  public void accept(ExpVisitor visitor);

} // End Exp

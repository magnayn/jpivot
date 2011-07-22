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


/**
 * serialization for mondrian position tree node - used  by memento
 */
public class PositionNodeBean {
  private ExpBean reference;
  private PositionNodeBean[] children;

  public PositionNodeBean() {
  }
  
  /**
   * @return reference
   */
  public ExpBean getReference() {
    return reference;
  }

  /**
   * @param reference
   */
  public void setReference(ExpBean reference) {
    this.reference = reference;
  }

  /**
   * @return
   */
  public PositionNodeBean[] getChildren() {
    return children;
  }

  /**
   * @param children
   */
  public void setChildren(PositionNodeBean[] children) {
    this.children = children;
  }

} // PositionNodeBean

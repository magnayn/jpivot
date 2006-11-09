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

import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Visitor;

/**
 * Created on 11.10.2002
 * 
 * @author av
 */
public class HierarchyImpl implements Hierarchy {
  Dimension dimension;
  Level[] levels;
  String label;
  boolean hasAll;

  /**
   * Returns the dimension.
   * @return Dimension
   */
  public Dimension getDimension() {
    return dimension;
  }

  /**
   * Returns the levels.
   * @return Level[]
   */
  public Level[] getLevels() {
    return levels;
  }

  /**
   * Sets the dimension.
   * @param dimension The dimension to set
   */
  public void setDimension(Dimension dimension) {
    this.dimension = dimension;
  }

  /**
   * Sets the levels.
   * @param levels The levels to set
   */
  public void setLevels(Level[] levels) {
    this.levels = levels;
  }


  public void accept(Visitor visitor) {
    visitor.visitHierarchy(this);
  }
  
  public Object getRootDecoree() {
    return this;
  }

  /**
   * Returns the label.
   * @return String
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the label.
   * @param label The label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

  public boolean hasAll() {
    return hasAll;
  }

  public void setHasAll(boolean hasAll) {
    this.hasAll = hasAll;
  }
}

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
import com.tonbeller.jpivot.olap.model.Visitor;

/**
 * Created on 11.10.2002
 * 
 * @author av
 */
public class DimensionImpl implements Dimension {
  Hierarchy[] hierarchies;
  boolean time;
  boolean measure;
  String label;
  /**
   * Returns the hierarchies.
   * @return Hierarchy[]
   */
  public Hierarchy[] getHierarchies() {
    return hierarchies;
  }

  /**
   * Returns the measure.
   * @return boolean
   */
  public boolean isMeasure() {
    return measure;
  }

  /**
   * Returns the time.
   * @return boolean
   */
  public boolean isTime() {
    return time;
  }

  /**
   * Sets the hierarchies.
   * @param hierarchies The hierarchies to set
   */
  public void setHierarchies(Hierarchy[] hierarchies) {
    this.hierarchies = hierarchies;
  }

  /**
   * Sets the measure.
   * @param measure The measure to set
   */
  public void setMeasure(boolean measure) {
    this.measure = measure;
  }

  /**
   * Sets the time.
   * @param time The time to set
   */
  public void setTime(boolean time) {
    this.time = time;
  }


  public void accept(Visitor visitor) {
    visitor.visitDimension(this);
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

}

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

import java.util.List;

import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.Visitor;

/**
 * Created on 11.10.2002
 * 
 * @author av
 */
public class MemberImpl extends PropertyHolderImpl implements Member {

  Level level;
  int rootDistance;
  String label;

  public MemberImpl() {
  }

  public MemberImpl(Property[] properties) {
    super(properties);
  }

  public MemberImpl(List propertyList) {
    super(propertyList);
  }

  /**
   * Returns the rootDistance.
   * @return int
   */
  public int getRootDistance() {
    return rootDistance;
  }

  /**
   * Sets the rootDistance.
   * @param rootDistance The rootDistance to set
   */
  public void setRootDistance(int rootDistance) {
    this.rootDistance = rootDistance;
  }

  /**
   * Returns the level.
   * @return Level
   */
  public Level getLevel() {
    return level;
  }

  /**
   * Sets the level.
   * @param level The level to set
   */
  public void setLevel(Level level) {
    this.level = level;
  }

  public Object getRootDecoree() {
    return this;
  }

  public void accept(Visitor visitor) {
    visitor.visitMember(this);
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

  public boolean isAll() {
    return false;
  }

  public boolean isCalculated() {
     return false;
  }

}

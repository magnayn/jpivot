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

import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.Visitor;

/**
 * Created on 11.10.2002
 * 
 * @author av
 */
public class PropertyImpl extends PropertyHolderImpl implements Property {
  String name;
  String value;
  Alignment alignment = Alignment.LEFT;
  
  public Alignment getAlignment() {
    return alignment;
  }
  public void setAlignment(Alignment alignment) {
    this.alignment = alignment;
  }
  public PropertyImpl() {
  }

  public PropertyImpl(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public PropertyImpl(String name, String value, Alignment alignment) {
    this.name = name;
    this.value = value;
    this.alignment = alignment;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void accept(Visitor visitor) {
    visitor.visitProperty(this);
  }

  public Object getRootDecoree() {
    return this;
  }

  public String getLabel() {
    return value;
  }
  
  
}

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

import com.tonbeller.jpivot.olap.model.Cell;
import com.tonbeller.jpivot.olap.model.NumberFormat;
import com.tonbeller.jpivot.olap.model.Visitor;

/**
 * Created on 11.10.2002
 * 
 * @author av
 */
public class CellImpl extends PropertyHolderImpl implements Cell {
  String formattedValue;
  Object value;

  /**
   * Returns the formattedValue.
   * @return String
   */
  public String getFormattedValue() {
    return formattedValue;
  }

  /**
   * Returns the value.
   * @return Object
   */
  public Object getValue() {
    return value;
  }

  /**
   * Sets the formattedValue.
   * @param formattedValue The formattedValue to set
   */
  public void setFormattedValue(String formattedValue) {
    this.formattedValue = formattedValue;
  }

  /**
   * Sets the value.
   * @param value The value to set
   */
  public void setValue(Object value) {
    this.value = value;
  }

  public boolean isNull() {
    return value == null;
  }

  public void accept(Visitor visitor) {
    visitor.visitCell(this);
  }
  
  public Object getRootDecoree() {
    return this;
  }

  public NumberFormat getFormat() {
    if (value instanceof Number)
      return new NumberFormatImpl();
    return null;
  }

}

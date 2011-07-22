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

import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.PropertyHolder;

/**
 * Created on 11.10.2002
 * 
 * @author av
 */
public class PropertyHolderImpl implements PropertyHolder {
  private static final Property[] EMPTY = new Property[0];
  Property[] properties;
  
  public PropertyHolderImpl() {
    this.properties = EMPTY;
  }
  
  public PropertyHolderImpl(Property[] properties) {
    this.properties = properties;
  }

  public PropertyHolderImpl(List propertyList) {
    this.properties = (Property[]) propertyList.toArray(new Property[propertyList.size()]);
  }
  
  /**
   * Returns the properties.
   * @return Property[]
   */
  public Property[] getProperties() {
    return properties;
  }


  /**
   * Sets the properties.
   * @param properties The properties to set
   */
  public void setProperties(Property[] properties) {
    this.properties = properties;
  }


  /**
   * @see com.tonbeller.jpivot.olap.model.Member#getProperty(String)
   */
  public Property getProperty(String name) {
    for (int i = 0; i < properties.length; i++)
      if (name.equals(properties[i].getName()))
        return properties[i];
    return null;
  }

  /**
   * very slow!
   */
  public void addProperty(PropertyImpl property) {
    int N = properties.length;
    Property[] old = properties;
    properties = new Property[N + 1];
    System.arraycopy(old, 0, properties, 0, N);
    properties[N] = property;
  }

}

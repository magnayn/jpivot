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
 * Property of a Member
 * @author av
 */
public interface Property extends Visitable, Decorator, Displayable, PropertyHolder, Alignable {
  /**
   * @return the name of the Property
   */
  String getName();
  
  /**
   * the value of the property
   */
  String getValue();
  
  /**
   * if true, this property may be a nested property whose path is 
   * specified by '.' in its name.
   */
  boolean isNormalizable();
}

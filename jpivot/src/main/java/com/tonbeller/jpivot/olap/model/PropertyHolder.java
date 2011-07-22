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
 * container for name/value pairs. 
 * 
 * @author av
 */

public interface PropertyHolder {

  /**
   * get all properties of this member
   */
  Property[] getProperties();
  
  /**
   * get a specific property of this member
   */
  Property getProperty(String name);
}

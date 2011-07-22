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
 * An Olap Item is a DWH meta object,
 * eg. dimension, level, member
 */
public interface OlapItem {
  
  public static final int TYPE_CATALOG = 1;
  public static final int TYPE_CUBE = 2;
  public static final int TYPE_DIMENSION = 3;
  public static final int TYPE_HIERARCHY = 4;
  public static final int TYPE_LEVEL = 5;
  public static final int TYPE_MEMBER = 6;
  public static final int TYPE_PROPERTY = 7;  

  /**
   * @return type of item
   */
  public int getType();
  
	/**
	 * Label is the string to be externally displayed
	 * @return label
	 */
	public String getLabel();

	/**
	  * @return the unique name
	  */
	public String getUniqueName();

	/**
	  * @return caption (can be null)
	  */
	public String getCaption();

	/**
	 * @return name
	 */
	public String getName();
	
	/**
	  * @param propName  name of the property to be retrieved
	  * @return
	  */
	public String getProperty(String propName);
	
	/**
	 * any OlapItem contains a map of properties,
	 * key and value of type String
	 * @return properties  property map
	 */
	public java.util.Map getProperties();

} // OlapItem

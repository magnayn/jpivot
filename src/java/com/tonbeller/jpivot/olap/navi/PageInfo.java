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
package com.tonbeller.jpivot.olap.navi;

/**
 * Describes a page
 * 
 * @author av
 */
public interface PageInfo {

  /** 
   * 0-based index of page page
   */
  int getPageIndex();

  /** 
   * 1-based number of page page
   */
  int getPageNo();
  
  /**
   * number of available pages
   */
  int getPageCount();
  
  /**
   * short description of the first Item
   */
  String getFirstShort();
  
  /**
   * long description of the first Item
   */
  String getFirstLong();
  
  /**
   * short description of the last Item
   */
  String getLastShort();
  
  /**
   * long description of the last Item
   */
  String getLastLong();
}

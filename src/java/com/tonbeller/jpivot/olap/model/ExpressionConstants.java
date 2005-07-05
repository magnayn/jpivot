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
 * @author av
 */
public interface ExpressionConstants {

  /** sort hierarchically ascending */
  static final int ASC = 1;
  /** sort hierarchically descending */
  static final int DESC = 2;
  /** sort ascending breaking hierarchy */
  static final int BASC = 3;
  /** sort descending breaking hierarchy */
  static final int BDESC = 4;
  /** perform topcount */
  static final int TOPCOUNT = 5;
  /** perform bottomcount */
  static final int BOTTOMCOUNT = 6;

}

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

import com.tonbeller.jpivot.util.JPivotException;
/**
 * @author av
 */
public class OlapException extends JPivotException {

  /**
   * Constructor for OlapException.
   */
  public OlapException() {
    super();
  }

  /**
   * Constructor for OlapException.
   * @param arg0
   */
  public OlapException(String arg0) {
    super(arg0);
  }

  /**
   * Constructor for OlapException.
   * @param arg0
   * @param arg1
   */
  public OlapException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  /**
   * Constructor for OlapException.
   * @param arg0
   */
  public OlapException(Throwable arg0) {
    super(arg0);
  }

}

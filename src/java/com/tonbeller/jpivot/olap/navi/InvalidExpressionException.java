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

import com.tonbeller.jpivot.util.JPivotException;
/**
 * @author av
 */
public class InvalidExpressionException extends JPivotException {

  public InvalidExpressionException() {
    super();
  }

  public InvalidExpressionException(String message) {
    super(message);
  }

  public InvalidExpressionException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidExpressionException(Throwable cause) {
    super(cause);
  }

}

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
 * provides access to the decorated object
 * 
 * @author av
 */
public interface Decorator {

  /**
   * returns the object that has been created by the olap server. If this is
   * part of a decorator chain, unwinds the chain and returns the root of all decorators.
   */
  Object getRootDecoree();
}

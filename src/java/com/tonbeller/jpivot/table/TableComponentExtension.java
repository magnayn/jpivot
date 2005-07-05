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
package com.tonbeller.jpivot.table;


/**
 * Extends a TableComponent. An extension is for example 
 * "drill down navigation" that may or may not be available.
 * 
 * @author av
 */
public interface TableComponentExtension extends PartBuilder {
  /**
   * unique name of this extension. Used for JSP programming
   */
  String getId();
  
  /**
   * enable or disable this extension. A disabled extension should not be rendered
   * and should not respond to user actions.
   */
  void setEnabled(boolean enabled);
  boolean isEnabled();
  
}

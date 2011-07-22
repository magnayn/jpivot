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
package com.tonbeller.jpivot.olap.query;

/**
 * MDX Elements like Members, Levels ,Hierarchies 
 *   have a common set op properties like "Unique Name"
 */
public interface MDXElement {
  
  /**
   * return the unique name of an MDX Olap element
   */
  String getUniqueName();
  

} // MDXElement

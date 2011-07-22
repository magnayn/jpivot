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
package com.tonbeller.jpivot.table.span;

import java.beans.PropertyChangeListener;
import java.util.List;

import com.tonbeller.jpivot.ui.Available;
import com.tonbeller.wcf.bookmarks.Bookmarkable;

/**
 * defines which properties shall be visible and
 * which shall be hidden
 * 
 * @author av
 */
public interface PropertyConfig extends Bookmarkable, Available {

  /**
   * shall properties be shown at all?
   */
  boolean isShowProperties();
  void setShowProperties(boolean b);
  
  /**
   * does the underlying OLAP model support properties?
   */
  boolean isAvailable();
  
  /**
   * sets the ordered list of visible MemberPropertyMeta instances.
   * @param metas list containing MemberPropertyMeta. Properties which are not 
   * present in the list will not be shown. If null, all properties are shown.
   */
  void setVisiblePropertyMetas(List metas);
  List getVisiblePropertyMetas();
  
  /**
   * notifies the listener that this config has changed (and the model has possibly to be redrawn)
   */
  void addPropertyChangeListener(PropertyChangeListener l);
  void removePropertyChangeListener(PropertyChangeListener l);
  

}

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

import java.util.Comparator;

/**
 * Something that can be displayed in a GUI.
 * 
 * @author av
 */
public interface Displayable extends Visitable {
  public static final Comparator LABEL_COMPARATOR = new Comparator(){
    public int compare(Object o1, Object o2) {
      Displayable d1 = (Displayable) o1;
      Displayable d2 = (Displayable) o2;
      return d1.getLabel().compareTo(d2.getLabel());
    }
  };

  /**
   * name of this item
   */
  String getLabel();

}

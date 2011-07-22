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

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 29.10.2002
 * 
 * @author av
 */
public class SpanConfigSupport implements SpanConfig {
  
  int defaultDirection = SpanConfig.NO_SPAN;
  List clsList = new ArrayList();
  List dirList = new ArrayList();
  
  /**
   * sets the span direction for a specific class. The direction will be
   * applied to all span, whose getObject() returns an instance of clazz.
   * <p>
   * The order is significant, its the order that chooseDirection will use
   * to test. So you *must* call setDirection with a more specific class 
   * before the less specific, e.g. after setDirection(Object.class) no
   * other classes will be recognized, because every class is assignable to
   * object.
   */
  public void setDirection(Class clazz, int direction) {
     clsList.add(clazz);
     dirList.add(new Integer(direction));
  }

  /**
   * returns the direction for span. The class of the spans object will
   * be tested against
   */
  public int chooseSpanDirection(Span span) {
    Object obj = span.getObject();
    if (obj == null)
      return defaultDirection;
    Class clazz = obj.getClass();
    int N = clsList.size();
    for (int i = 0; i < N; i++) {
      Class c = (Class)clsList.get(i);
      if (c.isAssignableFrom(clazz))
        return ((Integer)dirList.get(i)).intValue();
    }
    // use default    
    return defaultDirection;
  }

  /**
   * Returns the defaultDirection.
   * @return int
   */
  public int getDefaultDirection() {
    return defaultDirection;
  }

  /**
   * Sets the defaultDirection.
   * @param defaultDirection The defaultDirection to set
   */
  public void setDefaultDirection(int defaultDirection) {
    this.defaultDirection = defaultDirection;
  }

  /**
   * returns true, if the objects returned by getObject() are equal.
   */
  public boolean equals(Span span1, Span span2) {
    return span1.getObject().equals(span2.getObject());
  }

}

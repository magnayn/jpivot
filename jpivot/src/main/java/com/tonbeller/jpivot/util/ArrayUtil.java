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
package com.tonbeller.jpivot.util;

import java.lang.reflect.Array;

public class ArrayUtil {
  
  /**
   * copy an array to an arry of its natural type
   * @param array input
   * @return array of natural type
   */
  public static Object naturalCast(Object[] array) {
    Class clazz = array[0].getClass();
    Object[] newArray = (Object[]) Array.newInstance(clazz, array.length);
    for (int i = 0; i < newArray.length; i++) {
      newArray[i] = array[i];
    }
    return newArray;
  }

} // ArrayUtil

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
package com.tonbeller.jpivot.test.olap;

import com.tonbeller.jpivot.olap.model.impl.AxisImpl;

/**
 * Created on 02.12.2002
 * 
 * @author av
 */
public class TestAxis extends AxisImpl {
  public int indexOf(TestPosition pos) {
    return getPositions().indexOf(pos);
  }
  
}

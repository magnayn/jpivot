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

import com.tonbeller.jpivot.olap.navi.ResetQuery;

/**
 * Created on 06.12.2002
 * 
 * @author av
 */
public class TestResetQuery extends TestExtensionSupport implements ResetQuery {

  /**
   * @see com.tonbeller.jpivot.olap.navi.ResetQuery#reset()
   */
  public void reset() {
    model().reset();
    fireModelChanged();
  }

}

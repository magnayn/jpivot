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

import com.tonbeller.jpivot.olap.navi.SwapAxes;

/**
 * Created on 21.10.2002
 * 
 * @author av
 */
public class TestSwapAxes extends TestExtensionSupport implements SwapAxes {
  boolean swapped;
  
  TestOlapModel olapModel() {
    return (TestOlapModel)getModel();
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SwapAxes#canSwapAxes()
   */
  public boolean canSwapAxes() {
    return true;
  }

  public void setSwapAxes(boolean newSwapped) {
    if (this.swapped != newSwapped) {
      this.swapped = newSwapped;
      TestAxis a = olapModel().getAxis(0);
      TestAxis b = olapModel().getAxis(1);
      olapModel().setAxis(0, b);
      olapModel().setAxis(1, a);
      fireModelChanged();
    }
  }

  public boolean isSwapAxes() {
    return swapped;
  }


}

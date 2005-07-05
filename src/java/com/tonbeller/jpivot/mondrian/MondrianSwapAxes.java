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
package com.tonbeller.jpivot.mondrian;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.navi.SwapAxes;

/**
 * @author hh
 *
 * Implementation of the Swap Axes Extension for Mondrian Data Source.
 */
public class MondrianSwapAxes extends ExtensionSupport implements SwapAxes {

  /**
   * Constructor sets ID
   */
  public MondrianSwapAxes() {
    super.setId(SwapAxes.ID);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SwapAxes#canSwapAxes()
   * @return true, if the Mondrian Query exists and has two axes
   */
  public boolean canSwapAxes() {
    MondrianModel model = (MondrianModel) getModel();
    mondrian.olap.Query monQuery = ((MondrianQueryAdapter)model.getQueryAdapter()).getMonQuery();
    if ( monQuery != null )
      return ( monQuery.getAxes().length == 2 );
    else
      return false;
  }


  public void setSwapAxes(boolean swap) {
    MondrianModel model = (MondrianModel) getModel();
    ((MondrianQueryAdapter)model.getQueryAdapter()).setSwapAxes(swap);
  }

  public boolean isSwapAxes() {
    MondrianModel model = (MondrianModel) getModel();
    return ((MondrianQueryAdapter)model.getQueryAdapter()).isSwapAxes();
  }

}

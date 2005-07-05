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
package com.tonbeller.jpivot.xmla;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.mdxparse.ParsedQuery;
import com.tonbeller.jpivot.olap.navi.SwapAxes;

/**
 * swap axes extension
 */
public class XMLA_SwapAxes extends ExtensionSupport implements SwapAxes {

  /**
   * Constructor sets ID
   */
  public XMLA_SwapAxes() {
    super.setId(SwapAxes.ID);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SwapAxes#canSwapAxes()
   * @return true, if the Mondrian Query exists and has two (or more?) axes
   */
  public boolean canSwapAxes() {
    XMLA_Model model = (XMLA_Model) getModel();
    ParsedQuery pQuery = model.getPQuery();
    if (pQuery != null)
      return (pQuery.getAxes().length >= 2);
    else
      return false;
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SwapAxes#setSwapAxes
   */
  public void setSwapAxes(boolean swap) {
    XMLA_Model model = (XMLA_Model) getModel();
    // swap the axes of the current query object
    XMLA_QueryAdapter qad = (XMLA_QueryAdapter) model.getQueryAdapter();
    qad.setSwapAxes(swap);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SwapAxes#isSwapAxes
   */
  public boolean isSwapAxes() {
    XMLA_Model model = (XMLA_Model) getModel();
    // swap the axes of the current query object
    XMLA_QueryAdapter qad = (XMLA_QueryAdapter) model.getQueryAdapter();
    return qad.isSwapAxes();
  }

} // End XMLA_SwapAxes

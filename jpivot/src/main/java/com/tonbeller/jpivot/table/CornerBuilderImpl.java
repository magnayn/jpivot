/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 */
package com.tonbeller.jpivot.table;

import org.w3c.dom.Element;

/**
 * renders the upper left corner
 * 
 * @author av
 */
public class CornerBuilderImpl extends PartBuilderSupport implements CornerBuilder {

  /**
   * @see com.tonbeller.jpivot.ui.table.CornerBuilder#render(TableRenderer)
   */
  public Element build(int colSpan, int rowSpan) {
    Element corner = table.elem("corner");
    corner.setAttribute("rowspan", Integer.toString(rowSpan));
    corner.setAttribute("colspan", Integer.toString(colSpan));
    return corner;
  }

}

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
package com.tonbeller.jpivot.table;

import org.w3c.dom.Element;

/**
 * Created on 18.10.2002
 * 
 * @author av
 */
public abstract class CornerBuilderDecorator extends PartBuilderDecorator implements CornerBuilder {
  
  /**
   * Constructor for CornderElementRendererDecorator.
   */
  public CornerBuilderDecorator(CornerBuilder delegate) {
    super(delegate);
  }

  /**
   * @see com.tonbeller.jpivot.ui.table.CornerBuilder#render(TableRenderer)
   */
  public Element build(int colSpan, int rowSpan) {
    return ((CornerBuilder)delegate).build(colSpan, rowSpan);
  }

}

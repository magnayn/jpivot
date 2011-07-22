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
package com.tonbeller.jpivot.excel;

import org.w3c.dom.Element;

import com.tonbeller.jpivot.olap.model.Cell;
import com.tonbeller.jpivot.table.CellBuilder;
import com.tonbeller.jpivot.table.CellBuilderDecorator;
import com.tonbeller.wcf.component.RendererParameters;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * adds excel attributes to cell elements
 */
public class ExcelCellBuilderDecorator extends CellBuilderDecorator {

  protected boolean excelMode;

  public void startBuild(RequestContext context) {
    super.startBuild(context);
    this.excelMode= RendererParameters.isExcelMode(context);
  }
  
  public ExcelCellBuilderDecorator(CellBuilder delegate) {
    super(delegate);
  }
  /*
   * (non-Javadoc)
   * 
   * @see com.tonbeller.jpivot.table.CellBuilderDecorator#build(com.tonbeller.jpivot.olap.model.Cell,
   *      boolean)
   */
  public Element build(Cell cell, boolean even) {
    Element cellElem = super.build(cell, even);
    // AR_MOD
    if (excelMode && !cell.isNull() && (cell.getValue() instanceof Number)) {
      Object value = cell.getValue();
      com.tonbeller.jpivot.olap.model.NumberFormat nf = cell.getFormat();
      String rawValue = value.toString();
      cellElem.setAttribute("rawvalue", rawValue);
      Number num = (Number) cell.getValue();
      String numFormat = "\\#\\#0";

      numFormat = (nf.isGrouping() ? "\\#\\," + numFormat : numFormat);
      if (nf.getFractionDigits() > 0) {
        numFormat = numFormat + ".";
        for (int x = 0; x < nf.getFractionDigits(); x++)
          numFormat = numFormat + "#";
      }
      String msoformat = nf.isPercent() ? "Percent" : numFormat;
      cellElem.setAttribute("mso-number-format", msoformat);
    }
    // AR_MOD END
    return cellElem;
  }
}
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

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.table.span.HierarchyHeaderFactory;
import com.tonbeller.jpivot.table.span.IgnorePropertiesHierarchyHeaderFactory;
import com.tonbeller.jpivot.table.span.LevelHeaderFactory;
import com.tonbeller.jpivot.table.span.Span;
import com.tonbeller.jpivot.table.span.SpanCalc;

/**
 * common functionality for row axis builders
 * @author av
 */
public class RowAxisBuilderImpl extends AxisBuilderSupport implements RowAxisBuilder, RowAxisConfig {
  int positionHeader;
  SpanCalc headerSpans;
  private static final Logger logger = Logger.getLogger(RowAxisBuilderImpl.class);

  public RowAxisBuilderImpl() {
    super(new SpanBuilderImpl("row-heading", "heading-heading"));
    setMemberIndent(true);
    setShowParentMembers(false);
    setHierarchyHeader(NO_HEADER);
    setMemberSpan(HIERARCHY_THEN_POSITION_SPAN);
    setHeaderSpan(HIERARCHY_THEN_POSITION_SPAN);
    setPositionHeader(HIERARCHY_HEADER);
  }

  public void buildRow(Element parent, int rowIndex) {
    boolean even = (rowIndex % 2 == 0);
    for (int i = 0; i < spanCalc.getHierarchyCount(); i++) {
      Span span = spanCalc.getSpan(rowIndex, i);
      if (span.isSignificant()) {
        int rowspan = span.getPositionSpan();
        int colspan = span.getHierarchySpan();
        buildHeading(parent, span, rowspan, colspan, even);
      }
    }
  }

  /**
   * @see com.tonbeller.jpivot.ui.table.AxisBuilder#getColumnCount()
   */
  public int getColumnCount() {
    return spanCalc.getHierarchyCount();
  }

  /**
   * @see com.tonbeller.jpivot.ui.table.AxisBuilder#getRowCount()
   */
  public int getRowCount() {
    return spanCalc.getPositionCount();
  }

  /**
   * Returns the positionHeader.
   * @return int
   */
  public int getPositionHeader() {
    return positionHeader;
  }

  /**
   * Sets the positionHeader.
   * @param positionHeader The positionHeader to set
   */
  public void setPositionHeader(int positionHeader) {
    this.positionHeader = positionHeader;
    setDirty(true);
  }

  public void buildHeaderRow(Element parent, int rowIndex) {
    boolean even = (rowIndex % 2 == 0);
    for (int i = 0; i < headerSpans.getHierarchyCount(); i++) {
      Span span = headerSpans.getSpan(rowIndex, i);
      if (logger.isInfoEnabled())
        logger.info("building header row: " + span);
      if (span.isSignificant()) {
        int rowspan = span.getPositionSpan();
        int colspan = span.getHierarchySpan();
        buildHeading(parent, span, rowspan, colspan, even);
      }
    }
  }

  public int getHeaderRowCount() {
    if (headerSpans == null)
      return 0;
    return headerSpans.getPositionCount();
  }

  public SpanCalc getHeaderSpanCalc() {
    return headerSpans;
  }

  /**
   * called from startBuild
   */
  public void initialize(Axis axis) {
    super.initialize(axis);

    switch (positionHeader) {
    case HIERARCHY_LEVEL_HEADER:
      logger.info("HIERARCHY_LEVEL_HEADER");
      SpanCalc sc1 = spanCalc.createPositionHeader(new IgnorePropertiesHierarchyHeaderFactory());
      SpanCalc sc2 = spanCalc.createPositionHeader(new LevelHeaderFactory());
      headerSpans = SpanCalc.appendBelow(sc1, sc2);
      break;
    case LEVEL_HEADER:
      logger.info("LEVEL_HEADER");
      headerSpans = spanCalc.createPositionHeader(new LevelHeaderFactory());
      break;
    case HIERARCHY_HEADER:
      logger.info("HIERARCHY_HEADER");
      headerSpans = spanCalc.createPositionHeader(new HierarchyHeaderFactory());
      break;
    default:
      headerSpans = null;
      break;
    }
    if (headerSpans != null)
      headerSpans.setConfig(spanCalc.getConfig());
  }

  public void stopBuild() {
    super.stopBuild();
    // avoid memory leak
    headerSpans = null;
  }

  protected Axis getAxis() {
    return table.getRowAxis();
  }

}

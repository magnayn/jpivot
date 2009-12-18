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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.navi.MemberTree;
import com.tonbeller.jpivot.table.span.HierarchyHeaderFactory;
import com.tonbeller.jpivot.table.span.LevelHeaderFactory;
import com.tonbeller.jpivot.table.span.PropertyConfig;
import com.tonbeller.jpivot.table.span.PropertySpanBuilder;
import com.tonbeller.jpivot.table.span.Span;
import com.tonbeller.jpivot.table.span.SpanCalc;
import com.tonbeller.jpivot.table.span.SpanConfigSupport;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * Created on 24.10.2002
 *
 * @author av
 */
public abstract class AxisBuilderSupport extends PartBuilderSupport implements AxisBuilder,
    AxisConfig, PropertyChangeListener {

  private static final Logger logger = Logger.getLogger(AxisBuilderSupport.class);
  
  protected SpanCalc spanCalc;
  protected SpanBuilder spanBuilder;
  protected PropertySpanBuilder propertySpanBuilder;
  protected AxisHeaderBuilder axisHeaderBuilder;

  // from AxisConfig
  protected boolean showParentMembers = false;
  protected boolean memberIndent = false;
  protected int hierarchyHeader = NO_HEADER;
  protected int memberSpan = HIERARCHY_THEN_POSITION_SPAN;
  protected int headerSpan = HIERARCHY_THEN_POSITION_SPAN;

  protected AxisBuilderSupport(SpanBuilder spanBuilder) {
    this.spanBuilder = spanBuilder;
  }

  public void initialize(RequestContext context, TableComponent table) throws Exception {
    logger.info("initialize");
    super.initialize(context, table);
    spanBuilder.initialize(context, table);
    propertySpanBuilder = new PropertySpanBuilder(table.getOlapModel());
    propertySpanBuilder.addPropertyChangeListener(this);
    propertySpanBuilder.initialize(context);
    axisHeaderBuilder = new AxisHeaderBuilderSupport(spanBuilder);
  }

  public void destroy(HttpSession session) throws Exception {
    logger.info("destroy");
    propertySpanBuilder.destroy(session);
    propertySpanBuilder = null;
    super.destroy(session);
  }

  /**
   * called from startBuild()
   */
  protected void initialize(Axis axis) {
    logger.info("initialize(Axis)");
    if (showParentMembers) {
      MemberTree tree = (MemberTree) table.getOlapModel().getExtension(MemberTree.ID);
      if (tree != null) {
        logger.info("adding LevelAxisDecorator");
        axis = new LevelAxisDecorator(axis, tree);
      }
    }
    logger.info("creating SpanCalc");
    spanCalc = new SpanCalc(axis);

    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDirection(Member.class, memberSpan);
    scs.setDirection(Displayable.class, headerSpan);
    spanCalc.setConfig(scs);

    propertySpanBuilder.addPropertySpans(spanCalc);

    switch (hierarchyHeader) {
    case HIERARCHY_HEADER:
      spanCalc.addHierarchyHeader(new HierarchyHeaderFactory(), true);
      break;
    case LEVEL_HEADER:
      spanCalc.addHierarchyHeader(new LevelHeaderFactory(), true);
      break;
    }
  }

  /**
   * Returns the showParentMembers.
   * @return boolean
   */
  public boolean isShowParentMembers() {
    return showParentMembers;
  }

  /**
   * Sets the showParentMembers.
   * @param showParentMembers The showParentMembers to set
   */
  public void setShowParentMembers(boolean showParentMembers) {
    this.showParentMembers = showParentMembers;
    setDirty(true);
  }

  /**
   * Returns the spanBuilder.
   * @return SpanBuilder
   */
  public SpanBuilder getSpanBuilder() {
    return spanBuilder;
  }

  /**
   * Sets the spanBuilder.
   * @param spanBuilder The spanBuilder to set
   */
  public void setSpanBuilder(SpanBuilder spanBuilder) {
    this.spanBuilder = spanBuilder;
    setDirty(true);
  }

  /**
   * Returns the memberIndent.
   * @return boolean
   */
  public boolean isMemberIndent() {
    return memberIndent;
  }

  /**
   * Sets the memberIndent.
   * @param memberIndent The memberIndent to set
   */
  public void setMemberIndent(boolean memberIndent) {
    this.memberIndent = memberIndent;
    setDirty(true);
  }

  /**
   * @see com.tonbeller.jpivot.ui.table.AxisBuilder#getAxisConfig()
   */
  public AxisConfig getAxisConfig() {
    return this;
  }

  /**
   * Returns the headerSpan.
   * @return int
   */
  public int getHeaderSpan() {
    return headerSpan;
  }

  /**
   * Returns the hierarchyHeader.
   * @return int
   */
  public int getHierarchyHeader() {
    return hierarchyHeader;
  }

  /**
   * Returns the memberSpan.
   * @return int
   */
  public int getMemberSpan() {
    return memberSpan;
  }

  /**
   * Sets the headerSpan.
   * @param headerSpan The headerSpan to set
   */
  public void setHeaderSpan(int headerSpan) {
    this.headerSpan = headerSpan;
    setDirty(true);
  }

  /**
   * Sets the hierarchyHeader.
   * @param hierarchyHeader The hierarchyHeader to set
   */
  public void setHierarchyHeader(int hierarchyHeader) {
    this.hierarchyHeader = hierarchyHeader;
    setDirty(true);
  }

  /**
   * Sets the memberSpan.
   * @param memberSpan The memberSpan to set
   */
  public void setMemberSpan(int memberSpan) {
    this.memberSpan = memberSpan;
    setDirty(true);
  }

  /** 
   * returns the row/column axis or null if result is 
   * 1- or 0-dimensional 
   */
  protected abstract Axis getAxis();

  public void startBuild(RequestContext context) {
    Axis axis = getAxis();
    if (axis != null)
      initialize(axis);
    super.startBuild(context);
    spanBuilder.startBuild(context);
  }

  public void stopBuild() {
    spanBuilder.stopBuild();
    super.stopBuild();
    // avoid memory leak
    spanCalc = null;
  }

  /**
   * only valid between startBuild() and stopBuild()
   */
  public SpanCalc getSpanCalc() {
    return spanCalc;
  }

  /**
   * builds a single cell of the axis.
   * @param row
   */
  protected void buildHeading(Element row, Span span, int rowspan, int colspan, boolean even) {
    axisHeaderBuilder.build(row, span, rowspan, colspan, even, isMemberIndent());
  }

  public PropertyConfig getPropertyConfig() {
    return propertySpanBuilder;
  }

  public void propertyChange(PropertyChangeEvent evt) {
    setDirty(true);
  }

  /**
   * stores settings for Member Properties. The axis style is stored in the AxisStyleUI table extension 
   */
  public Object retrieveBookmarkState(int levelOfDetail) {
    return getPropertyConfig().retrieveBookmarkState(levelOfDetail);
  }

  /**
   * restores settings for Member Properties. The axis style is stored in the AxisStyleUI table extension 
   */
  public void setBookmarkState(Object state) {
    getPropertyConfig().setBookmarkState(state);
  }

  public AxisHeaderBuilder getAxisHeaderBuilder() {
    return axisHeaderBuilder;
  }

  public void setAxisHeaderBuilder(AxisHeaderBuilder axisHeaderBuilder) {
    this.axisHeaderBuilder = axisHeaderBuilder;
  }

}

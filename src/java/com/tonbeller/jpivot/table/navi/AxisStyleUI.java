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
package com.tonbeller.jpivot.table.navi;

import com.tonbeller.jpivot.table.AxisConfig;
import com.tonbeller.jpivot.table.ColumnAxisConfig;
import com.tonbeller.jpivot.table.RowAxisConfig;
import com.tonbeller.jpivot.table.TableComponent;
import com.tonbeller.jpivot.table.TableComponentExtensionSupport;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * Created on 06.12.2002
 * 
 * @author av
 */
public class AxisStyleUI extends TableComponentExtensionSupport {

  public static final String ID = "axisStyle";

  // defaults
  boolean levelStyle = false;
  boolean hideSpans = false;

  public String getId() {
    return ID;
  }

  /**
   * Returns the hideSpans.
   * @return boolean
   */
  public boolean isHideSpans() {
    return hideSpans;
  }

  /**
   * Returns the levelStyle.
   * @return boolean
   */
  public boolean isLevelStyle() {
    return levelStyle;
  }

  /**
   * Sets the hideSpans.
   * @param hideSpans The hideSpans to set
   */
  public void setHideSpans(boolean hideSpans) {
    if (table == null)
      return;

    this.hideSpans = hideSpans;
    RowAxisConfig rac = (RowAxisConfig) table.getRowAxisBuilder().getAxisConfig();
    ColumnAxisConfig cac = (ColumnAxisConfig) table.getColumnAxisBuilder().getAxisConfig();

    if (hideSpans) {
      rac.setMemberSpan(AxisConfig.HIERARCHY_SPAN);
      cac.setMemberSpan(AxisConfig.HIERARCHY_SPAN);
    } else {
      rac.setMemberSpan(AxisConfig.HIERARCHY_THEN_POSITION_SPAN);
      cac.setMemberSpan(AxisConfig.HIERARCHY_THEN_POSITION_SPAN);
    }
  }

  /**
   * Sets the levelStyle.
   * @param levelStyle The levelStyle to set
   */
  public void setLevelStyle(boolean levelStyle) {
    if (table == null)
      return;

    this.levelStyle = levelStyle;
    // indent on columnaxis does not look really good
    RowAxisConfig rac = (RowAxisConfig) table.getRowAxisBuilder().getAxisConfig();
    ColumnAxisConfig cac = (ColumnAxisConfig) table.getColumnAxisBuilder().getAxisConfig();
    if (levelStyle) {
      rac.setShowParentMembers(true);
      cac.setShowParentMembers(true);
      rac.setMemberIndent(false);
      rac.setPositionHeader(AxisConfig.HIERARCHY_LEVEL_HEADER);
      //cac.setHierarchyHeader(AxisConfig.LEVEL_HEADER);
    } else {
      rac.setShowParentMembers(false);
      cac.setShowParentMembers(false);
      rac.setMemberIndent(true);
      rac.setPositionHeader(AxisConfig.HIERARCHY_HEADER);
      //cac.setHierarchyHeader(AxisConfig.HIERARCHY_HEADER);
    }
  }

  public boolean isAvailable() {
    return true;
  }

  /**
   * initializes axis style
   */
  public void initialize(RequestContext context, TableComponent table) throws Exception {
    super.initialize(context, table);
    setLevelStyle(levelStyle);
    setHideSpans(hideSpans);
  }

  public static class BookmarkState {
    boolean levelStyle = false;
    boolean hideSpans = false;
    /**
     * @return
     */
    public boolean isHideSpans() {
      return hideSpans;
    }

    /**
     * @return
     */
    public boolean isLevelStyle() {
      return levelStyle;
    }

    /**
     * @param b
     */
    public void setHideSpans(boolean b) {
      hideSpans = b;
    }

    /**
     * @param b
     */
    public void setLevelStyle(boolean b) {
      levelStyle = b;
    }

  }

  /**
   * returns the bookmark state
   */
  public Object retrieveBookmarkState(int levelOfDetail) {
    BookmarkState x = new BookmarkState();
    x.setLevelStyle(isLevelStyle());
    x.setHideSpans(isHideSpans());
    return x;
  }

  /**
   * restores boomarkstat
   */
  public void setBookmarkState(Object state) {
    if (!(state instanceof BookmarkState))
      return;
    BookmarkState x = (BookmarkState) state;
    setLevelStyle(x.isLevelStyle());
    setHideSpans(x.isHideSpans());
  }
}

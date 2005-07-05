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

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.navi.DrillExpandMember;
import com.tonbeller.jpivot.table.span.Span;

/**
 * Created on 29.11.2002
 * 
 * @author av
 */
public class DrillExpandMemberUI extends DrillExpandUI {

  public static final String ID = "drillMember";
  public String getId() {
    return ID;
  }

  DrillExpandMember extension;

  protected boolean initializeExtension() {
    extension = (DrillExpandMember) table.getOlapModel().getExtension(DrillExpandMember.ID);
    return extension != null;
  }

  /**
   * @see com.tonbeller.jpivot.ui.table.navi.DrillExpandUI#canExpand(Span)
   */
  protected boolean canExpand(Span span) {
    if (positionContainsMember(span))
      return extension.canExpand((Member) span.getMember().getRootDecoree());
    return false;
  }

  /**
   * @see com.tonbeller.jpivot.ui.table.navi.DrillExpandUI#expand(Span)
   */
  protected void expand(Span span) {
    extension.expand((Member) span.getMember().getRootDecoree());
  }

  /**
   * @see com.tonbeller.jpivot.ui.table.navi.DrillExpandUI#canCollapse(Span)
   */
  protected boolean canCollapse(Span span) {
    if (positionContainsMember(span))
      return extension.canCollapse((Member) span.getMember().getRootDecoree());
    return false;
  }

  /**
   * @see com.tonbeller.jpivot.ui.table.navi.DrillExpandUI#collapse(Span)
   */
  protected void collapse(Span span) {
    extension.collapse((Member) span.getMember().getRootDecoree());
  }

  protected String getCollapseImage() {
    return "drill-member-collapse";
  }

  protected String getExpandImage() {
    return "drill-member-expand";
  }

  protected String getOtherImage() {
    return "drill-member-other";
  }

}

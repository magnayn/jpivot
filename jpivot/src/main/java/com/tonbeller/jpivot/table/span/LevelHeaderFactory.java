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
package com.tonbeller.jpivot.table.span;

import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.EmptyMember;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.VisitorSupportStrict;

/**
 * returns a new span containing the level of the given span.
 * If the given span contains a hierarchy or dimension (which don't have
 * a single level) the new span contains the hierarchy or dimension.
 * @author av
 */
public class LevelHeaderFactory extends VisitorSupportStrict implements SpanVisitor, SpanHeaderFactory {
  Span header;

  /**
   * @see com.tonbeller.jpivot.ui.table.span.SpanHeaderFactory#create(Span)
   */
  public Span create(Span span) {
    header = (Span)span.clone();
    span.getObject().accept(this);
    // level/hierarchy does not have a position
    header.setPosition(null);
    return header;
  }


  public void visitDimension(Dimension v) {
    header.setObject(v);
  }

  public void visitHierarchy(Hierarchy v) {
    header.setObject(v);
  }

  public void visitLevel(Level v) {
    header.setObject(v);
  }

  public void visitMember(Member v) {
    header.setObject(v.getLevel());
  }

  public void visitProperty(Property v) {
    header.setObject(new PropertyHeading(v.getLabel()));
  }

  public void visitPropertyHeading(PropertyHeading heading) {
    header.setObject(new PropertyHeading(heading.getLabel()));
  }

  public void visitEmptyMember(EmptyMember v) {
    header.setObject(v);
  }

}

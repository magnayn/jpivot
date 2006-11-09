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

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.jpivot.olap.model.EmptyMember;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.PropertyHolder;
import com.tonbeller.jpivot.olap.model.Visitable;
import com.tonbeller.jpivot.olap.model.VisitorSupportStrict;
import com.tonbeller.jpivot.table.span.PropertyHeading;
import com.tonbeller.jpivot.table.span.PropertyUtils;
import com.tonbeller.jpivot.table.span.Span;
import com.tonbeller.jpivot.table.span.SpanVisitor;

/**
 * renders a row or column heading. Creates a DOM Element from a Member, Hierarchy,
 * Dimension or Level.
 *
 * @author av
 */
public class SpanBuilderImpl extends PartBuilderSupport implements SpanBuilder {
  String memberName;
  String headingName;
  private static final Logger logger = Logger.getLogger(SpanBuilderImpl.class);

  /**
   * creates an instance
   * @param memberName either "row-heading" or "column-heading"
   * @param headingName element name for the heading of a row- or column-heading  (i.e. "heading-heading").
   */
  public SpanBuilderImpl(String memberName, String headingName) {
    this.memberName = memberName;
    this.headingName = headingName;
  }

  class RenderSwitch extends VisitorSupportStrict implements SpanVisitor {

    private static final String CAPTION = "caption";
    Element elem;
    private SBContext sbctx;

    public RenderSwitch(SBContext sbctx) {
      this.sbctx = sbctx;
    }

    void renderHeading(Displayable d) {
      elem = table.elem(headingName);
      Element caption = table.append(CAPTION, elem);
      String label = d.getLabel();
      caption.setAttribute(CAPTION, label);
      sbctx.setCaption(caption, label);
    }

    private void renderMember(Displayable d, String label) {
      elem = table.elem(memberName);
      Element caption = table.append(CAPTION, elem);
      //String label = d.getLabel();
      caption.setAttribute(CAPTION, label);
      sbctx.setCaption(caption, label);

      if (d instanceof PropertyHolder) {
        Property[] props = ((PropertyHolder) d).getProperties();
        PropertyUtils.addInlineProperties(caption, props);
        Property style = PropertyUtils.getInlineProperty(props, PropertyUtils.STYLE_PROPERTY);
        if (style != null) {
          String value = style.getValue().trim();
          if (value.length() > 0)
            elem.setAttribute(PropertyUtils.STYLE_PROPERTY, value.toLowerCase());
        }
      }

    }

    public void visitPropertyHeading(PropertyHeading v) {
      renderHeading(v);
    }

    public void visitDimension(Dimension v) {
      renderHeading(v);
    }

    public void visitHierarchy(Hierarchy v) {
      renderHeading(v);
    }

    public void visitLevel(Level v) {
      renderHeading(v);
    }

    public void visitMember(Member v) {
      renderMember(v, v.getLabel());
    }

    public void visitProperty(Property v) {
      renderMember(v, v.getValue());
    }

    public void visitEmptyMember(EmptyMember v) {
      renderMember(v, v.getLabel());
    }

    public Element getElem() {
      return elem;
    }

    public void setElem(Element elem) {
      this.elem = elem;
    }
  }

  /**
   * renders a row- or column heading
   */
  public Element build(SBContext sbctx, Span span, boolean even) {
    if (logger.isDebugEnabled())
      logger.debug("build " + span);
    
    RenderSwitch renderSwitch = new RenderSwitch(sbctx);
    
    Visitable v = span.getObject();
    
    v.accept(renderSwitch);
    Element elem = renderSwitch.getElem();
    for (Iterator it = table.clickableIterator(); it.hasNext();)
      ((ClickableMember) it.next()).decorate(sbctx, span.getObject());
    return elem;
  }

}

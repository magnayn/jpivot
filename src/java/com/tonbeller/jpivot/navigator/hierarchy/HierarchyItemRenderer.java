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
package com.tonbeller.jpivot.navigator.hierarchy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.wcf.catedit.Category;
import com.tonbeller.wcf.catedit.DefaultItemElementRenderer;
import com.tonbeller.wcf.catedit.Item;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.utils.DomUtils;

/**
 * renders a Hierarchy 
 * @author av
 */
public class HierarchyItemRenderer extends DefaultItemElementRenderer {

  public Element render(RequestContext context, Document factory, Category cat, Item item) {
    Element elem = super.render(context, factory, cat, item);
    HierarchyItem hi = (HierarchyItem)item;
    if (hi.isClickable())
      elem.setAttribute("id", hi.getId());

    if (!hi.getSlicerSelection().isEmpty()) {
      Member m = (Member)hi.getSlicerSelection().get(0);
      Element e = DomUtils.appendElement(elem, "slicer-value");
      e.setAttribute("label", m.getLabel());
      e.setAttribute("level", m.getLevel().getLabel());
    }
    
    return elem;
    
  }
}

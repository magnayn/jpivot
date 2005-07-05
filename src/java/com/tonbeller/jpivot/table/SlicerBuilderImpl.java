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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.navi.MemberProperties;
import com.tonbeller.jpivot.table.span.PropertyConfig;
import com.tonbeller.jpivot.table.span.PropertyUtils;
import com.tonbeller.jpivot.table.span.ScopedPropertyMetaSet;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * creates a DOM Element from slicer member
 * 
 * @author av
 */
public class SlicerBuilderImpl extends PartBuilderSupport implements SlicerBuilder {
  
  ScopedPropertyMetaSet visible;
  MemberProperties extension; 
  PropertyConfig propertyConfig;
  
  public void startBuild(RequestContext context) {
    propertyConfig = table.getPropertyConfig();
    
    extension = getExtension();
    if (extension == null) {
      // we can not handle individual visible properties w/o the extension
      visible = null;
      return;
    }

    List list = propertyConfig.getVisiblePropertyMetas();
    if (list == null) {
      visible = null;
      return;
    }
    visible = new ScopedPropertyMetaSet(extension);
    visible.addAll(list);
  }

  public void stopBuild() {
    visible = null;
    extension = null;
    propertyConfig = null;
  }

  public Element build(Member m) {
    Element e = table.elem("member");
    e.setAttribute("level", m.getLevel().getLabel());
    e.setAttribute("caption", m.getLabel());
    e.setAttribute("depth", Integer.toString(m.getRootDistance()));
    addMemberProperties(e, m);
    return e;
  }

  private void addMemberProperties(Element e, Member m) {
    if (!propertyConfig.isShowProperties())
      return;

    Property[] props = visibleProperties(m);
    PropertyUtils.addProperties(e, props);
  }

  private Property[] visibleProperties(Member m) {
    Property[] src = m.getProperties();
    String scope = null;
    if (extension != null)
      scope = extension.getPropertyScope(m);
    List list = new ArrayList();
    for (int i = 0; i < src.length; i++) {
      Property p = src[i];
      if (PropertyUtils.isInline(p.getName()))
        continue;
      if (scope != null && visible != null) {
        if (visible.contains(scope, p.getName()))
            list.add(p);
      }
      else
        list.add(p);
    }
    return (Property[])list.toArray(new Property[list.size()]);
  }
  
  MemberProperties getExtension() {
    return (MemberProperties) table.getOlapModel().getExtension(MemberProperties.ID);
  }
  
  public boolean isAvailable() {
    return getExtension() != null;
  }
  
}
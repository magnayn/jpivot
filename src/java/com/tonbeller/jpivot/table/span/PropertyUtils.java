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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import com.tonbeller.jpivot.olap.model.MemberPropertyMeta;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.impl.PropertyImpl;
import com.tonbeller.tbutils.res.Resources;

/**
 * Handles nested Properties.
 * <p>
 * Example 1: 
 * <code>src="x", href="y"</code> becomes
 * <pre>
 * &lt;property name="src" value="x"/&gt;
 * &lt;property name="href" value="y"/&gt;
 * </pre>
 * <p>
 * Example 2:
 * <code>image="x", image.src="y", image.href="z"</code> becomes
 * <pre>
 * &lt;property name="image" value="x&gt;
 *   &lt;property name="src" value="y"/&gt;
 *   &lt;property name="href" value="z"/&gt;
 * &lt;/property&gt;
 * </pre>
 */
public class PropertyUtils {

  private static final String delimiter = ".";
  private static final char delimiterChar = '.';
  private static final String INLINE_PREFIX = "inline.property.";
  
  public static final String STYLE_PROPERTY = "style";

  /** 
   * maps multiple names in lower case to the 'standard' name of the inline property. Example:
   * 'image' and 'bild' (german) map to the standard property name 'image'.
   */
  static Map inlineProps;

  static {
    Map map = new HashMap();
    int prefixLength = INLINE_PREFIX.length();
    Resources res = Resources.instance(PropertyUtils.class);
    for (Iterator it = res.keySet().iterator(); it.hasNext();) {
      String key = (String) it.next();
      if (key.startsWith(INLINE_PREFIX)) {
        String value = res.getString(key);
        key = key.substring(prefixLength);
        map.put(key, value);
      }
    }
    inlineProps = Collections.unmodifiableMap(map);
  };

  private PropertyUtils() {
  }

  /**
   * true if name contains the delimiter '.'
   */
  public static boolean isNested(String propertyName) {
    return propertyName.indexOf(delimiterChar) > 0;
  }

  /**
   * if propertyName denotes a nested property (i.e. contains the '.' delimiter),
   * returns the name of the root property. 
   * @param propertyName name of nested Property, e.g. "myName.image"
   * @return name of root property, e.g. "myName"
   */
  public static String getRootName(String propertyName) {
    int pos = propertyName.indexOf(delimiterChar);
    if (pos >= 0)
      return propertyName.substring(0, pos);
    return propertyName;
  }

  /** 
   * true if name is rendered inline (e.g. 'link', 'color', 'arrow')
   */
  public static boolean isInline(String propertyName) {
    if (propertyName.startsWith("$"))
      return true;
    String name = propertyName.toLowerCase();
    return inlineProps.containsKey(name);
  }
  
  public static boolean isStyleProperty(String propertyName) {
    String name = propertyName.toLowerCase();
    String value = (String)inlineProps.get(name);
    return STYLE_PROPERTY.equals(value);
  }

  /**
   * creates nested properties where the hierarchy is derived by the
   * property names.
   */
  public static Property[] normalize(Property[] properties) {
    if (!needsNormalization(properties))
      return properties;
    Map map = new HashMap();
    List result = new ArrayList();
    for (int i = 0; i < properties.length; i++) {
      Property src = properties[i];
      PropertyImpl parent = null;
      StringBuffer nameBuffer = new StringBuffer();
      StringTokenizer st = new StringTokenizer(src.getName(), delimiter);
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        nameBuffer.append(delimiterChar).append(token);
        String name = nameBuffer.toString();

        PropertyImpl child = (PropertyImpl) map.get(name);
        if (child == null) {
          child = new PropertyImpl();
          child.setName(token);
          child.setLabel(token);
          child.setValue("");
          map.put(name, child);
          if (parent == null)
            result.add(child);
          else
            parent.addProperty(child);
        }
        parent = child;
      }
      parent.setValue(src.getValue());
      parent.setLabel(src.getLabel());
      parent.setAlignment(src.getAlignment());
    }
    return (Property[]) result.toArray(new Property[result.size()]);
  }

  private static boolean needsNormalization(Property[] properties) {
    if (properties == null || properties.length < 2)
      return false;
    for (int i = 0; i < properties.length; i++) {
      Property p = properties[i];
      if (!p.isNormalizable())
        continue;
      if (p.getName().indexOf(delimiter) > 0) 
        return true;
    }
    return false;
  }

  /**
   * adds property children to the target element. If the
   * name of a property contains the "." char, its treated specially (see below).
   */

  public static void addProperties(Element target, Property[] properties) {
    properties = normalize(properties);
    recurseAddProperties(target, properties, false);
  }

  /**
   * adds inline properties to <code>caption</code> and converts
   * the property names to lower case.
   */
  public static void addInlineProperties(Element target, Property[] properties) {
    if (properties.length > 0) {
      List list = new ArrayList();
      for (int i = 0; i < properties.length; i++) {
        if (isInline(properties[i].getName()))
          list.add(properties[i]);
      }
      properties = (Property[]) list.toArray(new Property[list.size()]);
      properties = normalize(properties);
      recurseAddProperties(target, properties, true);
    }
  }

  private static void recurseAddProperties(Element parent, Property[] properties, boolean inline) {
    if (properties == null)
      return;
    for (int i = 0; i < properties.length; i++) {
      Property p = properties[i];
      Element elem = parent.getOwnerDocument().createElement("property");
      parent.appendChild(elem);
      if (inline) {
        String name = p.getName().toLowerCase();
        name = (String) inlineProps.get(name);
        elem.setAttribute("name", name);
      } else
        elem.setAttribute("name", p.getName());
      elem.setAttribute("label", p.getLabel());
      elem.setAttribute("value", p.getValue());
      recurseAddProperties(elem, p.getProperties(), inline);
    }
  }

  /**
   * removes Metas that correspond to nested properties
   * @param metas
   * @return
   */
  public static MemberPropertyMeta[] getRootMetas(MemberPropertyMeta[] metas) {
    Set nameSet = new HashSet();
    List roots = new ArrayList();
    // copy root properties
    for (int i = 0; i < metas.length; i++) {
      String name = metas[i].getName();
      if (!isNested(name)) {
        nameSet.add(name);
        roots.add(metas[i]);
      }
    }

    // create a root property for nested properties
    // that do not have a root property
    for (int i = 0; i < metas.length; i++) {
      String name = metas[i].getName();
      if (isNested(name)) {
        String rootName = getRootName(name);
        if (!nameSet.contains(rootName)) {
          nameSet.add(rootName);
          // often label == propertyName, so better cut off '.' 
          String label = getRootName(metas[i].getLabel());
          String scope = metas[i].getScope();
          roots.add(new MemberPropertyMeta(label, rootName, scope));
        }
      }
    }
    return (MemberPropertyMeta[]) roots.toArray(new MemberPropertyMeta[roots.size()]);
  }

  /**
   * returns the Inline Property with given name
   * @param props all properties
   * @param name one of the names of the inline property
   * @return null or the property
   */
  public static Property getInlineProperty(Property[] props, String name) {
    name = (String) inlineProps.get(name.toLowerCase());
    for (int i = 0; i < props.length; i++) {
      String s = props[i].getName().toLowerCase();
      s = (String) inlineProps.get(s);
      if (s != null && s.equals(name))
        return props[i];
    }
    return null;
  }

}

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
package com.tonbeller.jpivot.olap.model;

import java.util.Comparator;


/**
 * @author av
 */
public final class MemberPropertyMeta implements Displayable {
  private String name;
  private String scope;
  private String label;
  
  /**
   * vergleicht den return Wert von getKey() ohne die String instanzen wirklich zu erzeugen.
   */
  public static final Comparator KEY_COMPARATOR = new Comparator() {
    public int compare(Object o1, Object o2) {
      MemberPropertyMeta m1 = (MemberPropertyMeta) o1;
      MemberPropertyMeta m2 = (MemberPropertyMeta) o2;
      int comp = m1.getScope().compareTo(m2.getScope());
      if (comp == 0)
        return m1.getName().compareTo(m2.getName());
      return comp;
    }
  };
  
  public MemberPropertyMeta() {
  }

  public MemberPropertyMeta(String label, String name, String scope) {
    this.label = label;
    this.name = name;
    this.scope = scope;
  }

  public void accept(Visitor visitor) {
    visitor.visitMemberPropertyMeta(this);
  }

  public String getName() {
    return name;
  }

  public String getScope() {
    return scope;
  }

  public void setName(String string) {
    name = string;
  }

  public void setScope(String string) {
    scope = string;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String string) {
    label = string;
  }
  
  public String getKey() {
    return scope + "." + name;
  }
  
  public String toString() {
    return "MemberPropertyMeta[" + name + ", " + label + ", " + scope + "]";
  }

}

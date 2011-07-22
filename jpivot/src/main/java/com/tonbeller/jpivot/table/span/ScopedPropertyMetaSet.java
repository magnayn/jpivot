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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.MemberPropertyMeta;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.navi.MemberProperties;

/**
 * A MemberPropertyMeta Set implementation that supports different scopes (Level and Hierarchy).
 * An Entry is identified by the Scope (either Level or Hierarchy) of the PropertyMeta and
 * the property name. Example: Property "a" belongs to Level "l1" of Hierarchy "h", another
 * Property "a" (same name) to Level "l2" (different Level) of the same Hierarchy "h". If
 * these properties are added to a Level-scoped set, it would contain 2 different entries.
 * When added to a Hierarchy-scoped  set, it would contain one entry only (the first one added).
 * @author av
 */
public class ScopedPropertyMetaSet {
  
  /**
   * ordered list of MemberPropertyMetas for a scope
   * 
   * @author av
   * @since 07.03.2005
   */
  class ScopeList {
    List list = new ArrayList();
    Map  map = new HashMap();
    void add(MemberPropertyMeta mpm) {
      String name = mpm.getName();
      if (!map.containsKey(name)) {
        map.put(name, mpm);
        list.add(mpm);
      }
    }
    void remove(MemberPropertyMeta mpm) {
      mpm = (MemberPropertyMeta) map.get(mpm.getName());
      if (mpm == null)
        return;
      map.remove(mpm.getName());
      list.remove(mpm);
    }
    MemberPropertyMeta lookup(String name) {
      return (MemberPropertyMeta) map.get(name);
    }
    boolean contains(String name) {
      return map.containsKey(name);
    }
    Iterator iterator() {
      return list.iterator();
    }
  }

  private MemberProperties extension;
  
  public ScopedPropertyMetaSet(MemberProperties extension) {
    this.extension = extension;
  }

  // For every scope (=key) contains a ScopeList (=value) which contains the MemberPropertyMeta instances.
  private Map scopeMap = new HashMap();

  public void addAll(Collection memberPropertyMetas) {
    for (Iterator it = memberPropertyMetas.iterator(); it.hasNext();) {
      MemberPropertyMeta mpm = (MemberPropertyMeta) it.next();
      add(mpm);
    }
  }

  public void removeAll(Collection memberPropertyMetas) {
    for (Iterator it = memberPropertyMetas.iterator(); it.hasNext();) {
      MemberPropertyMeta mpm = (MemberPropertyMeta) it.next();
      remove(mpm);
    }
  }

  /**
   * for every property of member, creates a MemberPropertyMeta and adds it to this set
   * @param member
   */
  public void addMember(Member member) {
    if (member == null)
      return;
    String scope = extension.getPropertyScope(member);
    Property[] properties = member.getProperties();
    properties = PropertyUtils.normalize(properties);
    for (int i = 0; i < properties.length; i++) {
      String label = properties[i].getLabel();
      String name = properties[i].getName();
      MemberPropertyMeta mpm = new MemberPropertyMeta(label, name, scope);
      this.add(mpm);
    }
  }

  public void add(MemberPropertyMeta mpm) {
    Object scope = mpm.getScope();
    ScopeList scopeList = (ScopeList) scopeMap.get(scope);
    if (scopeList == null) {
      scopeList = new ScopeList();
      scopeMap.put(scope, scopeList);
    }
    scopeList.add(mpm);
  }

  public void remove(MemberPropertyMeta mpm) {
    Object scope = mpm.getScope();
    ScopeList scopeList = (ScopeList) scopeMap.get(scope);
    if (scopeList == null)
      return;
    scopeList.remove(mpm);
  }

  /**
   * true, if mpm is contained in the visiblePropertiyMetas collection
   * @param mpm
   * @return
   */
  public boolean contains(MemberPropertyMeta mpm) {
    return contains(mpm.getScope(), mpm.getName());
  }
  /**
   * true if the property <code>name</code> is contained in
   * the <code>scope</code>. Example usage:
   * <pre>
   *   Member m = ...
   *   Property p = m.getProperty("foo");
   *   MemberProperties extension = (MemberProperties ) model.getExtension(MemberProperties.ID);
   *   
   *   if (set.contains(extension.getScope(m), p.getName())
   *     ...
   * </pre>
   * @param scope
   * @param name
   * @return
   */
  public boolean contains(String scope, String name) {
    ScopeList scopeList = (ScopeList) scopeMap.get(scope);
    if (scopeList == null)
      return false;
    return scopeList.contains(name);
  }

  MemberPropertyMeta lookup(String scope, String name) {
    ScopeList scopeList = (ScopeList) scopeMap.get(scope);
    if (scopeList == null)
      return null;
    return scopeList.lookup(name);
  }
  
  /**
   * creates a new List that contains all the MemberPropertyMetas that are contained in <code>metas</code>
   * and are contained in this set (intersection). The order is not changed.
   * @param metas a list of MemberPropertyMetas
   * @return
   */
  public List intersectList(List metas) {
    List list = new ArrayList();
    for (Iterator it = metas.iterator(); it.hasNext();) {
      MemberPropertyMeta mpm = (MemberPropertyMeta) it.next();
      mpm = lookup(mpm.getScope(), mpm.getName());
      if (mpm != null)
        list.add(mpm);
    }
    return list;
  }


  /**
   * returns a list of Metas that pass the filter
   */
  public List metaList(MemberPropertyMetaFilter filter) {
    List list = new ArrayList();
    for (Iterator it = scopeMap.values().iterator(); it.hasNext();) {
      ScopeList scopeList = (ScopeList ) it.next();
      for (Iterator vt = scopeList.iterator(); vt.hasNext();) {
        MemberPropertyMeta meta = (MemberPropertyMeta) vt.next();
        if (filter.accept(meta))
          list.add(meta);
      }
    }
    return list;
  }

  /**
   * returns a subset containing the metas that pass the filter
   */
  public ScopedPropertyMetaSet metaSet(MemberPropertyMetaFilter filter) {
    ScopedPropertyMetaSet set = new ScopedPropertyMetaSet(extension);
    for (Iterator it = scopeMap.values().iterator(); it.hasNext();) {
      ScopeList scopeList = (ScopeList) it.next();
      for (Iterator vt = scopeList.iterator(); vt.hasNext();) {
        MemberPropertyMeta meta = (MemberPropertyMeta) vt.next();
        if (filter.accept(meta))
          set.add(meta);
      }
    }
    return set;
  }

  /**
   * @return sorted (!) array of all names - for test use only
   */
  String[] getAllNames() {
    Set allNames = new TreeSet();
    for (Iterator it = scopeMap.values().iterator(); it.hasNext();) {
      ScopeList scopeList = (ScopeList) it.next();
      for (Iterator vt = scopeList.iterator(); vt.hasNext();) {
        MemberPropertyMeta meta = (MemberPropertyMeta) vt.next();
        allNames.add(meta.getName());
      }
    }
    return (String[]) allNames.toArray(new String[allNames.size()]);
  }

  /**
   * accepts all metas
   */
  static class AllFilter implements MemberPropertyMetaFilter {
    public boolean accept(MemberPropertyMeta meta) {
      return true;
    }
  }
  public MemberPropertyMetaFilter createAllFilter() {
    return new AllFilter();
  }

  /**
   * accepts metas that belong to a set of scopes.
   * @see ScopedPropertyMetaSet#getScope
   */
  class ScopesFilter implements MemberPropertyMetaFilter {
    Set scopes;
    public ScopesFilter(Set scopes) {
      this.scopes = scopes;
    }
    public boolean accept(MemberPropertyMeta meta) {
      return scopes.contains(meta.getScope());
    }
  }
  public MemberPropertyMetaFilter createScopesFilter(Set scopes) {
    return new ScopesFilter(scopes);
  }

  /**
   * accepts metas that are not rendered inline
   */
  static class IgnoreInlineFilter implements MemberPropertyMetaFilter {
    public boolean accept(MemberPropertyMeta meta) {
      return !PropertyUtils.isInline(meta.getName());
    }
  }
  public MemberPropertyMetaFilter createIgnoreInlineFilter() {
    return new IgnoreInlineFilter();
  }

}

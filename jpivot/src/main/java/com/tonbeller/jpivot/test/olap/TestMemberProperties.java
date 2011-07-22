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
package com.tonbeller.jpivot.test.olap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.MemberPropertyMeta;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.navi.MemberProperties;

/**
 * @author av
 */
public class TestMemberProperties extends TestExtensionSupport implements MemberProperties {
  boolean initialized = false;
  boolean levelScope;

  public String getId() {
    return MemberProperties.ID;
  }

  /** maps level -> LevelProps */
  HashMap levelMap = new HashMap();
  static class LevelProps {
    Set names = new HashSet();
    List metas = new ArrayList();
    boolean contains(String name) {
      return names.contains(name);
    }
    void add(MemberPropertyMeta m) {
      names.add(m.getName());
      metas.add(m);
    }
    MemberPropertyMeta[] toArray() {
      return (MemberPropertyMeta[]) metas.toArray(new MemberPropertyMeta[metas.size()]);
    }
  }
  
  void initialize() {
    initialized = true;
    for (int i = 0; i < model().getDimensions().length; i++) {
      TestDimension dim = (TestDimension) model().getDimensions()[i];
      for (int j = 0; j < dim.getHierarchies().length; j++) {
        TestHierarchy th = (TestHierarchy) dim.getHierarchies()[j];
        for (int k = 0; k < th.getRootMembers().length; k++) {
          recurse(th.getRootMembers()[k]);
        }
      }
    }
  }

  private void recurse(TestMember member) {
    Property[] p = member.getProperties();
    String scope = getPropertyScope(member);
    Level level = member.getLevel();
    LevelProps levelProps = (LevelProps) levelMap.get(level);
    if (levelProps == null) {
      levelProps = new LevelProps();
      levelMap.put(level, levelProps);
    }

    for (int i = 0; i < p.length; i++) {
      if (!levelProps.contains(p[i].getName())) {
        MemberPropertyMeta pm = new MemberPropertyMeta();
        pm.setName(p[i].getName());
        pm.setLabel(p[i].getName());
        pm.setScope(scope);
        levelProps.add(pm);
      }
    }

    for (Iterator it = member.getChildMember().iterator(); it.hasNext();)
      recurse((TestMember) it.next());
  }

  public MemberPropertyMeta[] getMemberPropertyMetas(Level level) {
    if (!initialized) {
      initialize();
    }
    LevelProps levelProps = (LevelProps) levelMap.get(level);
    return levelProps.toArray();
  }

  public String getPropertyScope(Member m) {
    if (levelScope)
      return m.getLevel().getLabel();
    return m.getLevel().getHierarchy().getLabel();
  }

  /**
   * @return
   */
  public boolean isLevelScope() {
    return levelScope;
  }

  /**
   * @param b
   */
  public void setLevelScope(boolean b) {
    levelScope = b;
  }

  public void setVisibleProperties(MemberPropertyMeta[] props) {
  }

}

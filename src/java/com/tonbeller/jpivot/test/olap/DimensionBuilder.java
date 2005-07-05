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

import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.impl.LevelImpl;

/**
 * Created on 22.10.2002
 * 
 * @author av
 */
public class DimensionBuilder {
  String hierName;
  String[] levelNames;
  int[] childCount;

  LevelImpl[] levels;

  public TestDimension build(String hierName, String[] levelNames, int[] childCount) {
    this.hierName = hierName;
    this.levelNames = levelNames;
    this.childCount = childCount;

    // build the dim / hier / levels
    TestDimension dim = new TestDimension();
    dim.setLabel(hierName);
    TestHierarchy hier = new TestHierarchy();
    hier.setLabel(hierName);
    hier.setDimension(dim);
    dim.setHierarchies(new Hierarchy[] { hier });

    levels = new LevelImpl[levelNames.length];
    for (int i = 0; i < levels.length; i++) {
      TestLevel level = new TestLevel();
      level.setLabel(levelNames[i]);
      level.setHierarchy(hier);
      levels[i] = level;
    }
    hier.setLevels(levels);

    // build the members
    TestMember[] roots = buildRoots();
    hier.rootMembers = roots;

    return dim;
  }

  TestMember[] buildRoots() {
    TestMember[] members = new TestMember[childCount[0]];
    for (int i = 0; i < childCount[0]; i++) {
      TestMember member = new TestMember();
      member.setLabel(levelNames[0] + "[" + i + "]");
      member.setLevel(levels[0]);
      member.setVisible(true);
      appendChildren(member, 1);
      members[i] = member;
    }
    return members;
  }


  void appendChildren(TestMember parent, int levelIndex) {
    if (levelIndex >= childCount.length)
      return;

    for (int i = 0; i < childCount[levelIndex]; i++) {
      TestMember member = new TestMember();
      member.setLabel(levelNames[levelIndex] + "[" + i + "]");
      member.setParentMember(parent);
      member.setLevel(levels[levelIndex]);
      member.setRootDistance(levelIndex);
      parent.addChildMember(member);
      appendChildren(member, levelIndex + 1);
    }
  }

}

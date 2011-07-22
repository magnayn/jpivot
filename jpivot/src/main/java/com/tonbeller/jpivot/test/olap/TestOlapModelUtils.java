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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;

/**
 * Created on 02.12.2002
 *
 * @author av
 */
public class TestOlapModelUtils {

  /**
   * private Constructor for TestOlapModelUtils.
   */
  private TestOlapModelUtils() {
  }

  public static List getVisible(TestDimension dim) {
    TestHierarchy th = (TestHierarchy) dim.getHierarchies()[0];
    List roots = Arrays.asList(th.getRootMembers());
    ArrayList list = new ArrayList();
    getVisible(roots, list);
    return list;
  }

  static void getVisible(List members, ArrayList list) {
    Iterator it = members.iterator();
    while (it.hasNext()) {
      TestMember m = (TestMember) it.next();
      if (m.isVisible())
        list.add(m);
      getVisible(m.getChildMember(), list);
    }
  }

  public static TestAxis createAxis(Member[] members) {
    List list = Arrays.asList(members);
    return createAxis(list);
  }

  public static TestAxis createAxis(List members) {
    TestAxis a = new TestAxis();
    Iterator it = members.iterator();
    while (it.hasNext()) {
      Member m = (Member) it.next();
      TestPosition p = new TestPosition(a);
      p.setMembers(new Member[] { m });
      a.addPosition(p);
    }
    return a;
  }

  public static TestAxis crossJoin(Axis a1, Axis a2) {
    TestAxis a = new TestAxis();
    Iterator i1 = a1.getPositions().iterator();
    while (i1.hasNext()) {
      Member[] m1 = ((Position) i1.next()).getMembers();
      Iterator i2 = a2.getPositions().iterator();
      while (i2.hasNext()) {
        Member[] m2 = ((Position) i2.next()).getMembers();

        Member[] m = new Member[m1.length + m2.length];
        for (int i = 0; i < m1.length; i++)
          m[i] = m1[i];
        for (int i = 0, j = m1.length; i < m2.length; i++, j++)
          m[j] = m2[i];
        TestPosition p = new TestPosition(a);
        p.setMembers(m);
        a.addPosition(p);

      }
    }
    return a;
  }

  public static TestAxis createAxis(TestDimension dim) {
    return createAxis(getVisible(dim));
  }

  public static TestAxis createAxis(TestDimension[] dims) {
    TestAxis a = createAxis(getVisible(dims[0]));
    for (int i = 1; i < dims.length; i++) {
      TestAxis b = createAxis(getVisible(dims[i]));
      a = crossJoin(a, b);
    }
    return a;
  }

  public static List findPositions(TestAxis axis, Member member) {
    List list = new ArrayList();
    for (Iterator it = axis.getPositions().iterator(); it.hasNext();) {
      Object o = it.next();
      TestPosition p = (TestPosition) o;
      if (p.contains(member))
        list.add(p);
    }
    return list;
  }

  public static TestAxis findAxis(TestOlapModel model, Member member) {
    TestHierarchy hier = (TestHierarchy) member.getLevel().getHierarchy();
    return findAxis(model, hier);
  }

  public static TestAxis findAxis(TestOlapModel model, Hierarchy hier) {
    TestAxis[] axes = model.getAxes();
    for (int i = 0; i < axes.length; i++) {
      Hierarchy[] th = axes[i].getHierarchies();
      for (int j = 0; j < th.length; j++)
        if (hier.equals(th[j]))
          return axes[i];
    }
    return null;
  }


  public static void rebuildAxis(TestOlapModel tom, TestMember m) {
    // find hierarchies
    TestAxis axis = TestOlapModelUtils.findAxis(tom, m);
    rebuildAxis(tom, axis);
  }

  public static void rebuildAxis(TestOlapModel tom, TestAxis axis) {
    Hierarchy[] dims = axis.getHierarchies();

    // crossjoin a new axis
    TestAxis newAxis = TestOlapModelUtils.createAxis((TestDimension) dims[0].getDimension());
    for (int i = 1; i < dims.length; i++) {
      TestAxis ax = TestOlapModelUtils.createAxis((TestDimension) dims[i].getDimension());
      newAxis = TestOlapModelUtils.crossJoin(newAxis, ax);
    }

    // replace old axis
    TestAxis[] axes = tom.getAxes();
    for (int i = 0; i < axes.length; i++)
      if (axes[i] == axis)
        axes[i] = newAxis;
  }

  public static void hideAll(Hierarchy hier) {
    TestMember[] members = ((TestHierarchy) hier).getRootMembers();
    for (int i = 0; i < members.length; i++)
      recurseHideAll(members[i]);
  }

  private static void recurseHideAll(TestMember member) {
    member.setVisible(false);
    if (member.hasChildren()) {
      for (Iterator it = member.getChildMember().iterator(); it.hasNext();) {
        TestMember child = (TestMember) it.next();
        recurseHideAll(child);
      }
    }
  }

  public static void setVisible(List members) {
	if (members.size() > 0) {
		Member m0 = (Member) members.get(0);
		hideAll(m0.getLevel().getHierarchy());
		for (Iterator it = members.iterator(); it.hasNext();)
		   ((TestMember) it.next()).setVisible(true);
    }
  }

  public static void setVisible(Member[] members) {
    hideAll(members[0].getLevel().getHierarchy());
    for (int i = 0; i < members.length; i++)
       ((TestMember) members[i]).setVisible(true);
  }

  public static   int indexOf(Object[] array, Object obj) {
    for (int i = 0; i < array.length; i++)
      if (array[i] == obj)
        return i;
    return -1;
  }

}

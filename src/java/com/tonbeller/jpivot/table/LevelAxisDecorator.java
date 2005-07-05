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
import java.util.Iterator;
import java.util.List;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Visitor;
import com.tonbeller.jpivot.olap.navi.MemberTree;

/**
 * Decorates an Axis by adding the parents of all members.
 * Every Position will contain an equal number of members, 
 * where some of them will be the same. For example, an
 * axis in the result contains 2 members in 1 hierarchy
 * <p>
 * <table>
 *   <tr><th>   </th><th>Revenue</th></tr>
 *   <tr><td>USA</td><td>1000</td></tr>
 *   <tr><td>CA </td><td>100</td></tr>
 * </table>
 * <p>
 * will become
 * <p>
 * <table>
 *   <tr><th>   </th><th>    </th><th>Revenue</th></tr>
 *   <tr><td>USA</td><td>USA</td><td>1000</td></tr>
 *   <tr><td>USA</td><td>CA  </td><td>100</td></tr>
 * </table>
 * 
 * @author av
 */

public class LevelAxisDecorator implements Axis {
  Axis axis;
  MemberTree tree;
  int[] levelCount; // max(rootDistance) + 1
  int totalLevelCount;

  List positions;

  /**
   * Constructor for LevelAxisDecorator.
   */
  public LevelAxisDecorator(Axis axis, MemberTree tree) {
    this.axis = axis;
    this.tree = tree;
    computeLevelCount();
    makePositions();
  }

  /**
   * for each hierarchy of the underlying axis compute the
   * number of levels (maxRootDistance - minRootDistance).
   */
  void computeLevelCount() {
    int hierarchyCount = axis.getHierarchies().length;
    levelCount = new int[hierarchyCount];
    for (int i = 0; i < hierarchyCount; i++)
      levelCount[i] = Integer.MIN_VALUE;

    Iterator it = axis.getPositions().iterator();
    while (it.hasNext()) {
      Position p = (Position) it.next();
      Member[] members = p.getMembers();
      for (int i = 0; i < members.length; i++) {
        int count = members[i].getRootDistance() + 1;
        levelCount[i] = Math.max(levelCount[i], count);
      }
    }

    // the number of members per position is the sum of all deltas
    totalLevelCount = 0;
    for (int i = 0; i < hierarchyCount; i++)
      totalLevelCount += levelCount[i];
  }

  void makePositions() {
    positions = new ArrayList();
    Iterator it = axis.getPositions().iterator();
    while (it.hasNext()) {
      Position p = (Position) it.next();
      positions.add(makePosition(p));
    }
  }

  Position makePosition(Position source) {
    Member[] members = source.getMembers();
    Member[] result = new Member[totalLevelCount];
    int offset = 0;
    for (int i = 0; i < members.length; i++) {
      int count = levelCount[i];
      addParents(result, offset, count, members[i]);
      offset += count;
    }
    return new MyPosition(source, result);
  }

  void addParents(Member[] result, int offset, int totalCount, Member member) {
    int memberCount = member.getRootDistance() + 1;
    int fillCount = totalCount - memberCount;
    // fill from right to left because we want the parents to appear left
    offset = offset + totalCount - 1;
    for (int i = 0; i < fillCount; i++)
      result[offset--] = member;

    for (int i = 0; i < memberCount; i++) {
      result[offset--] = member;
      member = tree.getParent(member);
    }
  }

  /**
   * @see com.tonbeller.jpivot.olap.model.Axis#getPositions()
   */
  public List getPositions() {
    return positions;
  }

  /**
   * returns the hierarchies of the underlying axis.
   * @see com.tonbeller.jpivot.olap.model.Axis#getHierarchies()
   */
  public Hierarchy[] getHierarchies() {
    return axis.getHierarchies();
  }

  /**
   * @see com.tonbeller.jpivot.olap.model.impl.AxisDecorator#getAxis()
   */
  public Axis getAxis() {
    return axis;
  }

  public Object getRootDecoree() {
    return axis.getRootDecoree();
  }

  private static class MyPosition implements Position {
    Position position;
    Member[] members;

    MyPosition(Position position, Member[] members) {
      this.position = position;
      this.members = members;
    }

    /**
     * @see com.tonbeller.jpivot.olap.model.impl.PositionDecorator#getPosition()
     */
    public Position getPosition() {
      return position;
    }

    /**
     * @see com.tonbeller.jpivot.olap.model.Position#getMembers()
     */
    public Member[] getMembers() {
      return members;
    }

    public Object getRootDecoree() {
      return position.getRootDecoree();
    }

    /**
     * @see com.tonbeller.jpivot.olap.model.Visitable#accept(Visitor)
     */
    public void accept(Visitor visitor) {
      visitor.visitPosition(this);
    }

  }

  /**
   * @see com.tonbeller.jpivot.olap.model.Visitable#accept(Visitor)
   */
  public void accept(Visitor visitor) {
    visitor.visitAxis(this);
  }

}

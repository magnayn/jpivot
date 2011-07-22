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
 * <p>
 * If the all member is not visible on the axis, its not added as a parent, because
 * this would not add information. Otherwise its added like other parent members too. 
 * @author av
 */

public class LevelAxisDecorator implements Axis {
  Axis axis;
  MemberTree tree;
  int[] levelCount; 
  boolean[] skipAllMember;
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
    Hierarchy[] hiers = axis.getHierarchies();
    int hierarchyCount = axis.getHierarchies().length;
    levelCount = new int[hierarchyCount];
    skipAllMember = new boolean[hierarchyCount];
    for (int i = 0; i < hiers.length; i++) {
      levelCount[i] = Integer.MIN_VALUE;
      skipAllMember[i] = hiers[i].hasAll();
    }

    Iterator it = axis.getPositions().iterator();
    while (it.hasNext()) {
      Position p = (Position) it.next();
      Member[] members = p.getMembers();

      for (int i = 0; i < members.length; i++) {
        int count = members[i].getRootDistance() + 1;
        //ADVR 2010.07.26
        // if we have calculated members in the root, increase count for that item.
        // otherwise count=0 if All not present, which means that if no non-calculated
        // members are present, axis members will not be added in correctly and will
        // not render
        if (count==1 && !members[i].isAll()) {
            System.out.println("adjusted depth for :"+members[i].getLabel());
            count++;
        }
        levelCount[i] = Math.max(levelCount[i], count);
        if (members[i].isAll())
          skipAllMember[i] = false;
      }
    }

    // if the ALL member is not on the axis, we will not add it
    for (int i = 0; i < hierarchyCount; i++) {
      if (skipAllMember[i])
        levelCount[i] -= 1;
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

  private Position makePosition(Position source) {
    Member[] members = source.getMembers();
    Member[] result = new Member[totalLevelCount];
    int offset = 0;
    for (int i = 0; i < members.length; i++) {
      int totalCount = levelCount[i];
      int memberCount = members[i].getRootDistance() + 1;
      if (skipAllMember[i])
        memberCount -= 1;
      addParents(result, offset, totalCount, memberCount, members[i]);
      offset += totalCount;
    }
    return new MyPosition(source, result);
  }

  /**
   * adds members to result array from right to left, starting at offset
   * @param result the array to add the members to
   * @param offset the offset in the array
   * @param totalCount number of positions to fill in the array
   * @param memberCount the number of different members to add, rest will be padded
   * @param member start member
   */
  private void addParents(Member[] result, int offset, int totalCount, int memberCount, Member member) {
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

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
package com.tonbeller.jpivot.olap.model.impl;

import java.util.List;
import java.util.ListIterator;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;

/**
 * finds a group (span) of positions that are related in terms of a start position and a member.
 * The group consists of the positions before and after the start position, that share equal
 * members on the member's level and on the higher levels.
 * <p>
 * Example 1: returns the first 2 positions. A's and I's are equal within the group
 * <pre>
 * A I U  <- start position, member = I
 * A I V
 * A J U
 * A J V
 * </pre>
 * <p>
 * Example 2: returns the first 2 positions. A's and I's are equal within the group
 * <pre>
 * A I U  
 * A I V  <- start position, member = I
 * B I U
 * B I V
 * </pre>
 */

public class PositionSpan {
  Axis axis;
  Position position;
  Member member;

  int memberIndex; // index of member in position
  int startIndex;  // index of the first matching position
  int endIndex;    // index + 1 of the last matching position

  /**
   * Constructor for PositionSpan.
   */
  public PositionSpan(Axis axis, Position position, Member member) {
    this.axis = axis;
    this.position = position;
    this.member = member;

    memberIndex = indexOf(position.getMembers(), member);    
    initStartIndex();
    initEndIndex();
  }
  
  void initStartIndex() {
    List list = axis.getPositions();
    int  index = list.indexOf(position);
    ListIterator li = list.listIterator(index);
    loop: while (li.hasPrevious()) {
      Position p = (Position)li.previous();
      if (!match(p))
        break loop;
      index -= 1;
    }
    startIndex = index;
  }

  void initEndIndex() {
    List list = axis.getPositions();
    int  index = list.indexOf(position);
    ListIterator li = list.listIterator(index);
    loop: while (li.hasNext()) {
      Position p = (Position)li.next();
      if (!match(p))
        break loop;
      index += 1;
    }
    endIndex = index;
  }

  boolean match(Position p) {
    Member[] m1 = p.getMembers();
    Member[] m2 = position.getMembers();
    for (int i = 0; i <= memberIndex; i++)
      if (!m1[i].equals(m2[i]))
        return false;
    return true;
  }
  

  int indexOf(Object[] array, Object obj) {
    for (int i = 0; i < array.length; i++)
      if (array[i] == obj)
        return i;
    return -1;
  }

  /**
   * Returns the axis.
   * @return Axis
   */
  public Axis getAxis() {
    return axis;
  }

  /**
   * Returns (index + 1) of the last matching position
   * @return int
   */
  public int getEndIndex() {
    return endIndex;
  }

  /**
   * Returns the member.
   * @return Member
   */
  public Member getMember() {
    return member;
  }

  /**
   * Returns the position.
   * @return Position
   */
  public Position getPosition() {
    return position;
  }

  /**
   * Returns the index of the first matching position
   * @return int
   */
  public int getStartIndex() {
    return startIndex;
  }

  /**
   * Returns the index of member in positions
   * @return int
   */
  public int getMemberIndex() {
    return memberIndex;
  }

}

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

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;


/**
 * contains info about one cell of an table axis.
 */

public class Span implements Cloneable {
  int positionSpan = 1;
  int hierarchySpan = 1;
  int positionIndex;
  int hierarchyIndex;
  boolean significant = true;
  int indent;
  private static final Logger logger = Logger.getLogger(Span.class);

  Axis axis;
  Position position;
  Displayable object;

  public Span(Axis axis, Position position, Displayable object) {
    this.axis = axis;
    this.position = position;
    this.object = object;
  }

  public Span(Displayable object) {
    this.object = object;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("Span[");
    if (object == null)
      sb.append("null ");
    else
      sb.append(object.getLabel());
    sb.append(" positionSpan=").append(positionSpan);
    sb.append(" hierarchySpan=").append(hierarchySpan);
    sb.append(" positionIndex=").append(positionIndex);
    sb.append(" hierarchyIndex=").append(hierarchyIndex);
    sb.append(" significant=").append(significant);
    sb.append(" indent=").append(indent);
    sb.append("]");
    return sb.toString();
  }


  /**
   * initializes this to 1 row and 1 column
   */
  void initialize(int posIndex, int hierIndex) {
    this.positionIndex = posIndex;
    this.hierarchyIndex = hierIndex;
    this.positionSpan = 1;
    this.hierarchySpan = 1;
    this.significant = true;
  }

  /**
   * Returns the hierarchySpan.
   * @return int
   */
  public int getHierarchySpan() {
    return hierarchySpan;
  }

  /**
   * Returns the data of this axis cell. Common types are Member, Level or Hierarchy but other
   * objects are possible too.
   * @return Object
   */
  public Displayable getObject() {
    return object;
  }

  /**
   * Sets the object.
   * @param object The object to set
   */
  public void setObject(Displayable object) {
    this.object = object;
  }

  /**
   * Returns the positionSpan.
   * @return int
   */
  public int getPositionSpan() {
    return positionSpan;
  }

  /**
   * Returns the axis.
   * @return Axis
   */
  public Axis getAxis() {
    return axis;
  }

  /**
   * Returns the position.
   * @return Position
   */
  public Position getPosition() {
    return position;
  }

  /**
   * shorthand for (Member)span.getObject()
   */
  public Member getMember() {
    return (Member)object;
  }

  /**
   * Returns the hierarchyIndex.
   * @return int
   */
  public int getHierarchyIndex() {
    return hierarchyIndex;
  }

  /**
   * Returns the positionIndex.
   * @return int
   */
  public int getPositionIndex() {
    return positionIndex;
  }

  /**
   * true if this span denotes a member. I.e. getObject() instanceof Member
   */
  public boolean isMember() {
    return object instanceof Member;
  }

  /**
   * @see java.lang.Object#clone()
   */
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
			logger.error("exception caught", e);
      throw new RuntimeException(e.toString());
    }
  }

  /**
   * true, if this introduces a new span, i.e. is positioned on the upper left of a multi column or multi row field.
   * @return boolean
   */
  public boolean isSignificant() {
    return significant;
  }

  /**
   * Sets the axis.
   * @param axis The axis to set
   */
  public void setAxis(Axis axis) {
    this.axis = axis;
  }


  /**
   * Sets the position.
   * @param position The position to set
   */
  public void setPosition(Position position) {
    this.position = position;
  }

  /**
   * @return
   */
  public int getIndent() {
    return indent;
  }

  /**
   * @param i
   */
  public void setIndent(int i) {
    indent = i;
  }

}


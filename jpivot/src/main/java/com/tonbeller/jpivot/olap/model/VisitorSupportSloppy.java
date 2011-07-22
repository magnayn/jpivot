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

/**
 * default implementation of Visitor. Does nothing
 *
 * @author av
 */
public abstract class VisitorSupportSloppy implements Visitor {

  public VisitorSupportSloppy() {
    super();
  }

  public void visitAxis(Axis v) {
  }

  public void visitCell(Cell v) {
  }

  public void visitDimension(Dimension v) {
  }

  public void visitHierarchy(Hierarchy v) {
  }

  public void visitLevel(Level v) {
  }

  public void visitMember(Member v) {
  }

  public void visitPosition(Position v) {
  }

  public void visitProperty(Property v) {
  }

  public void visitResult(Result v) {
  }

  public void visitMemberPropertyMeta(MemberPropertyMeta v) {
  }

  public void visitBooleanExpr(BooleanExpr v) {
  }

  public void visitIntegerExpr(IntegerExpr v) {
  }

  public void visitDoubleExpr(DoubleExpr v) {
  }

  public void visitStringExpr(StringExpr v) {
  }

  public void visitFunCallExpr(FunCallExpr v) {
  }

  public void visitParameterExpr(ParameterExpr v) {
  }

  public void visitPropertyExpr(PropertyExpr v) {
  }

  public void visitEmptyMember(EmptyMember v) {
  }

}

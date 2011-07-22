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
 * default implementation of Visitor. Throws an UnsupportedOperationException
 * for each method.
 *
 * @author av
 */
public abstract class VisitorSupportStrict implements Visitor {

  public void visitAxis(Axis v) {
    throw new UnsupportedOperationException();
  }

  public void visitCell(Cell v) {
    throw new UnsupportedOperationException();
  }

  public void visitDimension(Dimension v) {
    throw new UnsupportedOperationException();
  }

  public void visitHierarchy(Hierarchy v) {
    throw new UnsupportedOperationException();
  }

  public void visitLevel(Level v) {
    throw new UnsupportedOperationException();
  }

  public void visitMember(Member v) {
    throw new UnsupportedOperationException();
  }

  public void visitPosition(Position v) {
    throw new UnsupportedOperationException();
  }

  public void visitProperty(Property v) {
    throw new UnsupportedOperationException();
  }

  public void visitResult(Result v) {
    throw new UnsupportedOperationException();
  }

  public void visitMemberPropertyMeta(MemberPropertyMeta v) {
    throw new UnsupportedOperationException();
  }

  public void visitBooleanExpr(BooleanExpr v) {
    throw new UnsupportedOperationException();
  }

  public void visitIntegerExpr(IntegerExpr v) {
    throw new UnsupportedOperationException();
  }

  public void visitDoubleExpr(DoubleExpr v) {
    throw new UnsupportedOperationException();
  }

  public void visitStringExpr(StringExpr v) {
    throw new UnsupportedOperationException();
  }

  public void visitFunCallExpr(FunCallExpr v) {
    throw new UnsupportedOperationException();
  }

  public void visitParameterExpr(ParameterExpr v) {
    throw new UnsupportedOperationException();
  }

  public void visitPropertyExpr(PropertyExpr v) {
    throw new UnsupportedOperationException();
  }

  public void visitEmptyMember(EmptyMember v) {
    throw new UnsupportedOperationException();
  }
}

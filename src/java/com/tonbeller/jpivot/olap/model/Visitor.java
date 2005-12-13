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
 * Created on 29.10.2002
 *
 * @author av
 */
public interface Visitor {
  void visitAxis(Axis v);
  void visitCell(Cell v);
  void visitDimension(Dimension v);
  void visitHierarchy(Hierarchy v);
  void visitLevel(Level v);
  void visitMember(Member v);
  void visitPosition(Position v);
  void visitProperty(Property v);
  void visitResult(Result v);
  void visitMemberPropertyMeta(MemberPropertyMeta v);

  void visitBooleanExpr(BooleanExpr v);
  void visitIntegerExpr(IntegerExpr v);
  void visitDoubleExpr(DoubleExpr v);
  void visitStringExpr(StringExpr v);
  void visitFunCallExpr(FunCallExpr v);
  void visitParameterExpr(ParameterExpr v);
  void visitPropertyExpr(PropertyExpr v);
  void visitEmptyMember(EmptyMember v);

}

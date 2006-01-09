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

package com.tonbeller.jpivot.olap.query;

import mondrian.olap.Exp;

import java.util.List;

import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;

public interface QuaxUti {
  static final int FUNTYPE_FUNCTION = 0;
  static final int FUNTYPE_PROPERTY = 1;
  static final int FUNTYPE_BRACES = 2;
  static final int FUNTYPE_TUPLE = 3;
  static final int FUNTYPE_INFIX = 4;

  /**
   * @param oExp
   * @return true if oExp is a member expression
   */
  boolean isMember(Object oExp);

  /**
   * @param oExp
   * @return true if oExp is a FunCall expression
   */
  boolean isFunCall(Object oExp);

  /**
   * @param oExp
   * @param member
   * @return true if oExp is equal to member
   */
  boolean equalMember(Object oExp, Member member);

  /**
   * @param oExp
   * @param function
   * @return true if oExp is a specific function call
   */
  boolean isFunCallTo(Object oExp, String function);

  /**
    * check, whether member is parent of other member
    * @param pMember (parent)
    * @param cMember (child)
    * @return true if cMember (2.arg) is child of pMember (1.arg)
    */
   boolean checkParent(Member pMember, Object cMembObj);

   /**
    * check, whether member is child of other member
    * @param pMember (child)
    * @param cMember (parent)
    * @return true if cMember (1.arg) is child of pMember (2.arg)
    */
   boolean checkChild(Member cMember, Object pMembObj);

  /**
   * check, whether member is descendant of other member
   * @param aMember (ancestor)
   * @param dMember (descendant)
   * @return true if dMember (2.arg) is descendant of aMember (1.arg)
   */
  boolean checkDescendantM(Member aMember, Member dMember);


  /**
   * check, whether member object is descendant of member
   * @param aMember (ancestor)
   * @param oMember (descendant member object)
   * @return true if 2.arg is descendant of 1.arg
   */
  boolean checkDescendantO(Member aMember, Object oMember);

  /**
   * check, whether funcall set contains member
   * @param f
   * @param m
   * @return true if FunCall contains member
   */
  boolean isMemberInFunCall(Object oExp, Member member) throws Quax.CannotHandleException;

  /**
   * check, whether a funcall set contains any child of a specific member
   * @param oExp - funcall
   * @param member
   * @return true, if FunCall contains member's child
   * @throws Quax.CannotHandleException
   */
  boolean isChildOfMemberInFunCall(Object oExp, Member member) throws Quax.CannotHandleException;

  /**
   * check, whether funcall set contains descendant of a specific member
   * @param f
   * @param m
   * @return true if FunCall contains descendant of member
   */
  boolean isDescendantOfMemberInFunCall(Object oExp, Member member)
    throws Quax.CannotHandleException;

  /**
   * check whether a Funcall does NOT resolve to top level of hierarchy
   * @param oExp - FunCall Exp
   * @return true, if any member of the set defined by funcall is NOT top level
   */
  boolean isFunCallNotTopLevel(Object oExp) throws Quax.CannotHandleException;

  /**
   * check, whether a member is on top level (has no parent);
   * @param m - member to be checked
   * @return true - if member is on top level
   */
  boolean isMemberOnToplevel(Object oMem);

  /**
    * check a Funcall expression whether we can handle it.
    *  currently we can basically handle following FunCalls
    *  member.children, member.descendants, level.members
    */
  boolean canHandle(Object oExp);

  Member getParentMember(Object oExp);
  Member memberForObj(Object oExp);

  Hierarchy hierForMember(Member member);
  Dimension dimForMember(Member member);
  StringBuffer funString(Object oExp);
  String getMemberUniqueName(Object oExp);

  /**
   * Expression Object for member
   * @param member
   * @return Expression Object
   */
  Object objForMember(Member member);

  /**
   * Expression Object for Dimension
   * @param member
   * @return Expression Object
   */
  Object objForDim(Dimension dim);

  String memberString(Member[] mPath);

  /**
    * generate an object for a list of members
    * @param mList list of members
    * @return null for empty lis, single member or set function otherwise
    */
  Object createMemberSet(List mList);


  /**
   * level depth for member
   * @param oExp - member
   * @return depth
   */
  int levelDepthForMember(Object oExp);

  /**
   * @param oExp
   * @return hierarchy for Exp
   * @throws Quax.CannotHandleException
   */
  Hierarchy hierForExp(Object oExp) throws Quax.CannotHandleException;

  /**
   * @param hier - the Hierarchy
   * @param expandAllMember - if true, an "All" member will be expanded
   * @return a set for the top level members of an hierarchy
   */
  Object topLevelMembers(Hierarchy hier, boolean expandAllMember );

  /**
   * generation of FunCalls
   * @param function name
   * @param args arguments
   * @param funType FUNTYPE
   * @return function object
   */
   Object createFunCall(String function, Object[] args, int funType);

  /**
   * get number of funCall arguments
   * @param oFun funcall expression
   * @return number of args
   */
  int funCallArgCount(Object oFun);

  /**
   * get funcall name
   * @param oFun funcall expression
   * @return function name
   */
  String  funCallName (Object oFun);

  /**
   * get funcall argument
   * @param oFun funcall expression
   * @param i - index of argument
   * @return argument object
   */
  Object funCallArg(Object oExp, int index);

  /**
   * determine the children of a member object
   * @param oMember
   * @return
   */
  Object[] getChildren(Object oMember);

  /**
   * get the members of a level
   */
  Object[] getLevelMembers(Level level);

  void addMemberUncles(List list, Member m, int[] maxLevel);
  void addMemberSiblings(List list, Member m, int[] maxLevel);
  void addMemberChildren(List list, Member m, int[] maxLevel);
  void addMemberDescendants(List list, Member m, Level lev, int[] maxLevel);
  void addLevelMembers(List list, Level lev, int[] maxLevel);
  Level LevelForObj(Object oLevel);
  Level getParentLevel(Level lev);

  /**
   * Converts an object to an expression.
   * In particular, adds expression wrappers ({@link mondrian.mdx.MemberExpr},
   * etc.) to olap elements ({@link Member}, etc.).
   *
   * @param o Object, which may or may not be an expression
   * @return An expression
   */
  Exp toExp(Object o);

} // QuaxUti

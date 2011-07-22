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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.util.TreeNode;

/**
 * Generate MDX expresstin for Tree Node
 */
public class ExpGenerator {

  TreeNode rootNode = null;
   int nDimension;
  Hierarchy[] hiers;

  QuaxUti uti;

  /**
   * c'tor
   */
  public ExpGenerator(QuaxUti uti) {
    this.uti = uti;
  }

  /**
   * init
   */
  public void init(TreeNode rootNode, Hierarchy[] hiers) {
    this.rootNode = rootNode;
    this.hiers = hiers;
    this.nDimension = hiers.length;
  }

  /**
   * generate MDX Expression 
   * @param genHierarchize
   * @return
   */
  public Object genExp() {
    Object exp = null;
    List nodes = rootNode.getChildren();

    // single members (nDimension = 1) are enclosed in set brackets
    List openSet = new ArrayList(); // collect single members
    // loop over top level nodes
    NodeLoop: for (Iterator iter = nodes.iterator(); iter.hasNext();) {
      TreeNode node = (TreeNode) iter.next();
      Object expForNode = null;
      expForNode = genExpForNode(node, nDimension);
      boolean closeOpenSet = false;
      if (nDimension == 1) {
        if (uti.isMember(expForNode)) {
          openSet.add(expForNode);
          continue NodeLoop;
        } else
          closeOpenSet = true;
      } else {
        if (uti.isFunCallTo(expForNode, "()")) {
          openSet.add(expForNode);
          continue NodeLoop;
        } else
          closeOpenSet = true;
      }

      if (closeOpenSet && openSet.size() > 0) {
        // close open set
        Object[] expArray = openSet.toArray(new Object[0]);
        Object set = uti.createFunCall("{}", expArray, QuaxUti.FUNTYPE_BRACES);
        if (exp == null) {
          exp = set;
        } else {
          // generate Union
          exp = uti.createFunCall("Union", new Object[] { exp, set }, QuaxUti.FUNTYPE_FUNCTION);
        }
        openSet.clear();
      }

      if (exp == null) {
        exp = expForNode;
      } else {
        // generate Union of Exp and expForNode
        exp = uti
            .createFunCall("Union", new Object[] { exp, expForNode }, QuaxUti.FUNTYPE_FUNCTION);
      }
    }
    if (openSet.size() > 0) {
      // close open set
      Object[] expArray = openSet.toArray(new Object[0]);
      Object set = uti.createFunCall("{}", expArray, QuaxUti.FUNTYPE_BRACES);
      if (exp == null) {
        exp = set;
      } else {
        // generate Union
        exp = uti.createFunCall("Union", new Object[] { exp, set }, QuaxUti.FUNTYPE_FUNCTION);
      }
      openSet.clear();
    }

    return exp;
  }

  /**
   * recursively generate Exp for a node
   * 
   * @param node
   * @return
   */
  private Object genExpForNode(TreeNode node, int untilIndex) {
    Object eNode = node.getReference();
    if (node.getLevel() == untilIndex)
      return eNode; // last dimension

    // use tuple representation if possible
    Object[] tuple = genTuple(node);
    if (tuple != null) {
      if (tuple.length == 1)
        return tuple[0];
      else
        return uti.createFunCall("()", tuple, QuaxUti.FUNTYPE_TUPLE);
    }

    // generate CrossJoin
    Object exp = null;
    List childNodes = node.getChildren();
    for (Iterator iter = childNodes.iterator(); iter.hasNext();) {
      TreeNode childNode = (TreeNode) iter.next();
      Object childExp = genExpForNode(childNode, untilIndex);

      Object eSet;
      if (!uti.isMember(eNode)) {
        // FunCall
        eSet = eNode;
      } else {
        // member
        eSet = uti.createFunCall("{}", new Object[] { eNode }, QuaxUti.FUNTYPE_BRACES);
      }
      if (childExp == null) {
        exp = eSet;
      } else {
        Object childSet = bracesAround(childExp);
        Object cj = uti.createFunCall("CrossJoin", new Object[] { eSet, childSet },
          QuaxUti.FUNTYPE_FUNCTION);
        if (exp == null)
          exp = cj;
        else {
          exp = uti.createFunCall("Union", new Object[] { exp, cj }, QuaxUti.FUNTYPE_FUNCTION);
        }
      }
    }
    return exp;
  }

  /**
   * generate Union object
   * 
   * @param oExps
   * @return
   */
  private Object genUnion(Object[] oExps) {
    if (oExps.length == 1)
      return oExps[0];
    Object oUnion = uti.createFunCall("Union", new Object[] { oExps[0], oExps[1] },
        QuaxUti.FUNTYPE_FUNCTION);
    for (int i = 2; i < oExps.length; i++)
      oUnion = uti.createFunCall("Union", new Object[] { oUnion, oExps[i] },
          QuaxUti.FUNTYPE_FUNCTION);
    return oUnion;
  }

  /**
   * put braces around single member or single tuple
   * 
   * @param oExp
   * @return set exp
   */
  private Object bracesAround(Object oExp) {
    Object oSet;
    if (uti.isMember(oExp) || uti.isFunCallTo(oExp, "()"))
      oSet = uti.createFunCall("{}", new Object[] { oExp }, QuaxUti.FUNTYPE_BRACES);
    else
      oSet = oExp;
    return oSet;
  }

  /**
   * generate Tuple Exp
   * 
   * @param node
   * @return
   */
  private Object[] genTuple(TreeNode node) {
    if (!uti.isMember(node.getReference()))
      return null;
    int size = nDimension - node.getLevel() + 1;
    if (size == 1) {
      return new Object[] { node.getReference() }; // single member
    }
    List childNodes = node.getChildren();
    if (childNodes.size() != 1)
      return null;
    Object[] nextTuple = genTuple((TreeNode) childNodes.get(0));
    if (nextTuple == null)
      return null;
    Object[] tupleMembers = new Object[size];
    tupleMembers[0] = node.getReference();
    for (int i = 1; i < tupleMembers.length; i++) {
      tupleMembers[i] = nextTuple[i - 1];
    }
    return tupleMembers;
  }

} // ExpGenerator

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

package com.tonbeller.jpivot.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tree Node for the for a general tree of Objects
 */
public class TreeNode {

  private TreeNode parent = null;
  private List children = null;
  private Object reference;

  /**
   * cTtor
   * @param obj referenced object
   */
  public TreeNode(Object obj) {
    this.parent = null;
    this.reference = obj;
    this.children = new ArrayList();
  }

  /**
   * remove node from tree
   */
  public void remove() {
    if (parent != null) {
      parent.removeChild(this);
    }
  }

  /**
   * remove child node
   * @param child
   */
  private void removeChild(TreeNode child) {
    if (children.contains(child))
      children.remove(child);

  }

  /**
   * add child node
   * @param child node to be added
   */
  public void addChildNode(TreeNode child) {
    child.parent = this;
    if (!children.contains(child))
      children.add(child);
  }

  /**
   * deep copy (clone)
   * @return copy of TreeNode
   */
  public TreeNode deepCopy() {
    TreeNode newNode = new TreeNode(reference);
    for (Iterator iter = children.iterator(); iter.hasNext();) {
      TreeNode child = (TreeNode) iter.next();
      newNode.addChildNode(child.deepCopy());
    }
    return newNode;
  }

  /**
   * deep copy (clone) and prune 
   * @param depth - number of child levels to be copied
   * @return copy of TreeNode
   */
  public TreeNode deepCopyPrune(int depth) {
    if (depth < 0)
      throw new IllegalArgumentException("Depth is negative");
    TreeNode newNode = new TreeNode(reference);
    if (depth == 0)
      return newNode;
    for (Iterator iter = children.iterator(); iter.hasNext();) {
      TreeNode child = (TreeNode) iter.next();
      newNode.addChildNode(child.deepCopyPrune(depth - 1));
    }
    return newNode;
  }

  /**
   * @return level = distance from root
   */
  public int getLevel() {
    int level = 0;
    TreeNode p = parent;
    while (p != null) {
      ++level;
      p = p.parent;
    }
    return level;
  }

  /**
   * walk through subtree of this node
   * @param callbackHandler function called on iteration 
   */
  public int walkTree(TreeNodeCallback callbackHandler) {
    int code = 0;
    code = callbackHandler.handleTreeNode(this);
    if (code != TreeNodeCallback.CONTINUE)
      return code;
    ChildLoop: for (Iterator iter = children.iterator(); iter.hasNext();) {
      TreeNode child = (TreeNode) iter.next();
      code = child.walkTree(callbackHandler);
      if (code >= TreeNodeCallback.CONTINUE_PARENT)
        return code;
    }
    return code;
  }

  /**
   * walk through children subtrees of this node
   * @param callbackHandler function called on iteration 
   */
  public int walkChildren(TreeNodeCallback callbackHandler) {
    int code = 0;
    ChildLoop: for (Iterator iter = children.iterator(); iter.hasNext();) {
      TreeNode child = (TreeNode) iter.next();
      code = callbackHandler.handleTreeNode(child);
      if (code >= TreeNodeCallback.CONTINUE_PARENT)
        return code;
      if (code == TreeNodeCallback.CONTINUE) {
        code = child.walkChildren(callbackHandler);
        if (code > TreeNodeCallback.CONTINUE_PARENT)
          return code;
      }
    }
    return code;
  }

  /**
   * @return List of children
   */
  public List getChildren() {
    return children;
  }

  /**
   * @return parent node
   */
  public TreeNode getParent() {
    return parent;
  }

  /**
   * @return reference object
   */
  public Object getReference() {
    return reference;
  }

  /**
   * set reference object
   * @param object reference
   */
  public void setReference(Object object) {
    reference = object;
  }

} // TreeNode

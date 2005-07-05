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

import java.util.List;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.core.ModelSupport;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.util.TreeNode;

/**
 * Olap Model based on MDX
 */
public abstract class MdxOlapModel extends ModelSupport {

  static Logger logger = Logger.getLogger(MdxOlapModel.class);

  /**
   * @return the current MDX statement
   */
  public abstract String getCurrentMdx();

  /**
   * restore quaxes from QuaxBeans
   * @param quaxes
   * @param quaxBeans
   * @throws OlapException
   */
  protected void quaxesFromBeans(Quax[] quaxes, QuaxBean[] quaxBeans) throws OlapException {
    for (int i = 0; i < quaxes.length; i++) {
      boolean qubonMode = quaxBeans[i].isQubonMode();

      quaxes[i].setQubonMode(qubonMode);

      quaxes[i].setGenerateIndex(quaxBeans[i].getGenerateIndex());
      quaxes[i].setGenerateMode(quaxBeans[i].getGenerateMode());

      quaxes[i].setNHierExclude(quaxBeans[i].getNHierExclude());

      // handle Position Tree
      PositionNodeBean rootBean = quaxBeans[i].getPosTreeRoot();

      TreeNode rootNode = createPosTreeFromBean(rootBean);
      quaxes[i].setPosTreeRoot(rootNode, true);
      quaxes[i].changed(this, false);

      quaxes[i].setHierarchizeNeeded(quaxBeans[i].isHierarchizeNeeded());

    } // for i (quaxes)
  }

  /**
   * restore sort settings from memento
   * @param sortExt
   * @param memento
   */
  protected void restoreSort(SortRankBase sortExt, Memento memento) {
    HandleSort: if (sortExt != null) {
      String[] sortPosUniqueNames = memento.getSortPosMembers();
      if (sortPosUniqueNames != null) {
        Member[] members = new Member[sortPosUniqueNames.length];
        for (int i = 0; i < members.length; i++) {
          Member m = lookupMemberByUName(sortPosUniqueNames[i]);
          if (m == null) {
            logger.warn("sort position member not found " + sortPosUniqueNames[i]);
            break HandleSort;
          }
          members[i] = m;
        }
        sortExt.setSortPosMembers(members);
        sortExt.setQuaxToSort(memento.getQuaxToSort());
        sortExt.setSortMode(memento.getSortMode());
        sortExt.setSorting(memento.isSorting());
        sortExt.setTopBottomCount(memento.getTopBottomCount());
      }
    }
  }

  /**
   *
   * @param rootBean
   * @return
   */
  private TreeNode createPosTreeFromBean(PositionNodeBean rootBean) throws OlapException {
    ExpBean expBean = rootBean.getReference(); // null for root
    Object exp;
    if (expBean == null)
      exp = null;
    else
      exp = createExpFromBean(expBean);
    TreeNode node = new TreeNode(exp);
    PositionNodeBean[] beanChildren = rootBean.getChildren();
    if(beanChildren == null)
      return node; // empty result
    for (int i = 0; i < beanChildren.length; i++) {
      TreeNode childNode = createPosTreeFromBean(beanChildren[i]);
      node.addChildNode(childNode);
    }
    return node;
  }

  /**
   * populate quax bean from quax
   * @param quaxBean
   * @param quax
   * @param createExpBean - static method to be called
   * @throws OlapException 
   */
  protected void beanFromQuax(QuaxBean quaxBean, Quax quax) throws OlapException {

    quaxBean.setOrdinal(quax.getOrdinal());
    quaxBean.setNDimension(quax.getNDimension());
    boolean qubonMode = quax.isQubonMode();
    quaxBean.setQubonMode(qubonMode);
    PositionNodeBean posTreeRoot = createPosTreeBean(quax.getPosTreeRoot(), null);
    quaxBean.setPosTreeRoot(posTreeRoot);

    quaxBean.setHierarchizeNeeded(quax.isHierarchizeNeeded());
    quaxBean.setGenerateIndex(quax.getGenerateIndex());
    quaxBean.setGenerateMode(quax.getGenerateMode());
    quaxBean.setNHierExclude(quax.getNHierExclude());
  }

  /**
   * 
   * @param node
   * @param createExpBean - static method to be called
   * @return
   * @throws OlapException 
   */
  PositionNodeBean createPosTreeBean(TreeNode node, ExpBean reference) throws OlapException {
    PositionNodeBean bean = new PositionNodeBean();
    bean.setReference(reference);
    if (node == null) {
      // possible, PosTreeRoot = null for empty axis
      bean.setChildren( new PositionNodeBean[0]);
      return bean;
    }
    List childrenList = node.getChildren();
    PositionNodeBean[] children = new PositionNodeBean[childrenList.size()];
    for (int i = 0; i < children.length; i++) {
      TreeNode childNode = (TreeNode) childrenList.get(i);
      ExpBean refBean = this.createBeanFromExp(childNode.getReference());
      children[i] = createPosTreeBean(childNode, refBean);
    }
    bean.setChildren(children);
    return bean;
  }

  /**
   * store sort parameters to memento
   * @param sortExt
   * @param memento
   */
  protected void storeSort(SortRankBase sortExt, Memento memento) {
    Member[] sortPosMembers = sortExt.getSortPosMembers();
    if (sortPosMembers != null) {
      String[] uniqueNames = new String[sortPosMembers.length];
      for (int i = 0; i < uniqueNames.length; i++) {
        uniqueNames[i] = ((MDXMember) sortPosMembers[i]).getUniqueName();
      }
      memento.setSortPosMembers(uniqueNames);
      memento.setQuaxToSort(sortExt.getQuaxToSort());
      memento.setTopBottomCount(sortExt.getTopBottomCount());
      memento.setSortMode(sortExt.getSortMode());
      memento.setSorting(sortExt.isSorting());
    }
  }

  protected abstract Object createExpFromBean(ExpBean expBean) throws OlapException;
  protected abstract ExpBean createBeanFromExp(Object exp) throws OlapException;

  public abstract Member lookupMemberByUName(String uniqueName);

} // MdxOlapModel

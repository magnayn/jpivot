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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.navi.CalcSet;
import com.tonbeller.jpivot.util.JPivotException;
import com.tonbeller.jpivot.util.TreeNode;
import com.tonbeller.jpivot.util.TreeNodeCallback;

public class Quax {

  static Logger logger = Logger.getLogger(Quax.class);

  protected int nDimension;

  private Hierarchy[] hiers;

  //  currently, we can handle the following Funcalls
  //  member.children, member.descendants, level.members
  // other funcalls are "unknown functions"
  private boolean[] containsUF;
  private List[] ufMemberLists; // if there are unknonwn functions
  // private UnknownFunction[] unknownFunctions;
  protected TreeNode posTreeRoot = null; // Position tree used in normal mode

  private int ordinal; // ordinal of query axis, never changed by swap
  private boolean qubonMode = false;
  private boolean hierarchizeNeeded = false;

  // if there are multiple hiers on this quax,
  //  "nHierExclude" hierarchies (from right to left)
  //  will *not* be included to the Hierarchize Function.
  // So MDX like
  // Crossjoin(Hierarchize(Dim1.A + Dim1.A.Children), {Measures.A.
  // Measures.B})
  // will be generated, so that the Measures are excluded from Hierarchize.
  private int nHierExclude = 0;
  private int generateMode = 0;
  private int generateIndex = -1; // we handle generate for only 1 dimension
  private Object expGenerate = null;
  private Collection changeListeners = new ArrayList();
  private QuaxUti uti;
  private Map canExpandMemberMap = new HashMap();
  private Map canExpandPosMap = new HashMap();
  private Map canCollapseMemberMap = new HashMap();
  private Map canCollapsePosMap = new HashMap();

  /**
   * c'tor
   *
   * @param ordinal
   */
  public Quax(int ordinal) {
    this.ordinal = ordinal;
    qubonMode = false;
  }

  /**
   * register change listener
   *
   * @param listener
   */
  public void addChangeListener(QuaxChangeListener listener) {
    changeListeners.add(listener);
  }

  /**
   * unregister change listener
   *
   * @param listener
   */
  public void removeChangeListener(QuaxChangeListener listener) {
    changeListeners.remove(listener);
  }

  /**
   * handle change
   *
   * @param source
   *          Originator of the quax change
   * @param changedMemberSet
   *          true if the memberset was changed by the navigator
   */
  public void changed(Object source, boolean changedMemberSet) {
    for (Iterator iter = changeListeners.iterator(); iter.hasNext();) {
      QuaxChangeListener listener = (QuaxChangeListener) iter.next();
      listener.quaxChanged(this, source, changedMemberSet);
    }
    canExpandMemberMap.clear();
    canExpandPosMap.clear();
    canCollapseMemberMap.clear();
    canCollapsePosMap.clear();

  }

  /**
   * Initialize quax from result positions
   *
   * @param positions
   */
  public void init(List positions) {
    Member[][] aPosMem;
    int nDimension = 0;
    hierarchizeNeeded = false;
    nHierExclude = 0;
    qubonMode = true;

    if (positions.size() == 0) {
      // the axis does not have any positions
      aPosMem = new Member[0][0];
      setHiers(new Hierarchy[0]);
      setHiers(hiers);
      return;
    } else {
      nDimension = ((Position) positions.get(0)).getMembers().length;
      aPosMem = new Member[positions.size()][nDimension];

      int j = 0;
      PositionLoop: for (Iterator iter = positions.iterator(); iter.hasNext();) {
        Position pos = (Position) iter.next();
        aPosMem[j++] = pos.getMembers();
      }
    }
    Hierarchy[] hiers = new Hierarchy[nDimension];
    for (int j = 0; j < hiers.length; j++) {
      Member m = aPosMem[0][j];
      hiers[j] = m.getLevel().getHierarchy();
    }
    setHiers(hiers);
    initPositions(aPosMem);

    // initialize the dimension flags
    // if there is only one set node per dimension,
    //  we are in qubon mode
    posTreeRoot.walkTree(new TreeNodeCallback() {

      /**
       * callback check qubon mode
       */
      public int handleTreeNode(TreeNode node) {
        int iDim = node.getLevel();

        if (iDim == Quax.this.nDimension)
          return TreeNodeCallback.BREAK; // bottom reached

        if (node.getChildren().size() == 1) {
          return TreeNodeCallback.CONTINUE; // continue next level
        } else {
          // more than one child - break out
          Quax.this.qubonMode = false;
          return TreeNodeCallback.BREAK;
        }
      }
    });

    if (qubonMode)
      nHierExclude = nDimension - 1; // nothing hierarchized

  }

  /**
   * Initialize position member arrays after first result gotten
   *
   * @param aPosMemStart
   */
  private void initPositions(Member[][] aPosMemStart) {
    // no positions - no tree
    if (aPosMemStart.length == 0) {
      posTreeRoot = null;
      return;
    }

    // before the position tree is created,
    //  we want to hierarchize
    /*
     * if (nDimension > 1) hierarchizePositions(aPosMemStart);
     */

    // init position tree
    posTreeRoot = new TreeNode(null); // root
    int iEnd = addToPosTree(aPosMemStart, 0, aPosMemStart.length, 0, posTreeRoot);
    while (iEnd < aPosMemStart.length) {
      iEnd = addToPosTree(aPosMemStart, iEnd, aPosMemStart.length, 0, posTreeRoot);
    }

    // try to factor out the members of the last dimension
    posTreeRoot.walkTree(new TreeNodeCallback() {

      /**
       * callback create member set for last dimension
       */
      public int handleTreeNode(TreeNode node) {
        int iDim1 = node.getLevel();

        if (iDim1 == Quax.this.nDimension - 1) {
          if (node.getChildren().size() <= 1)
            return TreeNodeCallback.CONTINUE_SIBLING; // continue
          // next
          // sibling
          // more than one child in last dimension
          // create a single set function node
          Object[] memArray = new Object[node.getChildren().size()];
          int i = 0;
          for (Iterator iter = node.getChildren().iterator(); iter.hasNext();) {
            TreeNode child = (TreeNode) iter.next();
            memArray[i++] = child.getReference();
          }
          node.getChildren().clear();
          Object oFun = uti.createFunCall("{}", memArray, QuaxUti.FUNTYPE_BRACES);
          TreeNode newChild = new TreeNode(oFun);
          node.addChildNode(newChild);
          return TreeNodeCallback.CONTINUE_SIBLING; // continue next
          // sibling
        }
        return TreeNodeCallback.CONTINUE;
      }
    });

    containsUF = new boolean[nDimension]; // init false
    ufMemberLists = new List[nDimension];

    if (logger.isDebugEnabled())
      logger.debug("after initPositions " + this.toString());
  }

  /**
   * add members of dimension to tree recursively
   *
   * @param aPosMem
   *          positon member array
   * @param iStartPos
   *          start position for this dimension
   * @param iEndPos
   *          start position for this dimension
   * @param iDim
   *          index of this dimension
   * @param parentNode
   *          parent node (previous dimension)
   * @return index of position where the member of this dimension changes
   */
  protected int addToPosTree(Member[][] aPosMem, int iStartPos, int iEndPos, int iDim,
      TreeNode parentNode) {
    Member currentOfDim = aPosMem[iStartPos][iDim];
    Object o = uti.objForMember(currentOfDim);
    TreeNode newNode = new TreeNode(o);
    parentNode.addChildNode(newNode);

    // check range where member of this dimension is constant
    int iEndRange = iStartPos + 1;
    for (; iEndRange < iEndPos; iEndRange++) {
      if (aPosMem[iEndRange][iDim] != aPosMem[iStartPos][iDim])
        break;
    }
    int nextDim = iDim + 1;
    if (nextDim < nDimension) {
      int iEndChild = addToPosTree(aPosMem, iStartPos, iEndRange, nextDim, newNode);
      while (iEndChild < iEndRange) {
        iEndChild = addToPosTree(aPosMem, iEndChild, iEndRange, nextDim, newNode);
      }
    }
    return iEndRange;
  }

  /**
   * find out, whether axis contains dimension
   *
   * @param dim
   * @return index of dimension, -1 if not there
   */
  public int dimIdx(Dimension dim) {
    if (hiers == null || hiers.length == 0)
      return -1; // quax was not initialized yet
    for (int i = 0; i < hiers.length; i++) {
      if (hiers[i].getDimension().equals(dim))
        return i;
    }
    return -1;
  }

  /**
   * regenerate the position tree as crossjoin between sets
   *
   * @param hiersChanged
   *          indicates that the hierarchies were changed
   */
  public void regeneratePosTree(Object[] sets, boolean hiersChanged) {
    if (hiersChanged) {
      nDimension = sets.length;
      hiers = new Hierarchy[nDimension];
      for (int i = 0; i < nDimension; i++) {
        try {
          hiers[i] = uti.hierForExp(sets[i]);
        } catch (CannotHandleException e) {
          logger.fatal("could not determine Hierarchy for set");
          logger.fatal(e);
          throw new IllegalArgumentException(e.getMessage());
        }
      }

      containsUF = new boolean[nDimension]; // init false
      ufMemberLists = new List[nDimension];
      generateIndex = 0;
      generateMode = 0;
    }
    if (posTreeRoot == null)
      return;
    posTreeRoot.getChildren().clear();
    TreeNode current = posTreeRoot;
    // it would be fine, if we could get rid of an existing Hierarchize
    // - but this is not easy to decide.
    // we will not do it, if there is a "children" function call
    //  not on the highest Level. This indicates that we have drilled
    //  down any member.
    nHierExclude = 0;
    int nChildrenFound = 0;
    boolean childrenFound = false;
    for (int i = 0; i < nDimension; i++) {
      TreeNode newNode;
      if (sets[i] instanceof SetExp) {
        SetExp setx = (SetExp) sets[i];
        newNode = new TreeNode(setx.getOExp());
        int mode = setx.getMode();
        if (mode > 0) {
          generateMode = mode;
          generateIndex = i;
          expGenerate = setx.getOExp();
        }
      } else {
        // can we remove an existing "hierarchize needed"?
        boolean bChildrenFound = findChildrenCall(sets[i], 0);
        if (bChildrenFound) {
          childrenFound = true;
          nChildrenFound = i + 1;
        }

        newNode = new TreeNode(sets[i]);
        if (generateIndex == i && generateMode == CalcSet.STICKY) {
          // there was a sticky generate on this hier
          //  reset, if set expression is different now
          if (!sets[i].equals(expGenerate))
            resetGenerate();
        }
      }
      current.addChildNode(newNode);
      current = newNode;
      if (!uti.canHandle(newNode.getReference())) {
        // indicate that dimension i contains an unknown function,
        //  which cannot be handled in some cases.
        // this will cause the member list of this dimension to be stored
        containsUF[i] = true;
      }
    }
    qubonMode = true;
    nHierExclude = nDimension - nChildrenFound;

    if (!childrenFound)
      hierarchizeNeeded = false;
  }

  /**
   * recursively find "children" Funcall
   */
  private boolean findChildrenCall(Object oExp, int level) {
    if (!uti.isFunCall(oExp))
      return false; // member or level or ...
    if (level > 0 && uti.isFunCallTo(oExp, "children"))
      return true;
    int nArgs = uti.funCallArgCount(oExp);
    for (int i = 0; i < nArgs; i++) {
      if (findChildrenCall(uti.funCallArg(oExp, i), level + 1))
        return true;
    }
    return false;
  }

  // ==========
  // Expand
  // ==========

  /**
   * check, whether a member in a specific position path can be expanded
   *
   * @param pathMembers
   *          position path to be expanded
   */
  public boolean canExpand(Member[] pathMembers) {
    int iDim = pathMembers.length - 1;

    // we only allow expand / collapse for a dimension
    //  left of a "sticky topcount"
    if (!allowNavigate(iDim, false))
      return false;

    // first check the cache
    List li = Arrays.asList(pathMembers);
    if (canExpandPosMap.containsKey(li)) {
      Boolean bCanExpand = (Boolean) canExpandPosMap.get(li);
      return bCanExpand.booleanValue();
    }

    // loop over Position Tree
    //  reject expansion, if the axis already contains child-positions
    boolean childFound = checkChildPosition(pathMembers);

    // cache the result
    Boolean bool = new Boolean(!childFound);
    canExpandPosMap.put(li, bool);

    return !childFound;
  }

  /**
   * expand position path
   *
   * @param mPath
   */
  public void expand(Member[] mPath) {

    if (qubonMode) {
      resolveUnions();
      if (logger.isDebugEnabled()) {
        logger.debug("expand after resolveUnions " + this.toString());
      }
    }

    int iDim = mPath.length - 1;

    // update the position member tree
    //  assume mPath = (Product.Drink,Time.2003,Customers.USA)
    //  1. find the node N1 for (Product.Drink,Time.2003)
    //  2. add the child node Customers.USA.Children to the node N1
    //
    // if the node N1 for (Product.Drink,Time.2003) was not found:
    // we look for a matching node and find for instance
    // node N2 = (Product.AllProducts.Children,Time.2003)
    // here, we cannot append Customers.USA.Children as a child node.
    // we add a new branch
    // (Product.Drink,Time.2003,Customers.USA.Children) to the tree.

    TreeNode bestNode = findBestNode(mPath);
    int bestNodeIndex = bestNode.getLevel() - 1;

    // add branch at startNode
    // example
    // dimensions: Product,MaritalStatus,Gender,Customer
    // mPath to Drill Down = (Product.AllProducts, MaritalStatus.M,
    // Gender.AllGender)
    // MaritalStatus.AllMaritalStatus was drilled down so best match is
    // (Product.AllProducts)
    // add the branch from MaritalStatus to this node giving
    // (Product.AllProducts,MaritalStatus.M,Gender.AllGender.children)
    // for the Customer Dimension, add all nodes matching
    // (Product.AllProducts, MaritalStatus.M, Gender.AllGender, * )

    List tailNodeList;
    if (mPath.length < nDimension) {
      tailNodeList = collectTailNodes(posTreeRoot, mPath);
    } else {
      tailNodeList = Collections.EMPTY_LIST;
    }

    TreeNode newNode;
    Object oMember = uti.objForMember(mPath[iDim]);
    Object fChildren = uti.createFunCall("Children", new Object[] { oMember},
        QuaxUti.FUNTYPE_PROPERTY);
    TreeNode parent = bestNode;

    // if bestNode is matching mPath[iDim]
    //  we will add the children Funcall to its parent
    // otherwise create path from bestNode to mPath[iDim-1] and
    //  add the children FunCall there
    if (bestNodeIndex == iDim) {
      parent = bestNode.getParent();
    } else {
      for (int i = bestNodeIndex + 1; i < mPath.length - 1; i++) {
        oMember = uti.objForMember(mPath[i]);
        newNode = new TreeNode(oMember);
        parent.addChildNode(newNode);
        parent = newNode;
      }
    }

    // any dimension left and including iDim will *not* be excluded from
    // hierarchize
    int n = nDimension - iDim - 1;
    if (n < nHierExclude)
      nHierExclude = n;

    newNode = new TreeNode(fChildren);
    parent.addChildNode(newNode);
    if (mPath.length < nDimension) {
      for (Iterator iter = tailNodeList.iterator(); iter.hasNext();) {
        TreeNode tailNode = (TreeNode) iter.next();
        newNode.addChildNode(tailNode.deepCopy());
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("after expand " + this.toString());
    }

    qubonMode = false;
    hierarchizeNeeded = true;
    changed(this, false);
  }

  /**
   * check, whether a member can be expanded
   *
   * @param member
   *          member to be expanded
   */
  public boolean canExpand(Member member) {

    // we only allow expand / collapse for a dimension
    //  left of a "sticky topcount"
    if (!allowNavigate(member, false))
      return false;

    // first check the cache
    if (canExpandMemberMap.containsKey(member)) {
      Boolean bCanExpand = (Boolean) canExpandMemberMap.get(member);
      return bCanExpand.booleanValue();
    }

    // loop over Position Tree
    //  reject expansion, if the axis already contains children of member
    boolean b = !findMemberChild(member);

    // cache the result
    Boolean bool = new Boolean(b);
    canExpandMemberMap.put(member, bool);

    return b;
  }

  /**
   * expand member all over position tree
   *
   * @param member
   */
  public void expand(final Member member) {

    if (qubonMode) {
      resolveUnions();
      if (logger.isDebugEnabled()) {
        logger.debug("expand after resolveUnions " + this.toString());
      }
    }

    // old stuff, always hierarchize everything
    nHierExclude = 0;

    final int iDim = this.dimIdx(uti.dimForMember(member));
    final List nodesForMember = new ArrayList();

    // update the position member tree
    //  wherever we find monMember, expand it
    //  collect all nodes for monMember in workList
    posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback find node matching member Path exactly
       */
      public int handleTreeNode(TreeNode node) {
        int iDimNode = node.getLevel() - 1;
        if (iDimNode < iDim)
          return TreeNodeCallback.CONTINUE; // we are below iDim,
        // don't care

        // iDimNode == iDim
        //  node Exp must contain children of member[iDim]
        Object oExp = node.getReference();
        if (uti.isMember(oExp)) {
          if (uti.equalMember(oExp, member))
            nodesForMember.add(node);
        } else {
          // must be FunCall
          if (isMemberInFunCall(oExp, member, iDim))
            nodesForMember.add(node);
        }
        return TreeNodeCallback.CONTINUE_SIBLING; // continue next
        // sibling
      }
    });

    // add children of member to each node in list
    Object oMember = uti.objForMember(member);
    Object fChildren = uti.createFunCall("Children", new Object[] { oMember},
        QuaxUti.FUNTYPE_PROPERTY);
    for (Iterator iter = nodesForMember.iterator(); iter.hasNext();) {
      TreeNode node = (TreeNode) iter.next();
      TreeNode newNode = new TreeNode(fChildren);
      for (Iterator iterator = node.getChildren().iterator(); iterator.hasNext();) {
        TreeNode child = (TreeNode) iterator.next();
        newNode.addChildNode(child.deepCopy());
      }
      TreeNode parent = node.getParent();
      parent.addChildNode(newNode);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("after expand member " + this.toString());
    }

    hierarchizeNeeded = true;
    changed(this, false);
  }

  // ==========
  // Collapse
  // ==========

  /**
   * check, whether a member path can be collapsed this is true if there is a child position path
   *
   * @param pathMembers
   *          position path to be collapsed
   */
  public boolean canCollapse(Member[] pathMembers) {

    int iDim = pathMembers.length - 1;

    // we only allow expand / collapse for a dimension
    //  left of a "sticky topcount"
    if (!allowNavigate(iDim, false))
      return false;

    // first check the cache
    List li = Arrays.asList(pathMembers);
    if (canCollapsePosMap.containsKey(li)) {
      Boolean bCanCollapse = (Boolean) canCollapsePosMap.get(li);
      return bCanCollapse.booleanValue();
    }

    // loop over Position Tree
    //  collapse is possible, if the axis already contains child-positions
    boolean childFound = checkChildPosition(pathMembers);

    // cache the result
    Boolean bool = new Boolean(childFound);
    canCollapsePosMap.put(li, bool);

    return childFound;
  }

  /**
   * remove child positions of mPath from position tree
   *
   * @param mPath
   *          member path to be collapsed
   */
  public void collapse(final Member[] mPath) {

    if (qubonMode) {
      resolveUnions();
      if (logger.isDebugEnabled()) {
        logger.debug("collapse after resolveUnions " + this.toString());
      }
    }

    final int iDim = mPath.length - 1;

    // determine FunCall nodes to be split
    final List[] splitLists = new List[mPath.length];
    for (int i = 0; i < splitLists.length; i++) {
      splitLists[i] = new ArrayList();
    }

    posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback Find child paths of member path. Collect FunCall nodes above in List. We have a
       * list for any dimension, so that we can avoid dependency conflicts when we split the
       * FunCalls.
       */
      public int handleTreeNode(TreeNode node) {
        // check, whether this node matches mPath
        Object oExp = node.getReference();
        int idi = node.getLevel() - 1;
        if (idi < iDim) {
          if (uti.isMember(oExp)) {
            if (uti.equalMember(oExp, mPath[idi]))
              return TreeNodeCallback.CONTINUE;
            else
              return TreeNodeCallback.CONTINUE_SIBLING;
          } else {
            // Funcall
            if (isMemberInFunCall(oExp, mPath[idi], idi))
              return TreeNodeCallback.CONTINUE;
            else
              return TreeNodeCallback.CONTINUE_SIBLING;
          }
        }
        //idi == iDim
        // oExp *must* be descendant of mPath[iDim] to get deleted
        boolean found = false;
        if (uti.isMember(oExp)) {
          // Member
          if (uti.checkDescendantO(mPath[iDim], oExp)) {
            found = true;
          }
        } else {
          // FunCall
          if (isChildOfMemberInFunCall(oExp, mPath[iDim], iDim))
            found = true;
        }

        if (found) {
          // add this node and all parent nodes, if they are funcalls,
          // to split list
          int level = node.getLevel();
          TreeNode currentNode = node;
          while (level > 0) {
            Object o = currentNode.getReference();
            if (!uti.isMember(o)) {
              // Funcall
              if (!splitLists[level - 1].contains(currentNode))
                splitLists[level - 1].add(currentNode);
            }
            currentNode = currentNode.getParent();
            level = currentNode.getLevel();
          }
        }
        return TreeNodeCallback.CONTINUE_SIBLING;
      } // handleTreeNode
    });

    // split all FunCall nodes collected in worklist
    //  start with higher levels to avoid dependency conflicts
    for (int i = splitLists.length - 1; i >= 0; i--) {
      for (Iterator iter = splitLists[i].iterator(); iter.hasNext();) {
        TreeNode n = (TreeNode) iter.next();
        splitFunCall(n, mPath[i], i);
      }
    }

    // remove child Paths of mPath from position tree
    //  collect nodes to be deleted
    final List removeList = new ArrayList();
    posTreeRoot.walkChildren(new TreeNodeCallback() {
      /**
       * callback remove child nodes of member path, first collect nodes in workList
       */
      public int handleTreeNode(TreeNode node) {
        // check, whether this node matches mPath
        Object oExp = node.getReference();
        int idi = node.getLevel() - 1;
        if (idi < iDim) {
          if (uti.isMember(oExp)) {
            if (uti.equalMember(oExp, mPath[idi]))
              return TreeNodeCallback.CONTINUE;
            else
              return TreeNodeCallback.CONTINUE_SIBLING;
          } else {
            // FunCall
            // cannot match as we just did the split of FunCalls
            return TreeNodeCallback.CONTINUE_SIBLING;
          }
        } else if (idi == iDim) {
          // *must* be descendant of mPath[iDim] to get deleted
          if (!uti.isMember(oExp)) {
            // FunCall
            if (uti.isFunCallTo(oExp, "Children")) {
              Object oMember = uti.funCallArg(oExp, 0);
              if (uti.objForMember(mPath[iDim]).equals(oMember)
                  || uti.checkDescendantO(mPath[iDim], oMember))
                removeList.add(node); // add to delete list
            } else if (uti.isFunCallTo(oExp, "{}")) {
              // set of members may be there as result of split,
              //  we will remove any descendant member from the set.
              // if the set is empty thereafter, we will add the node
              //  to the remove list.
              int nArgs = uti.funCallArgCount(oExp);
              List removeMembers = new ArrayList();
              for (int i = 0; i < nArgs; i++) {
                Object oSetMember = uti.funCallArg(oExp, i);
                if (uti.checkDescendantO(mPath[iDim], oSetMember)) {
                  removeMembers.add(oSetMember);
                }
              }
              int nRemove = removeMembers.size();
              if (nRemove == nArgs) {
                // all memers in set are descendants, remove the node
                removeList.add(node); // add to delete list
              } else if (nRemove > 0) {
                // remove descendant nodes from set
                Object[] remaining = new Object[nArgs - nRemove];
                int j = 0;
                for (int i = 0; i < nArgs; i++) {
                  Object oSetMember = uti.funCallArg(oExp, i);
                  if (!removeMembers.contains(oSetMember))
                    remaining[j++] = oSetMember;
                }
                if (remaining.length == 1) {
                  node.setReference(remaining[0]); // single
                  // member
                } else {
                  Object newSet = uti.createFunCall("{}", remaining, QuaxUti.FUNTYPE_BRACES);
                  node.setReference(newSet);
                }
              }

            } else if (uti.isFunCallTo(oExp, "Union")) {
              // HHTASK Cleanup, always use removeDescendantsFromFunCall
              Object oRemain = removeDescendantsFromFunCall(oExp, mPath[iDim], iDim);
              if (oRemain == null)
                removeList.add(node);
              else
                node.setReference(oRemain);
            }
            return TreeNodeCallback.CONTINUE_SIBLING;

          } else if (uti.isMember(oExp)) {
            if (uti.checkDescendantO(mPath[iDim], oExp))
              removeList.add(node);
          }
          return TreeNodeCallback.CONTINUE_SIBLING;
          // always break on level iDim, next sibling
        } else {
          // should never get here
          logger.error("unexpected tree node level " + idi + " " + uti.memberString(mPath));
        }
        return TreeNodeCallback.BREAK;
      } // handleTreeNode
    });

    // remove nodes collected in work list
    for (Iterator iter = removeList.iterator(); iter.hasNext();) {
      TreeNode nodeToRemove = (TreeNode) iter.next();
      removePathToNode(nodeToRemove);
    }

    // any dimension left and including iDim will *not* be excluded from
    // hierarchize
    int n = nDimension - iDim - 1;
    if (n < nHierExclude)
      nHierExclude = n;

    if (logger.isDebugEnabled()) {
      logger.debug("after collapse " + this.toString());
    }

    changed(this, false);
  } // collapse

  /**
   * check, whether a member path can be collapsed this is true if there is a child position path
   *
   * @param member
   *          position path to be collapsed
   */
  public boolean canCollapse(Member member) {

    // we only allow expand / collapse for a dimension
    //  left of a "sticky topcount"
    if (!allowNavigate(member, false))
      return false;

    // first check the cache
    if (canCollapseMemberMap.containsKey(member)) {
      Boolean bCanCollapse = (Boolean) canCollapseMemberMap.get(member);
      return bCanCollapse.booleanValue();
    }

    // loop over Position Tree
    //  can collapse, if we find a descendant of member
    boolean b = findMemberChild(member);

    // cache the result
    Boolean bool = new Boolean(b);
    canCollapseMemberMap.put(member, bool);

    return b;
  }

  /**
   * remove child nodes of monMember
   *
   * @param member
   *          member to be collapsed
   */
  public void collapse(final Member member) {

    if (qubonMode) {
      resolveUnions();
      if (logger.isDebugEnabled()) {
        logger.debug("collapse member after resolveUnions " + this.toString());
      }
    }

    final int iDim = this.dimIdx(uti.dimForMember(member));

    final List nodesForMember = new ArrayList();

    // update the position member tree
    //  wherever we find a descendant node of monMember, split and remove it
    //  collect all descendant nodes for monMember in workList
    posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback find node matching member Path exactly
       */
      public int handleTreeNode(TreeNode node) {
        int iDimNode = node.getLevel() - 1;
        if (iDimNode < iDim)
          return TreeNodeCallback.CONTINUE; // we are below iDim,
        // don't care

        // iDimNode == iDim
        //  node Exp must contain children of member[iDim]
        Object oExp = node.getReference();
        if (uti.isMember(oExp)) {
          if (uti.checkDescendantO(member, oExp))
            nodesForMember.add(node);
        } else {
          // must be FunCall

          if (isDescendantOfMemberInFunCall(oExp, member, iDimNode))
            nodesForMember.add(node);
        }
        return TreeNodeCallback.CONTINUE_SIBLING; // continue next
        // sibling
      }
    });

    for (Iterator iter = nodesForMember.iterator(); iter.hasNext();) {
      TreeNode node = (TreeNode) iter.next();
      Object oExp = node.getReference();
      if (uti.isMember(oExp)) {
        removePathToNode(node);
      } else {
        // FunCall
        Object oComplement = removeDescendantsFromFunCall(oExp, member, iDim);
        if (oComplement == null)
          removePathToNode(node);
        else
          node.setReference(oComplement); // replace node object by complement
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("after collapse " + this.toString());
    }

    changed(this, false);
  } // collapse

  // ==========
  // Drill Down
  // ==========

  /**
   * drill down is possible if there is no sticky generate
   */
  public boolean canDrillDown(Member member) {
    return allowNavigate(member, true);
  }

  /**
   * drill down
   *
   * @param member
   *          drill down member
   */
  public void drillDown(Member member) {
    final int iDim = this.dimIdx(uti.dimForMember(member));

    // collect the Exp's of all dimensions except iDim
    Object[] sets = new Object[nDimension];
    Object oMember = uti.objForMember(member);
    Object fChildren = uti.createFunCall("Children", new Object[] { oMember},
        QuaxUti.FUNTYPE_PROPERTY);
    DimensionLoop: for (int i = 0; i < nDimension; i++) {
      if (i == iDim) {
        // replace drilldown dimension by member.children
        sets[i] = fChildren;
      } else {
        // generate exp for all nodes of this dimension
        sets[i] = genExpForDim(i);
      }
    } // DimensionLoop

    // regenerate the position tree as crossjoin of sets
    regeneratePosTree(sets, false);

    changed(this, false);
  }

  // ==========
  // Drill Up
  // ==========

  /**
   * drill up is possible if at least one member in the tree is not at the top level of this
   * hierarchy.
   */
  public boolean canDrillUp(Hierarchy hier) {
    final int iDim = this.dimIdx(hier.getDimension());

    if (!allowNavigate(iDim, true))
      return false;

    int ret = posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback check for member of hierarchy not on top level
       */
      public int handleTreeNode(TreeNode node) {
        int iDimNode = node.getLevel() - 1;
        if (iDimNode < iDim)
          return TreeNodeCallback.CONTINUE;
        // iDimNode == workInt
        Object oExp = node.getReference();
        if (!uti.isMember(oExp)) {
          // FunCall
          if (isFunCallNotTopLevel(oExp, iDimNode))
            return TreeNodeCallback.BREAK; // got it
          else
            return TreeNodeCallback.CONTINUE_SIBLING;
        } else {
          // member

          if (uti.levelDepthForMember(oExp) > 0)
            return TreeNodeCallback.BREAK; // got it
          else
            return TreeNodeCallback.CONTINUE_SIBLING;
        } // member

      } // handlePositionTreeNode
    });

    return (ret == TreeNodeCallback.BREAK);
  }

  /**
   * drill down
   *
   * @param hier
   *          drill down member
   */
  public void drillUp(Hierarchy hier) {

    int iDim = dimIdx(hier.getDimension());

    // collect the Exp's of all dimensions
    Object[] sets = new Object[nDimension];

    DimensionLoop: for (int i = 0; i < nDimension; i++) {
      if (i == iDim) {
        // replace drillup dimension by drillup set
        sets[i] = drillupExp(iDim, hier);
      } else {
        sets[i] = genExpForDim(i);
      }
    } // DimensionLoop

    // regenerate the position tree as crossjoin of sets
    regeneratePosTree(sets, false);

    changed(this, false);
  }

  // ==========
  // Query Axis Set
  // ==========

  /**
   * MDX Generation
   * generate Exp from tree
   *
   * @return Exp for axis set
   */
  public Object genExp(boolean genHierarchize) {

    if (generateMode > 0 && generateIndex > 0)
      return genGenerateExp(genHierarchize);
    else
      return genNormalExp(genHierarchize);
  }

  /**
   * Normal MDX Generation - no Generate
   *
   * @return Exp for axis set
   */
  private Object genNormalExp(boolean genHierarchize) {

    ExpGenerator expGenerator = new ExpGenerator(uti);

    if (!genHierarchize) {
      // no Hierarchize
      expGenerator.init(posTreeRoot, hiers);
      Object exp = expGenerator.genExp();
      return exp;
    }

    // do we need a special hierarchize ?
    // this will be true, if nHierExclude > 0

    if (nHierExclude == 0) {
      // no special hierarchize needed
      expGenerator.init(posTreeRoot, hiers);
      Object exp = expGenerator.genExp();
      // Hierarchize around "everything"
      Object eHier = uti
          .createFunCall("Hierarchize", new Object[] { exp}, QuaxUti.FUNTYPE_FUNCTION);
      return eHier;
    }

    // special hierarchize to be generated
    // the Qubon Mode Hierarchies are factored out,
    //  as they consist only of a single set of members.
    // the left expression will be generated and then hierarchized,
    //  *before* beeing crossjoined to the right Expression.

    return genLeftRight(expGenerator, nDimension - nHierExclude, nHierExclude);
  }

  /**
   * generate an expression
   * with hierarchize for the hierarchies < nHierExclude
   * without hierarchize for the hierarchies >= nHierExclude
   */
  private Object genLeftRight(ExpGenerator expGenerator, int nLeft, int nRight) {
    // generate left expression to be hierarchized
    Object leftExp = null;
    if (nLeft > 0) {
      TreeNode leftRoot = posTreeRoot.deepCopyPrune(nLeft);
      leftRoot.setReference(null);
      Hierarchy[] leftHiers = new Hierarchy[nLeft];
      for (int i = 0; i < leftHiers.length; i++) {
        leftHiers[i] = hiers[i];
      }
      expGenerator.init(leftRoot, leftHiers);
      leftExp = expGenerator.genExp();
      leftExp = uti.createFunCall("Hierarchize", new Object[] { leftExp}, QuaxUti.FUNTYPE_FUNCTION);
    }

    // generate the right expression, not to be hierarchized
    Object rightExp = null;
    Hierarchy[] rightHiers = new Hierarchy[nRight];
    for (int i = 0; i < nRight; i++) {
      rightHiers[i] = hiers[nLeft + i];
    }

    // go down to the first hier to be excluded from hierarchize
    // note: the subtree tree under any node of the hierarchy above
    //  is always the same, so we can replicate any subtree under
    //  a node of hierarchy nLeft-1
    TreeNode rightRoot = new TreeNode(null);
    TreeNode current = posTreeRoot;
    for (int i = 0; i < nLeft; i++) {
      List list = current.getChildren();
      current = (TreeNode) list.get(0);
    }
    List list = current.getChildren();
    for (Iterator iter = list.iterator(); iter.hasNext();) {
      TreeNode node = (TreeNode) iter.next();
      TreeNode cnode = node.deepCopy();
      rightRoot.addChildNode(cnode);
    }

    expGenerator.init(rightRoot, rightHiers);
    rightExp = expGenerator.genExp();

    if (leftExp == null)
      return rightExp;

    Object exp = uti.createFunCall("CrossJoin", new Object[] { leftExp, rightExp},
        QuaxUti.FUNTYPE_FUNCTION);

    return exp;
  }

  /**
   * MDX Generation for Generate
   *
   * @return Exp for axis set
   */
  private Object genGenerateExp(boolean genHierarchize) {

    ExpGenerator expGenerator = new ExpGenerator(uti);

    // Generate(GSet, FSet) to be generated
    //  hierarchies >= generateIndex will not be "hierarchized"
    // we expect the hierarchies >= generateIndex to be excluded
    //  from hierarchize.
    if (nDimension - generateIndex > nHierExclude)
      logger.warn("unexpected values: nHierExclude=" + nHierExclude + " generateIndex="
          + generateIndex);

    // assume following situation:
    //  3 hierarchies
    // time - customers - product
    // we want top 5 customers, generated for each time member
    // 1. step
    //  generate expression until customers (only time here), result = set1
    //  if neccessary, put hierarchize around
    // 2. step
    //  Generate(set1, Topcount(Crossjoin ({Time.Currentmember}, Set for Customers),
    //                          5, condition))
    //  result = set2
    // 3.step
    //  append the tail nodes , here Product
    //  Crossjoin(set2 , Product dimension nodes)
    //
    // 1. step left expression, potentially hierarchized

    Object leftExp = null;
    // if     nHierExclude > nDimension - generateIndex
    //    and nHierExclude < nDimension
    // the the left expression (inside Generate) will be partly
    // hierarchized
    if (genHierarchize && nHierExclude > nDimension - generateIndex && nHierExclude < nDimension) {
      int nLeft = nDimension - nHierExclude;
      int nRight = generateIndex - nLeft;
      leftExp = genLeftRight(expGenerator, nLeft, nRight);
    } else {
      TreeNode leftRoot = posTreeRoot.deepCopyPrune(generateIndex);
      leftRoot.setReference(null);
      Hierarchy[] leftHiers = new Hierarchy[generateIndex];
      for (int i = 0; i < leftHiers.length; i++) {
        leftHiers[i] = hiers[i];
      }
      expGenerator.init(leftRoot, leftHiers);
      leftExp = expGenerator.genExp();
      if (genHierarchize)
        leftExp = uti.createFunCall("Hierarchize", new Object[] { leftExp},
            QuaxUti.FUNTYPE_FUNCTION);
    }

    // 2. step Generate(set1, Topcount())
    TreeNode topCountNode = posTreeRoot;
    // top count node can be anything like topcount, bottomcount, filter
    for (int i = 0; i <= generateIndex; i++) {
      // the path to the topcount node at generateIndex does not matter
      List children = topCountNode.getChildren();
      topCountNode = (TreeNode) children.get(0);
    }
    Object topcount = topCountNode.getReference();
    // we have to replace the "set" of the topcount function
    Object origTopcountSet = uti.funCallArg(topcount, 0);
    // generate the Tuple of dimension.currentmember until generateIndex
    Object currentMembersTuple = genCurrentTuple();
    Object ocj = uti.createFunCall("Crossjoin",
        new Object[] { currentMembersTuple, origTopcountSet}, QuaxUti.FUNTYPE_FUNCTION);
    // replace the topcout original set
    String fun = uti.funCallName(topcount);
    int n = uti.funCallArgCount(topcount);
    Object[] args = new Object[n];
    for (int i = 1; i < n; i++) {
      args[i] = uti.funCallArg(topcount, i);
    }
    args[0] = ocj;
    Object newTopcount = uti.createFunCall(fun, args, QuaxUti.FUNTYPE_FUNCTION);
    Object oGenerate = uti.createFunCall("Generate", new Object[] { leftExp, newTopcount},
        QuaxUti.FUNTYPE_FUNCTION);

    if (generateIndex + 1 == nDimension)
      return oGenerate;

    // 3. step append the tail nodes
    // generate CrossJoin
    int nRight = nDimension - generateIndex - 1;
    Hierarchy[] rightHiers = new Hierarchy[nRight];
    for (int i = 1; i <= nRight; i++) {
      rightHiers[nRight - i] = hiers[nDimension - i];
    }
    TreeNode root = new TreeNode(null);
    List list = topCountNode.getChildren();
    for (Iterator iter = list.iterator(); iter.hasNext();) {
      TreeNode node = (TreeNode) iter.next();
      root.addChildNode(node.deepCopy());
    }
    expGenerator.init(root, rightHiers);
    Object rightExp = expGenerator.genExp();

    Object exp = uti.createFunCall("CrossJoin", new Object[] { oGenerate, rightExp},
        QuaxUti.FUNTYPE_FUNCTION);
    return exp;
  }

  // ==========
  // private
  // ==========

  /**
   * generate {(dim1.Currentmember, dim2.Currentmember, ... )}
   *
   * @return
   */
  private Object genCurrentTuple() {
    Object[] currentsOfDim = new Object[generateIndex];
    for (int i = 0; i < currentsOfDim.length; i++) {
      Dimension dim = hiers[i].getDimension();
      currentsOfDim[i] = uti.createFunCall("CurrentMember", new Object[] { uti.objForDim(dim)},
          QuaxUti.FUNTYPE_PROPERTY);
    }
    Object oTuple;
    if (generateIndex > 1)
      oTuple = uti.createFunCall("()", currentsOfDim, QuaxUti.FUNTYPE_TUPLE);
    else
      oTuple = currentsOfDim[0]; //  just dimension.currentmember
    // generate set braces around tuple
    Object oSet = uti.createFunCall("{}", new Object[] { oTuple}, QuaxUti.FUNTYPE_BRACES);
    return oSet;
  }

  /**
   * @return true if child position can be found
   */
  private boolean checkChildPosition(final Member[] mPath) {

    int ret = posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback find node matching member Path exactly
       */
      public int handleTreeNode(TreeNode node) {
        int iDim = mPath.length - 1;
        int iDimNode = node.getLevel() - 1;
        Object oExp = node.getReference();
        if (iDimNode < iDim) {
          // node Exp must match member[iDim]
          if (uti.isMember(oExp)) {
            if (uti.equalMember(oExp, mPath[iDimNode]))
              return TreeNodeCallback.CONTINUE;
            else
              return TreeNodeCallback.CONTINUE_SIBLING; // continue
            // next
            // sibling
          } else {
            // must be FunCall
            if (isMemberInFunCall(oExp, mPath[iDimNode], iDimNode))
              return TreeNodeCallback.CONTINUE;
            else
              return TreeNodeCallback.CONTINUE_SIBLING; // continue
            // next
            // sibling
          }
        }

        // iDimNode == iDim
        //  node Exp must contain children of member[iDim]
        if (uti.isMember(oExp)) {
          if (uti.checkParent(mPath[iDimNode], oExp))
            return TreeNodeCallback.BREAK; // found
          else
            return TreeNodeCallback.CONTINUE_SIBLING; // continue
          // next
          // sibling
        } else {
          // must be FunCall
          if (isChildOfMemberInFunCall(oExp, mPath[iDimNode], iDimNode))
            return TreeNodeCallback.BREAK; // found
          else
            return TreeNodeCallback.CONTINUE_SIBLING; // continue
          // next
          // sibling
        }
      }
    });

    if (ret == TreeNodeCallback.BREAK)
      return true; // child path fund
    else
      return false;
  } // checkChildPosition

  /**
   * resolve the qubon mode unions and crossjoins only used in "old" expand mode
   */
  private void resolveUnions() {
    final List[] setLists = new List[nDimension];
    for (int i = 0; i < setLists.length; i++) {
      setLists[i] = new ArrayList();
    }
    posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback resolve sets of any dimension
       */
      public int handleTreeNode(TreeNode node) {
        int iDimNode = node.getLevel() - 1;
        Object oExp = node.getReference();
        if (!uti.isMember(oExp)) {
          // FunCall
          funToList(oExp, setLists[iDimNode]);
        } else {
          // member
          setLists[iDimNode].add(oExp);
        }
        return TreeNodeCallback.CONTINUE;
      } // handleTreeNode
    });

    // unions and sets are resolved, now resolve crossjoins
    posTreeRoot = new TreeNode(null);
    crossJoinTree(setLists, posTreeRoot, 0);

    qubonMode = false;
  }

  /**
   * find best tree node for member path (longest match)
   */
  private TreeNode findBestNode(final Member[] mPath) {
    final TreeNode[] bestNode = new TreeNode[1];
    bestNode[0] = posTreeRoot;
    posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback find node matching member Path exactly
       */
      public int handleTreeNode(TreeNode node) {
        int iDim = mPath.length - 1;
        int iDimNode = node.getLevel() - 1;
        Object oExp = node.getReference();
        if (!uti.isMember(oExp))
          return TreeNodeCallback.CONTINUE_SIBLING; // continue next
        // sibling
        if (uti.equalMember(oExp, mPath[iDimNode])) {
          // match
          if (iDimNode == iDim) {
            // found exactly matching node
            bestNode[0] = node;
            return TreeNodeCallback.BREAK;
          } else {
            // best match up to now
            bestNode[0] = node;
            return TreeNodeCallback.CONTINUE;
          }
        } else {
          // no match
          return TreeNodeCallback.CONTINUE_SIBLING; // continue next
          // sibling
        }
      }
    });

    return bestNode[0];
  }

  /**
   * collect tail nodes for all nodes matching member path
   */
  private List collectTailNodes(TreeNode startNode, final Member[] mPath) {

    final List tailNodes = new ArrayList();
    startNode.walkChildren(new TreeNodeCallback() {

      /**
       * callback find node matching mPath collect tail nodes
       */
      public int handleTreeNode(TreeNode node) {
        int iDim = mPath.length - 1;
        int iDimNode = node.getLevel() - 1;
        Object oExp = node.getReference();
        boolean match = false;
        if (uti.isMember(oExp)) {
          // exp is member
          if (uti.equalMember(oExp, mPath[iDimNode]))
            match = true;
        } else {
          // must be FunCall
          if (isMemberInFunCall(oExp, mPath[iDimNode], iDimNode))
            match = true;
        }

        if (match) {
          if (iDimNode == iDim) {
            // add the children to the tail list
            tailNodes.addAll(node.getChildren());
            return TreeNodeCallback.CONTINUE_SIBLING;
          } else {
            // iDimNode < iDim
            return TreeNodeCallback.CONTINUE;
          }
        } else
          return TreeNodeCallback.CONTINUE_SIBLING; // no match,
        // continue next
        // sibling

      } // handlePositionTreeNode
    });

    return tailNodes;
  }

  private boolean findMemberChild(final Member member) {

    final int iDim = this.dimIdx(uti.dimForMember(member));

    int ret = posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback find child node of member
       */
      public int handleTreeNode(TreeNode node) {
        int iDimNode = node.getLevel() - 1;
        if (iDimNode < iDim)
          return TreeNodeCallback.CONTINUE; // we are below iDim,
        // don't care

        // iDimNode == iDim
        //  node Exp must contain children of member[iDim]
        Object oExp = node.getReference();
        if (uti.isMember(oExp)) {
          if (uti.checkParent(member, oExp))
            return TreeNodeCallback.BREAK; // found
        } else {
          // must be FunCall
          if (isChildOfMemberInFunCall(oExp, member, iDimNode))
            return TreeNodeCallback.BREAK; // found
        }
        return TreeNodeCallback.CONTINUE_SIBLING; // continue next
        // sibling
      }
    });

    return (ret == TreeNodeCallback.BREAK);
  }

  /**
   * String representation (debugging)
   */
  public String toString() {
    final StringBuffer sbPosTree = new StringBuffer();
    sbPosTree.append("number of hierarchies excluded from HIEARARCHIZE=" + nHierExclude);
    sbPosTree.append('\n');
    if (posTreeRoot == null) {
      sbPosTree.append("Root=null");
      return sbPosTree.toString();
    }
    posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback quax to String
       */
      public int handleTreeNode(TreeNode node) {
        int iDimNode = node.getLevel() - 1;
        sbPosTree.append("\n");
        for (int i = 0; i < iDimNode - 1; i++) {
          sbPosTree.append("   ");
        }
        if (iDimNode > 0) {
          sbPosTree.append("+--");
        }

        Object oExp = node.getReference();
        if (!uti.isMember(oExp)) {
          // FunCall
          sbPosTree.append(uti.funString(oExp));
        } else {
          // member
          sbPosTree.append(uti.getMemberUniqueName(oExp));
        }
        return TreeNodeCallback.CONTINUE;
      } // handleTreeNode
    });
    return sbPosTree.toString();
  }

  /**
   * build tree resolving crossjoin
   *
   * @param currentNode
   * @param iDim
   */
  private void crossJoinTree(List[] setLists, TreeNode currentNode, int iDim) {
    for (Iterator iter = setLists[iDim].iterator(); iter.hasNext();) {
      Object oExp = iter.next();
      TreeNode newNode = new TreeNode(oExp);
      if (iDim < nDimension - 1)
        crossJoinTree(setLists, newNode, iDim + 1);
      currentNode.addChildNode(newNode);
    }
  }

  /**
   * split Funcall to node and complement
   */
  private void splitFunCall(TreeNode nFunCall, Member member, int iHier) {

    Object oExp = nFunCall.getReference();

    // it is possible (if the split member is of dimension to be collapsed),
    //  that this funcall does not contain member.
    // Then - there is nothing to split.
    if (!isMemberInFunCall(oExp, member, nFunCall.getLevel() - 1))
      return; // nothing to split
    Object oComplement = createComplement(oExp, member, iHier); // can be null
    if (oComplement == null) {
      // this means, that the set resolves to a single member,
      // mPath[iDimNode]
      nFunCall.setReference(uti.objForMember(member));
      // nothing to split
      return;
    }

    //  split the Funcall
    TreeNode newNodeComplement = new TreeNode(oComplement);
    TreeNode newNodeMember = new TreeNode(uti.objForMember(member));
    // add the children
    for (Iterator iter = nFunCall.getChildren().iterator(); iter.hasNext();) {
      TreeNode nChild = (TreeNode) iter.next();
      newNodeComplement.addChildNode(nChild.deepCopy());
      newNodeMember.addChildNode(nChild.deepCopy());
    }

    TreeNode nInsert = nFunCall.getParent();
    nFunCall.remove();
    nInsert.addChildNode(newNodeComplement);
    nInsert.addChildNode(newNodeMember);
  } // splitFuncall

  /**
   * remove Children node
   *
   * @param nodeToRemove
   */
  private void removePathToNode(TreeNode nodeToRemove) {
    if (nodeToRemove.getParent().getChildren().size() > 1) {
      // this node has siblings, just remove it
      nodeToRemove.remove();
    } else {
      // no siblings, remove the first parent node having siblings
      TreeNode parent = nodeToRemove.getParent();
      while (parent.getParent().getChildren().size() == 1) {
        parent = parent.getParent();
      }
      if (parent.getLevel() > 0) // should always be true
        parent.remove();
    }
  }

  /**
   * generate Exp for all nodes of dimension iDimension
   *
   * @param iDimension
   * @return Exp for all nodes
   */
  public Object genExpForDim(int iDimension) {
    // if we got a generate function on this hier, preserve it
    if (generateIndex >= 0 && generateIndex == iDimension && generateMode > CalcSet.SIMPLE) {
      TreeNode topCountNode = (TreeNode) posTreeRoot.getChildren().get(0);
      for (int i = 0; i < generateIndex; i++) {
        // the path to the topcount node at generateIndex does not
        // matter
        List children = topCountNode.getChildren();
        topCountNode = (TreeNode) children.get(0);
      }
      Object topcount = topCountNode.getReference();
      SetExp setexp = new SetExp(generateMode, topcount, hiers[iDimension]);
      return setexp;
    }
    List funCallList = collectFunCalls(iDimension);
    List memberList = collectMembers(iDimension);
    cleanupMemberList(funCallList, memberList, iDimension);

    if (funCallList.size() == 0 && memberList.size() == 1)
      return memberList.get(0); // single member only

    Object mSet = null;
    if (memberList.size() > 0) {
      Object[] aExp = memberList.toArray(new Object[0]);
      mSet = uti.createFunCall("{}", aExp, QuaxUti.FUNTYPE_BRACES);
    }
    if (funCallList.size() == 0)
      return mSet;

    if (funCallList.size() == 1 && mSet == null)
      return funCallList.get(0);

    Object set;
    int start;
    if (mSet != null) {
      set = mSet;
      start = 0;
    } else {
      set = funCallList.get(0);
      start = 1;
    }
    for (int j = start; j < funCallList.size(); j++) {
      set = uti.createFunCall("Union", new Object[] { set, funCallList.get(j)},
          QuaxUti.FUNTYPE_FUNCTION);
    }
    return set;
  }

  /**
   * create drillup expression for dimension
   *
   * @param iDim
   *          dimension to be drilled up
   * @return
   */
  private Object drillupExp(int iDim, Hierarchy hier) {

    // the drillup logic is:
    //  for all members of this dimension find the deepest level.
    //  find the members of this deepest level
    //  find the grandfathers of those deepest members
    //  drill up goes to the children of those grandfathers.
    // special cases:
    //  the deepest level has all members (level.members)
    //    the drillup goes to parent_level.members

    final int[] maxLevel = new int[1];
    maxLevel[0] = 0;

    List drillupList = collectDrillup(iDim, maxLevel);
    Object expForHier = null;
    if (maxLevel[0] == 0) {
      // drillup goes to top level members
      //  we generate an explicit member set rather than level.members
      //  usually, this is a single member "All xy"
      expForHier = uti.topLevelMembers(hier, false);
    } else {
      if (drillupList.size() == 1) {
        expForHier = drillupList.get(0);
      } else {
        // more than 1 set expression , need union
        for (Iterator iter = drillupList.iterator(); iter.hasNext();) {
          Object oExp = iter.next();
          if (expForHier == null)
            expForHier = oExp;
          else {
            expForHier = uti.createFunCall("Union", new Object[] { expForHier, oExp},
                QuaxUti.FUNTYPE_FUNCTION);
          }
        }
      }
    }
    return expForHier;
  }

  /**
   * collect drillup Exps of dimension i
   *
   * @param iDim
   */
  private List collectDrillup(final int iDim, final int[] maxLevel) {
    final List drillupList = new ArrayList();
    posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback collect GrandFathers of deepest for dimension workInt
       */
      public int handleTreeNode(TreeNode node) {
        int iDimNode = node.getLevel() - 1;
        if (iDimNode < iDim)
          return TreeNodeCallback.CONTINUE;
        // iDimNode == workInt
        Object oExp = node.getReference();
        if (!uti.isMember(oExp)) {
          // FunCall
          addFunCallToDrillup(drillupList, oExp, maxLevel);
        } else {
          // member
          Member m = uti.memberForObj(oExp);
          uti.addMemberUncles(drillupList, m, maxLevel);
        } // member
        return TreeNodeCallback.CONTINUE_SIBLING;
      } // handlePositionTreeNode
    });
    return drillupList;
  }

  /**
   * collect Funcalls of dimension iDim
   *
   * @param iDim
   */
  private List collectFunCalls(final int iDim) {
    if (posTreeRoot == null)
      return Collections.EMPTY_LIST;
    final List funCallList = new ArrayList();
    posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback collect Funcalls of dimension workInt
       */
      public int handleTreeNode(TreeNode node) {
        int iDimNode = node.getLevel() - 1;
        if (iDimNode < iDim)
          return TreeNodeCallback.CONTINUE;
        // iDimNode == workInt
        Object oExp = node.getReference();
        if (!uti.isMember(oExp)) {
          // FunCall
          // need unique representation in order to avoid doubles
          String unique = uti.funString(oExp).toString();
          if (!funCallList.contains(unique)) {
            funCallList.add(oExp);
            funCallList.add(unique);
          }
        }
        return TreeNodeCallback.CONTINUE_SIBLING;
      } // handlePositionTreeNode
    });

    // remove the unique strings, which were just added to avoid doubles
    for (Iterator iter = funCallList.iterator(); iter.hasNext();) {
      Object element = iter.next();
      if (element instanceof String)
        iter.remove();
    }

    return funCallList;
  }

  /**
   * remove members from member list being in FunCall list
   *
   * @param funCallList
   * @param memberList
   */
  private void cleanupMemberList(List funCallList, List memberList, int iDim) {
    if (funCallList.size() > 0 && memberList.size() > 0) {
      MemberLoop: for (Iterator itMem = memberList.iterator(); itMem.hasNext();) {
        Object oMember = itMem.next();
        Member m = uti.memberForObj(oMember);
        for (Iterator itFun = funCallList.iterator(); itFun.hasNext();) {
          Object oFun = itFun.next();
          if (isMemberInFunCall(oFun, m, iDim)) {
            itMem.remove();
            continue MemberLoop;
          }
        }
      } // MemberLoop
    }
  }

  /**
   * collect Members of dimension iDim
   *
   * @param iDim
   */
  List collectMembers(final int iDim) {
    if (posTreeRoot == null)
      return Collections.EMPTY_LIST;
    final List memberList = new ArrayList();
    posTreeRoot.walkChildren(new TreeNodeCallback() {

      /**
       * callback collect Funcalls of dimension workInt
       */
      public int handleTreeNode(TreeNode node) {
        int iDimNode = node.getLevel() - 1;
        if (iDimNode < iDim)
          return TreeNodeCallback.CONTINUE;
        // iDimNode == workInt
        Object oExp = node.getReference();
        if (uti.isMember(oExp) && !memberList.contains(oExp))
          memberList.add(oExp);
        return TreeNodeCallback.CONTINUE_SIBLING;
      } // handlePositionTreeNode
    });
    return memberList;
  }

  /**
   * add a Funcall to Drillup list
   */
  private void addFunCallToDrillup(List list, Object oFun, int[] maxLevel) {
    if (uti.isFunCallTo(oFun, "Union")) {
      for (int i = 0; i < 2; i++) {
        Object fExp = uti.funCallArg(oFun, i);
        addFunCallToDrillup(list, fExp, maxLevel);
      }
    } else if (uti.isFunCallTo(oFun, "{}")) {
      // set of members
      for (int i = 0; i < uti.funCallArgCount(oFun); i++) {
        Object oMember = uti.funCallArg(oFun, i);
        Member m = uti.memberForObj(oMember);
        uti.addMemberUncles(list, m, maxLevel);
      }
    } else if (uti.isFunCallTo(oFun, "Children")) {
      Object oMember = uti.funCallArg(oFun, 0);
      Member m = uti.memberForObj(oMember);
      uti.addMemberSiblings(list, m, maxLevel);
    } else if (uti.isFunCallTo(oFun, "Descendants")) {
      Object oMember = uti.funCallArg(oFun, 0);
      Member m = uti.memberForObj(oMember);
      Object oLevel = uti.funCallArg(oFun, 1);
      Level lev = uti.LevelForObj(oLevel);
      int level = uti.levelDepthForMember(m);
      int levlev = ((MDXLevel) lev).getDepth();
      if (levlev == level + 1)
        uti.addMemberSiblings(list, m, maxLevel); // same as children
      else if (levlev == level + 2)
        uti.addMemberChildren(list, m, maxLevel); // m *is* grandfather
      else {
        // add descendants of parent level
        Level parentLevel = uti.getParentLevel(lev);
        uti.addMemberDescendants(list, m, parentLevel, maxLevel);
      }
    } else if (uti.isFunCallTo(oFun, "Members")) {
      // add parent level members
      Object oLevel = uti.funCallArg(oFun, 0);
      Level lev = uti.LevelForObj(oLevel);
      int levlev = ((MDXLevel) lev).getDepth();
      if (levlev == 0)
        return; // cannot drill up
      Level parentLevel = uti.getParentLevel(lev);
      uti.addLevelMembers(list, parentLevel, maxLevel);
    } else {
      // must be Top/Bottom Function with arg[0] being base set
      Object oFun2 = uti.funCallArg(oFun, 0);
      addFunCallToDrillup(list, oFun2, maxLevel); // do not have a better
      // solution
    }
  }

  /**
   * add FunCall to list
   *
   * @param oFun
   * @param list
   */
  private void funToList(Object oFun, List list) {
    if (uti.isFunCallTo(oFun, "Union")) {
      Object oArg0 = uti.funCallArg(oFun, 0);
      Object oArg1 = uti.funCallArg(oFun, 1);
      funToList(oArg0, list);
      funToList(oArg1, list);
    } else if (uti.isFunCallTo(oFun, "{}")) {
      for (int i = 0; i < uti.funCallArgCount(oFun); i++) {
        // member sets are resolved to single members
        Object oMember = uti.funCallArg(oFun, i);
        list.add(oMember);
      }
    } else {
      list.add(oFun);
    }
  }

  // ==========
  // Utility
  // ==========

  /**
   * hierarchize the query axis position array
   */
  // this code is not working
  public void hierarchizePositions(final Member[][] aPosMem) {

    final int nDimension = aPosMem[0].length;
    final Map[] firstOccurrences = new HashMap[nDimension];
    for (int i = 0; i < nDimension; i++) {
      firstOccurrences[i] = new HashMap();
    }
    for (int i = 0; i < aPosMem.length; i++) {
      for (int j = 0; j < nDimension; j++) {
        // String uName = aPosMem[i][j].getUniqueName();
        if (!firstOccurrences[j].containsKey(aPosMem[i][j])) {
          firstOccurrences[j].put(aPosMem[i][j], new Integer(i));
        }
      } // j
    } //i

    Arrays.sort(aPosMem, new Comparator() {
      public int compare(Object o1, Object o2) {
        // compare two position member arrays
        Member[] a1 = (Member[]) o1;
        Member[] a2 = (Member[]) o2;

        DimensionLoop: for (int i = 0; i < a1.length; i++) {
          if (a1[i].equals(a2[i]))
            continue DimensionLoop;
          // first difference at dimension index i
          // if it is on different level, the descendant is higher
          // otherwise - decide by first occurrence
          int level1 = ((MDXLevel) a1[i].getLevel()).getDepth();
          int level2 = ((MDXLevel) a1[i].getLevel()).getDepth();
          if (level1 == level2) {
            int first1 = ((Integer) firstOccurrences[i].get(a1[i])).intValue();
            int first2 = ((Integer) firstOccurrences[i].get(a2[i])).intValue();
            return first1 - first2;
          } else {
            return level1 - level2;
          }
          // everything equal up to here
        } // DimensionLoop

        return 0; // equal positions, should not occur
      }
    });

  }

  //  /**
  //    * check, whether a parent.children Funcall is on the axis
  //    */
  //  public boolean isChildrenOnAxis(final Member parent) {
  //
  //    final int iDim = this.dimIdx(uti.dimForMember(parent));
  //
  //    int ret = posTreeRoot.walkChildren(new TreeNodeCallback() {
  //
  //      /**
  //       * callback
  //       * find child node of monMember
  //       */
  //
  //      public int handleTreeNode(TreeNode node) {
  //        int iDimNode = node.getLevel() - 1;
  //        if (iDimNode < iDim)
  //          return TreeNodeCallback.CONTINUE; // we are below iDim, don't care
  //
  //        // iDimNode == iDim
  //        // node Exp must be funcall evaluating to children of parent
  //        Object oExp = node.getReference();
  //        if (!uti.isMember(oExp)) {
  //          // must be FunCall
  //          if (uti.isMemberChildrenInFunCall(oExp, parent))
  //            return TreeNodeCallback.BREAK; // found
  //        }
  //        return TreeNodeCallback.CONTINUE_SIBLING; // continue next sibling
  //      }
  //    });
  //
  //    return (ret == TreeNodeCallback.BREAK);
  //  }

  // ==========
  // Getter / Setter
  // ==========

  /**
   * @return
   */
  public QuaxUti getUti() {
    return uti;
  }

  /**
   * @param uti
   */
  public void setUti(QuaxUti uti) {
    this.uti = uti;
  }

  /**
   * @return
   */
  public int getNDimension() {
    return nDimension;
  }

  /**
   * @return posTreeRoot
   */
  public TreeNode getPosTreeRoot() {
    return posTreeRoot;
  }

  /**
   * @return
   */
  public boolean isHierarchizeNeeded() {
    return hierarchizeNeeded;
  }

  /**
   * @param b
   */
  public void setHierarchizeNeeded(boolean b) {
    hierarchizeNeeded = b;
  }

  /**
   * @param posTreeRoot
   */
  public void setPosTreeRoot(TreeNode posTreeRoot, boolean hiersChanged) {
    this.posTreeRoot = posTreeRoot;
    if (hiersChanged) {
      // count dimensions, set hierarchies
      TreeNode firstNode = posTreeRoot;
      List hiersList = new ArrayList();
      List children = firstNode.getChildren();
      while (children.size() > 0) {
        firstNode = (TreeNode) children.get(0);
        Object oExp = firstNode.getReference();
        Hierarchy hier;
        try {
          hier = uti.hierForExp(oExp);
        } catch (CannotHandleException e) {
          logger.fatal("could not determine Hierarchy for set");
          logger.fatal(e);
          throw new IllegalArgumentException(e.getMessage());
        }
        hiersList.add(hier);
        ++nDimension;
        children = firstNode.getChildren();
      }
      hiers = (Hierarchy[]) hiersList.toArray(new Hierarchy[0]);
      nDimension = hiers.length;
      containsUF = new boolean[nDimension]; // init false
      ufMemberLists = new List[nDimension];

      // go through nodes and check for Unknown functions
      //  only one unknown function is possible in one hierarchy
      posTreeRoot.walkChildren(new TreeNodeCallback() {
        /**
         * callback find unknown functions
         */
        public int handleTreeNode(TreeNode node) {
          int iDimNode = node.getLevel() - 1;
          Object oExp = node.getReference();
          if (!uti.canHandle(oExp)) {
            // indicate that dimension i contains an unknown function,
            //  which cannot be handled in some cases.
            // this will cause the member list of this dimension to be stored
            containsUF[iDimNode] = true;
          }

          return TreeNodeCallback.CONTINUE;
        } // handlePositionTreeNode
      });
    }
  }

  /**
   * get Ordinal for axis, this is the immutable id of the quax
   *
   * @return ordinal
   */
  public int getOrdinal() {
    return ordinal;
  }

  /**
   * @param hierarchies
   */
  public void setHiers(Hierarchy[] hierarchies) {
    hiers = hierarchies;
    nDimension = hierarchies.length;
  }

  /**
   * @return hierarchies
   */
  public Hierarchy[] getHiers() {
    return hiers;
  }

  /**
   * @return
   */
  public boolean isQubonMode() {
    return qubonMode;
  }

  /**
   * @param qubonMode
   */
  public void setQubonMode(boolean qubonMode) {
    this.qubonMode = qubonMode;
  }

  /**
   * check, whether member is in set defined by funcall
   *
   * @param oExp -
   *          set funcall
   * @param member
   * @return
   */
  private boolean isMemberInFunCall(Object oExp, Member member, int hierIndex) {
    boolean b = false;
    try {
      b = uti.isMemberInFunCall(oExp, member);
    } catch (CannotHandleException e) {
      // it is an Unkown FunCall
      //  assume "true" if the member is in the List for this dimension
      if (ufMemberLists[hierIndex] == null)
        throw new IllegalArgumentException("Unknow Function - no member list, dimension="
            + hierIndex + " function=" + e.getMessage());

      b = ufMemberLists[hierIndex].contains(member);
    }
    return b;
  }

  /**
   * check whether a Funcall does NOT resolve to top level of hierarchy
   */
  private boolean isFunCallNotTopLevel(Object oExp, int hierIndex) {
    boolean b = false;

    try {
      b = uti.isFunCallNotTopLevel(oExp);
    } catch (CannotHandleException e) {
      // it is an Unkown FunCall
      //  assume "true" if the member is in the List for this dimension
      if (ufMemberLists[hierIndex] == null)
        throw new IllegalArgumentException("Unknow Function - no member list, dimension="
            + hierIndex + " function=" + e.getMessage());

      for (Iterator iter = ufMemberLists[hierIndex].iterator(); iter.hasNext();) {
        Member m = (Member) iter.next();
        if (!uti.isMemberOnToplevel(m)) {
          b = true;
          break;
        }
      }
    }
    return b;
  }

  /**
   * check whether a Funcall contains child of member
   */
  private boolean isChildOfMemberInFunCall(Object oExp, Member member, int hierIndex) {
    boolean b = false;

    try {
      b = uti.isChildOfMemberInFunCall(oExp, member);
    } catch (CannotHandleException e) {
      // it is an Unkown FunCall
      //  assume "true" if the member List for this dimension contains child of member
      if (ufMemberLists[hierIndex] == null)
        throw new IllegalArgumentException("Unknow Function - no member list, dimension="
            + hierIndex + " function=" + e.getMessage());

      for (Iterator iter = ufMemberLists[hierIndex].iterator(); iter.hasNext();) {
        Member m = (Member) iter.next();
        if (uti.checkParent(member, uti.objForMember(m))) {
          b = true;
          break;
        }
      }
    }
    return b;
  }

  /**
   * check whether a Funcall contains descendant of member
   */
  private boolean isDescendantOfMemberInFunCall(Object oExp, Member member, int hierIndex) {
    boolean b = false;

    try {
      b = uti.isDescendantOfMemberInFunCall(oExp, member);
    } catch (CannotHandleException e) {
      // it is an Unkown FunCall
      //  assume "true" if the member List for this dimension contains descendant of member
      if (ufMemberLists[hierIndex] == null)
        throw new IllegalArgumentException("Unknow Function - no member list, dimension="
            + hierIndex + " function=" + e.getMessage());

      for (Iterator iter = ufMemberLists[hierIndex].iterator(); iter.hasNext();) {
        Member m = (Member) iter.next();
        if (uti.checkDescendantM(member, m)) {
          b = true;
          break;
        }
      }
    }
    return b;
  }

  /**
   * remove descendants of member from Funcall set
   * @return the remainder after descendants were removed
   */
  private Object removeDescendantsFromFunCall(Object oFun, Member member, int iHier) {
    try {
      return removeDescendantsFromFunCall(oFun, member);
    } catch (CannotHandleException e) {
      // the FunCall was not handled,
      //  assume that it is an "Unkown FunCall" which was resolved by the latest result
      // the "Unknown Functions" are probably not properly resolved
      logger.error("Unkown FunCall " + uti.funCallName(oFun));

      if (ufMemberLists[iHier] == null)
        throw new IllegalArgumentException("Unknow Function - no member list, dimension=" + iHier
            + " function=" + e.getMessage());

      List newList = new ArrayList();
      for (Iterator iter = ufMemberLists[iHier].iterator(); iter.hasNext();) {
        Member m = (Member) iter.next();
        if (!uti.checkDescendantM(member, m)) {
          newList.add(uti.objForMember(m));
        }
      }
      return uti.createFunCall("{}", newList.toArray(), QuaxUti.FUNTYPE_BRACES);
    }
  }

  /**
   * remove descendants of member from Funcall set
   * @return the remainder after descendants were removed
   */
  private Object removeDescendantsFromFunCall(Object oFun, Member member)
      throws CannotHandleException {
    if (uti.isFunCallTo(oFun, "Children")) {
      // as we know, that there is a descendent of m in x.children,
      //  we know that *all* x.children are descendants of m
      return null;
    } else if (uti.isFunCallTo(oFun, "Descendants")) {
      // as we know, that there is a descendent of m in x.descendants
      //  we know that *all* x.descendants are descendants of m
      return null;
    } else if (uti.isFunCallTo(oFun, "Members")) {
      Level level = member.getLevel();
      Object[] members = uti.getLevelMembers(level);
      List remainder = new ArrayList();
      for (int i = 0; i < members.length; i++) {
        if (!uti.checkDescendantO(member, members[i]))
          remainder.add(members[i]);
      }
      return uti.createMemberSet(remainder);
    } else if (uti.isFunCallTo(oFun, "{}")) {
      List remainder = new ArrayList();
      for (int i = 0; i < uti.funCallArgCount(oFun); i++) {
        Object om = uti.funCallArg(oFun, i);
        if (!uti.checkDescendantO(member, om))
          remainder.add(om);
      }
      return uti.createMemberSet(remainder);
    } else if (uti.isFunCallTo(oFun, "Union")) {
      Object[] uargs = new Object[2];
      uargs[0] = removeDescendantsFromFunCall(uti.funCallArg(oFun, 0), member);
      uargs[1] = removeDescendantsFromFunCall(uti.funCallArg(oFun, 0), member);
      if (uargs[0] == null && uargs[1] == null)
        return null;
      if (uargs[1] == null)
        return uargs[0];
      if (uargs[0] == null)
        return uargs[1];
      if (uti.isMember(uargs[0])) {
        uargs[0] = uti.createFunCall("{}", new Object[] { uargs[0]}, QuaxUti.FUNTYPE_BRACES);
      }
      if (uti.isMember(uargs[1])) {
        uargs[1] = uti.createFunCall("{}", new Object[] { uargs[1]}, QuaxUti.FUNTYPE_BRACES);
      }
      if (uti.isFunCallTo(uargs[0], "{}") && uti.isFunCallTo(uargs[1], "{}"))
        return unionOfSets(uargs[0], uargs[1]);

      return uti.createFunCall("Union", uargs, QuaxUti.FUNTYPE_FUNCTION);
    }
    throw new CannotHandleException(uti.funCallName(oFun));

  }

  /**
   * determine complement set (set minus member)
   */
  private Object createComplement(Object oFun, Member member, int iHier) {
    try {
      return createComplement(oFun, member);
    } catch (CannotHandleException e) {
      // the FunCall was not handled,
      //  assume that it is an "Unkown FunCall" which was resolved by the latest result
      // the "Unknown Functions" are probably not properly resolved
      logger.error("Unkown FunCall " + uti.funCallName(oFun));
      if (ufMemberLists[iHier] == null)
        throw new IllegalArgumentException("Unknow Function - no member list, dimension=" + iHier
            + " function=" + e.getMessage());

      List newList = new ArrayList();
      for (Iterator iter = ufMemberLists[iHier].iterator(); iter.hasNext();) {
        Member m = (Member) iter.next();
        if (!member.equals(m)) {
          newList.add(uti.objForMember(m));
        }
      }
      return uti.createFunCall("{}", newList.toArray(), QuaxUti.FUNTYPE_BRACES);
    }
  }

  /**
   * determine complement set (set minus member)
   * @throws CannotHandleException
   */
  private Object createComplement(Object oFun, Member member) throws CannotHandleException {
    if (uti.isFunCallTo(oFun, "Children")) {
      Object oParent = uti.funCallArg(oFun, 0);
      // if member is NOT a child of Funcall arg, then the complement is the original set
      Object oMember = uti.objForMember(member);
      if (!uti.checkChild(member, oParent))
        return oFun;
      Object[] oChildren = uti.getChildren(oParent);
      if (oChildren.length < 2)
        return null;
      Object[] mComplement = new Object[oChildren.length - 1];
      int ii = 0;
      for (int i = 0; i < oChildren.length; i++) {
        if (!(oChildren[i].equals(oMember)))
          mComplement[ii++] = oChildren[i];
      }
      if (mComplement.length == 1)
        return mComplement[0]; // single member
      Object oComplement = uti.createFunCall("{}", mComplement, QuaxUti.FUNTYPE_BRACES);
      return oComplement;

    } else if (uti.isFunCallTo(oFun, "{}")) {
      int nComp = 0;
      int nArg = uti.funCallArgCount(oFun);
      Object oMember = uti.objForMember(member);
      for (int i = 0; i < nArg; i++) {
        Object o = uti.funCallArg(oFun, i);
        if (!(o.equals(oMember)))
          ++nComp;
      }
      if (nComp == 0)
        return null;
      if (nComp == nArg) {
        // complement = same
        return oFun;
      }

      Object[] mComplement = new Object[nComp];
      int ii = 0;
      for (int i = 0; i < nArg; i++) {
        Object o = uti.funCallArg(oFun, i);
        if (!(o.equals(oMember)))
          mComplement[ii++] = o;
      }
      if (mComplement.length == 1)
        return mComplement[0]; // single member
      Object oComplement = uti.createFunCall("{}", mComplement, QuaxUti.FUNTYPE_BRACES);
      return oComplement;
    } else if (uti.isFunCallTo(oFun, "Union")) {
      // Union of FunCalls, recursive
      // Complement(Union(a,b)) = Union(Complement(a), Complement(b))
      Object[] complements = new Object[2];
      for (int i = 0; i < 2; i++) {
        Object o = uti.funCallArg(oFun, i);
        complements[i] = createComplement(o, member);
      }
      if (complements[0] == null && complements[1] == null)
        return null;
      else if (complements[0] != null && complements[1] == null)
        return complements[0]; // No Union needed
      else if (complements[0] == null && complements[1] != null)
        return complements[1]; // No Union needed
      else {
        // complement can be single member
        if (!uti.isFunCall(complements[0])) {
          complements[0] = uti.createFunCall("{}", new Object[] { complements[0]},
              QuaxUti.FUNTYPE_BRACES);
        }
        if (!uti.isFunCall(complements[1])) {
          complements[1] = uti.createFunCall("{}", new Object[] { complements[1]},
              QuaxUti.FUNTYPE_BRACES);
        }

        if (uti.isFunCallTo(complements[0], "{}") && uti.isFunCallTo(complements[1], "{}")) {
          // create single set as union ow two sets
          return unionOfSets(complements[0], complements[1]);
        }
        Object newUnion = uti.createFunCall("Union", complements, QuaxUti.FUNTYPE_FUNCTION);
        return newUnion;
      }
    }
    // the fun call is not supported
    throw new CannotHandleException(uti.funCallName(oFun));

  }

  /**
   */
  public int getGenerateIndex() {
    return generateIndex;
  }

  /**
   */
  public void setGenerateIndex(int i) {
    generateIndex = i;
  }

  /**
   */
  public int getGenerateMode() {
    return generateMode;
  }

  /**
   */
  public void setGenerateMode(int i) {
    generateMode = i;
  }

  /**
   * reset generate "topcount"
   */
  public void resetGenerate() {
    generateMode = 0;
    generateIndex = -1;
    expGenerate = null;
  }

  /**
   * @return Returns the nHierExclude.
   */
  public int getNHierExclude() {
    return nHierExclude;
  }

  /**
   * @param hierExclude
   *          The nHierExclude to set.
   */
  public void setNHierExclude(int hierExclude) {
    nHierExclude = hierExclude;
  }

  /**
   * only allow expand/collapse left of a "sticky topcount"
   */
  private boolean allowNavigate(Member member, boolean qubon) {
    int iDim = dimIdx(uti.dimForMember(member));
    return allowNavigate(iDim, qubon);
  }

  /**
   * only allow expand/collapse left of a "sticky topcount"
   */
  private boolean allowNavigate(int iDim, boolean qubon) {
    if (qubon && generateIndex >= 0 && generateMode == CalcSet.STICKY && iDim == generateIndex)
      return false;
    else if (!qubon && generateIndex >= 0 && generateMode == CalcSet.STICKY
        && iDim >= generateIndex)
      return false;
    else
      return true;
  }

  /**
   * create new set as union of 2 sets
   */
  private Object unionOfSets(Object set1, Object set2) {
    // create single set as union ow two sets
    int n1 = uti.funCallArgCount(set1);
    int n2 = uti.funCallArgCount(set2);
    Object[] newSet = new Object[n1 + n2];
    int i = 0;
    for (int j = 0; j < n1; j++) {
      newSet[i++] = uti.funCallArg(set1, j);
    }
    for (int j = 0; j < n2; j++) {
      newSet[i++] = uti.funCallArg(set2, j);
    }
    return uti.createFunCall("{}", newSet, QuaxUti.FUNTYPE_BRACES);
  }

  /**
   * @param iHier index of Hierarchy
   * @param list  Member List
   */
  public void setHierMemberList(int iHier, List list) {
    ufMemberLists[iHier] = list;  
  }
  
  /**
   * 
   * @param iHier index of Hierarchy
   * @return true, if the Hierarchy has an unknown function
   */
  public boolean isUnknownFunction( int iHier) {
    return containsUF[iHier];
  }
  
   /**
   * indicate, that an "unknown" Funcall was not handled
   */
  public static class CannotHandleException extends JPivotException {

    /**
     * Constructor for CannotHandleException.
     *
     * @param arg0
     */
    public CannotHandleException(String arg0) {
      super(arg0);
    }
  } // CannotHandleException

} //Quax

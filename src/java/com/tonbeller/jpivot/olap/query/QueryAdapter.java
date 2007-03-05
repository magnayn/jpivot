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

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.core.Extension;
import com.tonbeller.jpivot.core.ModelSupport;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.CalcSet;
import com.tonbeller.jpivot.olap.navi.SortRank;

/**
 * Adapt the MDX query to the model
 */
public abstract class QueryAdapter {

  static Logger logger = Logger.getLogger(QueryAdapter.class);

  protected ModelSupport model;

  protected Quax[] quaxes; // Array of query axis state object
  protected boolean useQuax = false;
  protected boolean axesSwapped = false;
  protected boolean genMDXHierarchize = false;
  protected SortRankBase sortMan = null;

  /**
   * c'tor
   * @param model
   */
  protected QueryAdapter(ModelSupport model) {
    this.model = model;
    Extension sortExt = model.getExtension(SortRank.ID);
    if (sortExt != null) {
      sortMan = (SortRankBase) sortExt;
    }
    axesSwapped = false;
  }

  /**
   * @return
   */
  public Quax[] getQuaxes() {
    return quaxes;
  }

  /**
   * @param quaxs
   */
  public void setQuaxes(Quax[] quaxes) {
    this.quaxes = quaxes;
  }

  /**
    * find the Quax for a specific dimension
    * @param dim Dimension
    * @return Quax containg dimension
    */
  public Quax findQuax(Dimension dim) {
    for (int i = 0; i < quaxes.length; i++) {
      if (quaxes[i].dimIdx(dim) >= 0)
        return quaxes[i];
    }
    return null;
  }

  /**
   * after the startup query was run:
   * get the current positions as array of array of member.
   * Called from Model.getResult after the query was executed. 
   * @param result the result which redefines the query axes
   */
  public void afterExecute(Result result) {

    Axis[] axes = result.getAxes();

    // initialization: get the result positions and set it to quax 
    //   if the quaxes are not yet used to generate the query
    if (!useQuax) {
      AxisLoop : for (int i = 0; i < axes.length; i++) {
        List positions = axes[i].getPositions();
        quaxes[iASwap(i)].init(positions);
      } //AxisLoop
    } else {
      // hierarchize result if neccessary
      int iQuaxToSort = -1;
      if (sortMan != null)
        iQuaxToSort = sortMan.activeQuaxToSort();
      if (!genMDXHierarchize) {
        // not active currently
        Hierarchize : for (int i = 0; i < quaxes.length; i++) {
          if (quaxes[i].isHierarchizeNeeded() && i != iQuaxToSort)
             ((ResultBase) result).hierarchize(iASwap(i));
        }
      }

      QuaxLoop : for (int i = 0; i < quaxes.length; i++) {

        List positions = axes[iASwap(i)].getPositions();

        // after a result for CalcSet.GENERATE was gotten
        //  we have to re-initialize the quax,
        //  so that we can navigate. 
        if (quaxes[i].getGenerateMode() == CalcSet.GENERATE) {
          quaxes[i].resetGenerate();
          quaxes[i].init(positions);
          continue QuaxLoop;
        }

        // unknown function members are collected
        // - always for a "STICKY generate" unknown function
        // - on first result for any other unknown function
        int nDimension = quaxes[i].getNDimension();
        for (int j = 0; j < nDimension; j++) {
          // collect members for unknown functions on quax
          if (quaxes[i].isUnknownFunction(j)) {
            List memList = memListForHier(j, positions);
            quaxes[i].setHierMemberList(j, memList);
          }
        } // for dimensions of quax
      } // QuaxLoop
    }

    if (logger.isDebugEnabled()) {
      // print the result positions to logger
      AxisLoop : for (int i = 0; i < axes.length; i++) {
        List positions = axes[i].getPositions();
        logger.debug("Positions of axis " + i);

        if (positions.size() == 0) {
          // the axis does not have any positions
          logger.debug("0 positions");
        } else {
          int nDimension = ((Position) positions.get(0)).getMembers().length;
          PositionLoop : for (Iterator iter = positions.iterator(); iter.hasNext();) {
            Position pos = (Position) iter.next();
            Member[] mems = pos.getMembers();
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < nDimension; j++) {
              if (j > 0)
                sb.append(" * ");
              Member[] memsj = new Member[j + 1];
              for (int k = 0; k <= j; k++)
                memsj[k] = mems[k];
              if (this.canExpand(memsj))
                sb.append("(+)");
              else if (this.canCollapse(memsj))
                sb.append("(-)");
              else
                sb.append("   ");
              sb.append(((MDXElement) mems[j]).getUniqueName());
            }
            logger.debug(sb.toString());
          } //PositionLoop
        }

      } //AxisLoop
    }
  }

  /**
   * extract members of hier from Result
   * @param hierIndex
   * @return members of hier
   */
  private List memListForHier(int hierIndex, List positions) {
    List memList = new ArrayList();
    PositionLoop : for (Iterator iter = positions.iterator(); iter.hasNext();) {
      Position pos = (Position) iter.next();
      Member m = pos.getMembers()[hierIndex];
      if (!memList.contains(m))
        memList.add(m);
    }
    return memList;
  }

  /**
   * create set expression for list of members 
   * @param memList
   * @return set expression 
   */
  protected abstract Object createMemberSet(List memList);

  // ***************
  // Expand Collapse
  // ***************

  /**
    * find out, whether a member can be expanded.
    * this is true, if 
    * - the member is on an axis  and
    * - the member is not yet expanded  and
    * - the member has children
    * @see com.tonbeller.jpivot.olap.navi.DrillExpand#canExpand(Member)
    * @param Member to be expanded
    * @return true if the member can be expanded
    */
  public abstract boolean canExpand(Member member);

  /**
    * @see com.tonbeller.jpivot.olap.navi.DrillExpand#canExpand(Member)
    * @param position position to be expanded
    * @param Member to be expanded
    * @return true if the member can be expanded
    */
  public abstract boolean canExpand(Member[] pathMembers);

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#canExpand(Member)
   * @param Member to be collapsed
   * @return true if the member can be collapsed
   */
  public abstract boolean canCollapse(Member member);

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#canCollapse(Member)
   * @param position position to be expanded
   * @return true if the position can be collapsed
   */
  public abstract boolean canCollapse(Member[] pathMembers);

  /**
   * expand a member in all positions
   *  this is done by applying ToggleDrillState to the Query
   * 
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#expand(Member)
   * @param Member member to be expanded
   */
  public void expand(Member member) {
    Dimension dim = member.getLevel().getHierarchy().getDimension();
    Quax quax = findQuax(dim);
    if (logger.isInfoEnabled())
      logger.info("expand Member" + poString(null, member));
    if ((quax == null) || !quax.canExpand(member)) {
      logger.fatal("Expand Member failed for" + ((MDXElement) member).getUniqueName());
      //throw new java.lang.IllegalArgumentException("cannot expand");
      return;
    }
    quax.expand(member);
    model.fireModelChanged();
  }

  /**
   * expand a member in a specific position
   * 
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#expand(Member)
   * @param position position to be expanded
   * @param Member member to be expanded
   */
  public void expand(Member[] pathMembers) {
    Member member = pathMembers[pathMembers.length - 1];
    Dimension dim = member.getLevel().getHierarchy().getDimension();
    Quax quax = findQuax(dim);

    if (logger.isDebugEnabled())
      logger.info("expand Path" + poString(pathMembers, null));
    if ((quax == null) || !quax.canExpand(pathMembers)) {
      logger.fatal("Expand failed for" + poString(pathMembers, null));
      throw new java.lang.IllegalArgumentException("cannot expand");
    }

    quax.expand(pathMembers);
    model.fireModelChanged();
  }

  /**
   * collapse a member in all positions
   * 
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#expand(Member)
   * @param Member member to be collapsed
   */
  public void collapse(Member member) {
    Dimension dim = member.getLevel().getHierarchy().getDimension();

    if (logger.isInfoEnabled()) {
      logger.info("collapse " + ((MDXElement) member).getUniqueName());
    }
    Quax quax = findQuax(dim);
    if (quax == null) {
      logger.info("collapse Quax was null " + ((MDXElement) member).getUniqueName());
      return;
    }
    quax.collapse(member);

    model.fireModelChanged();
  }

  /**
    * collapse a member in a specific position
    * 
    * @see com.tonbeller.jpivot.olap.navi.DrillExpand#expand(Member)
    * @param position position to be collapsed
    */
  public void collapse(Member[] pathMembers) {

    if (logger.isDebugEnabled()) {
      logger.debug("collapse" + poString(pathMembers, null));
    }

    Member member = pathMembers[pathMembers.length - 1];
    Dimension dim = member.getLevel().getHierarchy().getDimension();
    Quax quax = findQuax(dim);
    if (quax == null) {
      logger.debug("collapse Quax was null" + poString(pathMembers, null));
      return;
    }

    quax.collapse(pathMembers);
    model.fireModelChanged();
  }

  // ************
  // DrillReplace
  // ************

  /**
   * drill down is possible if <code>member</code> has children
   */
  public abstract boolean canDrillDown(Member member);

  /**
   * drill up is possible if
   * at least one member in the tree is not at the top level of this hierarchy.
   */
  public boolean canDrillUp(Hierarchy hier) {

    Quax quax = findQuax(hier.getDimension());
    return (quax == null) ? false : quax.canDrillUp(hier);
  }

  /**
   * After switch to Qubon mode:
   * replaces the members. Let <code>H</code> be the hierarchy
   * that member belongs to. Then drillDown will replace all members from <code>H</code>
   * that are currently visible with the children of <code>member</code>.
   */
  public void drillDown(Member member) {

    // switch to Qubon mode, if not yet in
    Quax quax = findQuax(member.getLevel().getHierarchy().getDimension());

    if (quax == null) {
        logger.info("drillDown Quax was null" + poString(null, member));
        return;
    }

    // replace dimension iDim by monMember.children
    quax.drillDown(member);

    model.fireModelChanged();

    if (logger.isInfoEnabled()) {
      logger.info("drillDown " + poString(null, member));
    }
  }

  /**
   * After switch to Qubon mode:
   * replaces all visible members of hier with the members of the
   * next higher level.
   */
  public void drillUp(Hierarchy hier) {

    // switch to Qubon mode, if not yet in
    Quax quax = findQuax(hier.getDimension());
    if (quax == null) {
        logger.info("drillUp Hierarchy Quax was null" + hier.getLabel());
        return;
    }
    quax.drillUp(hier);

    model.fireModelChanged();

    if (logger.isInfoEnabled())
      logger.info("drillUp Hierarchy " + hier.getLabel());
  }

  // ********
  // misc
  // ********

  /**
   * @return true, if axes are currently swapped
   */
  public boolean isSwapAxes() {
    return axesSwapped;
  }

  /**
   * swap axis index if neccessary
   * @param original index
   * @return swapped index
   */
  public int iASwap(int i) {
    if (axesSwapped)
      return (i + 1) % 2;
    else
      return i;
  }

  /**
   * check, whether a parent.children Funcall is on any axis
   */
  /*
  public boolean isChildrenOnAxis(Member parent) {
    Quax quax = findQuax(parent.getLevel().getHierarchy().getDimension());
    return quax.isChildrenOnAxis(parent);
  }
  */

  // ********
  // Internal
  // ********

  /** 
   * display position member for debugging purposes
   * @param posMembers
   * @param member
   * @return
   */
  protected String poString(Member[] posMembers, Member member) {
    StringBuffer sb = new StringBuffer();
    if (posMembers != null) {
      sb.append(" Position=");
      for (int i = 0; i < posMembers.length; i++) {
        if (i > 0)
          sb.append(" ");
        sb.append(((MDXElement) posMembers[i]).getUniqueName());
      }
    }
    if (member != null) {
      sb.append(" Member=");
      sb.append(((MDXElement) member).getUniqueName());
    }
    return sb.toString();
  }


  /**
   * @return true if quas is to be used
   */
  public boolean isUseQuax() {
    return useQuax;
  }

  /**
   * @param b - true if quas is to be used
   */
  public void setUseQuax(boolean b) {
    useQuax = b;
  }

  // ********
  // Interface QueryAdapterHolder
  // ********

  /**
   * ask a QueryAdapterHolder to get a query adapter
   */
  public interface QueryAdapterHolder {
    QueryAdapter getQueryAdapter();
  }

} //QueryAdapter

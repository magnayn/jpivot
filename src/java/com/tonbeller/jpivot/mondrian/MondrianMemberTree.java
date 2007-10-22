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
package com.tonbeller.jpivot.mondrian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import mondrian.olap.ResultLimitExceededException;
import mondrian.olap.SchemaReader;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.MemberTree;
import com.tonbeller.jpivot.olap.query.Quax;

/**
 * Implementation of the DrillExpand Extension for Mondrian Data Source.
 */
public class MondrianMemberTree extends ExtensionSupport implements MemberTree {

  static Logger logger = Logger.getLogger(MondrianMemberTree.class);

  /**
   * Constructor sets ID
   */
  public MondrianMemberTree() {
    super.setId(MemberTree.ID);
  }

  /**
   * @return the root members of a hierarchy. This is for example the "All"
   *         member or the list of measures.
   */
  public Member[] getRootMembers(Hierarchy hier) {
    try {
      return internalGetRootMembers(hier);
    } catch (ResultLimitExceededException e) {
      logger.error(null, e);
      throw new TooManyMembersException(e);
    }
  }

  private Member[] internalGetRootMembers(Hierarchy hier) {
    MondrianModel model = (MondrianModel) getModel();
    mondrian.olap.Hierarchy monHier = ((MondrianHierarchy) hier).getMonHierarchy();
    mondrian.olap.Query q = ((MondrianQueryAdapter) model.getQueryAdapter()).getMonQuery();
    // Use the schema reader from the query, because it contains calculated
    // members defined in both the cube and the query.
    SchemaReader scr = model.getSchemaReader();
    mondrian.olap.Member[] monMembers = scr.getHierarchyRootMembers(monHier);
    ArrayList aMem = new ArrayList();
    final List visibleRootMembers = new ArrayList();
    int k = monMembers.length;
    for (int i = 0; i < k; i++) {
      mondrian.olap.Member monMember = monMembers[i];
      if (isVisible(monMember)) {
        aMem.add(model.addMember(monMembers[i]));
      }
    }

    // find the calculated members for this hierarchy
    //  show them together with root level members
     mondrian.olap.Formula[] formulas = q.getFormulas();
    for (int i = 0; i < formulas.length; i++) {
      mondrian.olap.Formula f = formulas[i];
      mondrian.olap.Member monMem = f.getMdxMember();
      if (monMem != null) {
        // is the member for this hierarchy,
        // and is it visible?
        // if yes add it
        if (monMem.getHierarchy().equals(monHier)) {
          if (!isVisible(monMem))
            continue;
          Member m = model.addMember(monMem);
          if (!aMem.contains(m))
            aMem.add(m);
        }
      }
    }

    // order members according to occurrence in query result
    //  if there is no result available, do not sort
    Result res = model.currentResult();
    if (res != null) {
        // locate the appropriate result axis
        // find the Quax for this hier
        MondrianQueryAdapter adapter = (MondrianQueryAdapter) model.getQueryAdapter();
        Quax quax = adapter.findQuax(hier.getDimension());
        if (quax != null) {
            int iDim = quax.dimIdx(hier.getDimension());
            int iAx = quax.getOrdinal();
            if (adapter.isSwapAxes())
              iAx = (iAx + 1) % 2;
            Axis axis = res.getAxes()[iAx];
            List positions = axis.getPositions();

            for (Iterator iter = positions.iterator(); iter.hasNext();) {
              Position pos = (Position) iter.next();
              Member[] posMembers = pos.getMembers();
              MondrianMember mem = (MondrianMember) posMembers[iDim];
              // only add hierarchy items from the query results
              // if they are actually in in the currently expanding hierarchy!!
              if (mem.getMonMember().getHierarchy().equals(monHier)) {             
                if (!(mem.getMonMember().getParentMember() == null))
                  continue; // ignore, not root
                if (!visibleRootMembers.contains(mem))
                  visibleRootMembers.add(mem);
  
                // Check if the result axis contains invisible members
                if (!aMem.contains(mem)) {
                    aMem.add(mem);
                }
              }
            }
        }
    }

    Member[] members = (Member[]) aMem.toArray(new Member[0]);

    // If there is no query result, do not sort
    if (visibleRootMembers.size() != 0) {
        Arrays.sort(members, new Comparator() {
          public int compare(Object arg0, Object arg1) {
            Member m1 = (Member) arg0;
            Member m2 = (Member) arg1;
            int index1 = visibleRootMembers.indexOf(m1);
            int index2 = visibleRootMembers.indexOf(m2);
            if (index2 == -1)
              return -1; // m2 is higher, unvisible to the end
            if (index1 == -1)
              return 1; // m1 is higher, unvisible to the end
            return index1 - index2;
          }
        });
    }

    return members;
  }

  private boolean isVisible(mondrian.olap.Member monMember) {
    // Name convention: if member starts with "." its hidden
    if (monMember.getName().startsWith("."))
      return false;

    MondrianModel model = (MondrianModel) getModel();
    // Use the schema reader from the query, because it contains calculated
    // members defined in both the cube and the query.
    SchemaReader scr = model.getSchemaReader();

    return MondrianUtil.isVisible(scr, monMember);
  }

  /**
   * @return true if the member has children
   */
  public boolean hasChildren(Member member) {
    mondrian.olap.Member monMember = ((MondrianMember) member).getMonMember();
    if (monMember.isCalculatedInQuery())
      return false;
    if (monMember.getLevel().getChildLevel() != null)
      return true;
    // here for a leaf-level, but also for a level in a parent-child hierarchy:
    MondrianModel model = (MondrianModel) getModel();

    SchemaReader scr = model.getSchemaReader();
    return scr.isDrillable(monMember);
  }

  /**
   * @return the children of the member
   */
  public Member[] getChildren(Member member) {
    try {
      return internalGetChildren(member);
    } catch (ResultLimitExceededException e) {
      logger.error(null, e);
      throw new TooManyMembersException(e);
    }
  }

  private Member[] internalGetChildren(Member member) {
    mondrian.olap.Member monMember = ((MondrianMember) member).getMonMember();
    //  unreliable: always null in a parent-child hierarch
    // if (monMember.getLevel().getChildLevel() == null)
    //   return null;

    MondrianModel model = (MondrianModel) getModel();

    SchemaReader scr = model.getSchemaReader();
    mondrian.olap.Member[] monChildren = scr.getMemberChildren(monMember);

    List list = new ArrayList(monChildren.length);
    for (int i = 0; i < monChildren.length; i++) {
        mondrian.olap.Member m = monChildren[i];
        if (MondrianUtil.isVisible(scr, m)) {
            list.add(model.addMember(m));
        }
    }
    Member[] children = (Member[]) list.toArray(new Member[list.size()]);
    return children;
  }

  /**
   * @return the parent of member or null, if this is a root member
   */
  public Member getParent(Member member) {
    mondrian.olap.Member monMember = ((MondrianMember) member).getMonMember();

    MondrianModel model = (MondrianModel) getModel();

    SchemaReader scr = model.getSchemaReader();
    mondrian.olap.Member monParent = scr.getMemberParent(monMember);
    if (monParent == null)
      return null; // already top level
    Member parent = model.addMember(monParent);

    return parent;
  }

} // End MondrianMemberTree

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
package com.tonbeller.jpivot.xmla;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.mdxparse.Formula;
import com.tonbeller.jpivot.olap.mdxparse.ParsedQuery;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.navi.MemberTree;
import com.tonbeller.jpivot.util.StringUtil;

/**
 * Member Tree Implementation vor XMLA
 */
public class XMLA_MemberTree extends ExtensionSupport implements MemberTree {

  static Logger logger = Logger.getLogger(XMLA_MemberTree.class);

  /**
   * Constructor sets ID
   */
  public XMLA_MemberTree() {
    super.setId(MemberTree.ID);
  }

  /**
   * @return the root members of a hierarchy. This is for example
   * the "All" member or the list of measures.
   */
  public Member[] getRootMembers(Hierarchy hier) {
    XMLA_Model model = (XMLA_Model) getModel();
    Level[] levels = hier.getLevels();
    // get root level
    XMLA_Level rootLevel = null;
    for (int i = 0; i < levels.length; i++) {
      XMLA_Level xLev = (XMLA_Level) levels[i];
      if (xLev.getDepth() == 0) {
        rootLevel = xLev;
        break;
      }
    }
    if (rootLevel == null)
      return null; // should not occur

    Member[] rootMembers = new Member[0];
    try {
      rootMembers = rootLevel.getMembers();
    } catch (OlapException e) {
      logger.error(e);
    }
    // find the calculated members for this hierarchy
    //  show them together with root level members
    ArrayList aCalcMem = new ArrayList();
    ParsedQuery pq = ((XMLA_QueryAdapter) model.getQueryAdapter()).getParsedQuery();
    Formula[] formulas = pq.getFormulas();

    for (int i = 0; i < formulas.length; i++) {
      Formula f = formulas[i];
      if (!f.isMember())
        continue;

      String dimUMember = StringUtil.bracketsAround(f.getFirstName());
      String dimUHier = ((XMLA_Hierarchy) hier).getUniqueName();
      if (!(dimUHier.equals(dimUMember)))
        continue;

      String memberName = f.getUniqeName();
      XMLA_Member calcMem = (XMLA_Member) model.lookupMemberByUName(memberName);
      if (calcMem == null) {
        calcMem = new XMLA_Member(model, memberName, f.getLastName(), null, true);
      }
      aCalcMem.add(calcMem);
    }

    Member[] members = new Member[rootMembers.length + aCalcMem.size()];
    int k = rootMembers.length;
    for (int i = 0; i < k; i++) {
      members[i] = rootMembers[i];
    }
    for (Iterator iter = aCalcMem.iterator(); iter.hasNext();) {
      XMLA_Member calcMem = (XMLA_Member) iter.next();
      members[k++] = calcMem;
    }

    return members;
  }

  /**
   * @return true if the member has children
   */
  public boolean hasChildren(Member member) {

    XMLA_Member m = (XMLA_Member) member;
    if (m.isCalculated())
      return false;
    long ccard = m.getChildrenCardinality(); // -1 if not initialized
    if (ccard >= 0)
      return (ccard > 0);
    XMLA_Level xLev = (XMLA_Level) member.getLevel();
    if (xLev == null || xLev.getChildLevel() == null)
      return false;
    return true;
  }

  /**
   * @return the children of the member
   */
  public Member[] getChildren(Member member) {

    XMLA_Level xLev = (XMLA_Level) member.getLevel();

    if (xLev == null || xLev.getChildLevel() == null)
      return null;

    Member[] children = new Member[0];
    try {
      children = ((XMLA_Member) member).getChildren();
    } catch (OlapException e) {
      logger.error("?", e);
      return null;
    }
    return children;
  }

  /**
   * @return the parent of member or null, if this is a root member
   */
  public Member getParent(Member member) {

    XMLA_Level xLev = (XMLA_Level) member.getLevel();

    if (xLev == null || xLev.getDepth() == 0)
      return null; // already top level

    XMLA_Member parent = null;
    try {
      parent = (XMLA_Member) ((XMLA_Member) member).getParent();
    } catch (OlapException e) {
      logger.error("?", e);
      return null;
    }

    return parent;
  }

} // End XMLA_MemberTree

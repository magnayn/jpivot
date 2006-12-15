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
import java.util.Iterator;
import java.util.List;

import mondrian.olap.AxisOrdinal;
import mondrian.olap.Exp;
import mondrian.olap.QueryAxis;
import mondrian.olap.Syntax;
import mondrian.mdx.UnresolvedFunCall;
import mondrian.mdx.MemberExpr;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.ChangeSlicer;

/**
 * @author hh
 */
public class MondrianChangeSlicer extends ExtensionSupport implements ChangeSlicer {

  static Logger logger = Logger.getLogger(MondrianChangeSlicer.class);

  /**
   * Constructor sets ID
   */
  public MondrianChangeSlicer() {
    super.setId(ChangeSlicer.ID);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.ChangeSlicer#getSlicer()
   */
  public Member[] getSlicer() {

    MondrianModel model = (MondrianModel) getModel();
    // use result rather than query
    Result res = null;
    try {
      res = model.getResult();
    } catch (OlapException ex) {
      // do not handle
      return new Member[0];
    }

    Axis slicer = res.getSlicer();
    List positions = slicer.getPositions();
    List members = new ArrayList();
    for (Iterator iter = positions.iterator(); iter.hasNext();) {
      Position pos = (Position) iter.next();
      Member[] posMembers = pos.getMembers();
      for (int i = 0; i < posMembers.length; i++) {
        if (!members.contains(posMembers[i]))
          members.add(posMembers[i]);
      }
    }

    return (Member[]) members.toArray(new Member[0]);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.ChangeSlicer#setSlicer(Member[])
   */
  public void setSlicer(Member[] members) {
    MondrianModel model = (MondrianModel) getModel();
    MondrianQueryAdapter adapter = (MondrianQueryAdapter) model.getQueryAdapter();
    mondrian.olap.Query monQuery = adapter.getMonQuery();

    boolean logInfo = logger.isInfoEnabled();

    if (members.length == 0) {
      // empty slicer
      monQuery.setSlicerAxis(null);
      if (logInfo)
        logger.info("slicer set to null");
    } else {
      Exp[] monExpr = new Exp[members.length];
      for (int i = 0; i < monExpr.length; i++) {
        monExpr[i] = createExpressionFor(monQuery, (MondrianMember) members[i]);
      }

      UnresolvedFunCall f = new UnresolvedFunCall("()", Syntax.Parentheses, monExpr);
      monQuery.setSlicerAxis(new QueryAxis(false, f, AxisOrdinal.SLICER, QueryAxis.SubtotalVisibility.Undefined));
      if (logInfo) {
        StringBuffer sb = new StringBuffer("slicer=(");
        for (int i = 0; i < monExpr.length; i++) {
          if (i > 0)
            sb.append(",");
          sb.append(monExpr[i].toString());
        }
        sb.append(")");
        logger.info(sb.toString());
      }
    }
    model.fireModelChanged();
  }

  protected Exp createExpressionFor(mondrian.olap.Query monQuery, MondrianMember member) {
    return new MemberExpr(member.getMonMember());
  }

} // End MondrianChangeSlicer

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

import mondrian.mdx.MemberExpr;
import mondrian.mdx.UnresolvedFunCall;
import mondrian.olap.Exp;
import mondrian.olap.FunCall;
import mondrian.olap.Literal;
import mondrian.olap.Syntax;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.olap.navi.SortRank;
import com.tonbeller.jpivot.olap.query.QuaxChangeListener;
import com.tonbeller.jpivot.olap.query.SortRankBase;

/**
 * @author hh
 *
 * Implementation of the Sort Extension for Mondrian Data Source.
 */
public class MondrianSortRank extends SortRankBase implements SortRank, QuaxChangeListener {

  static Logger logger = Logger.getLogger(MondrianSortRank.class);

  public MondrianSortRank() {
    super.setId(SortRank.ID);
  }

  /**
   * apply sort to mondrian query
   */
  public void addSortToQuery() {
    if (sorting && sortPosMembers != null) {
      MondrianModel model = (MondrianModel) getModel();
      mondrian.olap.Query monQuery = ((MondrianQueryAdapter) model.getQueryAdapter()).getMonQuery();

      switch (sortMode) {
      case com.tonbeller.jpivot.olap.navi.SortRank.ASC:
      case com.tonbeller.jpivot.olap.navi.SortRank.DESC:
      case com.tonbeller.jpivot.olap.navi.SortRank.BASC:
      case com.tonbeller.jpivot.olap.navi.SortRank.BDESC:
        // call sort
        orderAxis(monQuery, sortMode);
        bSortOnQuery = true;
        break;
      case com.tonbeller.jpivot.olap.navi.SortRank.TOPCOUNT:
        topBottomAxis(monQuery, "TopCount");
        bSortOnQuery = true;
        break;
      case com.tonbeller.jpivot.olap.navi.SortRank.BOTTOMCOUNT:
        topBottomAxis(monQuery, "BottomCount");
        bSortOnQuery = true;
        break;
      default:
        return; // do nothing
      }
    }
  }

  /**
   * Convert sort mode ordinal to sort mode name
   * @param sortMode mode
   * @return name of sort mode
   */
   static private String sortModeName(int sortMode) {
     switch (sortMode) {
       case com.tonbeller.jpivot.olap.navi.SortRank.ASC :
         return "ASC";
       case com.tonbeller.jpivot.olap.navi.SortRank.DESC :
         return "DESC";
       case com.tonbeller.jpivot.olap.navi.SortRank.BASC :
         return "BASC";
       case com.tonbeller.jpivot.olap.navi.SortRank.BDESC :
         return "BDESC";
       default :
         return null;
     }
   }

  /**
   * add Order Funcall to QueryAxis
   * @param monQuery
   * @param sortMode
   */
  private void orderAxis(mondrian.olap.Query monQuery, int sortMode) {
    // Order(TopCount) is allowed, Order(Order) is not permitted
    //removeTopLevelSort(monQuery, new String[] { "Order" });
    mondrian.olap.QueryAxis monAx = monQuery.getAxes()[quaxToSort.getOrdinal()];
    Exp setForAx = monAx.getSet();
    Exp memToSort;
    if (sortPosMembers.length == 1) {
      memToSort = new MemberExpr(((MondrianMember) sortPosMembers[0]).getMonMember());
    } else {
      MemberExpr[] memberExprs = new MemberExpr[sortPosMembers.length];
      for (int i = 0; i < memberExprs.length; i++) {
        memberExprs[i] = new MemberExpr(((MondrianMember) sortPosMembers[i]).getMonMember());
      }
      memToSort = new UnresolvedFunCall("()", Syntax.Parentheses, memberExprs);
    }
    String sDirection = sortModeName(sortMode);
    UnresolvedFunCall funOrder = new UnresolvedFunCall("Order", new Exp[] { setForAx, memToSort,
        Literal.createSymbol(sDirection)});
    monAx.setSet(funOrder);
  }

  /**
   * add Top/BottomCount Funcall to QueryAxis
   */
  private void topBottomAxis(mondrian.olap.Query monQuery, String function) {
    // TopCount(TopCount) and TopCount(Order) is not permitted
    //removeTopLevelSort(monQuery, new String[] { "TopCount", "BottomCount", "Order" });
    mondrian.olap.QueryAxis monAx = monQuery.getAxes()[quaxToSort.getOrdinal()];
    Exp setForAx = monAx.getSet();
    Exp memToSort;
    if (sortPosMembers.length > 1) {
      MemberExpr[] memberExprs = new MemberExpr[sortPosMembers.length];
      for (int i = 0; i < memberExprs.length; i++) {
        memberExprs[i] = new MemberExpr(((MondrianMember) sortPosMembers[i]).getMonMember());
      }
      memToSort = new UnresolvedFunCall("()", Syntax.Parentheses, memberExprs);
    } else {
      memToSort = new MemberExpr(((MondrianMember) sortPosMembers[0]).getMonMember());
    }
    UnresolvedFunCall funOrder = new UnresolvedFunCall(function, new Exp[] { setForAx,
        Literal.create(new Integer(topBottomCount)), memToSort});
    monAx.setSet(funOrder);
  }

  /**
   * remove sort function from query axis
   */
  public void removeTopLevelSort() {
    if (!bSortOnQuery)
      return;
    MondrianModel model = (MondrianModel) getModel();
    mondrian.olap.Query monQuery = ((MondrianQueryAdapter) model.getQueryAdapter()).getMonQuery();
    mondrian.olap.QueryAxis monAx = monQuery.getAxes()[quaxToSort.getOrdinal()];
    Exp setForAx = monAx.getSet();
    // if the axis set is *not* a Ordering function, return
    if (!(setForAx instanceof FunCall))
      return;
    FunCall funOrder = (FunCall) setForAx;
    String funame = funOrder.getFunName();
    if ("Order".equalsIgnoreCase(funame) || "TopCount".equalsIgnoreCase(funame)
        || "BottomCount".equalsIgnoreCase(funame)) {
      Exp set0 = funOrder.getArg(0);
      monAx.setSet(set0);
      bSortOnQuery = false;
    }
  }

} // End MondrianSortRank

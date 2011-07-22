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
import java.util.List;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.mdxparse.FunCall;
import com.tonbeller.jpivot.olap.mdxparse.ParsedQuery;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.ChangeSlicer;
import com.tonbeller.jpivot.olap.query.MDXElement;
import com.tonbeller.jpivot.util.ArrayUtil;

/**
 * change slicer extension
 */
public class XMLA_ChangeSlicer extends ExtensionSupport implements ChangeSlicer {

  static Logger logger = Logger.getLogger(XMLA_ChangeSlicer.class);

  /**
   * Constructor sets ID
   */
  public XMLA_ChangeSlicer() {
    super.setId(ChangeSlicer.ID);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.ChangeSlicer#getSlicer()
   */
  public Member[] getSlicer() {

    XMLA_Model model = (XMLA_Model) getModel();
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
    XMLA_Model model = (XMLA_Model) getModel();
    XMLA_QueryAdapter adapter = (XMLA_QueryAdapter) model.getQueryAdapter();
    ParsedQuery pq = adapter.getParsedQuery();

    boolean logInfo = logger.isInfoEnabled();

    if (members.length == 0) {
      // empty slicer
      pq.setSlicer(null); // ???
      if (logInfo)
        logger.info("slicer set to null");
    } else {
      FunCall f = new FunCall("()",(XMLA_Member[]) ArrayUtil.naturalCast(members), FunCall.TypeParentheses);
      pq.setSlicer(f);
      if (logInfo) {
        StringBuffer sb = new StringBuffer("slicer=(");
        for (int i = 0; i < members.length; i++) {
          if (i > 0)
            sb.append(",");
          sb.append(((MDXElement)members[i]).getUniqueName());
        }
        sb.append(")");
        logger.info(sb.toString());
      }
    }

    model.fireModelChanged();
  }

} // End XMLA_ChangeSlicer

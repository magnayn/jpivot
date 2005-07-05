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
package com.tonbeller.jpivot.test.olap;

import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.jpivot.olap.model.Expression;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.navi.ExpressionParser;

public class TestExpressionParser extends TestExtensionSupport implements ExpressionParser {

  public Expression parse(String name) throws InvalidSyntaxException {
    Dimension[] dims = model().getDimensions();
    for (int i = 0; i < dims.length; i++) {
      Dimension d = dims[i];
      if (name.equals(d.getLabel()))
        return d;
      Expression e = findHierarchies(name, d.getHierarchies());
      if (e != null)
        return e;
    }
    // not a dim, hierarchy, level. create member!
    TestMember tm = new TestMember();
    tm.setLabel(name);
    return tm;
  }
  
  private Expression findHierarchies(String name, Hierarchy[] hiers) {
    for (int i = 0; i < hiers.length; i++) {
      Hierarchy h = hiers[i];
      if (name.equals(h.getLabel()))
        return h;
      Expression e = findLevels(name, h.getLevels());
      if (e != null)
        return e;
    }
    return null;
  }
  
  private Expression findLevels(String name, Level[] levels) {
    for (int i = 0; i < levels.length; i++) {
      Level l = levels[i];
      if (name.equals(l.getLabel()))
        return l;
    }
    return null;
  }

  public String unparse(Expression expr) {
    return ((Displayable) expr).getLabel();
  }

  public Member lookupMember(String uniqueName) throws InvalidSyntaxException {
    Object obj = parse(uniqueName);
    if (obj instanceof Member)
      return (Member)obj;
    return null;
  }

  public Level lookupLevel(String uniqueName) throws InvalidSyntaxException {
    Object obj = parse(uniqueName);
    if (obj instanceof Level)
      return (Level)obj;
    return null;
  }

  public Hierarchy lookupHierarchy(String uniqueName) throws InvalidSyntaxException {
    Object obj = parse(uniqueName);
    if (obj instanceof Hierarchy)
      return (Hierarchy)obj;
    return null;
  }

  public Dimension lookupDimension(String uniqueName) throws InvalidSyntaxException {
    Object obj = parse(uniqueName);
    if (obj instanceof Dimension)
      return (Dimension)obj;
    return null;
  }

}
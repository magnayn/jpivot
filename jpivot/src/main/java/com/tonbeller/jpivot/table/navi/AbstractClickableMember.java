/*
 * Copyright (c) 1971-2003 TONBELLER AG, Bensheim.
 * All rights reserved.
 */
package com.tonbeller.jpivot.table.navi;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Expression;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.navi.ExpressionParser;
import com.tonbeller.jpivot.olap.navi.ExpressionParser.InvalidSyntaxException;
import com.tonbeller.jpivot.table.ClickableMember;
import com.tonbeller.jpivot.table.TableComponent;
import com.tonbeller.wcf.controller.RequestContext;

public abstract class AbstractClickableMember implements  ClickableMember {
  private static final Logger logger = Logger.getLogger(AbstractClickableMember.class);
  
  boolean suppressAllMember = true;
  boolean suppressCalcMember = true;
  boolean suppressMeasures = true;

  /**
   * parsed version of uniqueName
   */
  private Expression expression;

  /**
   * needed to format the urlPattern
   */
  protected ExpressionParser parser;

  /**
   * unique name of the member, level, hierarchy or dimension which shall
   * be clickable.
   */
  private String uniqueName;

  protected AbstractClickableMember(String uniqueName) {
    this.uniqueName = uniqueName;
  }

  protected boolean match(Member member) {
    if (suppressCalcMember && member.isCalculated())
      return false;
    if (suppressAllMember && member.isAll())
      return false;
    if (suppressMeasures && member.getLevel().getHierarchy().getDimension().isMeasure())
      return false;
    
    // wenn kein Name angegeben wurde, dann akzeptieren wir alle Member
    if (empty(uniqueName))
      return true;
    
    if (parser == null || expression == null)
      return false;
    if (expression instanceof Level)
      return expression.equals(member.getLevel());
    if (expression instanceof Hierarchy)
      return expression.equals(member.getLevel().getHierarchy());
    if (expression instanceof Dimension)
      return expression.equals(member.getLevel().getHierarchy().getDimension());
    throw new IllegalArgumentException("unknown type: " + uniqueName);
  }

  private boolean empty(String s) {
    return s == null || s.length() == 0;
  }

  public void startRendering(RequestContext context, TableComponent table) {
    expression = null;
    parser = null;
    OlapModel model = table.getOlapModel();
    
    // wenn kein uniqueName angegeben ist, müssen wir auch nichts parsen
    if (empty(uniqueName))
      return;
    
    parser = (ExpressionParser) model.getExtension(ExpressionParser.ID);
    if (parser != null) {
      try {
        // we do not use parser.parse() here because it searches for member names too. This
        // makes a lot(!) of SQL queries - so we restrict ourselves to level, hierarchy, dimension.
        expression = parser.lookupLevel(uniqueName);
        if (expression == null)
          expression = parser.lookupHierarchy(uniqueName);
        if (expression == null)
          expression = parser.lookupDimension(uniqueName);
      } catch (InvalidSyntaxException e) {
        // we do not throw an exception here. If the user 
        // has entered an invalid value, it will be ignored
        logger.warn(null, e);
      }
    }
  }

  public void stopRendering() {
    parser = null;
    expression = null;
  }

  protected boolean isSuppressAllMember() {
    return suppressAllMember;
  }

  protected void setSuppressAllMember(boolean suppressAllMember) {
    this.suppressAllMember = suppressAllMember;
  }

  protected boolean isSuppressCalcMember() {
    return suppressCalcMember;
  }

  protected void setSuppressCalcMember(boolean suppressCalcMember) {
    this.suppressCalcMember = suppressCalcMember;
  }

  protected boolean isSuppressMeasures() {
    return suppressMeasures;
  }

  protected void setSuppressMeasures(boolean suppressMeasures) {
    this.suppressMeasures = suppressMeasures;
  }

}

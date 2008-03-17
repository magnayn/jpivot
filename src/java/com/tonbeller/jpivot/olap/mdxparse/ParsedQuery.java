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
package com.tonbeller.jpivot.olap.mdxparse;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.olap.model.OlapException;

/**
 * this is the result of parsing an MDX 
 */
public class ParsedQuery implements Exp {

  static Logger logger = Logger.getLogger(ParsedQuery.class);

  List formulas = new ArrayList();
  Map paraMap = new Hashtable();
  //List paramRefs = new ArrayList();

  ArrayList axisDef = null;
  String cube;
  List cellProps = new ArrayList();

  Exp slicer = null;

  private QueryAxis[] axes = new QueryAxis[0];

  /**
   * called after parse to make things easier
   * 
   */
  public void afterParse() throws OlapException {
    if (axisDef != null) {
      axes = (QueryAxis[]) axisDef.toArray(new QueryAxis[0]);
      axisDef = null;
    }
    // collect Parameters
    collectParams();
  }

  /**
   * 
   * @return QueryAxis[]
   */
  public QueryAxis[] getAxes() {
    return this.axes;
  }

  /**
   * 
   * @param axes
   */
  public void setAxes(QueryAxis[] axes) {
    this.axes = axes;
  }

  /**
   * Returns the cube.
   * @return String
   */
  public String getCube() {
    // remove enclosing brackets, if there
    if (cube.charAt(0) == '[' && cube.charAt(cube.length() - 1) == ']')
      return cube.substring(1, cube.length() - 1);
    else
      return cube;
  }

  /**
   * Sets the cube.
   * @param cube The cube to set
   */
  public void setCube(String cube) {
    this.cube = cube;
  }

  /**
   * get the formulas of this query
   * @return Formula[]
   */
  public Formula[] getFormulas() {
    return (Formula[]) formulas.toArray(new Formula[0]);
  }

  public String toMdx() {
    StringBuffer mdx = new StringBuffer();
    boolean isFollow;
    if (formulas.size() > 0) {
      mdx.append("WITH ");
      for (Iterator iter = formulas.iterator(); iter.hasNext();) {
        mdx.append(' ');
        Formula form = (Formula) iter.next();
        mdx.append(form.toMdx());
      }
      mdx.append(' ');
    }
    mdx.append("SELECT ");
    isFollow = false;
    for (int i = 0; i < axes.length; i++) {
      QueryAxis qa = axes[i];
      if (isFollow)
        mdx.append(", ");
      isFollow = true;
      mdx.append(qa.toMdx());
    }
    mdx.append(" FROM ");
    mdx.append(cube);

    if (slicer != null) {
      mdx.append(" WHERE ");
      mdx.append(slicer.toMdx());
    }

    // add CELL PROPERTIES VALUE, FORMATTED_VALUE, ...
    if (cellProps.size() > 0) {
      mdx.append(" CELL PROPERTIES VALUE, FORMATTED_VALUE");
      for (Iterator iter = cellProps.iterator(); iter.hasNext();) {
        CompoundId cid = (CompoundId) iter.next();
        String str = cid.toMdx();
        if (str.equalsIgnoreCase("VALUE"))
          continue; // default
        if (str.equalsIgnoreCase("FORMATTED_VALUE"))
          continue; // default
        mdx.append(" ,");
        mdx.append(str);
      }
    }
    return mdx.toString();
  }


  public String toDrillMdx() {
    StringBuffer mdx = new StringBuffer();
    boolean isFollow;
    if (formulas.size() > 0) {
      mdx.append("WITH ");
      for (Iterator iter = formulas.iterator(); iter.hasNext();) {
        mdx.append(' ');
        Formula form = (Formula) iter.next();
        mdx.append(form.toMdx());
      }
      mdx.append(' ');
    }
    // dsf
    mdx.append("DRILLTHROUGH SELECT ");

    isFollow = false;
    for (int i = 0; i < axes.length; i++) {
      QueryAxis qa = axes[i];
      if (isFollow)
        mdx.append(", ");
      isFollow = true;
      mdx.append(qa.toMdx());
    }
    mdx.append(" FROM ");
    mdx.append(cube);

    if (slicer != null) {
      mdx.append(" WHERE ");
      mdx.append(slicer.toMdx());
    }

    // add CELL PROPERTIES VALUE, FORMATTED_VALUE, ...
    if (cellProps.size() > 0) {
      mdx.append(" CELL PROPERTIES VALUE, FORMATTED_VALUE");
      for (Iterator iter = cellProps.iterator(); iter.hasNext();) {
        CompoundId cid = (CompoundId) iter.next();
        String str = cid.toMdx();
        if (str.equalsIgnoreCase("VALUE"))
          continue; // default
        if (str.equalsIgnoreCase("FORMATTED_VALUE"))
          continue; // default
        mdx.append(" ,");
        mdx.append(str);
      }
    }
    return mdx.toString();
  }

  
  
  
  /**
   * 
   * @see java.lang.Object#clone()
   */
  public Object clone() {
    ParsedQuery cloned = new ParsedQuery();
    if (formulas.size() > 0) {
      ArrayList clonedFormulas = new ArrayList();
      for (Iterator iter = formulas.iterator(); iter.hasNext();) {
        Formula form = (Formula) iter.next();
        clonedFormulas.add(form.clone());
      }
      cloned.formulas = clonedFormulas;
    }
    if (axes.length > 0) {
      QueryAxis[] clonedAxes = new QueryAxis[axes.length];
      for (int i = 0; i < clonedAxes.length; i++) {
        clonedAxes[i] = (QueryAxis) axes[i].clone();
      }
      cloned.setAxes(clonedAxes);
    }
    if (slicer != null)
      cloned.slicer = (Exp) this.slicer.clone();
    
    // you need to wrap the cube in backets again!
    // Why does getCube remove the brackets?
    cloned.setCube("[" + this.getCube() + "]");
    return cloned;
  }

  /**
   * @return sliecer exp
   */
  public Exp getSlicer() {
    return slicer;
  }

  /**
   * set the slicer exp
   * @param exp
   */
  public void setSlicer(Exp exp) {
    slicer = exp;
  }

  /**
   * Collect Parameters and Parameter references
   */
  private void collectParams() throws OlapException {

    // walk Formulas
    int iAxis = -2;
    for (Iterator iter = formulas.iterator(); iter.hasNext();) {
      Formula formula = (Formula) iter.next();
      walkTreeForParams(formula.getExp(), iAxis);
      // dont forget the member properties
      for(int i = 0; i < formula.memberProperties.length; i++) {
        walkTreeForParams(formula.memberProperties[i].getExp(), iAxis);
      }
    }

    // walk axes
    for (int i = 0; i < axes.length; i++) {
      Exp exp = axes[i].getExp();
      iAxis = i;
      walkTreeForParams(exp, iAxis);
    }

  }

  /**
   * Collect Parameters and Parameter references
   * in a subtree below (and including) exp
   * visitor ???
   */
  private void walkTreeForParams(Exp exp, int iAxis) throws OlapException {
    if (!(exp instanceof FunCall))
      return;
    FunCall f = (FunCall) exp;
    if (f.isCallTo("Parameter")) {
      int nArgs = f.getArgs().length;
      if (nArgs != 3 && nArgs != 4) {
        throw new OlapException("Syntax Error in Parameter: wrong number of arguments");
      }
      if (!(f.getArgs()[0] instanceof Literal)) {
        throw new OlapException("Syntax Error in Parameter definition - 1.argument");
      }

      Literal eName = (Literal) f.getArgs()[0];
      String paraName = eName.stringValue();
      // Found a parameter
      // if it is already there, throw an error
      if (paraMap.containsKey(paraName.toUpperCase())) {
        // already there, error
        String err = "Parameter defined more than once:" + paraName;
        logger.error(err);
        throw new OlapException(err);
      } else {
        // analyze and add the Parameter
        int type;
        Object value = null;
        f.pQuery = this;
        // second Parameter must be ID, either a hierarchy or NUMERIC or STRING
        CompoundId id = (CompoundId) f.getArgs()[1];
        String[] ids = id.toStringArray();
        if (ids.length > 1) {
          // error, must be NUMERIC or STRING or name of an hierarchy
          throw new OlapException("Syntax Error in Parameter definition - 2.argument");
        }
        if (ids[0].equalsIgnoreCase("NUMERIC")) {
          type = Parameter.TYPE_NUMERIC;
          // 3.argument (value) is literal
          if (!(f.getArgs()[0] instanceof Literal)) {
            throw new OlapException("Syntax Error in Parameter definition - 3.argument");
          }
          Literal val = (Literal) f.getArgs()[2];
          value = val.getValueObject();

        } else if (ids[0].equalsIgnoreCase("STRING")) {
          type = Parameter.TYPE_STRING;
          Literal val = (Literal) f.getArgs()[2];
          value = val.getValueObject();
        } else {
          // hierarchy expected
          type = Parameter.TYPE_MEMBER;
          CompoundId val = (CompoundId) f.getArgs()[2];
          value = val.toMdx(); // member String
        }
        Parameter par = new Parameter(paraName, type, iAxis);
        par.setOValue(value);
        if (nArgs == 4) {
          // set description
          Literal desc = (Literal) f.getArgs()[3];
          String description = (String) desc.getValueObject();
          par.setDescription(description);
        }
        paraMap.put(paraName.toUpperCase(), par);
      }
    } else if (f.isCallTo("ParamRef")) {
      f.pQuery = this;
    }

    // recurse down on params
    for (int i = 0; i < f.getArgs().length; i++) {
      walkTreeForParams(f.getArgs()[i], iAxis);
    }

  }

  /**
   * @return parameter map
   */
  public Map getParaMap() {
    return paraMap;
  }

  /**
   * add a formula for a member
   */
  public void addFormula(String[] names, Exp exp, MemberProperty[] memberProperties) {
    Formula newFormula = new Formula(names, exp, memberProperties);
    formulas.add(newFormula);
  }

  /**
   * add a formula for a set
   */
  public void addFormula(String[] names, Exp exp) {
    Formula newFormula = new Formula(names, exp);
    formulas.add(newFormula);
  }

  /**
   * remove a formula
   */
  public void removeFormula(String uniqueName) {
    for (Iterator iter = formulas.iterator(); iter.hasNext();) {
      Formula formula = (Formula) iter.next();
      if (uniqueName.equals(formula.getUniqeName()))
        iter.remove();
    }
  }

  /**
   * @see com.tonbeller.jpivot.olap.mdxparse.Exp#accept
   */
  public void accept(ExpVisitor visitor) {
    visitor.visitParsedQuery(this);
  }

  /**
   * @return
   */
  public List getCellProps() {
    return cellProps;
  }

} // End ParsedQuery

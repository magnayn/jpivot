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
package com.tonbeller.jpivot.olap.navi;

import java.util.Map;

import com.tonbeller.jpivot.core.Extension;
import com.tonbeller.jpivot.olap.model.Expression;

/**
 * sets a parameter of the MDX Query
 * 
 * @author av
 */
public interface SetParameter extends Extension {
  public static final String ID = "setParameter";

  /**
   * sets a parameter
   * @param paramName name of the parameter in the MDX query
   * @param expr the value of the parameter, e.g. a member
   */
  void setParameter(String paramName, Expression expr);

  /** 
   * for scripting.
   * @return Map containing parameter names (= keys) and strings to display value (= value) 
   */
  public Map getDisplayValues();
  
  /**
   * return the names of all defined parameters in this query
   * @return the names of all defined parameters in this query
   */
  public String[] getParameterNames();
}

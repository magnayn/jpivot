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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.jpivot.olap.model.Expression;
import com.tonbeller.jpivot.olap.navi.SetParameter;

/**
 * @author av
 */
public class TestSetParameter extends TestExtensionSupport implements SetParameter {
  Map params = new HashMap();
  
  public void setParameter(String paramName, Expression paramValue) {
    String label = ((Displayable)paramValue).getLabel();
    System.out.println("setting Parameter " + paramName + " to " + label);
    params.put(paramName, label);
  }
  
  /** for scripting */
  public Map getDisplayValues() {
    return params;
  }
  
  public String[] getParameterNames() {
    Set keys = params.keySet();
    return (String[]) keys.toArray(new String[keys.size()]);
  }

}

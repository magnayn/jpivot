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
package com.tonbeller.jpivot.olap.model;

import java.util.Locale;

import com.tonbeller.tbutils.res.Resources;

public class OlapResources {

  private OlapResources() {
  }
  public static Resources instance(Locale locale) {
    return Resources.instance(locale, OlapResources.class);    
  }

}

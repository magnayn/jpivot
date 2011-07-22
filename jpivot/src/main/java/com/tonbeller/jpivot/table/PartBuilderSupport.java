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
package com.tonbeller.jpivot.table;

import javax.servlet.http.HttpSession;

import com.tonbeller.wcf.controller.RequestContext;



/**
 * Created on 18.10.2002
 * 
 * @author av
 */
public abstract class PartBuilderSupport implements PartBuilder {
  protected TableComponent table;


  public void startBuild(RequestContext context) {
  }
  
  public void stopBuild() {
  }

  public boolean isAvailable() {
    return true;
  }

  public void initialize(RequestContext context, TableComponent table) throws Exception {
    this.table = table;
  }
 
  public void destroy(HttpSession session) throws Exception {
  }
  
  protected void setDirty(boolean dirty) {
    // initialized?
    if (table != null)
      table.setDirty(dirty);
  }
  
  /**
   * returns null
   */
  public Object retrieveBookmarkState(int levelOfDetail) {
    return null;
  }

  /**
   * does nothing
   */
  public void setBookmarkState(Object state) {
  }

}

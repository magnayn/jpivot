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
 * forwards lifecycle events to decoree
 * @author av
 */
public abstract class PartBuilderDecorator extends PartBuilderSupport {
  PartBuilder delegate;

  protected PartBuilderDecorator(PartBuilder delegate) {
    this.delegate = delegate;
  }
  
  public void startBuild(RequestContext context) {
    super.startBuild(context);
    delegate.startBuild(context);
  }

  public void stopBuild() {
    delegate.stopBuild();
    super.stopBuild();
  }

  public void initialize(RequestContext context, TableComponent table) throws Exception {
    super.initialize(context, table);
    delegate.initialize(context, table);
  }

  public void destroy(HttpSession session) throws Exception {
    super.destroy(session);
    delegate.destroy(session);
  }

}

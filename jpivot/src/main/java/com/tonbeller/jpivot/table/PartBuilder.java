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

import com.tonbeller.jpivot.ui.Available;
import com.tonbeller.wcf.bookmarks.Bookmarkable;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * Builds a part of the table
 * 
 * @author av
 */
public interface PartBuilder extends Bookmarkable, Available {

  /**
   * deferred ctor, called once after creation
   */
  void initialize(RequestContext context, TableComponent table) throws Exception;
  
  /**
   * destructor, called once after session timeout
   * @param session
   */
  void destroy(HttpSession session) throws Exception;

  /**
   * called before the DOM is built after the TableRenderer has been fully initialized
   */
  void startBuild(RequestContext context);

  /**
   * called after the DOM has been built.
   */
  void stopBuild();
  
  /**
   * true, if the current olapModel supports all extensions that are required
   * by this partBuilder. If false, a GUI will not show related items.
   */
  boolean isAvailable();
}

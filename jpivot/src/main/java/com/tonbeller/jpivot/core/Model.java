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
package com.tonbeller.jpivot.core;

import java.util.Locale;
import java.util.Map;

import com.tonbeller.wcf.bookmarks.Bookmarkable;

/**
 * A Model provides core functionality. It may be extended by optional 
 * <code>Extension</code>s.
 */

public interface Model extends Bookmarkable {
  /**
   * returns the extension if this model instance supports it. Returns null
   * if the extension is not supported.
   */
  Extension getExtension(String id);

  /**
   * returns a Map containing all extensions JSP scripting.
   * Key is the extension id, value is the extension.
   */
  Map getExtensions();
    
  /**
   * sets the locale for messages, data display etc
   */
  void setLocale(Locale locale);
  
  /**
   * adds an extension to this model. Must call extension.setModel(this) to
   * set the model reference.
   */
  public void addExtension(Extension extension);
  
  /**
   * adds a model change listener
   */
  void addModelChangeListener(ModelChangeListener l);
  
  /**
   * removes a model change listener
   */
  void removeModelChangeListener(ModelChangeListener l);

  /**
   * Returns the top element of the decorator chain. If any extensions decorate the model, a decorator
   * chain is built and its head is returned. Otherwise this is returned
   */
  public Model getTopDecorator();
  
  /**
   * Returns the bottom element of the decorator chain. The returned model does not decorate any other model,
   * its the end of the chain.
   */
  public Model getRootModel();
  
}

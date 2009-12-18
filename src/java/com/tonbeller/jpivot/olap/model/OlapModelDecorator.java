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
import java.util.Map;

import javax.servlet.ServletContext;

import com.tonbeller.jpivot.core.Extension;
import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.core.Model;
import com.tonbeller.jpivot.core.ModelChangeListener;

/**
 * a default decorator that delegates everything
 * 
 * @author av
 */
public class OlapModelDecorator extends ExtensionSupport implements OlapModel {

  protected OlapModel delegate;

  public String getID() {
    return delegate.getID();
  }
  public void setID(String ID) {
    delegate.setID(ID);
  }

  /**
   * default ctor
   * @see #setDelegate
   * @see #decorate
   */
  public OlapModelDecorator() {
  }

  public OlapModelDecorator(OlapModel delegate) {
    this.delegate = delegate;
  }

  public Result getResult() throws OlapException {
    return delegate.getResult();
  }

  public Dimension[] getDimensions() {
    return delegate.getDimensions();
  }

  public Member[] getMeasures() {
    return delegate.getMeasures();
  }

  public Extension getExtension(String id) {
    return delegate.getExtension(id);
  }

  public Map getExtensions() {
    return delegate.getExtensions();
  }

  public void setLocale(Locale locale) {
    delegate.setLocale(locale);
  }

  public void addExtension(Extension extension) {
    delegate.addExtension(extension);
  }

  public void addModelChangeListener(ModelChangeListener l) {
    delegate.addModelChangeListener(l);
  }

  public void removeModelChangeListener(ModelChangeListener l) {
    delegate.removeModelChangeListener(l);
  }

  public Object retrieveBookmarkState(int levelOfDetail) {
    return delegate.retrieveBookmarkState(levelOfDetail);
  }

  public void setBookmarkState(Object state) {
    delegate.setBookmarkState(state);
  }

  /**
   * returns the top of the decorator chain
   */
  public Model getTopDecorator() {
    return delegate.getTopDecorator();
  }

  public Model getRootModel() {
    return delegate.getRootModel();
  }
  /**
   * decorates the <code>modelToDecorate</code> with this
   */
  public Model decorate(Model modelToDecorate) {
    this.delegate = (OlapModel) modelToDecorate;
    return this;
  }

  /**
   * sets the decorated model
   */
  public void setDelegate(OlapModel modelToDecorate) throws OlapException {
    this.delegate = modelToDecorate;
  }

  /**
   * gets the decorated model. This is the immediate child in the decorator chain
   * @see #getRootDecoree
   * @see #getTopDecorator
   */
  public OlapModel getDelegate() {
    return delegate;
  }

  public void initialize() throws OlapException {
    delegate.initialize();

  }

  public void destroy() {
    delegate.destroy();
  }
  
  public void setServletContext(ServletContext servletContext) {
    delegate.setServletContext(servletContext);
  }
}

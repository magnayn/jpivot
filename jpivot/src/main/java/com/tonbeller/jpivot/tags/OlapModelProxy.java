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
package com.tonbeller.jpivot.tags;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.core.ModelChangeEvent;
import com.tonbeller.jpivot.core.ModelChangeListener;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.OlapModelDecorator;
import com.tonbeller.jpivot.olap.model.impl.Empty;
import com.tonbeller.jpivot.tags.StateManager.State;
import com.tonbeller.tbutils.testenv.Environment;

/**
 * proxy for OlapModel. There is one instance per session, the GUI
 * components refer to this instance. When the underlying OlapModel (decoree)
 * is exchanged, a structureChanged event is fired.
 * <p />
 * OlapModelProxy is responsible for calling initialize/destroy on its decoree(s).
 * <p />
 * When a new decoree is set, the previous one will be destroyed.
 */

public class OlapModelProxy extends OlapModelDecorator implements HttpSessionBindingListener {
  ArrayList listeners = new ArrayList();
  public static final String DEFAULT_NAME = "default";

  private static final Logger logger = Logger.getLogger(OlapModelProxy.class);

  private ModelChangeListener modelChangeListener = new ModelChangeListener() {
    public void modelChanged(ModelChangeEvent e) {
      fireModelChanged();
    }

    public void structureChanged(ModelChangeEvent e) {
      fireStructureChanged();
    }
  };

  class MyState implements State {
    String name;
    OlapModel model;

    MyState(OlapModel model) {
      this.name = DEFAULT_NAME;
      this.model = model;
    }

    MyState(String name, OlapModel model) {
      this.name = name;
      this.model = model;
    }

    public void initialize() throws Exception {
      try {
        if (logger.isInfoEnabled())
          logger.info("initializing: " + model);
        model.initialize();
      } catch (OlapException e) {
        logger.error(null, e);
        throw e;
      }
    }

    public void destroy() throws Exception {
      if (logger.isInfoEnabled())
        logger.info("destroying: " + model);
      model.destroy();
    }

    public void show() throws OlapException {
      if (logger.isInfoEnabled())
        logger.info("activating: " + model);
      model.addModelChangeListener(modelChangeListener);
      setDelegate(model);
      fireStructureChanged();
    }

    public void hide() throws OlapException {
      if (logger.isInfoEnabled())
        logger.info("deactivating: " + model);
      model.removeModelChangeListener(modelChangeListener);
      setDelegate(Empty.EMPTY_MODEL);
      fireStructureChanged();
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  StateManager stateManager;

  private OlapModelProxy(boolean stackMode) {
    super(Empty.EMPTY_MODEL);
    if (stackMode)
      stateManager = new StackStateManager();
    else
      stateManager = new PageStateManager();
    if (Environment.isTest())
      stateManager.setLogger(new TestStateLogger());
  }

  public static OlapModelProxy instance(String id, HttpSession session) {
    return instance(id, session, true);
  }

  /**
   * retrieves the instance from the session. If the instance does not exist,
   * a new instance is created and stored as a session attribute
   * 
   * @param id the name of the session attribute that will be created. Use this name to reference
   * from other tags like pivot table, chart etc.
   * 
   * @param session the current session, will contain the new created attribute
   * 
   * @param stackMode if true, queries with different names are stacked, i.e. a StackStateManager
   * will be used. Otherwise queries with different names will exist parallel, i.e. a PageStackManager
   * is used. This attribute is evaluated only when the session attribute is created, i.e. when
   * this method is called for the first time. 
   */
  public static OlapModelProxy instance(String id, HttpSession session, boolean stackMode) {
    OlapModelProxy omp = (OlapModelProxy) session.getAttribute(id);
    if (omp == null) {
      omp = new OlapModelProxy(stackMode);
      session.setAttribute(id, omp);
    }
    return omp;
  }

  /**
   * destroys the current model, if present. Then the new model is initalized
   * and shown.
   */
  public void initializeAndShow(OlapModel model) throws Exception {
    State s = new MyState(model);
    stateManager.initializeAndShow(s);
  }

  String nonEmptyQueryName(String queryName) {
    if (queryName == null || queryName.length() == 0)
      return DEFAULT_NAME;
    return queryName;
  }

  /**
   * destroys the current model with the given <code>queryName</code>, if present. 
   * Then the new model is initalized and shown. Depending on the StateManager type
   * queries with other names may be popped off the stack and destroyed.
   * 
   * @see #showByName(String)
   */
  public void initializeAndShow(String queryName, OlapModel model) throws Exception {
    queryName = nonEmptyQueryName(queryName);
    State s = new MyState(queryName, model);
    stateManager.initializeAndShow(s);
  }

  /**
   * shows the query that was initialized with <code>queryName</code>.
   * @see #initializeAndShow(String, OlapModel)
   */
  public void showByName(String queryName) throws Exception {
    queryName = nonEmptyQueryName(queryName);
    stateManager.showByName(queryName);
  }

  public void destroyAll() throws Exception {
    stateManager.destroyAll();
  }

  public void destroyQuery(String queryName) throws Exception {
    queryName = nonEmptyQueryName(queryName);
    stateManager.destroyByName(queryName);
  }

  private void fireModelChanged() {
    ModelChangeEvent e = new ModelChangeEvent(this);
    for (Iterator iter = listeners.iterator(); iter.hasNext();) {
      ModelChangeListener l = (ModelChangeListener) iter.next();
      l.modelChanged(e);
    }
  }

  private void fireStructureChanged() {
    ModelChangeEvent e = new ModelChangeEvent(this);
    for (Iterator iter = listeners.iterator(); iter.hasNext();) {
      ModelChangeListener l = (ModelChangeListener) iter.next();
      l.structureChanged(e);
    }
  }

  public void addModelChangeListener(ModelChangeListener l) {
    listeners.add(l);
  }

  public void removeModelChangeListener(ModelChangeListener l) {
    listeners.remove(l);
  }

  public void valueBound(HttpSessionBindingEvent ev) {
  }

  public void valueUnbound(HttpSessionBindingEvent ev) {
    logger.info("session timeout");
    try {
      stateManager.destroyAll();
    } catch (Exception e) {
      logger.error(null, e);
    }
  }

  /**
   * OlapModelProxy is responsible for calling initialize/destroy
   * @throws RuntimeExecption if called
   */
  public void destroy() {
    throw new RuntimeException("must not be called");
  }

  /**
   * OlapModelProxy is responsible for calling initialize/destroy
   * @throws RuntimeExecption if called
   */
  public void initialize() {
    throw new RuntimeException("must not be called");
  }

  /**
   * for Tests only
   */
  public StateManager getStateManager() {
    return stateManager;
  }
}

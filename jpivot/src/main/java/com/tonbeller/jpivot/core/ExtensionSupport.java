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

/**
 * Default implementation of an Extension
 * @author av
 */

public class ExtensionSupport implements Extension {

  private String id;
  private Model model;

  /**
   * Returns the id.
   * @return String
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the model.
   * @return ModelSupport
   */
  public Model getModel() {
    return model;
  }

  /**
   * Sets the id.
   * @param id The id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Sets the model. Used by ModelFactory.
   * @param model The model to set
   */
  public void setModel(Model model) {
    this.model = model;
  }

  /**
   * does not decorate, returns the parameter
   */
  public Model decorate(Model decoratedModel) {
    return decoratedModel;
  }

  /**
  	* Notification after model initialization is complete 
  	*/
  public void modelInitialized(){
  	// default: no action
  }

}

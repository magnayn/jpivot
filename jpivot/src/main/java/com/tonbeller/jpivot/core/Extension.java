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
 * An optional extension to a model
 * @see Model
 * @author av
 */

public interface Extension {

  /**
   * Set the model that is extended.
   */
  void setModel(Model model);
  
  /**
   * returns the id of this extension. The id identifies the extension within the model.
   */
  String getId();
  
  /**
   * allow an extension to decorate the model. The default implementation should
   * return  <code>modelToDecorate</code>
   * @param modelToDecorate the model to decorate. It may be different from the model
   * passed to setModel() because other extensions may already have decorated 
   * modelToDecorate.
   */
  Model decorate(Model modelToDecorate);
  
  /**
   * Notification after model initialization is complete 
   */
  void modelInitialized();
  
}

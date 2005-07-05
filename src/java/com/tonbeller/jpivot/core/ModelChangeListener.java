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

import java.util.EventListener;

/**
 * Created on 14.10.2002
 * 
 * @author av
 */
public interface ModelChangeListener extends EventListener {

  /** model data have changed, e.g. user has navigated */
  void modelChanged(ModelChangeEvent e);

  /** major change, e.g. extensions added/removed */
  void structureChanged(ModelChangeEvent e);

}

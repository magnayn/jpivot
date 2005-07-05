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
package com.tonbeller.jpivot.test.olap;

import com.tonbeller.jpivot.core.ExtensionSupport;

/**
 * Created on 24.10.2002
 * 
 * @author av
 */
public class TestExtensionSupport extends ExtensionSupport {
  protected void fireModelChanged() {
    TestOlapModel tom = (TestOlapModel)getModel();
    if (tom != null) // extension w/o model
      tom.fireModelChanged();    
  }

  protected TestOlapModel model() {
    return (TestOlapModel) super.getModel();
  }
  
}

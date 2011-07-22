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

/**
 * Created on 09.01.2003
 * 
 * @author av
 */
public abstract class TableComponentExtensionSupport extends PartBuilderSupport implements TableComponentExtension {

  protected boolean enabled = true;

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    setDirty(true);
  }

  public boolean isEnabled() {
    return enabled;
  }

  /**
   * returns the enabled property
   */
  public Object retrieveBookmarkState(int levelOfDetail) {
    return new Boolean(enabled);
  }

  /**
   * sets the enabled property
   */
  public void setBookmarkState(Object state) {
    if (state instanceof Boolean)
      enabled = ((Boolean) state).booleanValue();
  }

}

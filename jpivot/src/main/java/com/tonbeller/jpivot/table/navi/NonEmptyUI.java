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
package com.tonbeller.jpivot.table.navi;

import com.tonbeller.jpivot.core.ModelChangeEvent;
import com.tonbeller.jpivot.core.ModelChangeListener;
import com.tonbeller.jpivot.olap.navi.NonEmpty;
import com.tonbeller.jpivot.table.TableComponent;
import com.tonbeller.jpivot.table.TableComponentExtensionSupport;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * show/hide non empty rows/columns. The toolbar button is connected to the boolean suppress property.
 * 
 * @author av
 */
public class NonEmptyUI extends TableComponentExtensionSupport implements ModelChangeListener {

  public static final String ID = "nonEmpty";
  public String getId() {
    return ID;
  }

  NonEmpty extension;

  public void initialize(RequestContext context, TableComponent table) throws Exception {
    super.initialize(context, table);
		table.getOlapModel().addModelChangeListener(this);
		extension = getNonEmpty();
  }

  public boolean isButtonPressed() {
    if (extension != null)
      return extension.isNonEmpty();
    return false;
  }

  public void setButtonPressed(boolean value) {
    if (extension != null)
      extension.setNonEmpty(value);
  }

  public boolean isAvailable() {
    return getNonEmpty() != null;
  }

  NonEmpty getNonEmpty() {
    return (NonEmpty) table.getOlapModel().getExtension(NonEmpty.ID);
  }

  public void modelChanged(ModelChangeEvent e) {
  }

  public void structureChanged(ModelChangeEvent e) {
		extension = getNonEmpty();
  }

}

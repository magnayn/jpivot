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

import com.tonbeller.jpivot.olap.navi.ResetQuery;
import com.tonbeller.jpivot.table.TableComponent;
import com.tonbeller.jpivot.table.TableComponentExtensionSupport;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * Created on 06.12.2002
 * 
 * @author av
 */
public class ResetQueryUI extends TableComponentExtensionSupport {
  public static final String ID = "resetQuery";

  public String getId() {
    return ID;
  }

  public void initialize(RequestContext context, TableComponent table) throws Exception {
    super.initialize(context, table);
  }

  public boolean isAvailable() {
    return getExtension() != null;
  }

  public boolean isButtonPressed() {
    return false;
  }

  public void setButtonPressed(boolean value) {
    if (value && getExtension() != null)
      getExtension().reset();
  }

  ResetQuery getExtension() {
    return (ResetQuery) table.getOlapModel().getExtension(ResetQuery.ID);
  }

}

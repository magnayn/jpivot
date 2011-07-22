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
package com.tonbeller.jpivot.navigator.hierarchy;

import com.tonbeller.jpivot.navigator.member.MemberSelectionModel;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * called when the user clicks on a HierarchyItem
 * 
 * @author av
 */
public interface HierarchyItemClickHandler {
  void itemClicked(RequestContext context, HierarchyItem item, MemberSelectionModel selection, boolean allowChangeOrder);
}

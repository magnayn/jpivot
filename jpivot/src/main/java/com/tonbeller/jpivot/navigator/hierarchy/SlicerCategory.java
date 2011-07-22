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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.tonbeller.jpivot.navigator.member.MemberSelectionModel;
import com.tonbeller.jpivot.olap.model.Hierarchy;
//import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.OlapUtils;
import com.tonbeller.jpivot.olap.navi.ChangeSlicer;
import com.tonbeller.tbutils.res.Resources;
import com.tonbeller.wcf.catedit.Item;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.selection.SelectionModel;

/**
 * Created on 09.12.2002
 * 
 * @author av
 */
class SlicerCategory extends AbstractCategory {
  public SlicerCategory(HierarchyNavigator navi, String name, String icon) throws OlapException {
    super(navi, name, icon);

    /* Take active hierarchies instead of active 
     dimensions, to remember the slicer on a hierarchy that is 
     not the default (first)
     */
    Set slicerHiers = OlapUtils.getSlicerHierarchies(navi.getOlapModel());
    for (Iterator it = slicerHiers.iterator(); it.hasNext();) {
      Hierarchy hier = (Hierarchy) it.next();
      HierarchyItem hi = new HierarchyItem(this, hier);
      items.add(hi);
    }

    Collections.sort(items);
  }

  /**
   * calls HierarchyNavigator.itemClicked with the appropriate selection model
   */
  public void itemClicked(RequestContext context, HierarchyItem item) {
    // create a selection model
    MemberSelectionModel selection = new MemberSelectionModel();
    if (navi.getSlicerExtension() == null)
      selection.setMode(SelectionModel.NO_SELECTION);
    else
    	// SeraSoft - Enable support for compound slicer 
    	//  selection.setMode(SelectionModel.SINGLE_SELECTION);
    	selection.setMode(SelectionModel.MULTIPLE_SELECTION);

    selection.setOrderedSelection(item.getSlicerSelection());
    navi.itemClicked(context, item, selection, false);
  }

  void setSelection(HierarchyItem item, Collection selection) {
    item.setSlicerSelection(selection);
  }

  public boolean isOrderSignificant() {
    return false;
  }

  void prepareApplyChanges() {
  }

  void applyChanges() {
    if (!isDirty())
      return;
    setDirty(false);

    ChangeSlicer slicerExtension = navi.getSlicerExtension();
    if (slicerExtension == null)
      return;

    List memberList = new ArrayList();
    for (Iterator it = items.iterator(); it.hasNext();) {
      HierarchyItem hi = (HierarchyItem) it.next();
      memberList.addAll(hi.getSlicerSelection());
    }
    Member[] memberArr = (Member[]) memberList.toArray(new Member[memberList.size()]);
    slicerExtension.setSlicer(memberArr);
  }

  public boolean isEmptyAllowed() {
    return true;
  }

  String validateSelection(HierarchyItem item, Collection selection) {
  	// Enable support for compound slicer
	// Disable validation of only one item as of in the previous version
    //    if (selection.size() > 1) {
    //      Resources res = getNavigator().getRes();
    //      return res.getString("selection.mustSelectOneOrLess");
    //    }
    return null;
  }

  /**
   * adds an item and sorts the list
   */
  public void addItem(Item item) {
    super.addItem(item);
    Collections.sort(items);
  }

  public boolean isSlicer() {
    return true;
  }

}

 	  	 

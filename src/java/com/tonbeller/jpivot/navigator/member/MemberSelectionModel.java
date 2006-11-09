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
package com.tonbeller.jpivot.navigator.member;

import java.util.List;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.wcf.selection.DefaultSelectionModel;

/**
 * A SelectionModel that allows only <code>Members</code> to be selected.
 * The Model may contain objects of other types but only Members are
 * selectable.
 * @author av
 */
public class MemberSelectionModel extends DefaultSelectionModel {
  
  List orderedSelection;

  /**
   * Constructor for MemberSelectionModel.
   */
  public MemberSelectionModel() {
    super();
  }

  /**
   * Constructor for MemberSelectionModel.
   * @param mode
   */
  public MemberSelectionModel(int mode) {
    super(mode);
  }

  /**
   * true if item is a member
   */
  public boolean isSelectable(Object item) {
    return super.isSelectable(item) && item instanceof Member;
  }
  
  public void setOrderedSelection(List list) {
    super.setSelection(list);
    this.orderedSelection = list;
  }

  public List getOrderedSelection() {
    return orderedSelection;
  }

}

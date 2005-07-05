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
package com.tonbeller.jpivot.olap.query;

import java.util.EventListener;

/**
 * @param quax the Quax being changed
 * @param source the initiator object of the change
 * @param changedMemberSet true if the member set was changed
 *                         by the navigator
 */
public interface QuaxChangeListener extends EventListener {
  void quaxChanged(Quax quax, Object source, boolean changedMemberSet);
} // QuaxChangedListener

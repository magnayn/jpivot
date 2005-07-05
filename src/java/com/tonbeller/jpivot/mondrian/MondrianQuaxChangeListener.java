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
/*
 * Created on 13.06.2003
 *  by hh
 */
package com.tonbeller.jpivot.mondrian;

import java.util.EventListener;


/**
 * @param quax the Quax being changed
 * @param source the initiator object of the change
 * @param changedMemberSet true if the member set was changed
 *                         by the navigator
 */
public interface MondrianQuaxChangeListener extends EventListener {
	void quaxChanged(MondrianQuax quax, Object source, boolean changedMemberSet);
}

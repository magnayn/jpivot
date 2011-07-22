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
package com.tonbeller.jpivot.olap.model;



/**
 * OLAP Member.
 * Member may have properties, e.g. a customer may have a phone number.
 * @author av
 */
public interface Member extends Expression, PropertyHolder, Visitable, Displayable, Decorator {

  /**
   * returns the distance from the root member of the hierarchy. 
   * This is used to compute indentation.
   * @return 0 for the root member(s), &gt; 0 else
   */
  int getRootDistance();
  
  /**
   * get the level to which this member belongs.
   * @return not null
   */
  Level getLevel();
  
  /**
   * allows to compare members from the current result and the previous
   * result.
   */
  boolean equals(Object other);
  
  /**
   * @return true, if the member is an "All" member
   */
  boolean isAll();
 
  /**
   * an OLAP Member may be calculated i.e. derived from original members
   * @return true,if the member is calculated 
   */
  boolean isCalculated();
}

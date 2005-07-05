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

import java.util.List;
import java.util.Map;

/**
 * Query Result Processor
 */
public interface QueryResultHandler {

  /**
   * handle AxisInfo Tag, called for any AxisInfo
   */
  void handleAxisInfo(String axisName, int axisNumber);

  /**
    * handle HierarchyInfo Tag, called for any AxisInfo,HierarchyInfo
    */
  void handleHierInfo(String hierName, int axisNumber, int number);

  /**
    * handle Axis Tag, called for any axis
    */
  void handleAxis(String axisName, int axisOrdinal);

  /**
    * handle Tuple Tag, called for any axis, Tuple
    */
  void handleTuple(int axisOrdinal, int positionOrdinal);

  /**
    * handle Member Tag, called for any member, axis, Tuple
    */
  void handleMember(
    String uniqueName,
    String caption,
    String levUName,
    String displayInfo,
    Map otherProps,
    int axisOrdinal,
    int positionOrdinal,
    int memberOrdinal);

  /**
    * handle CellData Tag
    */
   void handleCellData();

  /**
    * handle Cell tag
    */
  void handleCell( int iOrdinal, Object value, String fmtValue, String fontSize);


  /**
   * holds data for the drillthrough header
   * @param headerList
   */
  void setDrillHeader ( Map  header);

  /**
   * sets the results for a drillthrough (is an arraylist of map) 
   * @param rows
   */
  void setDrillRows(List rows);

  
} // QueryResultHandler

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
 * Handle the cell formatting exit
 */
public interface CellFormatter {
  
  /**
   * user provided cell formatting function 
   * @param cell
   * @return the formatted value
   */
  public String formatCell(Cell cell);

} // CellFormatter

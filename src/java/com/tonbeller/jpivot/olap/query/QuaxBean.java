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

import java.io.Serializable;

/**
 * Java Bean object to hold the state of Quax.
 */
public class QuaxBean implements Serializable {

  boolean qubonMode = false;
  int ordinal; // ordinal of query axis, never changed by swap
  int nDimension;
  PositionNodeBean posTreeRoot;
  boolean hierarchizeNeeded;
  int generateIndex = 0;
  int generateMode = 0;
  int nHierExclude = 0;

  /**
   * Get nDimension.
   * @return nDimension
   */
  public int getNDimension() {
    return nDimension;
  }

  /**
   * Set nDimension.
   * @param nDimension
   */
  public void setNDimension(int nDimension) {
    this.nDimension = nDimension;
  }

  /**
   * Get ordinal.
   * @return ordinal
   */
  public int getOrdinal() {
    return ordinal;
  }

  /**
   * Set ordinal.
   * @param ordinal
   */
  public void setOrdinal(int ordinal) {
    this.ordinal = ordinal;
  }

  /**
   * Get qubonMode.
   * @return qubonMode
   */
  public boolean isQubonMode() {
    return qubonMode;
  }

  /**
   * Set qubonMode.
   * @param qubonMode
   */
  public void setQubonMode(boolean qubonMode) {
    this.qubonMode = qubonMode;
  }

  /**
   * @return posTreeRoot
   */
  public PositionNodeBean getPosTreeRoot() {
    return posTreeRoot;
  }

  /**
   * @param posTreeRoot
   */
  public void setPosTreeRoot(PositionNodeBean posTreeRoot) {
    this.posTreeRoot = posTreeRoot;
  }

  /**
   * @return
   */
  public boolean isHierarchizeNeeded() {
    return hierarchizeNeeded;
  }

  /**
   * @param b
   */
  public void setHierarchizeNeeded(boolean b) {
    hierarchizeNeeded = b;
  }

  /**
   * @return
   */
  public int getGenerateIndex() {
    return generateIndex;
  }

  /**
   * @return
   */
  public int getGenerateMode() {
    return generateMode;
  }

  /**
   * @param i
   */
  public void setGenerateIndex(int i) {
    generateIndex = i;
  }

  /**
   * @param i
   */
  public void setGenerateMode(int i) {
    generateMode = i;
  }

  /**
   * @return Returns the nHierExclude.
   */
  public int getNHierExclude() {
    return nHierExclude;
  }
  /**
   * @param hierExclude The nHierExclude to set.
   */
  public void setNHierExclude(int hierExclude) {
    nHierExclude = hierExclude;
  }
} // QuaxBean

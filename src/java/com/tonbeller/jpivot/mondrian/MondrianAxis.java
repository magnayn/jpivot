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
package com.tonbeller.jpivot.mondrian;

import java.util.ArrayList;
import java.util.List;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Visitor;

/**
 * MondrianAxis is an adapter class for the Result Mondrian Axis.
 */
public class MondrianAxis implements Axis {

  private mondrian.olap.Axis monAxis = null;
  private MondrianModel model = null;
  private ArrayList aPositions = null;
  private MondrianHierarchy[] hierarchies = null;
  private int ordinal; // -1 for slicer

  /**
   * Constructor
   * @param monAxis Axis as defined in Mondrian
   */
  public MondrianAxis(int iOrdinal, mondrian.olap.Axis monAxis, MondrianModel model) {
    this.ordinal = iOrdinal;
    this.monAxis = monAxis;
    this.model = model;

    aPositions = new ArrayList();

    if (iOrdinal >= 0) {
      // it is not the slicer
      // get hierarchies from mondrian query, rather than from result, which can be empty
 
      MondrianQueryAdapter adapter = (MondrianQueryAdapter) model.getQueryAdapter();
      mondrian.olap.Hierarchy[] monHiers = adapter.getMonQuery().getMdxHierarchiesOnAxis(iOrdinal);
      hierarchies = new MondrianHierarchy[monHiers.length];
      for (int j = 0; j < hierarchies.length; j++) {
        hierarchies[j] = model.lookupHierarchy(monHiers[j].getUniqueName());
      }
    }

    mondrian.olap.Position[] monPositions = monAxis.positions;
    for (int i = 0; i < monPositions.length; i++) {
      MondrianPosition position = new MondrianPosition(monPositions[i], iOrdinal, model);
      aPositions.add(position);
      if (iOrdinal == -1) {
        // for the slicer,  extract the hierarchies from the members
        mondrian.olap.Member[] monMembers = monPositions[i].getMembers();
        if (i == 0) { // first position only, as all positions have same hierarchies
          // create the hierarchies array
          hierarchies = new MondrianHierarchy[monMembers.length];
          for (int j = 0; j < monMembers.length; j++) {
            hierarchies[j] = model.lookupHierarchy(monMembers[j].getHierarchy().getUniqueName());
          }
        }
      }
    }

  }

  /**
   * @see com.tonbeller.jpivot.olap.model.Axis#getPositions()
   */
  public List getPositions() {
    return aPositions;
  }

  /**
   * @see com.tonbeller.jpivot.olap.model.Axis#getHierarchies()
   */
  public Hierarchy[] getHierarchies() {
    return hierarchies;
  }

  /**
   * @see com.tonbeller.jpivot.olap.model.Visitable#accept(Visitor)
   */
  public void accept(Visitor visitor) {
    visitor.visitAxis(this);
  }

  public Object getRootDecoree() {
    return this;
  }

  /**
   * Returns the ordinal.
   * @return int
   */
  public int getOrdinal() {
    return ordinal;
  }

} // MondrianAxis

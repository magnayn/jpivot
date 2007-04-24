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
import java.util.Iterator;
import java.util.List;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.impl.FormatStringParser;
import com.tonbeller.jpivot.olap.query.ResultBase;
import mondrian.olap.Position;
import mondrian.olap.Member;
import mondrian.olap.ResultLimitExceededException;
import mondrian.olap.ResourceLimitExceededException;
import mondrian.olap.MemoryLimitExceededException;

/**
 * Result implementation for Mondrian
 */
public class MondrianResult extends ResultBase {

  private mondrian.olap.Result monResult = null;
  private int[] posize;
  private FormatStringParser formatStringParser = new FormatStringParser();

  /**
   * Constructor
   * @param model the associated MondrianModel
   */
  protected MondrianResult(mondrian.olap.Result monResult, MondrianModel model)
        throws ResultLimitExceededException {
    super(model);
    this.monResult = monResult;

    initData();
  }

  /**
   * initData creates all the wrapper objects
   */
  private void initData() throws ResultLimitExceededException {
    final int cellCountLimit = Integer.getInteger(
                                MondrianModel.CELL_LIMIT_PROP, 
                                MondrianModel.CELL_LIMIT_DEFAULT).intValue(); 

    MondrianModel mmodel = (MondrianModel) model;

    mondrian.olap.Axis[] monAxes = monResult.getAxes();
    // first step: walk through axes and add the members to the model
    int nCells = 1;
    posize = new int[monAxes.length];
    for (int i = 0; i < monAxes.length; i++) {
      List monPositions = monAxes[i].getPositions();
      int size = 0;
      Iterator pit = monPositions.iterator();
      while (pit.hasNext()) {
        Position position = (Position) pit.next();
        Iterator mit = position.iterator();
        while (mit.hasNext()) {
          mmodel.addMember((Member) mit.next());
        }
        size++;
      }
      // check for OutOfMemory
      mmodel.checkListener();

      posize[i] = size;
      nCells = nCells * size;
    }
    mondrian.olap.Axis monSlicer = monResult.getSlicerAxis();
    List monPositions = monSlicer.getPositions();
    Iterator pit = monPositions.iterator();
    while (pit.hasNext()) {
      Position position = (Position) pit.next();
        Iterator mit = position.iterator();
        while (mit.hasNext()) {
          mmodel.addMember((Member) mit.next());
        }
        // check for OutOfMemory
        mmodel.checkListener();
    }

    // second step: create the result data
    axesList = new ArrayList();
    for (int i = 0; i < monAxes.length; i++) {
      axesList.add(new MondrianAxis(i, monAxes[i], mmodel));
      // check for OutOfMemory
      mmodel.checkListener();
    }
    slicer = new MondrianAxis(-1, monSlicer, mmodel);

    int[] iar = new int[monAxes.length];
    for (int i = 0; i < monAxes.length; i++) {
      iar[i] = 0;
    }
    for (int i = 0; i < nCells; i++) {
      mondrian.olap.Cell monCell = monResult.getCell(iar);
      MondrianCell cell = new MondrianCell(monCell, mmodel);
      cell.setFormattedValue(monCell.getFormattedValue(), formatStringParser);
      aCells.add(cell);
      if (nCells > 1) {
        // not for 0-dimensional case
        increment(iar); 
      }

      // check for OutOfMemory every 1000 cells created
      if (i % 1000 == 0) {

        // According to Java5 memory monitor are we close to running
        // out of memory.
        mmodel.checkListener();

        // Have we read in too many cells.
        if ((cellCountLimit > 0) && (cellCountLimit < aCells.size())) {
            StringBuffer buf = new StringBuffer(100);
            buf.append("TooManyCells limit=");
            buf.append(cellCountLimit);
            buf.append(" for mdx: ");
            buf.append(mmodel.getCurrentMdx());
            throw new ResourceLimitExceededException(buf.toString());
        }
      }
    }

  }

  /**
   * increment int array according to size of axis positions
   *  first index changes fastest
   * (0,0), (1,0) ... (NX-1, 0)
   * (0,1), (1,1) ... (NX-1, 1)
   */
  private void increment(int[] iar) {
    int nn = ++iar[0];
    // done for the 1-dimensional case
    if (iar.length > 1 && nn >= posize[0]) {
      iar[0] = 0;
      for (int i = 1; i < iar.length; i++) {
        int kk = ++iar[i];
        if (kk < posize[i])
          break;
        else
          iar[i] = 0;
      }
    }
  }

  /**
   * Returns the axes.
   * @return Axis[]
   */
  public Axis[] getAxes() {
    if (monResult == null)
      return null; // todo error handling
    return (Axis[]) axesList.toArray(new MondrianAxis[0]);
  }

} // MondrianResult

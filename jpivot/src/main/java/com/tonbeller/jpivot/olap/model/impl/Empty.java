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
package com.tonbeller.jpivot.olap.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import com.tonbeller.jpivot.core.ModelSupport;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.model.Visitor;

/**
 * an empty OlapModel that contains no axes and no cells
 * 
 * @author av
 * @since 21.04.2005
 */
public class Empty {
  public static final OlapModel EMPTY_MODEL = new EmptyModel();
  public static final Result EMPTY_RESULT = new EmptyResult(false);
  public static final Result OVERFLOW_RESULT = new EmptyResult(true);
  public static final Axis EMPTY_AXIS = new EmptyAxis();

  private Empty() {
  }

  public static class EmptyModel extends ModelSupport implements OlapModel {
    private String ID = null;

    public String getID() {
      return ID;
    }

    public void setID(String ID) {
      this.ID = ID;
    }

    public Result getResult() throws OlapException {
      return EMPTY_RESULT;
    }

    public Dimension[] getDimensions() {
      return new Dimension[0];
    }

    public Member[] getMeasures() {
      return new Member[0];
    }

    public void initialize() {
    }

    public void destroy() {
    }

    public void setServletContext(ServletContext servletContext) {
    }
 
  }

  static class EmptyResult implements Result {
    static List cells;
    boolean overflow;
    public EmptyResult(boolean overflow) {
      this.overflow = overflow;
      cells = new ArrayList();
      //cells.add(new CellImpl());
    }

    public List getCells() {
      return cells;
    }

    public Axis[] getAxes() {
      return new Axis[0];
    }

    public Axis getSlicer() {
      return EMPTY_AXIS;
    }

    public void accept(Visitor visitor) {
      visitor.visitResult(this);
    }

    public Object getRootDecoree() {
      return this;
    }

    public boolean isOverflowOccured() {
      return overflow;
    }
  }

  static class EmptyAxis implements Axis {
    public List getPositions() {
      return Collections.EMPTY_LIST;
    }

    public Hierarchy[] getHierarchies() {
      return new Hierarchy[0];
    }

    public void accept(Visitor visitor) {
      visitor.visitAxis(this);
    }

    public Object getRootDecoree() {
      return this;
    }
  }
}
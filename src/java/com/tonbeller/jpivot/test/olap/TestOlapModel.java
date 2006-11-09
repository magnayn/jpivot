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
package com.tonbeller.jpivot.test.olap;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.core.ModelSupport;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.model.impl.CellImpl;
import com.tonbeller.jpivot.olap.model.impl.PropertyImpl;
import com.tonbeller.jpivot.olap.model.impl.ResultImpl;
import com.tonbeller.jpivot.olap.navi.DrillExpandMember;

/**
 * Created on 02.12.2002
 *
 * @author av
 */
public class TestOlapModel extends ModelSupport implements OlapModel {

  TestAxis axes[], slicer;
  TestDimension[] dimensions;
  TestDimension measures;
  private static Logger logger = Logger.getLogger(TestOlapModel.class);
  Random random = new Random();
  private String ID = null;
  public String getID() { return ID; }
  public void setID(String ID) { this.ID = ID; }

  public TestOlapModel() {
    reset();
  }

  void reset() {
    DimensionBuilder db = new DimensionBuilder();
    measures = db.build("Measures", new String[] { "Measures" }, new int[] { 3 });
    measures.setMeasure(true);
    TestDimension region =
      db.build("Region", new String[] { "All Region", "Region", "City", "Customer" }, new int[] { 1, 5, 8, 200 });
    TestDimension time = db.build("Time", new String[] { "Year", "Month" }, new int[] { 5, 12 });
    time.setTime(true);
    TestDimension products =
      db.build("Products", new String[] { "All Products", "Category", "Product" }, new int[] { 1, 3, 10 });
    TestDimension advertising =
      db.build("Advertising", new String[] { "All Advertising", "Media", "Target", "Type" }, new int[] { 1, 4, 3, 3 });
    TestDimension material =
      db.build("Material", new String[] { "All Materials", "Category", "Brand", "Quality" }, new int[] { 1, 4, 3, 3 });

    dimensions = new TestDimension[6];
    dimensions[0] = measures;
    dimensions[1] = region;
    dimensions[2] = time;
    dimensions[3] = products;
    dimensions[4] = advertising;
    dimensions[5] = material;

    PropertyBuilder pb = new PropertyBuilder();
    pb.build(products);
    pb.build(region);

    axes = new TestAxis[2];
    axes[0] = TestOlapModelUtils.createAxis(new TestDimension[] { measures });
    axes[1] = TestOlapModelUtils.createAxis(new TestDimension[] { products, time });

    TestMember m = ((TestHierarchy) advertising.getHierarchies()[0]).getRootMembers()[0];
    m = (TestMember) m.getChildMember().get(0);
    setSlicer(m);

  }

  public void setAxis(int index, TestAxis axis) {
    axes[index] = axis;
  }

  public TestAxis getAxis(int index) {
    return axes[index];
  }

  public TestAxis[] getAxes() {
    return axes;
  }

  public void setAxes(TestAxis[] axes) {
    this.axes = axes;
  }

  public int indexOf(Axis axis) {
    for (int i = 0; i < axes.length; i++)
      if (axes[i].equals(axis))
        return i;
    return -1;
  }

  public void setSlicer(TestMember m) {
    slicer = new TestAxis();
    TestPosition p = new TestPosition(slicer);
    p.setMembers(new TestMember[] { m });
    List list = new ArrayList();
    list.add(p);
    slicer.setPositions(list);
  }

  public Result getResult() throws OlapException {

    // make result reproducable for testing purposes
    random.setSeed(123427);

    int cellCount = 1;
    for (int i = 0; i < axes.length; i++)
      cellCount *= axes[i].getPositions().size();

    Locale locale = super.getLocale();
    if (locale == null)
      locale = Locale.getDefault();
    DecimalFormat fmt = (DecimalFormat) DecimalFormat.getInstance(locale);
    fmt.applyPattern("#,##0.00");
    ArrayList cells = new ArrayList();
    for (int i = 0; i < cellCount; i++) {
      CellImpl c = new CellImpl();
      if (false) {
        c.setValue(new Integer(i));
        c.setFormattedValue(Integer.toString(i));
      } else {
        double value = random.nextGaussian() * 100 + 1000;
        c.setValue(new Double(value));
        c.setFormattedValue(fmt.format(value));
      }
      addProperties(c, i);
      cells.add(c);
    }

    ResultImpl res = new ResultImpl();
    res.setAxes(axes);
    res.setCells(cells);
    res.setSlicer(slicer);
    return res;
  }

  /**
   * @see com.tonbeller.jpivot.olap.model.OlapModel#getDimensions()
   */
  public Dimension[] getDimensions() {
    return dimensions;
  }

  /**
   * @see com.tonbeller.jpivot.olap.model.OlapModel#getMeasures()
   */
  public Member[] getMeasures() {
    TestHierarchy hier = (TestHierarchy) measures.getHierarchies()[0];
    return hier.getRootMembers();
  }

  /** searches for the named dimension */
  public Dimension getDimension(String name) {
    for (int i = 0; i < dimensions.length; i++)
      if (name.equals(dimensions[i].getLabel()))
        return dimensions[i];
    return null;
  }

  /**
   * which positions in the result contain member
   */
  public List findPositions(Member m) {
    List list = new ArrayList();
    for (int i = 0; i < axes.length; i++)
      list.addAll(TestOlapModelUtils.findPositions(axes[i], m));
    return list;
  }

  public static void addProperties(CellImpl c, int i) {
    Property[] props = new Property[1];
    PropertyImpl p = new PropertyImpl();
    props[0] = p;

    switch ((i / 2) % 10) {
      case 0 :
        p.setName("arrow");
        p.setValue("up");
        break;
      case 1 :
        p.setName("arrow");
        p.setValue("down");
        break;
      case 2 :
        p.setName("arrow");
        p.setValue("none");
        break;
      case 4 :
        p.setName("style");
        p.setValue("red");
        break;
      case 5 :
        p.setName("style");
        p.setValue("yellow");
        break;
      case 6 :
        p.setName("style");
        p.setValue("green");
        break;
      default :
        break;
    }
    p.setLabel(p.getName());

    c.setProperties(props);
  }

  /**
   * expands top member on rows - gives more interesting result for junit tests.
   */
  public void expand0() {
    expandRow(0);
  }

  public void expandRow(int positionIndex) {
    TestPosition p = (TestPosition) axes[1].getPositions().get(positionIndex);
    Member m = p.getMembers()[0];
    DrillExpandMember de = (DrillExpandMember) getExtension(DrillExpandMember.ID);
    de.expand(m);
  }

  /**
   * collapses top member on rows (for unit tests)
   */
  public void collapse0() {
    collapseRow(0);
  }

  public void collapseRow(int positionIndex) {
    TestPosition p = (TestPosition) axes[1].getPositions().get(positionIndex);
    Member m = p.getMembers()[0];
    DrillExpandMember de = (DrillExpandMember) getExtension(DrillExpandMember.ID);
    de.collapse(m);
  }

  /**
   * Returns the slicer.
   * @return TestAxis
   */
  public TestAxis getSlicer() {
    return slicer;
  }

  /**
   * Sets the slicer.
   * @param slicer The slicer to set
   */
  public void setSlicer(TestAxis slicer) {
    this.slicer = slicer;
  }

  public Object getRootDecoree() {
    return this;
  }

  /**
   * does nothing
   */
  public void initialize() {
    logger.debug("init");
  }

  /**
   * does nothing
   */
  public void destroy() {
    logger.debug("destroy");
    super.destroy();
  }

  public TestDimension getMeasuresDim() {
    return measures;
  }
  public void setServletContext(ServletContext servletContext) {
  }
 
}

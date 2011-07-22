package com.tonbeller.jpivot.olap.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import com.tonbeller.jpivot.core.ModelSupport;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.model.Visitor;

/**
 * A ScalarOlapModel is an OlapModel that contains a single cell.
 * It is 1-dimensional with Measures as its only dimension.
 * <p />
 * Properties:
 * <dl>
 *   <dt>caption</dt><dd>Caption of the only measure</dd>
 *   <dt>result.value</dt><dd>value of the single cell</dd>
 *   <dt>result.formattedValue</dt><dd>formatted value of the single cell</dd>
 * </dl>
 * 
 * @author av
 * @since 21.04.2005
 */
public class ScalarOlapModel extends ModelSupport implements OlapModel, Result {
  private static final String MEASURES = "Measures";
  private static final String MEASURE = "Measure";

  MemberImpl measure;
  MemberImpl[] measures;
  LevelImpl level;
  HierarchyImpl hierarchy;
  DimensionImpl dimension;
  DimensionImpl[] dimensions;
  String ID;

  CellImpl cell;
  List cells;
  Axis[] axes;
  Axis slicer;

  boolean overflowOccured;

  public ScalarOlapModel() {
    measure = new MemberImpl();
    measures = new MemberImpl[] { measure};
    level = new LevelImpl();
    level.setLabel(MEASURES);
    measure.setLevel(level);
    hierarchy = new HierarchyImpl();
    hierarchy.setLabel(MEASURES);
    hierarchy.setLevels(new LevelImpl[] { level});
    level.setHierarchy(hierarchy);
    dimension = new DimensionImpl();
    dimension.setLabel(MEASURES);
    dimension.setHierarchies(new HierarchyImpl[] { hierarchy});
    hierarchy.setDimension(dimension);
    dimensions = new DimensionImpl[] { dimension};

    PositionImpl pos = new PositionImpl();
    pos.setMembers(new Member[] { measure});
    List posns = new ArrayList(1);
    posns.add(pos);
    AxisImpl axis = new AxisImpl();
    axis.setPositions(posns);
    axes = new Axis[] { axis};

    cell = new CellImpl();
    List list = new ArrayList(1);
    list.add(cell);
    cells = Collections.unmodifiableList(list);
    
    slicer = new AxisImpl();
    
    // some initial data
    setCaption(MEASURE);
    setValue(new Double(0));
    setFormattedValue("0.00");
  }

  /**
   * returns a collection containing a single cell
   */
  public List getCells() {
    return cells;
  }

  /**
   * returns one axis (measures)
   */
  public Axis[] getAxes() {
    return axes;
  }

  /**
   * returns an empty axis
   */
  public Axis getSlicer() {
    return slicer;
  }

  public void accept(Visitor visitor) {
    visitor.visitResult(this);
  }

  public Object getRootDecoree() {
    return this;
  }

  /**
   * returns the formatted value of the single cell
   */
  public String getFormattedValue() {
    return cell.getFormattedValue();
  }

  /**
   * sets the formatted value of the single cell
   */
  public void setFormattedValue(String formattedValue) {
    cell.setFormattedValue(formattedValue);
    fireModelChanged();
  }

  /**
   * returns the overflow property
   */
  public boolean isOverflowOccured() {
    return overflowOccured;
  }

  /**
   * sets the overflow property
   */
  public void setOverflowOccured(boolean overflowOccured) {
    this.overflowOccured = overflowOccured;
    fireModelChanged();
  }

  /**
   * returns the value of the single cell
   */
  public Object getValue() {
    return cell.getValue();
  }

  /**
   * sets the value of the single cell
   */
  public void setValue(Object value) {
    cell.setValue(value);
    fireModelChanged();
  }

  /**
   * sets the caption of the only measure
   */
  public void setCaption(String caption) {
    measure.setLabel(caption);
    fireModelChanged();
  }

  /**
   * returns the caption of the only measure
   */
  public String getCaption() {
    return measure.getLabel();
  }

  public Result getResult() throws OlapException {
    return this;
  }

  public Dimension[] getDimensions() {
    return dimensions;
  }

  public Member[] getMeasures() {
    return measures;
  }

  /**
   * does nothing
   */
  public void initialize() throws OlapException {
  }

  /**
   * does nothing
   */
  public void setServletContext(ServletContext servletContext) {
  }

  public String getID() {
    return ID;
  }

  public void setID(String id) {
    ID = id;
  }

}

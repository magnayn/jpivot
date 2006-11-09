package com.tonbeller.jpivot.mondrian;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.DrillExpandPosition;
import com.tonbeller.jpivot.olap.navi.PlaceHierarchiesOnAxes;
import com.tonbeller.jpivot.olap.navi.PlaceMembersOnAxes;
import com.tonbeller.jpivot.olap.query.DrillExpandPositionExt;
import com.tonbeller.jpivot.olap.query.ResultBase;
import com.tonbeller.jpivot.tags.MondrianModelFactory;

/**
 * Tests various bug fixes
 */
public class VariousTest extends TestCase {

  /**
   * Constructor
   * @param arg0
   */
  public VariousTest(String arg0) {
    super(arg0);
  }

  public void testSetCollapse() throws Exception {

    /*
     select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} ON columns,
     {[Product].[All Products]} ON rows from [Sales]
     */
    String mdxQuery = "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} "
        + "ON columns, " + "{[Product].[All Products]} ON rows from [Sales]";

    String renderFile = null; // "c:\\x\\testSetCollapse";
    int renderNum = 0;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    DrillExpandPositionExt mdep = (DrillExpandPositionExt) model
        .getExtension(DrillExpandPosition.ID);
    PlaceHierarchiesOnAxes placeHier = (PlaceHierarchiesOnAxes) model
        .getExtension(PlaceHierarchiesOnAxes.ID);
    PlaceMembersOnAxes placeMem = (PlaceMembersOnAxes) model.getExtension(PlaceMembersOnAxes.ID);

    // first step
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");

    assertPosition(result, 1, 0, new String[] { "All Products"});

    // Drill Down
    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();
    Position pos0 = (Position) positions.get(0); // All Products
    Hierarchy hierProduct = pos0.getMembers()[0].getLevel().getHierarchy();
    mdep.expand(pos0, pos0.getMembers()[0]); // drilldown All Products
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    Position pos3 = (Position) positions.get(3); // Non-Consumable
    assertPosition(result, 1, 3, new String[] { "Non-Consumable"});
    MondrianMember nonCons = (MondrianMember) pos3.getMembers()[0];

    List mList = placeMem.findVisibleMembers(hierProduct);
    // Remove Non-Consumable from List
    for (Iterator iter = mList.iterator(); iter.hasNext();) {
      MondrianMember m = (MondrianMember) iter.next();
      if (m.equals(nonCons))
        iter.remove();
    }
    // get time Hierarchy
    Dimension[] dims = model.getDimensions();
    Dimension timeDim = null;
    for (int i = 0; i < dims.length; i++) {
      if (((MondrianDimension) dims[i]).getLabel().equals("Time")) {
        timeDim = dims[i];
        break;
      }
    }
    Object oTime = placeHier.createMemberExpression(timeDim.getHierarchies()[0]);
    Object oSet = placeMem.createMemberExpression(mList);
    placeHier.setQueryAxis(axes[1], new Object[] { oSet, oTime});

    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");
    axes = result.getAxes();
    positions = axes[1].getPositions();

    // Expand [All Products] , [1997]
    assertPosition(result, 1, 0, new String[] { "All Products", "1997"});
    pos0 = (Position) positions.get(0);
    mdep.expand(pos0, pos0.getMembers()[1]);

    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");

    assertPosition(result, 1, 4, new String[] { "All Products", "Q4"});

    // Collapse All Products
    axes = result.getAxes();
    positions = axes[1].getPositions();
    Position pos4 = (Position) positions.get(4); // "All Products", "Q4"
    mdep.collapse(pos4, pos4.getMembers()[0]);
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 1, 1, new String[] { "All Products", "Q1"});
    assertEquals(positions.size(), 6);
  }

  /**
   * assert position
   */
  private void assertPosition(Result result, int iAxis, int iPos, String[] posMembers) {
    Position pos = (Position) result.getAxes()[iAxis].getPositions().get(iPos);
    for (int i = 0; i < posMembers.length; i++) {
      String str = pos.getMembers()[i].getLabel();
      assertEquals(str, posMembers[i]);
    }
  }
} // VariousTest

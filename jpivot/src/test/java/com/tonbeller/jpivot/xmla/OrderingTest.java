package com.tonbeller.jpivot.xmla;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.tonbeller.jpivot.core.ModelFactory;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.DrillExpandPosition;
import com.tonbeller.jpivot.olap.navi.PlaceMembersOnAxes;
import com.tonbeller.jpivot.olap.navi.SortRank;
import com.tonbeller.jpivot.olap.query.DrillExpandPositionExt;
import com.tonbeller.jpivot.olap.query.ResultBase;

/**
 * @author hh
 *
 */
public class OrderingTest extends TestCase {

  /**
   * Constructor
   * @param arg0
   */
  public OrderingTest(String arg0) {
    super(arg0);
  }

  public void testOrderDrillPos() throws Exception {

    /*
    select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,
    {([Gender].[All Gender], [Customers].[All Customers],
      [Product].[All Products] ) } on rows
      from Sales where ([Time].[1997])
    */
    String mdxQuery =
      "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,"
        + "{([Gender].[All Gender], [Customers].[All Customers],"
        + "[Product].[All Products] ) } on rows"
        + " from Sales where ([Time].[1997])";

    String renderFile = null; // "c:\\x\\testOrderDrillPos";
    int renderNum = 0;

    Result result;

    URL confUrl = XMLA_Model.class.getResource("config.xml");
    XMLA_Model model = (XMLA_Model) ModelFactory.instance(confUrl);

    model.setMdxQuery(mdxQuery);
		TestConnection.initModel(model);

    DrillExpandPositionExt mdep =
      (DrillExpandPositionExt)model.getExtension(DrillExpandPosition.ID);
    XMLA_SortRank msr = (XMLA_SortRank)model.getExtension(SortRank.ID);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    // Expand All Customers
    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();
    Position pos = (Position)positions.get(0);

    assertTrue(mdep.canExpand(pos, pos.getMembers()[1]));
    mdep.expand(pos, pos.getMembers()[1]); // drilldown All Customers

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 3, "All Gender", "USA", "All Products");

    // Expand All Gender, USA, All Products
    pos = (Position)positions.get(3);
    assertTrue(mdep.canExpand(pos, pos.getMembers()[2]));
    mdep.expand(pos, pos.getMembers()[2]); // drilldown All Products
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 5, "All Gender", "USA", "Food");

    // Expand All Gender
    pos = (Position)positions.get(0);
    assertTrue(mdep.canExpand(pos, pos.getMembers()[0]));
    mdep.expand(pos, pos.getMembers()[0]); // Expand All Gender
    result = model.getResult();
    if (renderFile != null)
   ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 11, "F", "USA", "Drink");

    // Collapse All Gender
    pos = (Position)positions.get(0);
    assertTrue(mdep.canCollapse(pos, pos.getMembers()[0]));
    mdep.collapse(pos, pos.getMembers()[0]); // collapse All Gender
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 4, "All Gender", "USA", "Drink");

    // Order Unit Sales DESC
    msr.setSortMode(SortRank.DESC);
    positions = axes[0].getPositions();
    Position sortPos = (Position)positions.get(0); // Unit Sales
    msr.setSorting(true);
    msr.sort(axes[1], sortPos);
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 2, "All Gender", "USA", "Food");

    // Order Store Cost ASC
    msr.setSortMode(SortRank.ASC);
    positions = axes[0].getPositions();
    sortPos = (Position)positions.get(1); // Store Cost
    msr.sort(axes[1], sortPos);
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 6, "All Gender", "USA", "Food");

    // remove Sort
    msr.setSorting(false);
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 5, "All Gender", "USA", "Food");

  }

  public void testOrderSwitchMode() throws Exception {

    /*
    select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,
    {([Product].[All Products], [Customers].[All Customers]) } on rows
      from Sales where ([Time].[1997])
    */
    String mdxQuery =
      "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,"
        + "{([Product].[All Products], [Customers].[All Customers]) } on rows"
        + " from Sales where ([Time].[1997])";

    String renderFile = null; // "c:\\x\\testOrderSwitchMode";
    int renderNum = 0;

    Result result;

    URL confUrl = XMLA_Model.class.getResource("config.xml");
    XMLA_Model model = (XMLA_Model) ModelFactory.instance(confUrl);

    model.setMdxQuery(mdxQuery);
		TestConnection.initModel(model);

    DrillExpandPositionExt mdep =
      (DrillExpandPositionExt)model.getExtension(DrillExpandPosition.ID);
    XMLA_SortRank msr = (XMLA_SortRank)model.getExtension(SortRank.ID);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");


    // Expand All Products
    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();
    Position pos = (Position)positions.get(0);

    assertTrue(mdep.canExpand(pos, pos.getMembers()[0]));
    mdep.expand(pos, pos.getMembers()[0]); // expand All Products

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 2, "Food", "All Customers", null);

    // Expand Food
    pos = (Position)positions.get(2);
    assertTrue(mdep.canExpand(pos, pos.getMembers()[0]));
    mdep.expand(pos, pos.getMembers()[0]); // expand Food
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 5, "Breakfast Foods", "All Customers", null);

    // Order Unit Sales DESC
    msr.setSortMode(SortRank.DESC);
    positions = axes[0].getPositions();
    Position sortPos = (Position)positions.get(0); // Unit Sales
    msr.setSorting(true);
    msr.sort(axes[1], sortPos);
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 3, "Snack Foods", "All Customers", null);

    // Order TopCount
    msr.setSortMode(SortRank.TOPCOUNT);
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 3, "Produce", "All Customers", null);

    // Order ASC
    msr.setSortMode(SortRank.ASC);
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 16, "Frozen Foods", "All Customers", null);

    // Order BottomCount
    msr.setSortMode(SortRank.BOTTOMCOUNT);
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 1, "Seafood", "All Customers", null);

    // remove unit sales from axis 0 (measures)
    XMLA_PlaceMembers mplace = (XMLA_PlaceMembers)model.getExtension(PlaceMembersOnAxes.ID);
    List ml = new ArrayList();
    positions = axes[0].getPositions();
    // omit first measure (Unit Sales)
    for (int i = 1; i < 3; i++) {
      Position p = (Position)positions.get(i);
      ml.add(p.getMembers()[0]);
    }
    Object o = mplace.createMemberExpression(ml);
    mplace.setQueryAxis(axes[0], new Object[] { o });
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    // nothing happens, except sorting is removed
    assertPosition(result, 1, "Seafood", "All Customers", null);
    // sorting must still be on
    assertTrue((msr.isSorting()));

    // expand Deli, BottomCount with Unit Sales is still active
    pos = (Position)positions.get(8);
    assertTrue(mdep.canExpand(pos, pos.getMembers()[0]));
    assertPosition(result, 8, "Deli", "All Customers", null);
    mdep.expand(pos, pos.getMembers()[0]); // expand Deli
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 3, "Side Dishes", "All Customers", null);

    // Order DESC
    msr.setSortMode(SortRank.DESC);
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 17, "Seafood", "All Customers", null);

    // remove Side Dishes from axis 1
    ml = new ArrayList();
    assertPosition(result, 10, "Side Dishes", "All Customers", null);
    for (int i = 0; i < positions.size(); i++) {
      if (i == 10)
        continue;
      Position p = (Position)positions.get(i);
      ml.add(p.getMembers()[0]);
    }
    Object o1 = mplace.createMemberExpression(ml);
    ml = new ArrayList();
    Position p = (Position)positions.get(0);
    ml.add(p.getMembers()[1]); // All Customers
    Object o2 = mplace.createMemberExpression(ml);
    mplace.setQueryAxis(axes[1], new Object[] { o1, o2 });
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    // sorting must be off
    assertTrue(!(msr.isSorting()));

    // Order Store Sales DESC
    msr.setSortMode(SortRank.DESC);
    positions = axes[0].getPositions();
    sortPos = (Position)positions.get(1); // Store Sales
    msr.setSorting(true);
    msr.sort(axes[1], sortPos);
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 13, "Eggs", "All Customers", null);
  }

  /**
   * assert position
   */
  private void assertPosition(Result result, int iPos, String first, String second, String third) {
    Position pos = (Position)result.getAxes()[1].getPositions().get(iPos);
    String s = pos.getMembers()[0].getLabel();
    assertEquals(s, first);
    if (second != null) {
      s = pos.getMembers()[1].getLabel();
      assertEquals(s, second);
    }
    if (third != null) {
      s = pos.getMembers()[2].getLabel();
      assertEquals(s, third);
    }
  }
 
} // End OrderingTest

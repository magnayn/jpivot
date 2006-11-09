package com.tonbeller.jpivot.mondrian;

import java.util.List;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.DrillReplace;
import com.tonbeller.jpivot.olap.query.DrillReplaceExt;
import com.tonbeller.jpivot.olap.query.ResultBase;
import com.tonbeller.jpivot.tags.MondrianModelFactory;

/**
 * @author hh
 *
 * Test expand/collapse with position and member
 */
public class DrillReplaceTest extends TestCase {

  /**
   * Constructor
   * @param arg0
   */
  public DrillReplaceTest(String arg0) {
    super(arg0);
  }

  public void testCrossJoin() throws Exception {

    /*
    select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,
    CrossJoin(  [Store].[USA].children, [Product].[All Products].[Drink].children) on rows
    from Sales  where ([Time].[1997])
    */
    String mdxQuery =
      "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns, "
        + "CrossJoin(  [Store].[USA].children, [Product].[All Products].[Drink].children) on rows "
        + "from Sales  where ([Time].[1997])";

    String renderFile = null; // "c:\\x\\testCrossJoin";
    int renderNum = 0;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 1, "CA", "Beverages");
    assertPosition(result, 6, "WA", "Alcoholic Beverages");

    DrillReplaceExt mdrep = (DrillReplaceExt) model.getExtension(DrillReplace.ID);

    // Drill down CA
    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();
    Position pos1 = (Position) positions.get(1); // (CA,Beverages)

    assertTrue(mdrep.canDrillDown(pos1.getMembers()[0]));
    mdrep.drillDown(pos1.getMembers()[0]); // drilldown CA

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 6, "Los Angeles", "Alcoholic Beverages");
    assertPosition(result, 10, "San Diego", "Beverages");
    assertPosition(result, 14, "San Francisco", "Dairy");

    // drill up the Store hierarchy
    axes = result.getAxes();
    positions = axes[1].getPositions();
    Position pos0 = (Position) positions.get(0); // no matter what it is
    Hierarchy hier = pos0.getMembers()[0].getLevel().getHierarchy();

    assertTrue(mdrep.canDrillUp(hier));
    mdrep.drillUp(hier); // drill up "Store"

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 2, "CA", "Dairy");
    assertPosition(result, 7, "WA", "Beverages");

    // another drill up of the Store hierarchy
    axes = result.getAxes();
    positions = axes[1].getPositions();
    pos0 = (Position) positions.get(0); // no matter what it is
    hier = pos0.getMembers()[0].getLevel().getHierarchy();

    assertTrue(mdrep.canDrillUp(hier));
    mdrep.drillUp(hier); // drill up "Store"

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 7, "USA", "Beverages");

    // last drill up of the Store hierarchy reaching "All Stores"
    axes = result.getAxes();
    positions = axes[1].getPositions();
    pos0 = (Position) positions.get(0); // no matter what it is
    hier = pos0.getMembers()[0].getLevel().getHierarchy();

    assertTrue(mdrep.canDrillUp(hier));
    mdrep.drillUp(hier); // drill up "Store"

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 2, "All Stores", "Dairy");

    // now drill up is not possible any more
    assertTrue(!mdrep.canDrillUp(hier));

  }

  public void testSingle() throws Exception {

    /*
      select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,
      { [Store].[USA].children } on rows 
      from Sales  where ([Time].[1997])
    */
    String mdxQuery =
      "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns, "
        + "{ [Store].[USA].children } on rows "
        + "from Sales  where ([Time].[1997])";

    String renderFile = null; // "c:\\x\\testSingle";
    int renderNum = 0;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 0, "CA", null);
    assertPosition(result, 1, "OR", null);

    DrillReplaceExt mdrep = (DrillReplaceExt) model.getExtension(DrillReplace.ID);

    // Drill down CA
    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();
    Position pos = (Position) positions.get(0); // CA
    assertTrue(mdrep.canDrillDown(pos.getMembers()[0]));
    mdrep.drillDown(pos.getMembers()[0]); // drilldown CA

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 1, "Beverly Hills", null);
    assertPosition(result, 3, "San Diego", null);

    // Drill down San Diego
    axes = result.getAxes();
    positions = axes[1].getPositions();
    pos = (Position) positions.get(3); // San Diego

    assertTrue(mdrep.canDrillDown(pos.getMembers()[0]));
    mdrep.drillDown(pos.getMembers()[0]); // drilldown San Diego

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 0, "Store 24", null);

    // this is the lowest level
    axes = result.getAxes();
    positions = axes[1].getPositions();
    pos = (Position) positions.get(0);
    assertTrue(!mdrep.canDrillDown(pos.getMembers()[0]));

    // drill up the Store hierarchy
    axes = result.getAxes();
    positions = axes[1].getPositions();
    pos = (Position) positions.get(0); // no matter what it is
    Hierarchy hier = pos.getMembers()[0].getLevel().getHierarchy();

    assertTrue(mdrep.canDrillUp(hier));
    mdrep.drillUp(hier); // drill up "Store"

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 2, "Los Angeles", null);
    assertPosition(result, 4, "San Francisco", null);

    // drill up for state level
    assertTrue(mdrep.canDrillUp(hier));
    mdrep.drillUp(hier); // drill up "Store"

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 2, "WA", null);

    // drill up for country level
    assertTrue(mdrep.canDrillUp(hier));
    mdrep.drillUp(hier); // drill up "Store"

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 2, "USA", null);

    // drill up for All Stores level
    assertTrue(mdrep.canDrillUp(hier));
    mdrep.drillUp(hier); // drill up "Store"

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 0, "All Stores", null);

    // now drill up is not possible any more
    assertTrue(!mdrep.canDrillUp(hier));

  }

  public void testTwoCrossjDown() throws Exception {

    String mdxQuery =
      "select {[Measures].[Sales Count]} on columns, "
        + "{ CrossJoin(  [Customers].[All Customers].[USA].[CA].children, [Product].[All Products].[Drink].children), "
        + "CrossJoin(  [Customers].[All Customers].[USA].[WA].children, [Product].[All Products].[Drink].children) } on rows "
        + "from Sales  where ([Time].[1997])";
   
    String renderFile = null; // "c:\\x\\testTwoCrossjDown";
    int renderNum = 0;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 2, "Altadena", "Dairy");
    assertPosition(result, 9, "Berkeley", "Alcoholic Beverages");

    DrillReplaceExt mdrep = (DrillReplaceExt) model.getExtension(DrillReplace.ID);

    // Drill down Altadena
    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();

    assertPosition(result, positions.size() - 1, "Yakima", "Dairy"); // last of WA expected

    Position pos = (Position) positions.get(0); // (Altadena,x)
    Hierarchy hier = pos.getMembers()[0].getLevel().getHierarchy();
    assertTrue(mdrep.canDrillDown(pos.getMembers()[0]));
    mdrep.drillDown(pos.getMembers()[0]); // drilldown Altadena

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 10, "Angela Evans", "Beverages");
    assertPosition(result, positions.size() - 7, "Wendell Kersten", "Dairy");

    // Drill up Customer hierarchy
    assertTrue(mdrep.canDrillUp(hier));
    mdrep.drillUp(hier); // drill up Customer

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, positions.size() - 1, "Woodland Hills", "Dairy"); // last of CA expected
  }

  public void testTwoCrossjUp() throws Exception {

    /*
     select {[Measures].[Sales Count]} on columns,
     { CrossJoin(  [Customers].[All Customers].[USA].[CA].[Berkeley].children, [Product].[All Products].[Drink].children),
       CrossJoin(  [Customers].[All Customers].[USA].[WA].[Seattle].children, [Product].[All Products].[Drink].children) } on rows
       from Sales  where ([Time].[1997])
     */

    String mdxQuery =
      "select {[Measures].[Sales Count]} on columns, "
        + "{ CrossJoin(  [Customers].[All Customers].[USA].[CA].[Berkeley].children, [Product].[All Products].[Drink].children), "
        + "CrossJoin(  [Customers].[All Customers].[USA].[WA].[Seattle].children, [Product].[All Products].[Drink].children) } on rows "
        + "from Sales  where ([Time].[1997])";

    String renderFile = null; // "c:\\x\\testTwoCrossjUp";
    int renderNum = 0;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();
    Position pos = (Position) positions.get(0);
    Hierarchy hier = pos.getMembers()[0].getLevel().getHierarchy();

    assertPosition(result, 4, "Alma Shelton", "Beverages");
    assertPosition(result, positions.size() - 3, "Yvonne Rose", "Alcoholic Beverages");

    DrillReplaceExt mdrep = (DrillReplaceExt) model.getExtension(DrillReplace.ID);

    // Drill up Customer hierarchy
    assertTrue(mdrep.canDrillUp(hier));
    mdrep.drillUp(hier); // drill up Customer

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();

    // ((MondrianResult)result).renderHtml("c:\\x\\x.html");
    assertPosition(result, 0, "Altadena", "Alcoholic Beverages"); // first of WA expected
    assertPosition(result, positions.size() - 1, "Yakima", "Dairy"); // last of WA expected

  }

  /**
   * assert position
   */
  private void assertPosition(Result result, int iPos, String firstMember, String secondMember) {
    Position pos = (Position) result.getAxes()[1].getPositions().get(iPos);
    String s = pos.getMembers()[0].getLabel();
    assertEquals(s, firstMember);
    if (secondMember != null) {
      s = pos.getMembers()[1].getLabel();
      assertEquals(s, secondMember);
    }
  }

} // End DrillReplaceTest

package com.tonbeller.jpivot.xmla;

import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import com.tonbeller.jpivot.core.ModelFactory;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.DrillExpandMember;
import com.tonbeller.jpivot.olap.navi.DrillExpandPosition;
import com.tonbeller.jpivot.olap.query.DrillExpandMemberExt;
import com.tonbeller.jpivot.olap.query.DrillExpandPositionExt;
import com.tonbeller.jpivot.olap.query.ResultBase;

/**
 * @author hh
 *
 * Test expand/collapse with position and member
 */
public class ExpandCollapseTest extends TestCase {

  /**
   * Constructor for ExpandCollapse.
   * @param arg0
   */
  public ExpandCollapseTest(String arg0) {
    super(arg0);
  }

  public void testExpandPosMem() throws Exception {

    /*
     select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,
     CrossJoin(  [Store].[USA].children, [Product].[All Products].[Drink].children) on rows 
     from Sales  where ([Time].[1997])
     */
    String mdxQuery =
      "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns, "
        + "CrossJoin(  [Store].[USA].children, [Product].[All Products].[Drink].children) on rows "
        + "from Sales  where ([Time].[1997])";

    String renderFile = null; // "c:\\x\\testExpandPosMem";
    int renderNum = 0;

    Result result;

    URL confUrl = XMLA_Model.class.getResource("config.xml");
    XMLA_Model model = (XMLA_Model) ModelFactory.instance(confUrl);

    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 1, "CA", "Beverages");
    assertPosition(result, 6, "WA", "Alcoholic Beverages");

    DrillExpandPositionExt mdep =
      (DrillExpandPositionExt) model.getExtension(DrillExpandPosition.ID);

    // expand (CA,Beverages) for CA

    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();
    Position pos = (Position) positions.get(1); // (CA,Beverages)

    assertTrue(mdep.canExpand(pos, pos.getMembers()[0]));
    mdep.expand(pos, pos.getMembers()[0]); // expand (CA,Beverages) for CA

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();
    assertPosition(result, 6, "Beverly Hills", "Alcoholic Beverages");
    assertPosition(result, 10, "Los Angeles", "Beverages");
    assertPosition(result, 17, "San Francisco", "Dairy");
    assertPosition(result, positions.size() - 1, "WA", "Dairy");

    // expand (CA,Beverages)) for Beverages
    pos = (Position) positions.get(1); // (CA,Beverages)
    assertTrue(mdep.canExpand(pos, pos.getMembers()[1]));
    mdep.expand(pos, pos.getMembers()[1]); // expand (CA,Beverages) for Beverages

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();
    assertPosition(result, 2, "CA", "Carbonated Beverages");
    assertPosition(result, 5, "CA", "Pure Juice Beverages");
    assertPosition(result, 10, "Beverly Hills", "Alcoholic Beverages");
    assertPosition(result, positions.size() - 4, "OR", "Dairy");

    // expand (Los Angeles,Beverages)) for Beverages

    pos = (Position) positions.get(14); // (Los Angeles,Beverages)
    assertTrue(mdep.canExpand(pos, pos.getMembers()[1]));
    mdep.expand(pos, pos.getMembers()[1]); // expand (LA,Beverages) for Beverages

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();
    assertPosition(result, 15, "Los Angeles", "Carbonated Beverages");
    assertPosition(result, 17, "Los Angeles", "Hot Beverages");
    assertPosition(result, 21, "San Diego", "Beverages");

    // collapse (CA,Beverages) for CA
    pos = (Position) positions.get(1); // (CA,Beverages)
    assertTrue(mdep.canCollapse(pos, pos.getMembers()[0]));
    mdep.collapse(pos, pos.getMembers()[0]); // collapse (CA,Beverages) for CA

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();
    assertPosition(result, 0, "CA", "Alcoholic Beverages");
    assertPosition(result, 5, "CA", "Pure Juice Beverages");
    assertPosition(result, 10, "WA", "Alcoholic Beverages");

    // collapse (CA,Beverages) for Beverages
    pos = (Position) positions.get(1); // (CA,Beverages)
    assertTrue(mdep.canCollapse(pos, pos.getMembers()[1]));
    mdep.collapse(pos, pos.getMembers()[1]); // collapse (CA,Beverages) for Beverages

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();
    assertPosition(result, 8, "WA", "Dairy");
    assertEquals(positions.size(), 9);

  }

  public void testExpandMem() throws Exception {
    /*
    select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns, 
    CrossJoin(  [Store].[USA].children, [Product].[All Products].[Drink].children) on rows 
    from Sales  where ([Time].[1997])
     */

    String mdxQuery =
      "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns, "
        + "CrossJoin(  [Store].[USA].children, [Product].[All Products].[Drink].children) on rows "
        + "from Sales  where ([Time].[1997])";

    String renderFile = null; // "c:\\x\\testExpandMem";
    int renderNum = 0;

    Result result;

    URL confUrl = XMLA_Model.class.getResource("config.xml");
    XMLA_Model model = (XMLA_Model) ModelFactory.instance(confUrl);

    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 1, "CA", "Beverages");
    assertPosition(result, 6, "WA", "Alcoholic Beverages");

    DrillExpandMemberExt mdem =
      (DrillExpandMemberExt) model.getExtension(DrillExpandMember.ID);
 
    // expand for CA

    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();
    Position pos = (Position) positions.get(1); // (CA,Beverages)

    assertTrue(mdem.canExpand(pos.getMembers()[0])); //CA
    mdem.expand(pos.getMembers()[0]); // CA

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 8, "Beverly Hills", "Dairy");
    assertPosition(result, 12, "San Diego", "Alcoholic Beverages");
    assertPosition(result, 16, "San Francisco", "Beverages");

    // expand for Beverages
    positions = result.getAxes()[1].getPositions();
    pos = (Position) positions.get(1); // (CA,Beverages)
    assertTrue(mdem.canExpand(pos.getMembers()[1]));
    mdem.expand(pos.getMembers()[1]); // expand for Beverages

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 2, "CA", "Carbonated Beverages");
    assertPosition(result, 5, "CA", "Pure Juice Beverages");
    assertPosition(result, 24, "Los Angeles", "Drinks");
    assertPosition(result, 32, "San Diego", "Hot Beverages");
    assertPosition(result, 44, "OR", "Carbonated Beverages");

    // expand for Los Angeles

    positions = result.getAxes()[1].getPositions();
    pos = (Position) positions.get(21); // (LA,Alcoholic Beverages)
    assertTrue(mdem.canExpand(pos.getMembers()[0])); // Los Angeles
    mdem.expand(pos.getMembers()[0]); // expand for Los Angeles

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 30, "Store 7", "Carbonated Beverages");

    // collapse for CA
    positions = result.getAxes()[1].getPositions();
    pos = (Position) positions.get(21); // (Los Angeles, Alcoholic Beverages)
    assertTrue(mdem.canCollapse(pos.getMembers()[0])); // Los angeles
    pos = (Position) positions.get(2); // (CA,Beverages)
    assertTrue(mdem.canCollapse(pos.getMembers()[0])); // CA
    mdem.collapse(pos.getMembers()[0]); // collapse CA

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 4, "CA", "Hot Beverages");
    assertPosition(result, 12, "OR", "Pure Juice Beverages");
    assertPosition(result, 14, "WA", "Alcoholic Beverages");

    // collapse for Beverages
    positions = result.getAxes()[1].getPositions();
    pos = (Position) positions.get(1); // (CA,Beverages)
    assertTrue(mdem.canCollapse(pos.getMembers()[1])); // Beverages
    mdem.collapse(pos.getMembers()[1]); // collapse  Beverages

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();
    assertPosition(result, 8, "WA", "Dairy");
    assertEquals(positions.size(), 9);

  }

  public void testMixedMode() throws Exception {

    String mdxQuery =
      " select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} ON columns, "
        + "CrossJoin( {[Promotion Media].[All Media]},"
        + " {[Product].[All Products].[Drink].children} ) ON rows "
        + "from [Sales] where ([Time].[1997]) ";

    String renderFile = null; // "c:\\x\\testMixedMode";
    int renderNum = 0;

    Result result;

    URL confUrl = XMLA_Model.class.getResource("config.xml");
    XMLA_Model model = (XMLA_Model) ModelFactory.instance(confUrl);

    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 2, "All Media", "Dairy");

    DrillExpandMemberExt mdem =
      (DrillExpandMemberExt) model.getExtension(DrillExpandMember.ID);
    DrillExpandPositionExt mdep =
      (DrillExpandPositionExt) model.getExtension(DrillExpandPosition.ID);

    // expand for All Media
    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();
    Position pos = (Position) positions.get(1); // (All Media,Beverages)

    assertTrue(mdem.canExpand(pos.getMembers()[0])); // All Media
    mdem.expand(pos.getMembers()[0]); // All Media

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertPosition(result, 3, "Bulk Mail", "Alcoholic Beverages");
    assertPosition(result, 10, "Daily Paper", "Beverages");
    assertPosition(result, 28, "Radio", "Beverages");
    assertPosition(result, 44, "TV", "Dairy");

    // expand (Radio, Beverages) for Beverages
    pos = (Position) positions.get(27); // (Radio, Alcoholic Beverages)
    assertTrue(!mdep.canExpand(pos, pos.getMembers()[0])); // Radio
    assertTrue(mdep.canExpand(pos, pos.getMembers()[1])); // Alcoholic Beverages
    mdep.expand(pos, pos.getMembers()[1]); // Alcoholic Beverages

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 28, "Radio", "Beer and Wine");
    axes = result.getAxes();
    positions = axes[1].getPositions();

    // expand for Dairy
    pos = (Position) positions.get(2); // (All Media, Dairy)
    assertTrue(mdem.canExpand(pos.getMembers()[1])); // Dairy
    mdem.expand(pos.getMembers()[1]); // Dairy

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertEquals(positions.size(), 61);

    // Collapse All Media
    pos = (Position) positions.get(0); // (All Media, )
    assertTrue(mdem.canCollapse(pos.getMembers()[0])); // All Media
    mdem.collapse(pos.getMembers()[0]); // All Media

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");
    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertEquals(positions.size(), 4);

    // Collapse dairy
    pos = (Position) positions.get(2); // (All Media, Dairy )
    assertTrue(mdep.canCollapse(pos, pos.getMembers()[1])); // All Media, Dairy
    mdep.collapse(pos, pos.getMembers()[1]);

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");
    axes = result.getAxes();
    positions = axes[1].getPositions();
    assertEquals(positions.size(), 3);
  }

  /**
   * assert position
   */
  private void assertPosition(Result result, int iPos, String firstMember, String secondMember) {
    Position pos = (Position) result.getAxes()[1].getPositions().get(iPos);
    String s = pos.getMembers()[0].getLabel();
    assertEquals(s, firstMember);
    s = pos.getMembers()[1].getLabel();
    assertEquals(s, secondMember);
  }

} // End ExpandCollapseTest

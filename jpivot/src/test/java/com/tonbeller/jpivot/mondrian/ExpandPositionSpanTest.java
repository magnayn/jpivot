package com.tonbeller.jpivot.mondrian;

import java.util.List;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.DrillExpandPosition;
import com.tonbeller.jpivot.olap.query.DrillExpandPositionExt;
import com.tonbeller.jpivot.olap.query.ResultBase;
import com.tonbeller.jpivot.tags.MondrianModelFactory;

/**
 * @author hh
 *
 * Test expand/collapse with position and member
 */
public class ExpandPositionSpanTest extends TestCase {

  /**
   * Constructor for ExpandCollapse.
   * @param arg0
   */
  public ExpandPositionSpanTest(String arg0) {
    super(arg0);
  }

  public void testExpandPositionSpan() throws Exception {
    /*
    select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,
    {([Gender].[All Gender], [Marital Status].[All Marital Status],
    [Customers].[All Customers],
    [Product].[All Products] ) } on rows 
    from Sales where ([Time].[1997])
    */

    String mdxQuery =
      "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,"
        + "{([Gender].[All Gender], [Marital Status].[All Marital Status],"
        + "[Customers].[All Customers],"
        + "[Product].[All Products] ) } on rows "
        + "from Sales where ([Time].[1997])";

    String renderFile = null; // "c:\\x\\testExpandPosMem";
    int renderNum = 0;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    DrillExpandPositionExt mdep =
      (DrillExpandPositionExt) model.getExtension(DrillExpandPosition.ID);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    List positions = result.getAxes()[1].getPositions();
    // expand All Gender
    Position pos = (Position) positions.get(0);
    assertTrue(mdep.canExpand(pos, pos.getMembers()[0]));
    mdep.expand(pos, pos.getMembers()[0]); // expand All Gender

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();
    assertPosition(
      result,
      1,
      new String[] { "F", "All Marital Status", "All Customers", "All Products" });

    // expand (F,,All Customers,)
    pos = (Position) positions.get(1);
    assertTrue(mdep.canExpand(pos, pos.getMembers()[2]));
    mdep.expand(pos, pos.getMembers()[2]); // expand All Customers

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();
    assertPosition(result, 4, new String[] { "F", "All Marital Status", "USA", "All Products" });

    // expand All Products
    pos = (Position) positions.get(4);
    assertTrue(mdep.canExpand(pos, pos.getMembers()[3]));
    mdep.expand(pos, pos.getMembers()[3]); // expand F, All, USA, All Products

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();
    assertPosition(result, 5, new String[] { "F", "All Marital Status", "USA", "Drink" });

    // expand USA
    pos = (Position) positions.get(5);
    assertTrue(mdep.canExpand(pos, pos.getMembers()[2]));
    mdep.expand(pos, pos.getMembers()[2]); // expand F, All, USA

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();

    assertPosition(result, 9, new String[] { "F", "All Marital Status", "CA", "Drink" });
    assertPosition(result, 14, new String[] { "F", "All Marital Status", "OR", "Food" });

    // expand All Marital status under F
    pos = (Position) positions.get(1);
    assertTrue(mdep.canExpand(pos, pos.getMembers()[1]));
    mdep.expand(pos, pos.getMembers()[1]); // expand All Marital Status

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();

    assertPosition(result, positions.size() - 2, new String[] { "F", "S", "WA", "Non-Consumable" });
    assertPosition(result, positions.size() - 5, new String[] { "F", "S", "WA", "All Products" });

    // Collapse all Products      
    pos = (Position) positions.get(positions.size() - 5);
    assertTrue(mdep.canCollapse(pos, pos.getMembers()[3]));
    mdep.collapse(pos, pos.getMembers()[3]); // "F", "S", "WA", "All Products"

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();

    assertPosition(result, positions.size() - 2, new String[] { "F", "S", "WA", "All Products" });

    // collapse All Marital Status
    pos = (Position) positions.get(1);
    assertTrue(mdep.canCollapse(pos, pos.getMembers()[1]));
    mdep.collapse(pos, pos.getMembers()[1]); // All Marital Status

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();

    assertPosition(result, 8, new String[] { "F", "All Marital Status", "CA", "All Products" });

    // collapse All Gender
    pos = (Position) positions.get(0);
    assertTrue(mdep.canCollapse(pos, pos.getMembers()[0]));
    mdep.collapse(pos, pos.getMembers()[0]); // All Gender

    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    positions = result.getAxes()[1].getPositions();
    assertEquals(positions.size(), 1);
  }

  /**
   * assert position
   */
  private void assertPosition(Result result, int iPos, String[] posMembers) {
    Position pos = (Position) result.getAxes()[1].getPositions().get(iPos);
    for (int i = 0; i < posMembers.length; i++) {
      String str = pos.getMembers()[i].getLabel();
      assertEquals(str, posMembers[i]);
    }
  }

} // End ExpandPositionSpanTest

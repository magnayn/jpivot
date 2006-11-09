package com.tonbeller.jpivot.mondrian;

import java.util.List;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.DrillExpandPosition;
import com.tonbeller.jpivot.olap.query.DrillExpandPositionExt;
import com.tonbeller.jpivot.olap.query.ResultBase;
import com.tonbeller.jpivot.tags.MondrianModelFactory;
import com.tonbeller.wcf.bookmarks.Bookmarkable;

/**
 * make sure that the measures "Store Cost,Store Sales, Unit Sales"
 * are NOT "hierarchized".
 */
public class CoSalUniTest extends TestCase {

  /**
   * Constructor
   * @param arg0
   */
  public CoSalUniTest(String arg0) {
    super(arg0);
  }

  public void testCoSalUni() throws Exception {

    /*
     select Crossjoin({[Store Size in SQFT].[All Store Size in SQFTs]},
     Crossjoin({[Store Type].[All Store Types]},
     {[Measures].[Store Cost], [Measures].[Store Sales], [Measures].[Unit Sales]})) ON columns,
     {[Product].[All Products]} ON rows
     from [Sales]
     where [Time].[1997]
     */
    String mdxQuery = "select Crossjoin({[Store Size in SQFT].[All Store Size in SQFTs]}, "
        + "Crossjoin({[Store Type].[All Store Types]}, "
        + "{[Measures].[Store Cost], [Measures].[Store Sales], [Measures].[Unit Sales]})) ON columns, "
        + "{[Product].[All Products]} ON rows from [Sales] where [Time].[1997]";

    String renderFile = null; //"c:\\x\\testCoSalUni";
    int renderNum = 0;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    // first step
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(),  renderFile + renderNum++ + ".html");

    assertPosition(result, 2, new String[] { "All Store Size in SQFTs", "All Store Types",
        "Unit Sales"});

    DrillExpandPositionExt mdep = (DrillExpandPositionExt) model
        .getExtension(DrillExpandPosition.ID);

    // Drill down All Store Size
    Axis[] axes = result.getAxes();
    List positions = axes[0].getPositions();
    Position pos0 = (Position) positions.get(0); // Drill down All Store Size

    assertTrue(mdep.canExpand(pos0, pos0.getMembers()[0]));
    mdep.expand(pos0, pos0.getMembers()[0]); // drilldown All Store Size

    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(),  renderFile + renderNum++ + ".html");

    assertPosition(result, 7, new String[] { "20319", "All Store Types", "Store Sales"});

    // drill down All Store Types below 20319.0
    axes = result.getAxes();
    positions = axes[0].getPositions();
    Position pos7 = (Position) positions.get(7); // "20319.0", "All Store Types"
    mdep.expand(pos7, pos7.getMembers()[1]); // drilldown All Store Size    
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(),  renderFile + renderNum++ + ".html");

    assertPosition(result, 24, new String[] { "20319", "Supermarket", "Store Cost"});

    // save bookmark and reload
    Object state = model.getBookmarkState(Bookmarkable.EXTENSIONAL);
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(),  renderFile + renderNum++ + ".html");

    assertPosition(result, 1, new String[] { "All Store Size in SQFTs", "All Store Types",
        "Store Sales"});

    model.setMdxQuery(mdxQuery);
    model.setBookmarkState(state);
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(),  renderFile + renderNum++ + ".html");

    assertPosition(result, 24, new String[] { "20319", "Supermarket", "Store Cost"});

  }

  /**
   * assert position
   */
  private void assertPosition(Result result, int iPos, String[] posMembers) {
    Position pos = (Position) result.getAxes()[0].getPositions().get(iPos);
    for (int i = 0; i < posMembers.length; i++) {
      String str = pos.getMembers()[i].getLabel();
      assertEquals(str, posMembers[i]);
    }
  }
} // CoSalUni

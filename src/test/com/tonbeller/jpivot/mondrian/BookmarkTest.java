package com.tonbeller.jpivot.mondrian;

import java.util.List;

import junit.framework.TestCase;
import mondrian.mdx.LevelExpr;
import mondrian.mdx.MemberExpr;
import mondrian.mdx.UnresolvedFunCall;
import mondrian.olap.Exp;
import mondrian.olap.FunCall;
import mondrian.olap.Literal;
import mondrian.olap.Syntax;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.DrillExpandPosition;
import com.tonbeller.jpivot.olap.navi.PlaceHierarchiesOnAxes;
import com.tonbeller.jpivot.olap.query.DrillExpandPositionExt;
import com.tonbeller.jpivot.olap.query.ResultBase;
import com.tonbeller.jpivot.tags.MondrianModelFactory;
import com.tonbeller.wcf.bookmarks.Bookmarkable;

/**
 * Test Bookmarks
 */
public class BookmarkTest extends TestCase {

  /**
   * Constructor
   * @param arg0
   */
  public BookmarkTest(String arg0) {
    super(arg0);
  }

  public void testTopCount() throws Exception {

    /*
    select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]}
    ON columns,
    {([Time].[1997],[Product].[All Products].[Food].[Produce].[Vegetables].[Fresh Vegetables].[Hermanos])}
    ON rows from [Sales]
     */
    String mdxQuery = "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} "
        + "ON columns, "
        + "{([Time].[1997],[Product].[All Products].[Food].[Produce].[Vegetables].[Fresh Vegetables].[Hermanos])}"
        + " ON rows from [Sales]";

    String renderFile = null; // "c:\\x\\testBMTopCount";
    int renderNum = 0;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    DrillExpandPositionExt mdep = (DrillExpandPositionExt) model
        .getExtension(DrillExpandPosition.ID);
    PlaceHierarchiesOnAxes placeHier = (PlaceHierarchiesOnAxes) model
        .getExtension(PlaceHierarchiesOnAxes.ID);
    //PlaceMembersOnAxes placeMem = (PlaceMembersOnAxes) model.getExtension(PlaceMembersOnAxes.ID);

    // first step
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");

    assertPosition(result, 1, 0, new String[] { "1997"});

    // put Time dimension on rows axis
    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();
    Position pos0 = (Position) positions.get(0);
    MondrianMember allTime = (MondrianMember) pos0.getMembers()[0];
    Object exp1 = placeHier.createMemberExpression(allTime.getLevel().getHierarchy());
    MondrianMember hermanos = (MondrianMember) pos0.getMembers()[1];
    MondrianLevel brandLevel = (MondrianLevel) hermanos.getLevel();
    positions = axes[0].getPositions();
    pos0 = (Position) positions.get(0);
    MondrianMember unitsales = (MondrianMember) pos0.getMembers()[0];
    // TopCount([Product].[Brand Name].Members, 5, [Measures].[Unit Sales]))
    UnresolvedFunCall brandNameMembers = new UnresolvedFunCall("members", Syntax.Property, new Exp[] { 
        new LevelExpr(brandLevel.getMonLevel())});

    UnresolvedFunCall funOrder = new UnresolvedFunCall("topcount", new Exp[] { brandNameMembers,
        Literal.create(new Integer(5)), new MemberExpr(unitsales.getMonMember())});
    placeHier.setQueryAxis(axes[1], new Object[] { exp1, funOrder});
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");
    assertPosition(result, 1, 1, new String[] { "1997", "Tell Tale"});

    // Drill Down
    axes = result.getAxes();
    positions = axes[1].getPositions();
    pos0 = (Position) positions.get(0);
    mdep.expand(pos0, pos0.getMembers()[0]); // drilldown 1997
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");

    assertPosition(result, 1, 5, new String[] { "Q1", "Hermanos"});

    // create Bookmark
    Object bm = model.getBookmarkState(Bookmarkable.EXTENSIONAL);

    model.setBookmarkState(bm);
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");
    assertPosition(result, 1, 7, new String[] { "Q1", "Ebony"});
    axes = result.getAxes();
    positions = axes[1].getPositions();
    Position pos = (Position) positions.get(1);
    assertTrue(mdep.canExpand(pos, pos.getMembers()[1]));
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

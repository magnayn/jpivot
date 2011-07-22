package com.tonbeller.jpivot.mondrian;

import java.util.List;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.query.ResultBase;
import com.tonbeller.jpivot.tags.MondrianModelFactory;

public class EmptyResultTest extends TestCase {

  /**
   * Constructor
   * @param arg0
   */
  public EmptyResultTest(String arg0) {
    super(arg0);
  }

  public void testEmptyResult() throws Exception {

    /*
     select {[Measures].[Unit Sales], [Measures].[Store Cost]} ON columns,
     Filter([Product].[Brand Name].Members, ([Measures].[Unit Sales] > 100000.0)) ON rows
     from [Sales] where [Time].[1997]
     */
    String mdxQuery = "select NON EMPTY {[Measures].[Unit Sales], [Measures].[Store Cost]} ON columns, "
        + "NON EMPTY Filter([Product].[Brand Name].Members, ([Measures].[Unit Sales] > 100000.0)) ON rows "
        + "from [Sales] where [Time].[1997]";

    String renderFile = null; //"c:\\x\\testEmptyResult";
    int renderNum = 0;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    // first step
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");
    Axis[] axes = result.getAxes();
    List positions = axes[0].getPositions();
    assertEquals(positions.size(), 0);
    positions = axes[1].getPositions();
    assertEquals(positions.size(), 0);
  }

} // EmptyResult

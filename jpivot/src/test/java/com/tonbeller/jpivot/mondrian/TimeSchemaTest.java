package com.tonbeller.jpivot.mondrian;

import java.net.URL;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.query.ResultBase;
import com.tonbeller.jpivot.tags.MondrianModelFactory;

/**
 * Time dimension compatibility Test for "old" Schema files
 */
public class TimeSchemaTest extends TestCase {

  /**
   * Constructor
   * 
   * @param arg0
   */
  public TimeSchemaTest(String arg0) {
    super(arg0);
  }

  /**
   * test time dimension having Non Time Level
   * 
   * @throws Exception
   */
  public void testTimeDimNonTimeLev() throws Exception {

    String mdxQuery = "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns, "
        + "Crossjoin([Time].[Quartale].members, [Customers].[All Customers].[USA].children) on rows "
        + "from Sales";

    String renderFile = null; //"c:\\x\\testTimeDimNonTimeLev";
    int renderNum = 0;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);

    // the catalog file is TestSchema.xml in "this" directory
//    URL url = this.getClass().getResource("TestSchema.xml");
//    String catUri = "file://" + url.getFile();

    URL url = this.getClass().getResource("TestSchema.xml");
    String connectString = TestConnection.getConnectString(url.toExternalForm());
    model.setConnectString(connectString);
    String jdbcDriver = TestConnection.getJdbcDriver();
    model.setJdbcDriver(jdbcDriver);
    model.initialize();

    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");

    assertPosition(result, 2, "Q1", "WA");
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

} // TimeSchemaTest

package com.tonbeller.jpivot.mondrian;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.JunitUtil;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.PlaceHierarchiesOnAxes;
import com.tonbeller.jpivot.olap.query.ResultBase;
import com.tonbeller.jpivot.tags.MondrianModelFactory;

/**
 * Test the "expand All member" of the PlaceHierarchies extension
 */
public class ExpandAllTest extends TestCase {

  /**
   * Constructor
   * @param arg0
   */
  public ExpandAllTest(String arg0) {
    super(arg0);
  }

  public void testExpandAllMember() throws Exception {

    /*
     select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} ON columns,
     {[Product].[All Products]} ON rows from [Sales]
     */
    String mdxQuery = "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} "
        + "ON columns, " + "{[Product].[All Products]} ON rows from [Sales]";

    String renderFile = null; //"c:\\x\\testExpandAllMember";
    int renderNum = 0;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);
    PlaceHierarchiesOnAxes placeHier = (PlaceHierarchiesOnAxes) model
        .getExtension(PlaceHierarchiesOnAxes.ID);

    // set the "Expand All member" feature
    placeHier.setExpandAllMember(true);
    
    // first step
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");

    JunitUtil.assertPosition(result, 1, 0, new String[] { "All Products"}, new String[] { "266773",
        "225627.23", "565238.13"});

    // get Customers Hierarchy and add it to axis 1
    Dimension[] dims = model.getDimensions();
    Dimension custoDim = null;
    for (int i = 0; i < dims.length; i++) {
      if ((dims[i]).getLabel().equals("Customers")) {
        custoDim = dims[i];
        break;
      }
    }
    Axis[] axes = result.getAxes();
    Position pos0 = (Position) axes[1].getPositions().get(0);
    Member[] mems = pos0.getMembers();
    Object[] ohiers = new Object[mems.length +1]; 
    for (int i = 0; i < mems.length; i++) {
      Hierarchy hier = mems[i].getLevel().getHierarchy();
      ohiers[i] = placeHier.createMemberExpression(hier);
    }
    Object oCust = placeHier.createMemberExpression(custoDim.getHierarchies()[0]);
    ohiers[mems.length] = oCust;
    placeHier.setQueryAxis(axes[1], ohiers);

    // expect "All Customers" to be expanded
    result = model.getResult();
    if (renderFile != null)
      ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ + ".html");

    JunitUtil.assertPosition(result, 1, 3, new String[] { "All Products", "USA"}, new String[] { "266773",
        "225627.23", "565238.13"});

  }

}
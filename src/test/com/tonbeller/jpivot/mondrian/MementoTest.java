/*
 * Created on 12.08.2003
 *  by hh
 */
package com.tonbeller.jpivot.mondrian;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.ChangeSlicer;
import com.tonbeller.jpivot.olap.navi.DrillExpandPosition;
import com.tonbeller.jpivot.olap.navi.DrillReplace;
import com.tonbeller.jpivot.olap.navi.ExpressionParser;
import com.tonbeller.jpivot.olap.navi.SortRank;
import com.tonbeller.jpivot.olap.query.DrillExpandPositionExt;
import com.tonbeller.jpivot.olap.query.DrillReplaceExt;
import com.tonbeller.jpivot.olap.query.ExpBean;
import com.tonbeller.jpivot.olap.query.PositionNodeBean;
import com.tonbeller.jpivot.olap.query.ResultBase;
import com.tonbeller.jpivot.tags.MondrianModelFactory;
import com.tonbeller.wcf.bookmarks.Bookmarkable;

/**
 * Test Memento State Serialization.
 */
public class MementoTest extends TestCase {

  /**
   */
  public MementoTest() {
    super();
  }

  /**
   * @param arg0
   */
  public MementoTest(String arg0) {
    super(arg0);
  }

  public void testMementoExpandExtensional() throws Exception {
    MondrianMemento memento = createMementoExpand(Bookmarkable.EXTENSIONAL);
    assertEquals(2, memento.getQuaxes().length);
    PositionNodeBean nb = memento.getQuaxes()[1].getPosTreeRoot();
    assertEquals(3, nb.getChildren().length);
    assertEquals( "[Gender].[All Gender].[F]", nb.getChildren()[2].getReference().getName());
    String xml = serializeMemento(memento);
    setMementoExpand(xml);
  }

  public void testMementoExpandIntensional() throws Exception {
    MondrianMemento memento = createMementoExpand(Bookmarkable.INTENSIONAL);
    assertEquals(2, memento.getQuaxes().length);
    // check measures on columns
    PositionNodeBean nb = memento.getQuaxes()[0].getPosTreeRoot();
    assertEquals(1, nb.getChildren().length);
    assertNull(nb.getReference());
    ExpBean eb = nb.getChildren()[0].getReference();
    assertEquals(3, eb.getArgs().length);
    // check rows
    nb = memento.getQuaxes()[1].getPosTreeRoot();
    assertEquals(1, nb.getChildren().length);
    assertNull(nb.getReference());
    String[] hiers = {"[Gender]", "[Marital Status]", "[Customers]", "[Product]"};
    for (int i = 0; i < hiers.length; i++) {
      assertEquals(1, nb.getChildren().length);
      nb = nb.getChildren()[0];
      eb = nb.getReference();
      assertEquals(ExpBean.TYPE_TOPLEVEL_MEMBERS, eb.getType());
      assertEquals(1, eb.getArgs().length);
      ExpBean arg = eb.getArgs()[0];
      assertEquals(ExpBean.TYPE_HIER, arg.getType());
      assertEquals(hiers[i], arg.getName());
    }
    
    // we use a different initial query to restore
    MondrianModel model = createSimpleModel();
    model.setBookmarkState(memento);
    Result result = model.getResult();
    Axis axis = result.getAxes()[1];
    // nur die toplevel member = ALL
    assertEquals(1, axis.getPositions().size());
    MondrianHierarchy[] mh = (MondrianHierarchy[]) axis.getHierarchies();
    for (int i = 0; i < hiers.length; i++) {
      assertEquals(hiers[i], mh[i].getUniqueName());
    }
  }

  public void testMementoDrillExtensional() throws Exception {
    MondrianMemento memento = createMementoDrill(Bookmarkable.EXTENSIONAL);
    assertEquals(2, memento.getQuaxes().length);
    PositionNodeBean nb = memento.getQuaxes()[1].getPosTreeRoot();
    assertEquals(1, nb.getChildren().length);
    assertEquals("[Store].[All Stores]", nb.getChildren()[0].getReference().getArgs()[0].getName());
    String xml = serializeMemento(memento);
    setMementoDrill(xml);
  }

  public void testMementoDrillIntensional() throws Exception {
    MondrianMemento memento = createMementoDrill(Bookmarkable.INTENSIONAL);
    assertEquals(2, memento.getQuaxes().length);
    PositionNodeBean nb = memento.getQuaxes()[1].getPosTreeRoot();
    assertNull(nb.getReference());
    assertEquals(1, nb.getChildren().length);
    nb = nb.getChildren()[0];
    ExpBean eb = nb.getReference();
    assertEquals(ExpBean.TYPE_TOPLEVEL_MEMBERS, eb.getType());
    assertEquals(1, eb.getArgs().length);
    eb = eb.getArgs()[0];
    assertEquals(ExpBean.TYPE_HIER, eb.getType());
    assertEquals("[Store]", eb.getName());
    
    MondrianModel model = createSimpleModel();
    model.setBookmarkState(memento);
    Result result = model.getResult();
    
    // nur die toplevel member = ALL
    Axis axis = result.getAxes()[1];
    assertEquals(1, axis.getPositions().size());
  }
  
  /**
   * slicer is restored by bookmark
   */
  public void testSlicerExtensional() throws Exception {
    MondrianMemento memento = createMementoSlicer(Bookmarkable.EXTENSIONAL);
    MondrianModel model = createSimpleModel();
    model.setBookmarkState(memento);
    Result result = model.getResult();
    Axis axis = result.getSlicer();
    assertNotNull(axis);
    Position pos = (Position) axis.getPositions().get(0);
    MondrianMember[] m = (MondrianMember[]) pos.getMembers();
    assertEquals(1, m.length);
    assertEquals("[Time].[1997]", m[0].getUniqueName());
  }
  
  /**
   * slicer is NOT restored by bookmark - because the members
   * may not be available anymore when the slicer is restored
   */
  public void testSlicerIntensional() throws Exception {
    MondrianMemento memento = createMementoSlicer(Bookmarkable.INTENSIONAL);
    MondrianModel model = createSimpleModel();
    model.setBookmarkState(memento);
    Result result = model.getResult();
    Axis axis = result.getSlicer();
    assertNotNull(axis);
    Position pos = (Position) axis.getPositions().get(0);
    MondrianMember[] m = (MondrianMember[]) pos.getMembers();
    assertEquals(0, m.length);
  }
  
  
  public void testSortExtensional() throws Exception {
    MondrianMemento memento = createMementoSort(createSimpleModel(), Bookmarkable.EXTENSIONAL);
    assertEquals("[Measures].[Unit Sales]", memento.getSortPosMembers()[0]);
    Result result = restoreMementoSort(createCalcMeasureModel(), memento);
    assertEquals(3, result.getAxes()[1].getPositions().size());  
  }

  public void testSortIntensional() throws Exception {
    MondrianMemento memento = createMementoSort(createSimpleModel(), Bookmarkable.INTENSIONAL);
    Result result = restoreMementoSort(createCalcMeasureModel(), memento);
    assertEquals(1, result.getAxes()[1].getPositions().size());  
    assertEquals("[Measures].[Unit Sales]", memento.getSortPosMembers()[0]);
    // ensure no ex is thrown when the sort position can not be restored
    MondrianModel model = createSimpleModel();
    SortRank sr = (SortRank) model.getExtension(SortRank.ID);
    memento.setSortPosMembers(new String[]{"[Measures].[Invalid Measure]"});
    model.setBookmarkState(memento);
    assertFalse(sr.isSorting());
    result = model.getResult();
    assertEquals(1, result.getAxes()[1].getPositions().size());  
  }
  
  public void testSortCalcMeasureExtensional() throws Exception {
    MondrianMemento memento = createMementoSort(createCalcMeasureModel(), Bookmarkable.EXTENSIONAL);
    assertEquals("[Measures].[Calculated]", memento.getSortPosMembers()[0]);
    Result result = restoreMementoSort(createSimpleModel(), memento);
    assertEquals(3, result.getAxes()[1].getPositions().size());  
  }

  public void testSortCalcMeasureIntensional() throws Exception {
    MondrianMemento memento = createMementoSort(createCalcMeasureModel(), Bookmarkable.INTENSIONAL);
    assertEquals("[Measures].[Calculated]", memento.getSortPosMembers()[0]);
    Result result = restoreMementoSort(createSimpleModel(), memento);
    assertEquals(1, result.getAxes()[1].getPositions().size());  
  }
  
  public MondrianMemento createMementoSlicer(int levelOfDetail) throws Exception {
    MondrianModel model = createSimpleModel();
    ChangeSlicer cs = (ChangeSlicer) model.getExtension(ChangeSlicer.ID);
    ExpressionParser ep = (ExpressionParser)model.getExtension(ExpressionParser.ID);
    Member m = ep.lookupMember("[Time].[1997]");
    cs.setSlicer(new Member[]{m});
    Result result = model.getResult();
    // create and test memento
    MondrianMemento memento = (MondrianMemento) model.getBookmarkState(levelOfDetail);
    return memento;
  }
  
  public MondrianMemento createMementoSort(MondrianModel model, int levelOfDetail) throws Exception {
    SortRank sr = (SortRank) model.getExtension(SortRank.ID);
    // first step
    Result result = model.getResult();
    sr.setSortMode(SortRank.ASC);
    sr.setSorting(true);
    Axis axis = result.getAxes()[0];
    Position pos = (Position) axis.getPositions().get(0);
    sr.sort(axis, pos);
    // second step
    result = model.getResult();
    // create and test memento
    MondrianMemento memento = (MondrianMemento) model.getBookmarkState(levelOfDetail);
    assertTrue(memento.isSorting());
    assertEquals(1, memento.getSortPosMembers().length);
    return memento;
  }
  
  public Result restoreMementoSort(MondrianModel model, MondrianMemento memento) throws Exception {
    SortRank sr = (SortRank) model.getExtension(SortRank.ID);
    model.setBookmarkState(memento);
    assertTrue(sr.isSorting());
    Result result = model.getResult();
    assertEquals(2, result.getAxes().length);
    return result;
  }

  private MondrianModel createSimpleModel() throws SAXException, IOException, OlapException {
    String mdxQuery =
      "select {[Measures].[Unit Sales]} on columns,"
      + "  [Product].[Product Family].Members ON rows\n"
        + "from [Sales]";
    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);
    return model;
  }

  private MondrianModel createCalcMeasureModel() throws SAXException, IOException, OlapException {
    String mdxQuery =
      "with member [Measures].[Calculated] as '([Measures].[Store Sales] - [Measures].[Store Cost])', format_string = \"#,##0.00\"\n"
        + "select {[Measures].[Calculated]} ON columns,\n"
        + "  [Product].[Product Family].Members ON rows\n"
        + "from [Sales]\n";
    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);
    return model;
  }

  
  public MondrianMemento createMementoExpand(int levelOfDetail) throws Exception {
    String renderFile = null; // "d:\\x\\createMementoExpand";
    int renderNum = 0;

    String mdxQuery =
      "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,"
        + "{([Gender].[All Gender], [Marital Status].[All Marital Status],"
        + "[Customers].[All Customers],"
        + "[Product].[All Products] ) } on rows "
        + "from Sales where ([Time].[1997])";

    Result result;
    List positions;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    DrillExpandPositionExt mdep =
      (DrillExpandPositionExt) model.getExtension(DrillExpandPosition.ID);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");
    positions = result.getAxes()[1].getPositions();
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
    mdep.expand(pos, pos.getMembers()[3]); // expand All Products

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");
    positions = result.getAxes()[1].getPositions();
    assertPosition(result, 5, new String[] { "F", "All Marital Status", "USA", "Drink" });

    // expand USA
    pos = (Position) positions.get(5);
    assertTrue(mdep.canExpand(pos, pos.getMembers()[2]));
    mdep.expand(pos, pos.getMembers()[2]); // expand USA

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

    return (MondrianMemento) model.getBookmarkState(levelOfDetail);

  }


  public void setMementoExpand(String xml) throws Exception {

    Result result;
    List positions;

    String renderFile = null; // "d:\\x\\setMementoExpand";
    int renderNum = 0;

    MondrianModel model = MondrianModelFactory.instance();
    //TestConnection.initModel(model);

    DrillExpandPositionExt mdep =
      (DrillExpandPositionExt) model.getExtension(DrillExpandPosition.ID);

    // restore memento
    XMLDecoder d = new XMLDecoder(new ByteArrayInputStream(xml.getBytes()));
    Object mo = d.readObject();
    d.close();
    model.setBookmarkState(mo);

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");
    positions = result.getAxes()[1].getPositions();

    // Collapse all Products      
    Position pos = (Position) positions.get(positions.size() - 5);
    assertTrue(mdep.canCollapse(pos, pos.getMembers()[3]));
    mdep.collapse(pos, pos.getMembers()[3]); // All Products

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

  public MondrianMemento createMementoDrill(int levelOfDetail) throws Exception {

    String renderFile = null; // "d:\\x\\createMementoDrill";
    int renderNum = 0;

    /*
    select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,
    CrossJoin(  [Store].[USA].children, [Product].[All Products].[Drink].children) on rows
    from Sales  where ([Time].[1997])
    */
    String mdxQuery =
      "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns, "
        + "CrossJoin(  [Store].[USA].children, [Product].[All Products].[Drink].children) on rows "
        + "from Sales  where ([Time].[1997])";

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    model.setMdxQuery(mdxQuery);
    TestConnection.initModel(model);

    // first step
    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 1, new String[] { "CA", "Beverages" });
    assertPosition(result, 6, new String[] { "WA", "Alcoholic Beverages" });

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
    assertPosition(result, 6, new String[] { "Los Angeles", "Alcoholic Beverages" });
    assertPosition(result, 10, new String[] { "San Diego", "Beverages" });
    assertPosition(result, 14, new String[] { "San Francisco", "Dairy" });

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

    assertPosition(result, 2, new String[] { "CA", "Dairy" });
    assertPosition(result, 7, new String[] { "WA", "Beverages" });

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

    assertPosition(result, 7, new String[] { "USA", "Beverages" });

    // create Memento and write it to xml string
    return (MondrianMemento) model.getBookmarkState(levelOfDetail);
  }

  public void setMementoDrill(String xml) throws Exception {

    String renderFile = null; // "d:\\x\\setMementoDrill";
    int renderNum = 0;
    //String renderFile = null;

    Result result;

    MondrianModel model = MondrianModelFactory.instance();
    DrillReplaceExt mdrep = (DrillReplaceExt) model.getExtension(DrillReplace.ID);

    // restore memento
    XMLDecoder d = new XMLDecoder(new ByteArrayInputStream(xml.getBytes()));
    Object mo = d.readObject();
    d.close();
    model.setBookmarkState(mo);

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    // last drill up of the Store hierarchy reaching "All Stores"
    Axis[] axes = result.getAxes();
    List positions = axes[1].getPositions();
    Position pos0 = (Position) positions.get(0); // no matter what it is
    Hierarchy hier = pos0.getMembers()[0].getLevel().getHierarchy();

    assertTrue(mdrep.canDrillUp(hier));
    mdrep.drillUp(hier); // drill up "Store"

    result = model.getResult();
    if (renderFile != null)
       ResultBase.renderHtml(result, model.getCurrentMdx(), renderFile + renderNum++ +".html");

    assertPosition(result, 2, new String[] { "All Stores", "Dairy" });

    // now drill up is not possible any more
    assertTrue(!mdrep.canDrillUp(hier));

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

  private String serializeMemento(MondrianMemento memento) {
    ByteArrayOutputStream outs = new ByteArrayOutputStream();
    XMLEncoder e = new XMLEncoder(outs);
    e.writeObject(memento);
    e.close();
    return outs.toString();
  }

} // End MementoExpandTest

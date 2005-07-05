/*
 * Created on 12.08.2003
 *  by hh
 */
package com.tonbeller.jpivot.xmla;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.tonbeller.jpivot.core.ModelFactory;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.DrillExpandPosition;
import com.tonbeller.jpivot.olap.navi.DrillReplace;
import com.tonbeller.jpivot.olap.query.DrillExpandPositionExt;
import com.tonbeller.jpivot.olap.query.DrillReplaceExt;
import com.tonbeller.jpivot.olap.query.ResultBase;
import com.tonbeller.wcf.bookmarks.Bookmarkable;

/**
 * Test Memento State Serialization.
 */
public class MementoTest extends TestCase {

  private static String CLASS_EXPBEAN = "[@class='com.tonbeller.jpivot.olap.query.ExpBean']";
  private static String CLASS_POSITIONNODEBEAN =
    "[@class='com.tonbeller.jpivot.olap.query.PositionNodeBean']";

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

  public void testMementoExpand() throws Exception {
    String xml = createMementoExpand();
    setMementoExpand(xml);
  }

  public void testMementoDrill() throws Exception {
    String xml = createMementoDrill();
    setMementoDrill(xml);
  }

  public String createMementoExpand() throws Exception {
    /*
    select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,
         {([Gender].[All Gender], [Marital Status].[All Marital Status],
         [Customers].[All Customers],
         [Product].[All Products] ) } on rows 
         from Sales where ([Time].[1997])
    */
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

    URL confUrl = XMLA_Model.class.getResource("config.xml");
    XMLA_Model model = (XMLA_Model) ModelFactory.instance(confUrl);

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

    // create Memento and write it to xml string
    XMLA_Memento memento = (XMLA_Memento) model.getBookmarkState(Bookmarkable.EXTENSIONAL);
    ByteArrayOutputStream outs = new ByteArrayOutputStream();
    XMLEncoder e = new XMLEncoder(outs);
    e.writeObject(memento);
    e.close();

    String xml = outs.toString();
    Document dom = buildDomTree(xml);

    // make dom tree from xml and do some testing
    // select <object class="com.tonbeller.jpivot.mondrian.MondrianMemento"> 
    XPath xpath =
      new DOMXPath("/java/object[@class='com.tonbeller.jpivot.xmla.XMLA_Memento']}");
    List results = xpath.selectNodes(dom);
    assertEquals(results.size(), 1); // there is only one memento
    Node node = (Node) results.get(0);
    // we have 2 QuaxBeans
    // <object class="com.tonbeller.jpivot.mondrian.MondrianQuaxBean"> 
    xpath = new DOMXPath("//object[@class='com.tonbeller.jpivot.olap.query.QuaxBean']");
    results = xpath.selectNodes(node);
    assertEquals(results.size(), 2); // expecting 2 QuaxBeans
    // select QuaxBean for "rows" axis
    node = (Node) results.get(1);
    /* old stuff as of mdx version 2
    // this QuaxBean node is expected to have 20 drillex's
    xpath = new DOMXPath("//object[@class='com.tonbeller.jpivot.mondrian.MondrianDrillExBean'");
    results = xpath.selectNodes(node);
    assertEquals(results.size(), 20); // expecting 20 DrillExBeans
    */
    // position tree root has 3 children, last ist [Gender].[All Gender].[F]
    xpath = new DOMXPath("./void[@property='posTreeRoot']/object" + CLASS_POSITIONNODEBEAN);
    results = xpath.selectNodes(node);
    node = (Node) results.get(0); // pos tree root
    xpath = new DOMXPath("./void[@property='children']/array" + CLASS_POSITIONNODEBEAN);
    results = xpath.selectNodes(node);
    node = (Node) results.get(0); // array of children, lenght = 3 expected
    String length = ((Element) node).getAttribute("length");
    assertEquals(length, "3");
    //int len = Integer.parseInt(length);
    //assertEquals(len, 3);
    xpath =
      new DOMXPath(
        "./void[@index='2']/object"
          + CLASS_POSITIONNODEBEAN
          + "/void[@property='reference']/object"
          + CLASS_EXPBEAN
          + "/void[@property='name']/string");
    results = xpath.selectNodes(node);
    node = (Node) results.get(0);
    String text = ((Element) node).getFirstChild().getNodeValue();
    assertEquals(text, "[Gender].[All Gender].[F]");

    return xml;
  }

  public void setMementoExpand(String xml) throws Exception {

    Result result;
    List positions;

    String renderFile = null; // "d:\\x\\setMementoExpand";
    int renderNum = 0;

    URL confUrl = XMLA_Model.class.getResource("config.xml");
    XMLA_Model model = (XMLA_Model) ModelFactory.instance(confUrl);

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

  public String createMementoDrill() throws Exception {

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

    URL confUrl = XMLA_Model.class.getResource("config.xml");
    XMLA_Model model = (XMLA_Model) ModelFactory.instance(confUrl);

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
    XMLA_Memento memento = (XMLA_Memento) model.getBookmarkState(Bookmarkable.EXTENSIONAL);
    ByteArrayOutputStream outs = new ByteArrayOutputStream();
    XMLEncoder e = new XMLEncoder(outs);
    e.writeObject(memento);
    e.close();

    String xml = outs.toString();
    Document dom = buildDomTree(xml);
    System.out.println(xml);

    // make dom tree from xml and do some testing
    // select <object class="com.tonbeller.jpivot.mondrian.MondrianMemento"> 
    XPath xpath =
      new DOMXPath("/java/object[@class='com.tonbeller.jpivot.xmla.XMLA_Memento'");
    List results = xpath.selectNodes(dom);
    assertEquals(results.size(), 1); // there is only one memento
    Node node = (Node) results.get(0);
    // we have 2 QuaxBeans
    // <object class="com.tonbeller.jpivot.mondrian.MondrianQuaxBean"> 
    xpath = new DOMXPath("//object[@class='com.tonbeller.jpivot.olap.query.QuaxBean'");
    results = xpath.selectNodes(node);
    assertEquals(results.size(), 2); // expecting 2 QuaxBeans
    // select QuaxBean for "rows" axis
    node = (Node) results.get(1);
    /* old stuff mdx version 2
    // this QuaxBean node is expected to have 2 Member Sets
    xpath = new DOMXPath("//object[@class='com.tonbeller.jpivot.mondrian.MondrianMemberSetBean'");
    results = xpath.selectNodes(node);
    assertEquals(results.size(), 2); // expecting 2 MemberSetBeans
    */
    // expect one node under root, [Store].[All Stores].Children
    xpath =
      new DOMXPath(
        "./void[@property='posTreeRoot']/object"
          + CLASS_POSITIONNODEBEAN
          + "/void[@property='children']/array"
          + CLASS_POSITIONNODEBEAN
          + "/void[@index='0']/object"
          + CLASS_POSITIONNODEBEAN
          + "/void[@property='reference']/object"
          + CLASS_EXPBEAN);
    results = xpath.selectNodes(node);
    node = (Node) results.get(0);
    // should be Exp bean for  tree root [Store].[All Stores].Children

    xpath =
      new DOMXPath(
        "./void[@property='args']/array"
          + CLASS_EXPBEAN
          + "/void[@index='0']/object"
          + CLASS_EXPBEAN
          + "/void[@property='name']/string");
    results = xpath.selectNodes(node);
    node = (Node) results.get(0);
    // should be Exp bean for  tree root [Store].[All Stores].Children
    String text = ((Element) node).getFirstChild().getNodeValue();
    assertEquals(text, "[Store].[All Stores]");

    return xml;

  }

  public void setMementoDrill(String xml) throws Exception {

    String renderFile = null; // "d:\\x\\setMementoDrill";
    int renderNum = 0;
    //String renderFile = null;

    Result result;

    URL confUrl = XMLA_Model.class.getResource("config.xml");
    XMLA_Model model = (XMLA_Model) ModelFactory.instance(confUrl);

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

  /**
   * Build Dom Tree from XML Source String
   */
  private Document buildDomTree(String source)
    throws ParserConfigurationException, IOException, SAXException {
    Document xmlDoc = null;

    //Instantiate a DocumentBuilderFactory.
    DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
    //Use the DocumentBuilderFactory to create a DocumentBuilder.
    DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
    //Use the DocumentBuilder to parse the XML input.
    if (source != null && source.compareTo("") != 0)
      // xmlDoc = dBuilder.parse(new StringBufferInputStream(source));
      xmlDoc = dBuilder.parse(new InputSource(new StringReader(source)));
    else
      xmlDoc = dBuilder.newDocument();

    return xmlDoc;
  }

} // End MementoExpandTest

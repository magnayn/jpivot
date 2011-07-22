package com.tonbeller.jpivot.table.span;

import java.util.List;

import junit.framework.TestCase;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.impl.PropertyHolderImpl;
import com.tonbeller.jpivot.olap.model.impl.PropertyImpl;
import com.tonbeller.wcf.utils.XmlUtils;

/**
 * Created on 28.11.2002
 *
 * @author av
 */
public class PropertyUtilsTest extends TestCase {
  Document doc;
  Element elm;

  /**
   * Constructor for PropertyUtilsTest.
   * @param arg0
   */
  public PropertyUtilsTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(PropertyUtilsTest.class);
  }

  public void testIsStyleProperty() {
    assertTrue(PropertyUtils.isStyleProperty(PropertyUtils.STYLE_PROPERTY));
    assertFalse(PropertyUtils.isStyleProperty(PropertyUtils.STYLE_PROPERTY + "x"));
    assertFalse(PropertyUtils.isStyleProperty(""));
  }
  
  /*
   * in: name="name1" value="value1"
   * out: <property name="name1" value="value1"/>
   */
  public void testAddProperties1() throws JaxenException {
    PropertyHolderImpl phi = new PropertyHolderImpl();
    PropertyImpl pi = new PropertyImpl();
    pi.setName("color");
    pi.setValue("red");
    phi.setProperties(new Property[] { pi });
    PropertyUtils.addProperties(elm, phi.getProperties());
    assertNodeCount(elm, "property[@name='color' and @value='red']", 1);
  }

  /*
   * in: name="img/href" value="a"
   *     name="image/src" value="b"
   *
   * out:
   * <property name="image">
   *   <property name="href" value="a"/>
   *   <property name="src" value="b"/>
   * </property>
   */
  public void testAddProperties2() throws JaxenException {
    PropertyHolderImpl phi = new PropertyHolderImpl();

    PropertyImpl p1 = new PropertyImpl();
    p1.setName("image.href");
    p1.setValue("a");

    PropertyImpl p2 = new PropertyImpl();
    p2.setName("image.src");
    p2.setValue("b");

    phi.setProperties(new Property[] { p1, p2 });
    PropertyUtils.addProperties(elm, phi.getProperties());

    //XmlUtils.print(doc, new PrintWriter(System.out));

    assertNodeCount(elm, "property[@name='image']", 1);
    assertNodeCount(elm, "property", 1);
    assertNodeCount(elm, "property/property[@name='href' and @value='a']", 1);
    assertNodeCount(elm, "property/property[@name='src' and @value='b']", 1);
  }

  /*
   * in: name="LiNk" value="VaLuE"
   * out: <property name="link" value="VaLuE"/>
   */
  public void testAddInlineProperties() throws JaxenException {
    PropertyHolderImpl phi = new PropertyHolderImpl();
    // pi1 = inline property
    PropertyImpl pi1 = new PropertyImpl();
    pi1.setName("LiNk");
    pi1.setValue("VaLuE");

    // pi2 = !inline property
    PropertyImpl pi2 = new PropertyImpl();
    pi2.setName("NoLiNk");
    pi2.setValue("NoVaLuE");
    
    phi.setProperties(new Property[] { pi1, pi2 });
    PropertyUtils.addInlineProperties(elm, phi.getProperties());
    assertNodeCount(elm, "property[@name='link' and @value='VaLuE']", 1);
    assertNodeCount(elm, "property[@name='LiNk']", 0);
    assertNodeCount(elm, "property[@name='NoLiNk']", 0);
    
    PropertyUtils.addProperties(elm, phi.getProperties());
    assertNodeCount(elm, "property[@name='LiNk']", 1);
    assertNodeCount(elm, "property[@name='NoLiNk']", 1);
    
  }

  public void testAddInlineProperties2() throws JaxenException {
    PropertyHolderImpl phi = new PropertyHolderImpl();
    PropertyImpl pi1 = new PropertyImpl();
    pi1.setName("BiLd");
    pi1.setValue("/path");
    phi.setProperties(new Property[] { pi1 });
    PropertyUtils.addInlineProperties(elm, phi.getProperties());
    assertNodeCount(elm, "property[@name='image' and @value='/path']", 1);
  }

  public void testNormalize1() {
    Property p[] = new PropertyImpl[3];
    p[0] = new PropertyImpl("img", "my img");
    p[1] = new PropertyImpl("img.src", "src value");
    p[2] = new PropertyImpl("img.href", "href value");

    p = PropertyUtils.normalize(p);
    assertEquals(1, p.length);
    Property[] np = p[0].getProperties();
    assertEquals(2, np.length);
    assertEquals("img", p[0].getName());
    assertEquals("my img", p[0].getValue());

    assertEquals("src", np[0].getName());
    assertEquals("src value", np[0].getValue());

    assertEquals("href", np[1].getName());
    assertEquals("href value", np[1].getValue());
  }

  public void testNormalize2() {
    Property p[] = new PropertyImpl[1];
    p[0] = new PropertyImpl("img", "my img");
    p = PropertyUtils.normalize(p);
    assertEquals(1, p.length);
    assertEquals("img", p[0].getName());
    assertEquals("my img", p[0].getValue());
  }

  public void testNormalize3() {
    Property p[] = new PropertyImpl[6];
    p[0] = new PropertyImpl("a", "a value");
    p[1] = new PropertyImpl("a.src", "a.src value");
    p[2] = new PropertyImpl("a.href", "a.href value");
    p[3] = new PropertyImpl("b", "b value");
    p[4] = new PropertyImpl("b.src", "b.src value");
    p[5] = new PropertyImpl("b.href", "b.href value");

    p = PropertyUtils.normalize(p);
    assertEquals(2, p.length);
    assert3("a", p[0]);
    assert3("b", p[1]);
  }

  void assert3(String name, Property p) {
    assertEquals(name, p.getName());
    assertEquals(name + " value", p.getValue());

    Property[] np = p.getProperties();
    assertEquals(2, np.length);

    assertEquals("src", np[0].getName());
    assertEquals(name + ".src value", np[0].getValue());

    assertEquals("href", np[1].getName());
    assertEquals(name + ".href value", np[1].getValue());
  }
  
  public void testFlags() {
    assertTrue(PropertyUtils.isNested("a.b"));
    assertFalse(PropertyUtils.isNested("a/b"));
    
    assertTrue(PropertyUtils.isInline("link"));
    assertTrue(PropertyUtils.isInline("style"));
    assertTrue(PropertyUtils.isInline("arrow"));
    assertTrue(PropertyUtils.isInline("image"));
    assertTrue(PropertyUtils.isInline("stil"));
    assertTrue(PropertyUtils.isInline("pfeil"));
    assertTrue(PropertyUtils.isInline("bild"));

    assertTrue(PropertyUtils.isInline("LiNk"));
    assertTrue(PropertyUtils.isInline("pFeIl"));
    
    assertFalse(PropertyUtils.isInline("xref"));
  }


  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    doc = XmlUtils.createDocument();
    elm = doc.createElement("elm");
    doc.appendChild(elm);
  }
  
  /**
   * evaluates xpathExpr on node and asserts that the
   * result contains <code>nodeCount</code> nodes
   */
  public void assertNodeCount(Node node, String xpathExpr, int nodeCount) throws JaxenException {
    XPath xpath = new DOMXPath(xpathExpr);
    List list = xpath.selectNodes(node);
    assertEquals("Node count" + xpathExpr, nodeCount, list.size());
  }

}

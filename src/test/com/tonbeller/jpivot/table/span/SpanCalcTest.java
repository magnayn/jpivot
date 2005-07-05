package com.tonbeller.jpivot.table.span;

import java.io.PrintWriter;
import java.util.Iterator;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Visitor;
import com.tonbeller.jpivot.table.LevelAxisDecorator;
import com.tonbeller.jpivot.test.olap.DimensionBuilder;
import com.tonbeller.jpivot.test.olap.TestDimension;
import com.tonbeller.jpivot.test.olap.TestHierarchy;
import com.tonbeller.jpivot.test.olap.TestMember;
import com.tonbeller.jpivot.test.olap.TestMemberTree;
import com.tonbeller.jpivot.test.olap.TestOlapModelUtils;

/**
 * Created on 29.10.2002
 * 
 * @author av
 */
public class SpanCalcTest extends TestCase {

  /**
   * Constructor for SpanCalcTest.
   * @param arg0
   */
  public SpanCalcTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(SpanCalcTest.class);
  }

  protected void setUp() throws Exception {
    
  }


  /**
   * Creates an axis for testing. 2 Hierachies/Dimensions A and X, A has 2 Levels B+C,
   * the first member B1 is expanded. 
   * <pre>
   * B0 Y0
   * B0 Y1
   * C0 Y0
   * C0 Y1
   * C1 Y0
   * C1 Y1
   * C2 Y0
   * C2 Y1
   * B1 Y0
   * B1 Y1
   * </pre>
   * @author av
   */

  public static Axis createAxis1() {
    DimensionBuilder db = new DimensionBuilder();

    TestDimension dim1 = db.build("A", new String[]{"B", "C"}, new int[]{2, 3});
    TestHierarchy hier1 = (TestHierarchy)dim1.getHierarchies()[0];
    TestMember[] members = hier1.getRootMembers();

    // expand first member
    for (Iterator it = members[0].getChildMember().iterator(); it.hasNext(); )
      ((TestMember)it.next()).setVisible(true);

    TestDimension dim2 = db.build("X", new String[]{"Y"}, new int[]{2});
    
    TestDimension dims[] = new TestDimension[]{ dim1, dim2 };

    return TestOlapModelUtils.createAxis(dims);
  }

  /**
   * Creates an axis for testing. 2 Hierachies/Dimensions A and X, A has 2 Levels B+C,
   * the first member B1 is expanded. Axis is decorated with parent members.
   * <pre>
   * B0 B0 Y0
   * B0 B0 Y1
   * B0 C0 Y0
   * B0 C0 Y1
   * B0 C1 Y0
   * B0 C1 Y1
   * B0 C2 Y0
   * B0 C2 Y1
   * B1 B1 Y0
   * B1 B1 Y1
   * </pre>
   * @author av
   */
  public static Axis createAxis2() {
    Axis axis = createAxis1();
    return new LevelAxisDecorator(axis, new TestMemberTree());
  }

  static class SpanElem implements Displayable {
    String s;
    SpanElem(String s) {
      this.s = s;
    }
    public String toString() {
      return s;
    }
    public String getLabel() {
      return s;
    }
    public void accept(Visitor visitor) {
      throw new UnsupportedOperationException();
    }
  }

  static class A extends SpanElem { public A() { super("a"); } }
  static class B extends SpanElem { public B() { super("b"); } }
  static class C extends SpanElem { public C() { super("c"); } }
  static class D extends SpanElem { public D() { super("d"); } }
  static class E extends SpanElem { public E() { super("e"); } }
  static class F extends SpanElem { public F() { super("f"); } }
  static class G extends SpanElem { public G() { super("g"); } }

 
  static final A a = new A();
  static final B b = new B();
  static final C c = new C();
  static final D d = new D();
  static final E e = new E();
  static final F f = new F();
  static final G g = new G();

  /**
   * <pre>
   * baaa
   * bbaa
   * bbba
   * bbbb
   * </pre>
   */  
  public static SpanCalc createTriangle() {
    Span[][] spans = new Span[4][4];
    A a = new A();
    B b = new B();
    for (int posIndex = 0; posIndex < spans.length; posIndex++) {
      for (int hierIndex = 0; hierIndex < spans[posIndex].length; hierIndex++) {
        if (posIndex < hierIndex)
          spans[posIndex][hierIndex] = new Span(a);
        else
          spans[posIndex][hierIndex] = new Span(b);
      }
    }
    return new SpanCalc(spans);
  }

  
  


  void check(SpanCalc sc, int posIndex, int hierIndex, boolean significant, int posSpan, int hierSpan) {
    sc.initialize();
    Span s = sc.getSpan(posIndex, hierIndex);
    assertEquals("significance: ", significant, s.isSignificant());
    assertEquals("positionSpan:" , posSpan, s.getPositionSpan());
    assertEquals("hierarchySpan: ", hierSpan, s.getHierarchySpan());
  }

  void assertMember(SpanCalc sc, int posIndex, int hierIndex) {
    sc.initialize();
  	Object o = sc.getSpan(posIndex, hierIndex).getObject();
  	assertTrue("Member expected", o instanceof Member);
  }
  
  void assertLevel(SpanCalc sc, int posIndex, int hierIndex) {
    sc.initialize();
  	Object o = sc.getSpan(posIndex, hierIndex).getObject();
  	assertTrue("Level expected", o instanceof Level);
  }
  
  void assertHierarchy(SpanCalc sc, int posIndex, int hierIndex) {
    sc.initialize();
  	Object o = sc.getSpan(posIndex, hierIndex).getObject();
  	assertTrue("Hierarchy expected", o instanceof Hierarchy);
  }
  
  void checkPlausibility(SpanCalc sc) {
    sc.initialize();
    // equal size
    Span[][] spans = sc.spans;
    int length = sc.spans[0].length;
    for (int i = 1; i < sc.spans.length; i++)
      assertEquals("spans[" + i + "].length: ", length, sc.spans[i].length);
      
    int[] sumPos = new int[sc.getHierarchyCount()];
    int[] sumHier = new int[sc.getPositionCount()];
    
    for (int posIndex = 0; posIndex < sc.getPositionCount(); posIndex ++) {
      for (int hierIndex = 0; hierIndex < sc.getHierarchyCount(); hierIndex ++) {
        Span s = sc.getSpan(posIndex, hierIndex);
        assertEquals("span[posIndex=" + posIndex + ", hierIndex=" + hierIndex + "] Position Index: ", posIndex, s.getPositionIndex());
        assertEquals("span[posIndex=" + posIndex + ", hierIndex=" + hierIndex + "] Hierarchy Index: ", hierIndex, s.getHierarchyIndex());
        assertTrue("span[posIndex=" + posIndex + ", hierIndex=" + hierIndex + "] Significance: ", s.isSignificant() || (s.getHierarchySpan() == 0 && s.getPositionSpan() == 0));
        if (s.isSignificant()) {
          for (int pi = 0; pi < s.getPositionSpan(); pi++) {
            for (int hi = 0; hi < s.getHierarchySpan(); hi++) {
              sumPos[hierIndex + hi] += 1;
              sumHier[posIndex + pi] += 1;
            }
          }
        }
      }
    }

    // check the sum of hierarchy/position Spans
    int sum = sumPos[0];
    for (int i = 0; i < sumPos.length; i++)
      assertEquals("span sumPositions[hierarchy=" + i + "]: ", sum, sumPos[i]);
      
    sum = sumHier[0];
    for (int i = 0; i < sumHier.length; i++)
      assertEquals("span sumHier[position=" + i + "]: ", sum, sumHier[i]);
  }
  
  /* -------------------------------- tests start here ----------------------------------- */
  
  public void testPlausibility() {
    checkPlausibility(new SpanCalc(createAxis1()));
    checkPlausibility(new SpanCalc(createAxis2()));
    checkPlausibility(createTriangle());
  }
  
  /**
   * NO_SPAN for a and NO_SPAN for b
   */  
  public void testTriangle_AN_BN() {
    SpanCalc sc = createTriangle();
    checkPlausibility(sc);
    for (int pi = 0; pi < sc.getPositionCount(); pi++)
      for (int hi = 0; hi < sc.getHierarchyCount(); hi++)
        check(sc, pi, hi, true, 1, 1);
  }


  /**
   * A spans on hierarchies, B spans on hieraries
   * <pre>
   * baaa
   * bbaa
   * bbba
   * bbbb
   * </pre>
   */  
  public void testTriangle_AH_BH() {
    
    SpanCalc sc = createTriangle();
    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDirection(A.class, SpanConfig.HIERARCHY_SPAN);
    scs.setDirection(B.class, SpanConfig.HIERARCHY_SPAN);
    sc.setConfig(scs);
    checkPlausibility(sc);
    // toString("testTriangle_AH_BH", sc);

    check(sc, 0, 0, true,  1, 1);
    check(sc, 0, 1, true,  1, 3);
    check(sc, 0, 2, false, 0, 0);
    check(sc, 0, 3, false, 0, 0);

    check(sc, 1, 0, true,  1, 2);
    check(sc, 1, 1, false, 0, 0);
    check(sc, 1, 2, true,  1, 2);
    check(sc, 1, 3, false, 0, 0);

    check(sc, 2, 0, true,  1, 3);
    check(sc, 2, 1, false, 0, 0);
    check(sc, 2, 2, false, 0, 0);
    check(sc, 2, 3, true,  1, 1);

    check(sc, 3, 0, true,  1, 4);
    check(sc, 3, 1, false, 0, 0);
    check(sc, 3, 2, false, 0, 0);
    check(sc, 3, 3, false, 0, 0);
  }

  /**
   * A spans on positions, B spans on positions
   * <pre>
   * baaa
   * bbaa
   * bbba
   * bbbb
   * </pre>
   */  
  public void testTriangle_AP_BP() {
    
    SpanCalc sc = createTriangle();
    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDirection(A.class, SpanConfig.POSITION_SPAN);
    scs.setDirection(B.class, SpanConfig.POSITION_SPAN);
    sc.setConfig(scs);
    checkPlausibility(sc);
    //toString("testTriangle_AP_BP", sc);
    check_AP_BP(sc);

    scs.setDirection(B.class, SpanConfig.HIERARCHY_THEN_POSITION_SPAN);
    sc.setConfig(scs);
    checkPlausibility(sc);
    check_AP_BP(sc);

  }

  void check_AP_BP(SpanCalc sc) {    
    check(sc, 0, 0, true,  4, 1);
    check(sc, 1, 0, false, 0, 0);
    check(sc, 2, 0, false, 0, 0);
    check(sc, 3, 0, false, 0, 0);
    
    check(sc, 0, 1, true,  1, 1);
    check(sc, 1, 1, true,  3, 1);
    check(sc, 2, 0, false, 0, 0);
    check(sc, 3, 0, false, 0, 0);
    
    check(sc, 0, 2, true,  1, 1);
    check(sc, 1, 2, true,  1, 1);
    check(sc, 2, 2, true,  2, 1);
    check(sc, 3, 2, false, 0, 0);

    check(sc, 0, 3, true,  1, 1);
    check(sc, 1, 3, true,  1, 1);
    check(sc, 2, 3, true,  1, 1);
    check(sc, 3, 3, true,  1, 1);
  }

  /**
   * A spans on hierarchies, B spans on positions
   * <pre>
   * baaa
   * bbaa
   * bbba
   * bbbb
   * </pre>
   */  
  public void testTriangle_AH_BP() {
    
    SpanCalc sc = createTriangle();
    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDirection(A.class, SpanConfig.HIERARCHY_SPAN);
    scs.setDirection(B.class, SpanConfig.POSITION_SPAN);
    sc.setConfig(scs);
    checkPlausibility(sc);
    //toString("testTriangle_AH_BP", sc);
    
    check(sc, 0, 0, true,  4, 1);
    check(sc, 1, 0, false, 0, 0);
    check(sc, 2, 0, false, 0, 0);
    check(sc, 3, 0, false, 0, 0);
    
    check(sc, 0, 1, true,  1, 3);
    check(sc, 1, 1, true,  3, 1);
    check(sc, 2, 0, false, 0, 0);
    check(sc, 3, 0, false, 0, 0);
    
    check(sc, 0, 2, false, 0, 0);
    check(sc, 1, 2, true,  1, 2);
    check(sc, 2, 2, true,  2, 1);
    check(sc, 3, 2, false, 0, 0);

    check(sc, 0, 3, false, 0, 0);
    check(sc, 1, 3, false, 0, 0);
    check(sc, 2, 3, true,  1, 1);
    check(sc, 3, 3, true,  1, 1);
  }


  public void testTriangle_AHP_BHP() {
    SpanCalc sc = createTriangle();
    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDirection(A.class, SpanConfig.HIERARCHY_THEN_POSITION_SPAN);
    scs.setDirection(B.class, SpanConfig.HIERARCHY_THEN_POSITION_SPAN);
    sc.setConfig(scs);
    checkPlausibility(sc);
    //toString("testTriangle_AHP_BHP", sc);
    
    check(sc, 0, 0, true, 4, 1);
    check(sc, 0, 1, true, 1, 3);
    check(sc, 0, 2, false, 0, 0);
    check(sc, 0, 3, false, 0, 0);
    check(sc, 1, 0, false, 0, 0);
    check(sc, 1, 1, true, 3, 1);
    check(sc, 1, 2, true, 1, 2);
    check(sc, 1, 3, false, 0, 0);
    check(sc, 2, 0, false, 0, 0);
    check(sc, 2, 1, false, 0, 0);
    check(sc, 2, 2, true, 2, 1);
    check(sc, 2, 3, true, 1, 1);
    check(sc, 3, 0, false, 0, 0);
    check(sc, 3, 1, false, 0, 0);
    check(sc, 3, 2, false, 0, 0);
    check(sc, 3, 3, true, 1, 1);

  }

  /**
   * <pre>
   * ab
   * ac
   * bc
   * </pre>
   * because of hierarchical position break algorithm, the 2 c's are not combined into a single span.
   */
  public void testHierarchicalPositionSpans1() {
    Span[][] spans = new Span[3][2];
    spans[0][0] = new Span(a);
    spans[0][1] = new Span(b);
    spans[1][0] = new Span(a);
    spans[1][1] = new Span(c);
    spans[2][0] = new Span(b);
    spans[2][1] = new Span(c);


    SpanCalc sc = new SpanCalc(spans);
    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDefaultDirection(SpanConfig.POSITION_SPAN);
    sc.setConfig(scs);
    checkPlausibility(sc);
    
    check(sc, 0, 0, true,  2, 1);
    check(sc, 1, 0, false, 0, 0);
    check(sc, 2, 0, true,  1, 1);
    
    // there are two C spans!!
    check(sc, 0, 1, true,  1, 1);
    check(sc, 1, 1, true,  1, 1);
    check(sc, 2, 1, true,  1, 1);
   
  }
  
  /**
   * <pre>
   * ac
   * bc
   * </pre>
   */
  public void testHierarchicalPositionSpans2() {
    Span[][] spans = new Span[2][2];
    spans[0][0] = new Span(a);
    spans[1][0] = new Span(b);
    spans[0][1] = new Span(c);
    spans[1][1] = new Span(c);
    SpanCalc sc = new SpanCalc(spans);

    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDefaultDirection(SpanConfig.POSITION_SPAN);
    sc.setConfig(scs);
    sc.initialize();
    checkPlausibility(sc);

    // there are two C spans!!
    check(sc, 0, 0, true,  1, 1);
    check(sc, 1, 0, true,  1, 1);
    check(sc, 0, 1, true,  1, 1);
    check(sc, 1, 1, true,  1, 1);
   
  }

  /**
   * <pre>
   * aae
   * abe
   * ace
   * ade
   * </pre>
   */
  public void testBug1() {
    Span[][] spans = new Span[4][3];    
    
    spans[0][0] = new Span(a);
    spans[1][0] = new Span(a);
    spans[2][0] = new Span(a);
    spans[3][0] = new Span(a);

    spans[0][1] = new Span(a);
    spans[1][1] = new Span(b);
    spans[2][1] = new Span(c);
    spans[3][1] = new Span(d);

    spans[0][2] = new Span(e);
    spans[1][2] = new Span(e);
    spans[2][2] = new Span(e);
    spans[3][2] = new Span(e);
    
    SpanCalc sc = new SpanCalc(spans);

    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDefaultDirection(SpanConfig.HIERARCHY_THEN_POSITION_SPAN);
    sc.setConfig(scs);
    sc.initialize();
    checkPlausibility(sc);

    // 4 differnt e's
    check(sc, 0, 2, true, 1, 1);
    check(sc, 1, 2, true, 1, 1);
    check(sc, 2, 2, true, 1, 1);
    check(sc, 3, 2, true, 1, 1);
   
  }



  /**
   * <pre>
   * aa
   * aa
   * </pre>
   */
  public void testMultiSpan1() {
    Span[][] spans = new Span[2][2];
    spans[0][0] = new Span(a);
    spans[1][0] = new Span(a);
    spans[0][1] = new Span(a);
    spans[1][1] = new Span(a);
    SpanCalc sc = new SpanCalc(spans);

    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDefaultDirection(SpanConfig.HIERARCHY_THEN_POSITION_SPAN);
    sc.setConfig(scs);
    checkPlausibility(sc);
    //toString("testMultiSpan1", sc);

    // all a's are united
    check(sc, 0, 0, true,  2, 2);
    check(sc, 1, 0, false, 0, 0);
    check(sc, 0, 1, false, 0, 0);
    check(sc, 1, 1, false, 0, 0);
   
  }

  /**
   * <pre>
   * aacd
   * aace
   * abcf
   * abcg
   * </pre>
   */
  public SpanCalc createMulti1() {
    Span[][] spans = new Span[4][4];
    spans[0][0] = new Span(a);
    spans[1][0] = new Span(a);
    spans[2][0] = new Span(a);
    spans[3][0] = new Span(a);

    spans[0][1] = new Span(a);
    spans[1][1] = new Span(a);
    spans[2][1] = new Span(b);
    spans[3][1] = new Span(b);

    spans[0][2] = new Span(c);
    spans[1][2] = new Span(c);
    spans[2][2] = new Span(c);
    spans[3][2] = new Span(c);

    spans[0][3] = new Span(d);
    spans[1][3] = new Span(e);
    spans[2][3] = new Span(f);
    spans[3][3] = new Span(g);

    return new SpanCalc(spans);
  }
  


  /**
   * <pre>
   * aacd
   * aace
   * abcf
   * abcg
   * </pre>
   */
  public void testMultiSpan2() {
    SpanCalc sc = createMulti1();

    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDefaultDirection(SpanConfig.HIERARCHY_THEN_POSITION_SPAN);
    sc.setConfig(scs);
    sc.initialize();
    checkPlausibility(sc);

    // there are two C spans!!
    check(sc, 0, 2, true,  2, 1);
    check(sc, 1, 2, false, 0, 0);
    check(sc, 2, 2, true,  2, 1);
    check(sc, 3, 1, false, 0, 0);
  }

  
  /** --------------------------------------------------------------------- **/  


  static class MySpanCalc extends SpanCalc {
    int counter;
    public MySpanCalc(Axis axis) {
      super(axis);
    }
    public void initialize() {
      ++ counter;
      super.initialize();
    }
  }
  
  public void testInitializer() {
  	Axis axis1 = createAxis1();
    MySpanCalc sc = new MySpanCalc(axis1);
    assertEquals(sc.counter, 0);
    sc.getHierarchyCount();
    sc.getHierarchyCount();
    assertEquals(sc.counter, 1);

    sc = new MySpanCalc(axis1);
    assertEquals(sc.counter, 0);
    sc.getPositionCount();
    sc.getPositionCount();
    assertEquals(sc.counter, 1);
    
    sc = new MySpanCalc(axis1);
    assertEquals(sc.counter, 0);
    sc.getSpan(0,0);
    sc.getSpan(0,0);
    assertEquals(sc.counter, 1);
    
  }
  
  /** --------------------------------------------------------------------- **/  
  class AllEqual implements Displayable {
    public boolean equals(Object o) {
      return o instanceof AllEqual;
    }
    public String getLabel() {
      return this.toString();
    }
    public void accept(Visitor visitor) {
      throw new UnsupportedOperationException();
    }
  }
  
  SpanHeaderFactory allEqualFactory = new SpanHeaderFactory() {
    public Span create(Span span) {
      AllEqual ae = new AllEqual();
      return new Span(span.getAxis(), span.getPosition(), ae);
    }
  };  

  /**
   * <pre>
   * Adds an object O to the hierarchy, that is equal with all its instances.
   * O B0 B0 Y0
   * O B0 B0 Y1
   * O B0 C0 Y0
   * O B0 C0 Y1
   * O B0 C1 Y0
   * O B0 C1 Y1
   * O B0 C2 Y0
   * O B0 C2 Y1
   * O B1 B1 Y0
   * O B1 B1 Y1
   * </pre>
   */
  public void testAddAllEqualToHierarchy() {
  	SpanCalc span2 = new SpanCalc(createAxis2());
    span2.addHierarchyHeader(allEqualFactory, true);
    checkPlausibility(span2);
    assertEquals(4, span2.getHierarchyCount());
  }

  /** --------------------------------------------------------------------- **/  

  /**
   * <pre>
   * H1 B0 B0 H2 Y0
   * H1 B0 B0 H2 Y1
   * H1 B0 C0 H2 Y0
   * H1 B0 C0 H2 Y1
   * H1 B0 C1 H2 Y0
   * H1 B0 C1 H2 Y1
   * H1 B0 C2 H2 Y0
   * H1 B0 C2 H2 Y1
   * H1 B1 B1 H2 Y0
   * H1 B1 B1 H2 Y1
   * </pre>
   */
  public void testAddHierarchiesToHierarchy() {
  	SpanCalc sc = new SpanCalc(createAxis2());
    sc.addHierarchyHeader(new HierarchyHeaderFactory(), true);
    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDefaultDirection(SpanConfig.POSITION_SPAN);
    sc.setConfig(scs);
    checkPlausibility(sc);

    assertEquals(5, sc.getHierarchyCount());
    assertEquals(10, sc.getPositionCount());

    assertHierarchy(sc, 0, 0);
    assertMember(sc, 0, 1);
    assertMember(sc, 0, 2);
    assertHierarchy(sc, 0, 3);
    assertMember(sc, 0, 4);
    
    // hierarchical spans on positions:
    // H2 is combined into spans of height 2 because of its parents!

    check(sc, 0, 0, true,  10, 1);
    check(sc, 0, 1, true,   8, 1);
    check(sc, 0, 2, true,   2, 1);
    check(sc, 0, 3, true,   2, 1);
    check(sc, 0, 4, true,   1, 1);
  }


  /** --------------------------------------------------------------------- **/  

  /**
   * <pre>
   * L1 B0 B0 B0 L3 Y0
   * L1 B0 B0 B0 L3 Y1
   * L1 B0 L2 C0 L3 Y0
   * L1 B0 L2 C0 L3 Y1
   * L1 B0 L2 C1 L3 Y0
   * L1 B0 L2 C1 L3 Y1
   * L1 B0 L2 C2 L3 Y0
   * L1 B0 L2 C2 L3 Y1
   * L1 B1 B1 B1 L3 Y0
   * L1 B1 B1 B1 L3 Y1
   * </pre>
   */
  public void testAddLevelsToHierarchy() {
  	SpanCalc sc = new SpanCalc(createAxis2());
    sc.addHierarchyHeader(new LevelHeaderFactory(), true);
    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDefaultDirection(SpanConfig.POSITION_SPAN);
    sc.setConfig(scs);
    checkPlausibility(sc);
    assertEquals(6, sc.getHierarchyCount());
    assertEquals(10, sc.getPositionCount());

    assertLevel(sc, 0, 0);
    assertLevel(sc, 1, 0);
    assertLevel(sc, 1, 4);

    assertMember(sc, 0, 2);
    assertMember(sc, 1, 2);
    assertLevel (sc, 2, 2);
    assertLevel (sc, 3, 2);
    assertLevel (sc, 4, 2);
    assertLevel (sc, 5, 2);
    assertLevel (sc, 6, 2);
    assertLevel (sc, 7, 2);
    assertMember(sc, 8, 2);
    assertMember(sc, 9, 2);

    assertMember(sc, 0, 1);
    assertMember(sc, 1, 1);

    // hierarchical spans on positions:
    // L3 is combined into spans of height 2 because of its parents!
    
    check(sc, 0, 0, true,   10, 1);
    check(sc, 0, 1, true,    8, 1);
    check(sc, 0, 2, true,    2, 1);
    check(sc, 0, 3, true,    2, 1);
    check(sc, 0, 4, true,    2, 1);
    check(sc, 0, 5, true,    1, 1);

    check(sc, 1, 0, false, 0, 0);
    check(sc, 1, 1, false, 0, 0);
    check(sc, 1, 2, false, 0, 0);
    check(sc, 1, 3, false, 0, 0);
    check(sc, 1, 4, false, 0, 0);
    check(sc, 1, 5, true,  1, 1);

    check(sc, 2, 2, true,  6, 1);
    
  }

  /** --------------------------------------------------------------------- **/  

  /**
   * <pre>
   * B  C  Y   (Levels)
   * --------
   * B0 B0 Y0
   * B0 B0 Y1
   * B0 C0 Y0
   * B0 C0 Y1
   * B0 C1 Y0
   * B0 C1 Y1
   * B0 C2 Y0
   * B0 C2 Y1
   * B1 B1 Y0
   * B1 B1 Y1
   * </pre>
   * @author av
   */

  public void testCreatePositionHeader1() {
    SpanCalc sc = new SpanCalc(createAxis2());
    
    SpanCalc h = sc.createPositionHeader(new LevelHeaderFactory());
    checkPlausibility(h);
    assertEquals(1, h.getPositionCount());
    assertEquals(3, h.getHierarchyCount());

    check(h, 0, 0, true,  1, 1);
    check(h, 0, 1, true,  1, 1);
    check(h, 0, 2, true,  1, 1);
    assertEquals("B", ((Level)h.getSpan(0,0).getObject()).getLabel());
    assertEquals("C", ((Level)h.getSpan(0,1).getObject()).getLabel());
    assertEquals("Y", ((Level)h.getSpan(0,2).getObject()).getLabel());
  }

  /**
   * <pre>
   * A  A  X   (Hierarchies)
   * --------
   * B0 B0 Y0
   * B0 B0 Y1
   * B0 C0 Y0
   * B0 C0 Y1
   * B0 C1 Y0
   * B0 C1 Y1
   * B0 C2 Y0
   * B0 C2 Y1
   * B1 B1 Y0
   * B1 B1 Y1
   * </pre>
   * @author av
   */

  public void testCreatePositionHeader2() {
    SpanCalc sc = new SpanCalc(createAxis2());
    
    SpanCalc h = sc.createPositionHeader(new HierarchyHeaderFactory());
    checkPlausibility(h);
    assertEquals(1, h.getPositionCount());
    assertEquals(3, h.getHierarchyCount());

    check(h, 0, 0, true,  1, 1);
    check(h, 0, 1, true,  1, 1);
    check(h, 0, 2, true,  1, 1);
    assertEquals("A", ((Hierarchy)h.getSpan(0,0).getObject()).getLabel());
    assertEquals("A", ((Hierarchy)h.getSpan(0,1).getObject()).getLabel());
    assertEquals("X", ((Hierarchy)h.getSpan(0,2).getObject()).getLabel());
  }
  
  public void toString(String title, SpanCalc sc) {
    PrintWriter pw = new PrintWriter(System.out);
    pw.println("*** " + title + " ***");
    pw.println("objects:");
    printObjects(sc, pw);
    pw.println("spans:");
    printSpans(sc, pw);
    pw.println("breaks");
    printBreaks(sc, pw);
    pw.println("code:");
    printCode(sc, pw);
    //pw.println("-----------------------------------------");
    pw.flush();
  }
  
  void printObjects(SpanCalc sc, PrintWriter out) {
    for (int pi = 0; pi < sc.getPositionCount(); pi++) {
      for (int hi = 0; hi < sc.getHierarchyCount(); hi++) {
        Span s = sc.getSpan(pi, hi);
        out.print(" ");
        out.print(s.getObject().toString());
      }
      out.println();
    }
  }

  void printBreaks(SpanCalc sc, PrintWriter out) {
    for (int pi = 0; pi < sc.getPositionCount(); pi++) {
      for (int hi = 0; hi < sc.getHierarchyCount(); hi++) {
        Span s = sc.getSpan(pi, hi);
        out.print(" ");
        out.print(sc.forcePositionBreak[pi][hi] ? "x" : ".");
      }
      out.println();
    }
  }

  void printCode(SpanCalc sc, PrintWriter out) {
    for (int pi = 0; pi < sc.getPositionCount(); pi++) {
      for (int hi = 0; hi < sc.getHierarchyCount(); hi++) {
        Span s = sc.getSpan(pi, hi);
        out.println("    check(sc, " 
          + s.getPositionIndex() + ", " 
          + s.getHierarchyIndex() + ", " 
          + s.isSignificant() + ", "
          + s.getPositionSpan() + ", "
          + s.getHierarchySpan() + ");");
      }
    }
  }
  
  void printSpans(SpanCalc sc, PrintWriter out) {
    
    char[][] carr = new char[sc.getPositionCount()][sc.getHierarchyCount()];
    char letter = 'a';
    
    for (int pi = 0; pi < sc.getPositionCount(); pi++) {
      for (int hi = 0; hi < sc.getHierarchyCount(); hi++) {
        Span s = sc.getSpan(pi, hi);
        assertEquals(pi, s.getPositionIndex());
        assertEquals(hi, s.getHierarchyIndex());
        if (s.isSignificant()) {
          for (int pspan = 0; pspan < s.getPositionSpan(); pspan++) {
            for (int hspan = 0; hspan < s.getHierarchySpan(); hspan++) {
              carr[pi + pspan][hi + hspan] = letter;
            }
          }
          letter ++;
        }
      }
    }
    
    for (int i = 0; i < carr.length; i++) {
      for (int j = 0; j < carr[i].length; j++)  {
        out.print(" ");
        out.print(carr[i][j]);
      }
      out.println();
    }
    
  }
  
}

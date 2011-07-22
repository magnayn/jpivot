package com.tonbeller.jpivot.table.span;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.MemberPropertyMeta;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.impl.HierarchyImpl;
import com.tonbeller.jpivot.olap.model.impl.LevelImpl;
import com.tonbeller.jpivot.olap.model.impl.MemberImpl;
import com.tonbeller.jpivot.olap.model.impl.PropertyImpl;

public class PropertySpanBuilderTest extends TestCase {

  private TestMemberProperties extension;

  public PropertySpanBuilderTest(String arg0) {
    super(arg0);
  }

  class MyMember extends MemberImpl implements Displayable {
    public MyMember(String label, Level level, List props) {
      super(props);
      setLevel(level);
      setLabel(label);
    }
  }

  private Member member(String label, Level level, String propstr) {
    List props = new ArrayList();
    StringTokenizer st = new StringTokenizer(propstr, ",", false);
    while (st.hasMoreTokens()) {
      String tok = st.nextToken();
      int i = tok.indexOf('=');
      String name = tok.substring(0, i);
      String value = tok.substring(i + 1);
      PropertyImpl p = new PropertyImpl(name, value);
      p.setLabel(value);
      props.add(p);
    }
    return new MyMember(label, level, props);
  }

  MemberPropertyMeta meta(String name, Level level) {
    if (extension.isLevelScope())
      return new MemberPropertyMeta(name, name, level.getLabel());
    return new MemberPropertyMeta(name, name, level.getHierarchy().getLabel());
  }

  Level getLevel1() {
    HierarchyImpl hier = new HierarchyImpl();
    hier.setLabel("hier");
    LevelImpl level = new LevelImpl("level", hier);
    return level;
  }

  // Achse mit Parent Membern:
  // Input: 
  //   All(a,b)  All(a,b)
  //   All(a,b)  Item(a,c)
  // Ergebnis (b wird in Item Spalte NICHT wiederholt):
  //   A a b A a _
  //   A a b I a c
  public void testHierarchy() {
    Level level = getLevel1();
    Span[][] s = new Span[2][2];
    Span all = new Span(member("A", level, "a=1,b=2"));
    Span item = new Span(member("I", level, "a=3,c=4"));
    s[0][0] = all;
    s[0][1] = all;
    s[1][0] = all;
    s[1][1] = item;
    SpanCalc sc = new SpanCalc(s);

    PropertySpanBuilder psb = new PropertySpanBuilder(extension);
    psb.setShowProperties(true);
    psb.addPropertySpans(sc);
    //dump(sc);
    assertSC(sc, new String[] { "A A 1 2 *", "A I 3 * 4" });
  }

  // Input
  //   A B
  //   A C
  // Output
  //   A B a b *
  //   A C a * c
  // wobei die beiden a's identisch gleich sein müssen (wg. SpanConfig.equals())
  public void testIdentity() {
    Level level = getLevel1();
    Span[][] s = new Span[2][2];
    Span A = new Span(member("A", level, "a=1"));
    Span B = new Span(member("B", level, "b=2"));
    Span C = new Span(member("C", level, "c=3"));
    s[0][0] = A;
    s[0][1] = B;
    s[1][0] = A;
    s[1][1] = C;
    SpanCalc sc = new SpanCalc(s);

    PropertySpanBuilder psb = new PropertySpanBuilder(extension);
    psb.setShowProperties(true);
    psb.addPropertySpans(sc);
    //dump(sc);
    assertSC(sc, new String[] { "A B 1 2 *", "A C 1 * 3" });
    assertTrue(sc.getSpan(0, 2).getObject() == sc.getSpan(1, 2).getObject());
  }

  void assertSC(SpanCalc sc, String[] rows) {
    assertEquals(rows.length, sc.getPositionCount());
    for (int pi = 0; pi < rows.length; pi++) {
      StringTokenizer st = new StringTokenizer(rows[pi]);
      int HC = st.countTokens();
      assertEquals(HC, sc.getHierarchyCount());
      for (int hi = 0; hi < HC; hi++) {
        String label = st.nextToken();
        if ("*".equals(label))
          continue;
        assertEquals(label, sc.getSpan(pi, hi).getObject().getLabel());
      }
    }
  }

  void dump(SpanCalc sc) {
    for (int pi = 0; pi < sc.getPositionCount(); pi++) {
      for (int hi = 0; hi < sc.getHierarchyCount(); hi++) {
        System.out.print(" " + sc.getSpan(pi, hi).getObject().getLabel());
      }
      System.out.println();
    }
  }

  SpanCalc makeSC32(Level level0, Level level1) {
    Span[][] s = new Span[3][2];
    s[0][0] = new Span(member("A", level0, "a=1"));
    s[1][0] = new Span(member("B", level0, "b=2"));
    s[2][0] = new Span(member("C", level0, "c=3"));
    s[0][1] = new Span(member("D", level1, "b=4,c=7"));
    s[1][1] = new Span(member("E", level1, "b=5,d=8"));
    s[2][1] = new Span(member("F", level1, "b=6,e=9"));
    return new SpanCalc(s);
  }

  public void testAddPropertySpansSameHier() {
    Level level = getLevel1();
    SpanCalc sc = makeSC32(level, level);
    PropertySpanBuilder psb = new PropertySpanBuilder(extension);
    psb.addPropertySpans(sc);
    psb.setShowProperties(true);
    psb.addPropertySpans(sc);
    //dump(sc);
    assertSC(sc, new String[] { "A D 1 4 7 * *", "B E * 5 * 8 *", "C F * 6 3 * 9" });
  }

  public void testAddPropertySpansSameDiffHier() {
    LevelImpl level0 = new LevelImpl();
    level0.setLabel("level0");
    HierarchyImpl hier0 = new HierarchyImpl();
    hier0.setLabel("hier0");
    level0.setHierarchy(hier0);

    LevelImpl level1 = new LevelImpl();
    HierarchyImpl hier1 = new HierarchyImpl();
    hier1.setLabel("hier1");
    level1.setHierarchy(hier1);
    level1.setLabel("level1");

    SpanCalc sc = makeSC32(level0, level1);
    PropertySpanBuilder psb = new PropertySpanBuilder(extension);
    psb.addPropertySpans(sc);
    psb.setShowProperties(true);
    psb.addPropertySpans(sc);
    //dump(sc);
    assertSC(sc, new String[] { "A 1 * * D 4 7 * *", "B * 2 * E 5 * 8 *", "C * * 3 F 6 3 * 9" });
  }

  public void testVisibleNames1() {
    Level level = getLevel1();
    SpanCalc sc = makeSC32(level, level);
    PropertySpanBuilder psb = new PropertySpanBuilder(extension);
    List vpm = new ArrayList();
    vpm.add(meta("b", level));
    vpm.add(meta("a", level));
    psb.setVisiblePropertyMetas(vpm);
    psb.setShowProperties(true);
    psb.addPropertySpans(sc);
    //dump(sc);
    assertSC(sc, new String[] { "A D 4 1", "B E 5 *", "C F 6 *" });
  }

  public void testVisibleNames2() {
    // different levels, same hierarchy
    HierarchyImpl hier = new HierarchyImpl();
    hier.setLabel("hier");
    LevelImpl level0 = new LevelImpl();
    level0.setLabel("level0");
    level0.setHierarchy(hier);
    LevelImpl level1 = new LevelImpl();
    level1.setHierarchy(hier);
    level1.setLabel("level1");

    SpanCalc sc = makeSC32(level0, level1);
    PropertySpanBuilder psb = new PropertySpanBuilder(extension);
    List vpm = new ArrayList();
    vpm.add(meta("b", level1));
    vpm.add(meta("a", level0));
    psb.setVisiblePropertyMetas(vpm);
    psb.setShowProperties(true);
    psb.addPropertySpans(sc);

    assertSC(sc, new String[] { "A D 4 1", "B E 5 *", "C F 6 *" });
  }

  public void testVisibleNames3() {
    // different levels, different hierarchies (1)
    HierarchyImpl hier0 = new HierarchyImpl();
    hier0.setLabel("hier0");
    LevelImpl level0 = new LevelImpl();
    level0.setLabel("level0");
    level0.setHierarchy(hier0);
    HierarchyImpl hier1 = new HierarchyImpl();
    hier1.setLabel("hier1");
    LevelImpl level1 = new LevelImpl();
    level1.setHierarchy(hier1);
    level1.setLabel("level1");

    SpanCalc sc = makeSC32(level0, level1);
    PropertySpanBuilder psb = new PropertySpanBuilder(extension);
    List vpm = new ArrayList();
    vpm.add(meta("b", level1));
    vpm.add(meta("a", level0));
    psb.setVisiblePropertyMetas(vpm);
    psb.setShowProperties(true);
    psb.addPropertySpans(sc);
    assertSC(sc, new String[] { "A 1 D 4", "B * E 5", "C * F 6" });
  }

  public void testVisibleNames4() {
    // different levels, different hierarchies (2)
    HierarchyImpl hier0 = new HierarchyImpl();
    hier0.setLabel("hier0");
    LevelImpl level0 = new LevelImpl();
    level0.setLabel("level0");
    level0.setHierarchy(hier0);
    HierarchyImpl hier1 = new HierarchyImpl();
    hier1.setLabel("hier1");
    LevelImpl level1 = new LevelImpl();
    level1.setHierarchy(hier1);
    level1.setLabel("level1");

    SpanCalc sc = makeSC32(level0, level1);
    PropertySpanBuilder psb = new PropertySpanBuilder(extension);
    List vpm = new ArrayList();
    vpm.add(meta("b", level0));
    vpm.add(meta("a", level0));
    vpm.add(meta("c", level1));
    psb.setVisiblePropertyMetas(vpm);
    psb.setShowProperties(true);
    psb.addPropertySpans(sc);
    assertSC(sc, new String[] { "A * 1 D 7", "B 2 * E *", "C * * F *" });
  }

  protected void setUp() throws Exception {
    extension = new TestMemberProperties();
  }

}

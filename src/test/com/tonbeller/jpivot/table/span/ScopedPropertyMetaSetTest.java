package com.tonbeller.jpivot.table.span;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.MemberPropertyMeta;
import com.tonbeller.jpivot.olap.model.impl.HierarchyImpl;
import com.tonbeller.jpivot.olap.model.impl.LevelImpl;
import com.tonbeller.jpivot.olap.model.impl.MemberImpl;
import com.tonbeller.jpivot.olap.model.impl.PropertyImpl;

/**
 * @author av
 */
public class ScopedPropertyMetaSetTest extends TestCase {
  private TestMemberProperties extension;

  MemberPropertyMetaFilter allFilter = new ScopedPropertyMetaSet.AllFilter();

  public void testLevelScope() {
    List vp = new ArrayList();
    vp.add(new MemberPropertyMeta("1label", "a", "l1"));
    vp.add(new MemberPropertyMeta("2label", "a", "l2"));
    vp.add(new MemberPropertyMeta("3label", "b", "l1"));
    vp.add(new MemberPropertyMeta("4label", "c", "l2"));

    ScopedPropertyMetaSet vpl = new ScopedPropertyMetaSet(null);
    vpl.addAll(vp);

    assertTrue(vpl.contains(new MemberPropertyMeta(null, "a", "l1")));
    assertTrue(vpl.contains(new MemberPropertyMeta(null, "a", "l2")));
    assertFalse(vpl.contains(new MemberPropertyMeta(null, "a", "l3")));

    assertTrue(vpl.contains(new MemberPropertyMeta(null, "b",  "l1")));
    assertFalse(vpl.contains(new MemberPropertyMeta(null, "b", "l2")));
    assertFalse(vpl.contains(new MemberPropertyMeta(null, "b", "l3")));

    assertFalse(vpl.contains(new MemberPropertyMeta(null, "c", "l1")));
    assertTrue(vpl.contains(new MemberPropertyMeta(null, "c", "l2")));
    assertFalse(vpl.contains(new MemberPropertyMeta(null, "c", "l3")));

    assertFalse(vpl.contains(new MemberPropertyMeta(null, "d", "l1")));
    assertFalse(vpl.contains(new MemberPropertyMeta(null, "d", "l2")));
    assertFalse(vpl.contains(new MemberPropertyMeta(null, "d", "l3")));

    String[] allNames = vpl.getAllNames();
    assertEquals(3, allNames.length);
    assertEquals("a", allNames[0]);
    assertEquals("b", allNames[1]);
    assertEquals("c", allNames[2]);

    vpl.remove(new MemberPropertyMeta(null, "a", "l3"));
    assertEquals(4, vpl.metaList(allFilter).size());
    vpl.remove(new MemberPropertyMeta(null, "a", "l1"));
    assertEquals(3, vpl.metaList(allFilter).size());

  }

  String scope(Level l) {
    return extension.getPropertyScope(l);
  }

  public void testHierarchyScope() {
    List vp = new ArrayList();

    LevelImpl l1 = new LevelImpl();
    l1.setLabel("l1");
    LevelImpl l2 = new LevelImpl();
    l2.setLabel("l2");
    LevelImpl l3 = new LevelImpl();
    l3.setLabel("l3");

    HierarchyImpl h1 = new HierarchyImpl();
    h1.setLabel("h1");
    HierarchyImpl h2 = new HierarchyImpl();
    h2.setLabel("h2");
    l1.setHierarchy(h1);
    l2.setHierarchy(h1);
    l3.setHierarchy(h2);

    vp.add(new MemberPropertyMeta("1label", "a", scope(l1)));
    vp.add(new MemberPropertyMeta("2label", "b", scope(l2)));
    vp.add(new MemberPropertyMeta("3label", "c", scope(l3)));

    ScopedPropertyMetaSet vpl = new ScopedPropertyMetaSet(null);
    vpl.addAll(vp);
    assertEquals(3, vpl.metaList(allFilter).size());
    vp.add(new MemberPropertyMeta("1label", "a", scope(l1)));
    assertEquals(3, vpl.metaList(allFilter).size());
    vp.add(new MemberPropertyMeta("1label", "a", scope(l2)));
    assertEquals(3, vpl.metaList(allFilter).size());

    assertEquals(scope(l1), scope(l1));
    assertEquals(scope(l1), scope(l2));
    assertNotSame(scope(l1), scope(l3));
    assertEquals(scope(l2), scope(l2));
    assertNotSame(scope(l2), scope(l3));
    assertEquals(scope(l3), scope(l3));

    assertTrue(vpl.contains(new MemberPropertyMeta(null, "a", scope(l1))));
    assertTrue(vpl.contains(new MemberPropertyMeta(null, "a", scope(l2))));
    assertFalse(vpl.contains(new MemberPropertyMeta(null, "a", scope(l3))));

    assertTrue(vpl.contains(new MemberPropertyMeta(null, "b", scope(l1))));
    assertTrue(vpl.contains(new MemberPropertyMeta(null, "b", scope(l2))));
    assertFalse(vpl.contains(new MemberPropertyMeta(null, "b", scope(l3))));

    assertFalse(vpl.contains(new MemberPropertyMeta(null, "c", scope(l1))));
    assertFalse(vpl.contains(new MemberPropertyMeta(null, "c", scope(l2))));
    assertTrue(vpl.contains(new MemberPropertyMeta(null, "c", scope(l3))));

    assertFalse(vpl.contains(new MemberPropertyMeta(null, "d", scope(l1))));
    assertFalse(vpl.contains(new MemberPropertyMeta(null, "d", scope(l2))));
    assertFalse(vpl.contains(new MemberPropertyMeta(null, "d", scope(l3))));

    String[] allNames = vpl.getAllNames();
    assertEquals(3, allNames.length);
    assertEquals("a", allNames[0]);
    assertEquals("b", allNames[1]);
    assertEquals("c", allNames[2]);

    Set scopes = new HashSet();
    scopes.add(scope(l1));
    MemberPropertyMetaFilter filter = vpl.createScopesFilter(scopes);
    ScopedPropertyMetaSet sub = vpl.metaSet(filter);
    assertEquals(2, sub.metaList(allFilter).size());
    assertTrue(sub.contains(new MemberPropertyMeta(null, "a", scope(l1))));
    assertTrue(sub.contains(new MemberPropertyMeta(null, "b", scope(l2))));
    assertFalse(sub.contains(new MemberPropertyMeta(null, "c", scope(l3))));

    scopes.clear();
    scopes.add(scope(l2));
    filter = vpl.createScopesFilter(scopes);
    sub = vpl.metaSet(filter);
    assertEquals(2, sub.metaList(allFilter).size());
    assertTrue(sub.contains(new MemberPropertyMeta(null, "a", scope(l1))));
    assertTrue(sub.contains(new MemberPropertyMeta(null, "b", scope(l2))));
    assertFalse(sub.contains(new MemberPropertyMeta(null, "c", scope(l3))));

    scopes.clear();
    scopes.add(scope(l3));
    filter = vpl.createScopesFilter(scopes);
    sub = vpl.metaSet(filter);
    assertEquals(1, sub.metaList(allFilter).size());
    assertFalse(sub.contains(new MemberPropertyMeta(null, "a", scope(l1))));
    assertFalse(sub.contains(new MemberPropertyMeta(null, "b", scope(l2))));
    assertTrue(sub.contains(new MemberPropertyMeta(null, "c", scope(l3))));

    vpl.remove(new MemberPropertyMeta(null, "a", scope(l3)));
    assertEquals(3, vpl.metaList(allFilter).size());
    vpl.remove(new MemberPropertyMeta(null, "a", scope(l1)));
    assertEquals(2, vpl.metaList(allFilter).size());

  }


  public void testIntersectList() {
    extension.setLevelScope(true);
    LevelImpl level = new LevelImpl();
    level.setLabel("level");
    List abc = new ArrayList();
    abc.add(new MemberPropertyMeta("1label", "a", scope(level)));
    abc.add(new MemberPropertyMeta("2label", "b", scope(level)));
    abc.add(new MemberPropertyMeta("4label", "c", scope(level)));

    List abd = new ArrayList();
    abd.add(new MemberPropertyMeta("1label", "a", scope(level)));
    abd.add(new MemberPropertyMeta("2label", "b", scope(level)));
    abd.add(new MemberPropertyMeta("4label", "d", scope(level)));

    ScopedPropertyMetaSet set = new ScopedPropertyMetaSet(extension);
    set.addAll(abc);
    List ab = set.intersectList(abd);
    assertEquals(2, ab.size());
  }

  public void testAddMember() {
    MemberImpl m = new MemberImpl();
    m.addProperty(new PropertyImpl("a", "av"));
    m.addProperty(new PropertyImpl("b", "bv"));
    LevelImpl l = new LevelImpl();
    l.setLabel("l");
    m.setLevel(l);
    HierarchyImpl h = new HierarchyImpl();
    h.setLabel("h");
    l.setHierarchy(h);

    extension.setLevelScope(true);
    ScopedPropertyMetaSet set = new ScopedPropertyMetaSet(extension);
    set.addMember(m);
    assertTrue(set.contains(new MemberPropertyMeta("a", "a", "l")));

    extension.setLevelScope(false);
    set = new ScopedPropertyMetaSet(extension);
    set.addMember(m);
    assertTrue(set.contains(new MemberPropertyMeta("a", "a", "h")));
  }

  public ScopedPropertyMetaSetTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    extension = new TestMemberProperties();
  }

}

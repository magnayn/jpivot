package com.tonbeller.jpivot.test.olap;

import junit.framework.TestCase;

/**
 * Created on 22.10.2002
 * 
 * @author av
 */
public class DimensionBuilderTest extends TestCase {

  /**
   * Constructor for DimensionBuilderTest.
   * @param arg0
   */
  public DimensionBuilderTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(DimensionBuilderTest.class);
  }

  public void testBuild() {
    DimensionBuilder db = new DimensionBuilder();
    
    TestDimension td = db.build("test", new String[]{"level-a", "level-b"}, new int[] {2, 3});
    TestHierarchy th = (TestHierarchy)td.getHierarchies()[0];
    assertEquals(2, th.getRootMembers().length);
    assertEquals(3, th.getRootMembers()[1].getChildMember().size());
  }

}

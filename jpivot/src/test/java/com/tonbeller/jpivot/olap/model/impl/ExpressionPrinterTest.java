package com.tonbeller.jpivot.olap.model.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Expression;
import com.tonbeller.jpivot.olap.model.ExpressionPrinter;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;

/**
 * @author av
 */
public class ExpressionPrinterTest extends TestCase {

  MemberImpl m1;
  MemberImpl m2;
  LevelImpl level;
  HierarchyImpl hier;
  DimensionImpl dim;
  
  public ExpressionPrinterTest(String arg0) {
    super(arg0);
  }
  
  public void testCall() {
    List args = new ArrayList();
    args.add(m1);
    args.add(level);
    args.add(hier);
    args.add(dim);    
    FunCallExprImpl f = new FunCallExprImpl();
    f.setArgs(args);
    f.setName("call");
    //System.out.println(print(f));
    assertEquals("call([Member 1], [Level 1], [Hierarchy 1], [Dimension 1])", print(f));
  }

  public void testProper() {
    List args = new ArrayList();
    args.add(level);
    FunCallExprImpl f = new FunCallExprImpl();
    f.setArgs(args);
    f.setName(".members");
    assertEquals("[Level 1].members", print(f));
  }

  public void testTupel() {
    List args = new ArrayList();
    args.add(m1);
    args.add(m2);
    FunCallExprImpl f = new FunCallExprImpl();
    f.setArgs(args);
    f.setName("()");
    assertEquals("([Member 1], [Member 2])", print(f));
  }

  public void testSet() {
    List args = new ArrayList();
    args.add(m1);
    args.add(m2);
    FunCallExprImpl f = new FunCallExprImpl();
    f.setArgs(args);
    f.setName("{}");
    assertEquals("{[Member 1], [Member 2]}", print(f));
  }

  public void testParam() {
    ParameterExprImpl parm = new ParameterExprImpl();
    parm.setName("my name");
    parm.setLabel("my label");
    parm.setType(0);
    parm.setValue(m1);
    //System.out.println(print(parm));
    assertEquals("Parameter(\"RandomID\", STRING, \"my label\", [Member 1])", print(parm));
  }

  private String print(Expression f) {
    ExpressionPrinter ep = new ExpressionPrinter();
    f.accept(ep);
    return ep.toString();
  }

  protected void setUp() throws Exception {
  m1 = new MemberImpl();
  m1.setLabel("Member 1");
  m2 = new MemberImpl();
  m2.setLabel("Member 2");
  level = new LevelImpl();
  level.setLabel("Level 1");
  m1.setLevel(level);
  m2.setLevel(level);
  hier = new HierarchyImpl();
  level.setHierarchy(hier);
  hier.setLabel("Hierarchy 1");
  hier.setLevels(new Level[]{level});
  dim =new DimensionImpl();
  dim.setLabel("Dimension 1");
  dim.setHierarchies(new Hierarchy[]{hier});
  hier.setDimension(dim);
}

}

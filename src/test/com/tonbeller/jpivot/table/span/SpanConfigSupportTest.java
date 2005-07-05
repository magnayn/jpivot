package com.tonbeller.jpivot.table.span;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Visitor;
import com.tonbeller.jpivot.test.olap.TestLevel;
import com.tonbeller.jpivot.test.olap.TestMember;

/**
 * Created on 29.10.2002
 * 
 * @author av
 */
public class SpanConfigSupportTest extends TestCase {

  /**
   * Constructor for SpanConfigSupportTest.
   * @param arg0
   */
  public SpanConfigSupportTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(SpanConfigSupportTest.class);
  }

  static class X implements Displayable {
    public String getLabel() { return this.toString(); }
    public void accept(Visitor visitor) {
      throw new UnsupportedOperationException();
    }
  }
  
  public void testChooseSpanDirection() {
    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDefaultDirection(100);
    scs.setDirection(Member.class, 200);
    scs.setDirection(Level.class, 300);
    
    check(scs, 100, new X());
    check(scs, 200, new TestMember());
    check(scs, 300, new TestLevel());
    
  }

  public void testOrder() {
    SpanConfigSupport scs = new SpanConfigSupport();
    scs.setDefaultDirection(100);
    scs.setDirection(Member.class, 200);
    scs.setDirection(Displayable.class, 300);
    // level is invisible, because Displayable will match
    scs.setDirection(Level.class, 400);
    
    check(scs, 200, new TestMember());
    check(scs, 300, new TestLevel());
    
  }
  
  void check(SpanConfig sc, int dir, Displayable o) {
    Span s = new Span(null, null, o);
    assertEquals(dir, sc.chooseSpanDirection(s));
  }

}

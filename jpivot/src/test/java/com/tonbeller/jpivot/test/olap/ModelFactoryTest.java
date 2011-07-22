package com.tonbeller.jpivot.test.olap;

import java.net.URL;

import junit.framework.TestCase;

import com.tonbeller.jpivot.core.Extension;
import com.tonbeller.jpivot.core.ModelChangeEvent;
import com.tonbeller.jpivot.core.ModelChangeListener;
import com.tonbeller.jpivot.core.ModelFactory;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Cell;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.DrillExpandMember;
import com.tonbeller.jpivot.tags.TestOlapModelTag;

public class ModelFactoryTest extends TestCase {

  private TestOlapModel model;
  int count = 0;
  
  /**
   * Constructor for ModelFactoryTest.
   * @param arg0
   */
  public ModelFactoryTest(String arg0) {
    super(arg0);
  }
  

  public void testInstance() throws Exception {
    assertNotNull(model);
    Extension ext = model.getExtension(DrillExpandMember.ID);
    assertNotNull(ext);
    assertNull(model.getExtension("xx"));
  }
  
  public void testResult() throws Exception {
    // assert !crash
    Result res = model.getResult();
  }
  
  public void testListener() {
    ModelChangeListener listener = new ModelChangeListener () {
      public void modelChanged(ModelChangeEvent e) {
        count += 1;
      }

      public void structureChanged(ModelChangeEvent e) {
      }
    };
    model.addModelChangeListener(listener);
    count = 0;
    model.fireModelChanged();
    assertEquals(count, 1);
  }

  public void testDrillExpand() throws Exception {
    Axis axis = model.getResult().getAxes()[1];
    int l0 = axis.getPositions().size();
    Position p = (Position)axis.getPositions().get(0);
    Member m = p.getMembers()[0];
    DrillExpandMember de = (DrillExpandMember)model.getExtension(DrillExpandMember.ID);
    assertTrue(!de.canCollapse(m));
    assertTrue(de.canExpand(m));
    
    de.expand(m);
    assertTrue(de.canCollapse(m));
    assertTrue(!de.canExpand(m));
    axis = model.getResult().getAxes()[1];
    int l1 = axis.getPositions().size();
    assertTrue(l0 < l1);
    
    de.collapse(m);
    assertTrue(!de.canCollapse(m));
    assertTrue(de.canExpand(m));
    axis = model.getResult().getAxes()[1];
    int l2 = axis.getPositions().size();
    assertEquals(l0, l2);
  }
  
  public void testTestable() throws Exception {
    // random numbers are no good for testing
    Cell c1 = (Cell)model.getResult().getCells().get(1);
    Cell c2 = (Cell)model.getResult().getCells().get(1);
    assertEquals(c1.getValue(), c2.getValue());
  }
  
  public void testTestable2() throws Exception {
    model.expand0();
    assertTrue(model.getResult().getAxes()[1].getPositions().size() > 1);
  }
  
  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    URL url = TestOlapModelTag.class.getResource("/com/tonbeller/jpivot/test/olap/config.xml");
    model = (TestOlapModel) ModelFactory.instance(url);
  }

}

package com.tonbeller.jpivot.tags;

import junit.framework.TestCase;

import com.tonbeller.jpivot.tags.StateManager.State;

/**
 * @author av
 * @since 15.02.2005
 */
public class StateManagerTest extends TestCase {
  State globalVisible;
  int globalShowCount;

  MyState a1;
  MyState a2;
  MyState a3;
  MyState b1;
  MyState b2;
  MyState c1;
  MyState c2;

  class MyState implements State {
    String name;
    String value;
    int initializeCount;
    int destroyCount;
    int hideCount;
    int showCount;

    public MyState(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public void initialize() {
      assertHidden();
      assertDestroyed();
      ++initializeCount;
      assertInitialized();
    }

    public void destroy() {
      assertHidden();
      assertInitialized();
      ++destroyCount;
      assertDestroyed();
    }

    public void show() {
      assertInitialized();
      assertHidden();
      ++ showCount;
      assertVisible();
      
      assertNull("only one state may be visible at once", globalVisible);
      globalVisible = this;
      ++ globalShowCount;
    }
    
    public void hide() {
      assertInitialized();
      assertVisible();
      ++ hideCount;
      assertHidden();

      assertEquals("state must be visible to be hidden", globalVisible, this);
      globalVisible = null;
    }

    void assertVisible() {
      assertTrue("visible", showCount == hideCount + 1);
    }
    void assertHidden() {
      assertTrue("hidden", showCount == hideCount);
    }
    void assertDestroyed() {
      assertTrue("initializeCount == destroyCount", initializeCount == destroyCount);
    }
    void assertInitialized() {
      assertTrue("initializeCount == destroyCount + 1", initializeCount == destroyCount + 1);
    }
  }

  void assertVisible(State s) {
    assertEquals("visible state ", s, globalVisible);
  }

  protected void setUp() throws Exception {
    super.setUp();
    a1 = new MyState("A", "a1");
    a2 = new MyState("A", "a2");
    a3 = new MyState("A", "a3");
    b1 = new MyState("B", "b1");
    b2 = new MyState("B", "b2");
    c1 = new MyState("C", "c1");
    c2 = new MyState("C", "c2");
  }

  /**
   * multiple initializeAndShow() with the same State Name "A"
   * @throws Exception 
   */
  public void testStackedReplace() throws Exception {
    StateManager s = new StackStateManager();
    s.initializeAndShow(a1);
    assertVisible(a1);
    a1.assertInitialized();
    s.initializeAndShow(a2);
    assertVisible(a2);
    a1.assertDestroyed();
    a2.assertInitialized();
    s.initializeAndShow(a3);
    assertVisible(a3);
    a2.assertDestroyed();
    a3.assertInitialized();
    s.destroyAll();
    a3.assertDestroyed();
    assertVisible(null);
  }

  /**
   * multiple initializeAndShow() with the different state names
   * @throws Exception 
   */
  public void testStackedStacked() throws Exception {
    StateManager s = new StackStateManager();
    s.initializeAndShow(a1);
    assertVisible(a1);
    a1.assertInitialized();

    // push b1 on top of a1
    s.initializeAndShow(b1);
    assertVisible(b1);
    a1.assertInitialized();
    b1.assertInitialized();

    // push c1 on top of a1, b1
    s.initializeAndShow(c1);
    assertVisible(c1);
    a1.assertInitialized();
    b1.assertInitialized();
    c1.assertInitialized();

    // remove c1, b1, a1 and make a2 current
    s.initializeAndShow(a2);
    assertVisible(a2);
    a1.assertDestroyed();
    b1.assertDestroyed();
    c1.assertDestroyed();
    a2.assertInitialized();

    // clear all
    s.destroyAll();
    assertVisible(null);
  }

  public void testStackedCurrentByName() throws Exception {
    StateManager s = new StackStateManager();
    s.initializeAndShow(a1);
    s.initializeAndShow(b1);
    s.initializeAndShow(c1);

    // initializeAndShow must be called only if the state changes
    globalShowCount = 0;

    // illegal name is ignored
    s.showByName("xxx");
    assertEquals(0, globalShowCount);
    a1.assertInitialized();
    b1.assertInitialized();
    c1.assertInitialized();

    // C is already current 
    s.showByName("C");
    assertEquals(0, globalShowCount);
    c1.assertInitialized();
    c1.assertVisible();
    b1.assertInitialized();
    b1.assertHidden();
    a1.assertHidden();
    a1.assertInitialized();
    
    s.showByName("B");
    c1.assertHidden();
    c1.assertDestroyed();
    b1.assertInitialized();
    b1.assertVisible();
    a1.assertInitialized();
    a1.assertHidden();
    assertEquals(1, globalShowCount);

    // "B" again does not change anything
    s.showByName("B");
    assertEquals(1, globalShowCount);
    c1.assertHidden();
    c1.assertDestroyed();
    b1.assertInitialized();
    b1.assertVisible();
    a1.assertInitialized();
    a1.assertHidden();

    s.showByName("A");
    assertEquals(2, globalShowCount);
    c1.assertDestroyed();
    c1.assertHidden();
    b1.assertDestroyed();
    b1.assertHidden();
    a1.assertInitialized();
    a1.assertVisible();
    s.showByName("A");
    assertEquals(2, globalShowCount);
  }

  public void testStackInitializeDestroy() throws Exception {
    StateManager s = new StackStateManager();
    s.initializeAndShow(a1);
    assertVisible(a1);
    assertEquals(1, a1.initializeCount);
    assertEquals(0, a1.destroyCount);

    s.initializeAndShow(a1);
    assertVisible(a1);
    assertEquals(2, a1.initializeCount);
    assertEquals(1, a1.destroyCount);
  }
  
  public void testStackedDestroyByNameCurrent() throws Exception {
    StateManager s = new StackStateManager();
    s.initializeAndShow(a1);
    s.initializeAndShow(b1);
    s.initializeAndShow(c1);
    
    s.destroyByName("C");
    a1.assertInitialized();
    a1.assertHidden();
    b1.assertInitialized();
    b1.assertVisible();
    assertEquals("initialize() called once", 1, b1.initializeCount);
    assertEquals("show() called twice", 2, b1.showCount);
    c1.assertDestroyed();
    c1.assertHidden();
  }
  
  public void testStackedDestroyByNameNotCurrent() throws Exception {
    StateManager s = new StackStateManager();
    s.initializeAndShow(a1);
    s.initializeAndShow(b1);
    s.initializeAndShow(c1);
    
    s.destroyByName("B");
    a1.assertInitialized();
    a1.assertVisible();
    b1.assertDestroyed();
    b1.assertHidden();
    c1.assertDestroyed();
    c1.assertHidden();
  }

  public void testPageStateManager() throws Exception {
    StateManager s = new PageStateManager();
    s.initializeAndShow(a1);
    assertVisible(a1);
    a1.assertInitialized();
    s.initializeAndShow(b1);
    assertVisible(b1);
    a1.assertInitialized();
    b1.assertInitialized();
    s.initializeAndShow(c1);
    assertVisible(c1);
    a1.assertInitialized();
    b1.assertInitialized();
    c1.assertInitialized();
    s.initializeAndShow(a2);
    assertVisible(a2);
    a1.assertDestroyed();
    a2.assertInitialized();
    b1.assertInitialized();
    c1.assertInitialized();
    s.destroyAll();
    a2.assertDestroyed();
    b1.assertDestroyed();
    c1.assertDestroyed();
    assertVisible(null);
  }

  public void testPageCurrentByName() throws Exception {
    StateManager s = new PageStateManager();
    s.initializeAndShow(a1);
    s.initializeAndShow(b1);
    s.initializeAndShow(c1);

    // initializeAndShow must be called only if the state changes
    globalShowCount = 0;
    s.showByName("C");
    assertEquals(0, globalShowCount);
    a1.assertHidden();
    a1.assertInitialized();
    b1.assertHidden();
    b1.assertInitialized();
    c1.assertVisible();
    c1.assertInitialized();
    
    s.showByName("B");
    assertEquals(1, globalShowCount);
    a1.assertHidden();
    a1.assertInitialized();
    b1.assertVisible();
    b1.assertInitialized();
    c1.assertHidden();
    c1.assertInitialized();

    s.showByName("B");
    assertEquals(1, globalShowCount);
    a1.assertHidden();
    a1.assertInitialized();
    b1.assertVisible();
    b1.assertInitialized();
    c1.assertHidden();
    c1.assertInitialized();

    s.showByName("A");
    assertEquals(2, globalShowCount);
    a1.assertVisible();
    a1.assertInitialized();
    b1.assertHidden();
    b1.assertInitialized();
    c1.assertHidden();
    c1.assertInitialized();

    s.showByName("A");
    assertEquals(2, globalShowCount);
    a1.assertVisible();
    a1.assertInitialized();
    b1.assertHidden();
    b1.assertInitialized();
    c1.assertHidden();
    c1.assertInitialized();
  }

  // destroys the current state
  public void testPageDestroyByNameCurrent() throws Exception {
    StateManager s = new PageStateManager();
    s.initializeAndShow(a1);
    s.initializeAndShow(b1);
    s.initializeAndShow(c1);
    
    s.destroyByName("C");
    a1.assertInitialized();
    a1.assertHidden();
    b1.assertInitialized();
    b1.assertHidden();
    c1.assertDestroyed();
    c1.assertHidden();
    
    s.showByName("B");
    b1.assertInitialized();
    assertEquals("initialize() called once", 1, b1.initializeCount);
    b1.assertVisible();
    assertEquals("show() called twice", 2, b1.showCount);
   }
  // destroy the not-current state
  public void testPageDestroyByNameNotCurrent() throws Exception {
    StateManager s = new PageStateManager();
    s.initializeAndShow(a1);
    s.initializeAndShow(b1);
    s.initializeAndShow(c1);
    
    s.destroyByName("B");
    a1.assertInitialized();
    a1.assertHidden();
    b1.assertDestroyed();
    b1.assertHidden();
    c1.assertInitialized();
    c1.assertVisible();
   }
}

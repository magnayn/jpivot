package com.tonbeller.jpivot.tags;

import java.util.Iterator;
import java.util.Stack;

/**
 * A Stack of states (name/value pairs). For every state name, there is only one value.
 * States with different names are stacked, every name is on the stack only once. Example:
 * <p />
 * <pre>
 * ps = new StackStateStrategy();
 * -- a1 is top of stack:
 * ps.setCurrent("A", a1);
 * -- replace a1 with a2:
 * ps.setCurrent("A", a2);
 * -- push b1 on the stack
 * ps.setCurrent("B", b1);
 * -- stack now contains a2, b1
 * -- push c1 on the stack
 * ps.setCurrent("C", c1);
 * -- pop c1 and b1 and make a3 top of stack
 * ps.setCurrent("A", a3);
 * -- stack contains a3 only
 * </pre>
 * 
 * @author av
 * @since 15.02.2005
 */
public class StackStateManager implements StateManager {
  Stack stack = new Stack();
  StateLogger logger = new Log4jStateLogger();

  private boolean stackContainsName(String name) {
    for (Iterator it = stack.iterator(); it.hasNext();) {
      State s = (State) it.next();
      if (name.equals(s.getName()))
        return true;
    }
    return false;
  }

  private void hideCurrent() throws Exception {
    State s = getCurrent();
    if (s != null) {
      logger.hide(s);
      s.hide();
    }
  }

  private void showCurrent() throws Exception {
    State s = getCurrent();
    if (s != null) {
      logger.show(s);
      s.show();
    }
  }

  private State getCurrent() {
    if (stack.isEmpty())
      return null;
    return (State) stack.peek();
  }

  /**
   * removes all properties from the stack
   * @throws Exception 
   */
  public void initializeAndShow(State s) throws Exception {
    hideCurrent();
    while (stackContainsName(s.getName())) {
      State t = (State) stack.pop();
      logger.destroy(s);
      t.destroy();
    }
    logger.initialize(s);
    s.initialize();
    stack.push(s);
    showCurrent();
  }

  /**
   * pops and destroys all states up to but not including the named one. The named
   * state will become the visible one.
   * @see #destroyByName 
   */
  public void showByName(String name) throws Exception {
    if (!stackContainsName(name)) {
      logger.error("not found in stack: " + name);
      return;
    }

    // already current?
    State s = getCurrent();
    if (name.equals(s.getName()))
      return;

    // unwind stack up to the requested name
    hideCurrent();
    while (!name.equals(s.getName())) {
      State t = (State) stack.pop();
      logger.destroy(t);
      t.destroy();
      s = (State) stack.peek();
    }
    showCurrent();
  }

  public void destroyAll() throws Exception {
    hideCurrent();
    while (!stack.isEmpty()) {
      State s = (State) stack.pop();
      logger.destroy(s);
      s.destroy();
    }
  }

  /**
   * pops and destroys all states up to and including the named one. If there is another
   * state beneath the named one, that will become the visible one.
   * @see #showByName 
   */
  public void destroyByName(String name) throws Exception {
    hideCurrent();
    while (stackContainsName(name)) {
      State t = (State) stack.pop();
      logger.destroy(t);
      t.destroy();
    }
    showCurrent();
  }

  public StateLogger getLogger() {
    return logger;
  }

  public void setLogger(StateLogger logger) {
    this.logger = logger;
  }

}

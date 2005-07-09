package com.tonbeller.jpivot.tags;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author av
 * @since 15.02.2005
 */
public class PageStateManager implements StateManager {
  Map map = new HashMap();
  State current;
  private static final Logger logger = Logger.getLogger(PageStateManager.class);

  void showCurrent() throws Exception {
    if (current != null)
      current.show();
  }
  
  void hideCurrent() throws Exception {
    if (current != null)
      current.hide();
  }
  
  public void initializeAndShow(State next) throws Exception {
    hideCurrent();
    
    // remove state with same name from map
    State prev = (State) map.get(next.getName());
    if (prev != null)
      prev.destroy();

    map.put(next.getName(), next);
    next.initialize();
    current = next;
    showCurrent();
  }

  /**
   * makes the named state the visible one
   */
  public void showByName(String name) throws Exception {
    State s = (State) map.get(name);
    if (s == null) {
      logger.error("could not find state for " + name);
      return;
    }
    if (current != s) {
      hideCurrent();
      current = s;
      showCurrent();
    }
  }

  /**
   * removes and destroys all states
   */
  public void destroyAll() throws Exception {
    hideCurrent();
    current = null;
    for (Iterator it = map.values().iterator(); it.hasNext();) {
      State s = (State) it.next();
      s.destroy();
    }
    map.clear();
  }

  /**
   * removes and destroys the named state
   */
  public void destroyByName(String name) throws Exception {
    State s = (State) map.get(name);
    if (s == null) {
      logger.error("query " + name + " not found");
      return;
    }
    if (s == current) {
      hideCurrent();
      current = null;
    }
    s.destroy();
    map.remove(name);
  }

}

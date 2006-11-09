/*
 * Copyright (c) 1971-2003 TONBELLER AG, Bensheim.
 * All rights reserved.
 */
package com.tonbeller.jpivot.tags;

import com.tonbeller.jpivot.tags.StateManager.State;

/**
 * methods will be called before the action on the state is performed.
 * 
 * @author av
 * @since Mar 23, 2006
 */
public interface StateLogger {
  void initialize(State s);

  void destroy(State s);

  void show(State s);

  void hide(State s);
  
  void error(String msg);

}

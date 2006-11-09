/*
 * Copyright (c) 1971-2003 TONBELLER AG, Bensheim.
 * All rights reserved.
 */
package com.tonbeller.jpivot.tags;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.tags.StateManager.State;

public class Log4jStateLogger implements StateLogger {
  private static final Logger logger = Logger.getLogger(Log4jStateLogger.class);

  public void initialize(State s) {
    logger.info("initialize " + s.getName());
  }

  public void destroy(State s) {
    logger.info("destroy " + s.getName());
  }

  public void show(State s) {
    logger.info("show " + s.getName());
  }

  public void hide(State s) {
    logger.info("hide " + s.getName());
  }

  public void error(String msg) {
    logger.error(msg);
  }

}

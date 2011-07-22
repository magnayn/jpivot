package com.tonbeller.jpivot.olap.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.core.ModelChangeListener;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.OlapModel;

public class ProtozoanOlapModel extends ScalarOlapModel {
  private static final CellImpl ERROR_CELL = new CellImpl();
  static {
    ERROR_CELL.setFormattedValue("splitQuery: Index not found");
    ERROR_CELL.setValue(new Double(Double.NaN));
  }

  private OlapModel realModel;
  private int idx;

  private static Logger logger = Logger.getLogger(ProtozoanOlapModel.class);

  public ProtozoanOlapModel(OlapModel om, int idx) {
    super();
    this.realModel = om;
    this.idx = idx;
  }

  public void addModelChangeListener(ModelChangeListener l) {
    realModel.addModelChangeListener(l);
  }

  public void removeModelChangeListener(ModelChangeListener l) {
    realModel.removeModelChangeListener(l);
  }

  public List getCells() {
    List l = null;
    try {
      l = realModel.getResult().getCells();
    } catch (OlapException e) {
      logger.error(null, e);
    }
    if (l == null) return null;

    List ret = new ArrayList();
    if(l.size() > idx) {
      ret.add(l.get(idx));
    } else {
      ret.add(ERROR_CELL);
      System.out.println("Index " + idx + " not in cell-list");
      logger.error("Index " + idx + " not in cell-list");
    }
    return ret;
  }
}

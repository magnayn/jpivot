/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 *
 * 
 */
package com.tonbeller.jpivot.table;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.SAXException;

import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.wcf.utils.ObjectFactory;

/**
 * creates a tablecomponent from xml configuration file
 * 
 * @author av
 */
public class TableComponentFactory {

  private TableComponentFactory() { }

  public static TableComponent instance(String id, URL configXml, OlapModel olapModel) throws IOException, SAXException {
    URL rulesXml = TableComponent.class.getResource("rules.xml");
    TableComponent table = (TableComponent) ObjectFactory.instance(rulesXml, configXml);
    table.setOlapModel(olapModel);
    table.setId(id);
    return table;
  }


}

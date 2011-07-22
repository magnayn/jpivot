package com.tonbeller.jpivot.xmla;

import com.tonbeller.jpivot.olap.model.OlapException;

/**
 * provide settings for test environment
 */
public class TestConnection {

  public static void initModel(XMLA_Model model) throws OlapException {
    String uri = System.getProperty("xmla.uri", "http://TBNTSRV3/XML4A/msxisapi.dll");
    //String uri = System.getProperty("xmla.uri", "http://PCHH/XMLA/msxisapi.dll");
    model.setUri(uri);
    String catalog = System.getProperty("xmla.catalog", "Foodmart 2000");
    model.setCatalog(catalog);
    model.initialize();
  }
}
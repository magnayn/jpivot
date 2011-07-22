package com.tonbeller.jpivot.olap.model.impl;

import junit.framework.TestCase;

import com.tonbeller.jpivot.olap.model.Property;

public class FormatStringParserTest extends TestCase {

  public void testParse() {
    FormatStringParser parser = new FormatStringParser();
    CellImpl cell = new CellImpl();
    
    FormatStringParser.Result res = parser.parse(cell, "value");
    assertEquals("value", res.getFormattedValue());
    assertEquals(0, res.getProperties().size());
    
    res = parser.parse(cell, "|a|b=c");
    assertEquals("a", res.getFormattedValue());
    assertEquals(1, res.getProperties().size());
    Property p = (Property) res.getProperties().get(0);
    assertEquals("b", p.getName());
    assertEquals("c", p.getValue());
    
    cell.setValue(new Long(3600L * 3 + 60 * 6 + 11));
    res = parser.parse(cell, "|11171|exit=hhhmmss");
    assertEquals("3:06:11", res.getFormattedValue());
    
    res = parser.parse(cell, "|11171|exit=hhhmmss|a=b");
    assertEquals("3:06:11", res.getFormattedValue());
    assertEquals(1, res.getProperties().size());
  }

}

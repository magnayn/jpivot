package com.tonbeller.jpivot.olap.model;


/**
 * @author av
 * @since 22.02.2005
 */
public interface Alignable {

  public static final class Alignment {
    private String attributeValue;
    public static final Alignment LEFT = new Alignment("left");
    public static final Alignment RIGHT = new Alignment("right");

    private Alignment(String attributeValue) {
      this.attributeValue = attributeValue;
    }

    public String getAttributeValue() {
      return attributeValue;
    }
  }

  Alignment getAlignment();
}

package com.tonbeller.jpivot.olap.model;

public class EmptyMember implements Displayable {

  public String getLabel() {
    return "";
  }

  public void accept(Visitor visitor) {
    visitor.visitEmptyMember(this);
  }

}

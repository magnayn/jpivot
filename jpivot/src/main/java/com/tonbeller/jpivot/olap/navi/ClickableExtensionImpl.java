package com.tonbeller.jpivot.olap.navi;

import java.util.Collection;
import java.util.Collections;

import com.tonbeller.jpivot.core.ExtensionSupport;

public class ClickableExtensionImpl extends ExtensionSupport implements ClickableExtension {
  Collection clickables = Collections.EMPTY_LIST;
  public String getId() {
    return ID;
  }
  public Collection getClickables() {
    return clickables;
  }

  public void setClickables(Collection clickables) {
    this.clickables = clickables;
  }

}
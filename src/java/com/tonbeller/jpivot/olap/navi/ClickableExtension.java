package com.tonbeller.jpivot.olap.navi;

import java.util.Collection;

import com.tonbeller.jpivot.core.Extension;

public interface ClickableExtension extends Extension {
  public static final String ID = "clickable";
  Collection getClickables();
  void setClickables(Collection clickables);
}

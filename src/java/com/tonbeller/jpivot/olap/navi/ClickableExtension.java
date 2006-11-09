package com.tonbeller.jpivot.olap.navi;

import java.util.Collection;

import com.tonbeller.jpivot.core.Extension;
import com.tonbeller.jpivot.table.ClickableMember;

public interface ClickableExtension extends Extension {
  public static final String ID = "clickable";
  /**
   * List of ClickableMember
   * @see ClickableMember
   */
  Collection getClickables();
  /**
   * List of ClickableMember
   * @see ClickableMember
   */
  void setClickables(Collection clickables);
}

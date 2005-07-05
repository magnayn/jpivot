/*
 * Created on 09.10.2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.tonbeller.jpivot.table.span;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.MemberPropertyMeta;
import com.tonbeller.jpivot.olap.navi.MemberProperties;


class TestMemberProperties extends ExtensionSupport implements MemberProperties {
  boolean levelScope;
  public MemberPropertyMeta[] getMemberPropertyMetas(Level level) {
    return null;
  }
  public boolean isLevelScope() {
    return levelScope;
  }
  public void setLevelScope(boolean levelScope) {
    this.levelScope = levelScope;
  }
  public String getPropertyScope(Member m) {
    if (levelScope)
      return m.getLevel().getLabel();
    return m.getLevel().getHierarchy().getLabel();
  }
  public String getPropertyScope(Level l) {
    if (levelScope)
      return l.getLabel();
    return l.getHierarchy().getLabel();
  }
  public void setVisibleProperties(MemberPropertyMeta[] props) {
    // ignored
  }

}
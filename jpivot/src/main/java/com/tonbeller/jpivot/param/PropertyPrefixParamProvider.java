/*
 * Created on 14.12.2004
 */
package com.tonbeller.jpivot.param;

import java.util.List;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.wcf.param.SessionParam;

/**
 * Default implementation for ParameterProvider. It uses the SqlAccess
 * Extension of the Olap Model.
 * @see com.tonbeller.jpivot.param.SqlAccess
 */
public class PropertyPrefixParamProvider extends AbstractParamProvider {
  String propertyPrefix;

  /**
   * creates a collection of SessionParam from member properties. Every member property,
   * whose name starts with <code>propertyPrefix</code> will create a SessionParam.
   * The name of the SessionParam is the rest of the property name after the
   * prefix, the SQL value is the value of the property, the MDX value will be
   * the member.
   *
   * @see com.tonbeller.jpivot.param.SqlAccess
   * @see com.tonbeller.wcf.param.SessionParam
   *
   * @param propertyPrefix the prefix of the properties that become SessionParam's.
   */
  public PropertyPrefixParamProvider(String propertyPrefix) {
    this.propertyPrefix = propertyPrefix;
  }

  protected void addMemberParams(List list, SqlAccess sa, Member member) {
    int prefixLength = propertyPrefix.length();
    Property[] p = member.getProperties();
    for (int i = 0; i < p.length; i++) {
      String propertyName = p[i].getName();
      if (!propertyName.startsWith(propertyPrefix))
        continue;
      String paramName = propertyName.substring(prefixLength);
      SessionParam sp = sa.createParameter(member, paramName, propertyName);
      if (sp != null)  // !calculated, !all
        list.add(sp);
    }
  }

}
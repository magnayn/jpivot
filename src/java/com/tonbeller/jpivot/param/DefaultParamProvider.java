/*
 * Created on 14.12.2004
 */
package com.tonbeller.jpivot.param;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.wcf.param.SessionParam;


/**
 * Default implementation for ParameterProvider. It uses the SqlAccess
 * Extension of the Olap Model.
 * @see com.tonbeller.jpivot.param.SqlAccess
 */
public class DefaultParamProvider implements ParameterProvider {
  String paramName;
  String propertyName;
  String propertyPrefix;

  /**
   * creates the Parameter from the member only, a member property or
   * a set of member properties
   * @param paramName if present, propertyPrefix must be null. If propertyName is not null,
   * a SessionParam is created from that property. I propertyName is null, a
   * SessionParam is created from that member.
   * @param propertyName the name of the property whose value should be taken into
   * the SessionParam instead of the members value.
   * @param propertyPrefix if non null, paramName and propertyName must be null. In
   * this case
   * @see com.tonbeller.jpivot.param.SqlAccess
   */
  private DefaultParamProvider(String paramName, String propertyName, String propertyPrefix) {
    this.paramName = paramName;
    this.propertyName = propertyName;
    this.propertyPrefix = propertyPrefix;
  }

  /**
   * creates a SessionParam from a member
   * @see com.tonbeller.jpivot.param.SqlAccess
   * @see com.tonbeller.wcf.param.SessionParam
   *
   * @param paramName name of the parameter
   * @return
   */
  public static ParameterProvider createMemberInstance(String paramName) {
    return new DefaultParamProvider(paramName, null, null);
  }

  /**
   * creates a SessionParam from a member property. The SQL Value will be the
   * value of the property, the MDX value will be the member.
   * @see com.tonbeller.jpivot.param.SqlAccess
   * @see com.tonbeller.wcf.param.SessionParam
   *
   * @param paramName name of the parameter
   * @param propertyName name of the member property whose value will become the SQL value of the parameter
   * @return
   */
  public static ParameterProvider createPropertyInstance(String paramName, String propertyName) {
    return new DefaultParamProvider(paramName, propertyName, null);
  }

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
   * @return
   */
  public static ParameterProvider createPropertyPrefixInstance(String propertyPrefix) {
    return new DefaultParamProvider(null, null, propertyPrefix);
  }

  public Collection createSessionParams(OlapModel model, Member member) {
    SqlAccess sa = (SqlAccess) model.getExtension(SqlAccess.ID);
    return createSessionParams(sa, member);
  }

  public Collection createSessionParams(SqlAccess sa, Member member) {
    if (sa == null)
      return Collections.EMPTY_LIST;

    List result = new ArrayList();
    if (paramName != null) {
      // create a single Parameter
      if (propertyName == null)
        result.add(sa.createParameter(member, paramName));
      else
        result.add(sa.createParameter(member, paramName, propertyName));
    } else if (propertyPrefix != null) {
      int prefixLength = propertyPrefix.length();
      Property [] p = member.getProperties();
      for (int i = 0; i < p.length; i++) {
        String propertyName = p[i].getName();
        if (!propertyName.startsWith(propertyPrefix))
          continue;
        String paramName = propertyName.substring(prefixLength);
        SessionParam sp = sa.createParameter(member, paramName, propertyName);
        result.add(sp);
      }
    } else
      throw new IllegalArgumentException("either paramName or propertyPrefix must be present");
    return result;
  }

}
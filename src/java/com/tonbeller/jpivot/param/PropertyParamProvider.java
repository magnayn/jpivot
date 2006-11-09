/*
 * Created on 14.12.2004
 */
package com.tonbeller.jpivot.param;

import java.util.List;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.wcf.param.SessionParam;

/**
 * creates a SessionParam from a member property
 * @see com.tonbeller.jpivot.param.SqlAccess
 * @see com.tonbeller.wcf.param.SessionParam
 *
 */
public class PropertyParamProvider extends AbstractParamProvider {
  private String paramName;
  private String propertyName;

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

  public PropertyParamProvider(String paramName, String propertyName) {
    this.paramName = paramName;
    this.propertyName = propertyName;
  }

  protected void addMemberParams(List list, SqlAccess sa, Member member) {
    SessionParam param = sa.createParameter(member, paramName, propertyName);
    if (param != null) // !calculated, !all
      list.add(param);
  }

}
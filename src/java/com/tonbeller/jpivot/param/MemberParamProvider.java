/*
 * Created on 14.12.2004
 */
package com.tonbeller.jpivot.param;

import java.util.List;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.wcf.param.SessionParam;

/**
 * creates a SessionParam from a member
 * @see com.tonbeller.jpivot.param.SqlAccess
 * @see com.tonbeller.wcf.param.SessionParam
 *
 */
public class MemberParamProvider extends AbstractParamProvider {
  String paramName;

  /**
   * creates a SessionParam from a member
   * @see com.tonbeller.jpivot.param.SqlAccess
   * @see com.tonbeller.wcf.param.SessionParam
   *
   * @param paramName name of the parameter
   * @return
   */

  public MemberParamProvider(String paramName) {
    this.paramName = paramName;
  }

  protected void addMemberParams(List list, SqlAccess sa, Member member) {
    SessionParam param = sa.createParameter(member, paramName);
    if (param != null) // !calculated, !all
      list.add(param);
  }

}
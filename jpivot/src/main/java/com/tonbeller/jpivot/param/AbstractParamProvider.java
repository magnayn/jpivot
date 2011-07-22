/*
 * Copyright (c) 1971-2003 TONBELLER AG, Bensheim.
 * All rights reserved.
 */
package com.tonbeller.jpivot.param;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.OlapModel;

public abstract class AbstractParamProvider implements ParameterProvider {

  public List createSessionParams(OlapModel model, Member member) throws OlapException {
    SqlAccess sa = (SqlAccess) model.getExtension(SqlAccess.ID);
    if (sa == null)
      return Collections.EMPTY_LIST;
    List list = new ArrayList();
    addMemberParams(list, sa, member);
    return list;
  }

  protected abstract void addMemberParams(List list, SqlAccess sa, Member member);
}

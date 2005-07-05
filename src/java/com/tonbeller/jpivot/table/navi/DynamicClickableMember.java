package com.tonbeller.jpivot.table.navi;

import com.tonbeller.jpivot.param.ParameterProvider;

/**
 * a clickable member with query scope. The DynamicClickableMember is attached to the
 * Query - its lifecycle ends when the query is replaced.
 * 
 * @author av
 * @since 15.12.2004
 */
public class DynamicClickableMember extends ClickableMemberSupport {
  public DynamicClickableMember(String uniqueName, String urlPattern, String page,
      ParameterProvider parameterProvider) {
    super(uniqueName, urlPattern, page, parameterProvider);
  }
}
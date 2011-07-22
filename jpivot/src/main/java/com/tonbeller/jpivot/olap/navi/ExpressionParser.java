/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 *
 * 
 */
package com.tonbeller.jpivot.olap.navi;

import com.tonbeller.jpivot.core.Extension;
import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Expression;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Level;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.util.JPivotException;

/**
 * serialisiert expr. Wird zunaechst nur
 * fuer unique names von Member, Hierarchy, Dimension, Level
 * benutzt.
 */

public interface ExpressionParser extends Extension {
  public static final String ID = "expressionParser";

  class InvalidSyntaxException extends JPivotException {
    public InvalidSyntaxException() {
    }

    public InvalidSyntaxException(String message) {
      super(message);
    }

    public InvalidSyntaxException(String message, Throwable cause) {
      super(message, cause);
    }

    public InvalidSyntaxException(Throwable cause) {
      super(cause);
    }
  }

  String unparse(Expression expr);

  Expression parse(String expr) throws InvalidSyntaxException;

  /**
   * typespecific lookup because Mondrians unique names are not unique. 
   * [Measures] is the unique name for both, Hierarchy and Dimension
   */
  Member lookupMember(String uniqueName) throws InvalidSyntaxException;

  /**
   * typespecific lookup because Mondrians unique names are not unique. 
   * [Measures] is the unique name for both, Hierarchy and Dimension.
   * much faster than lookupMember or parse(uname) because it does not have to look into the DB.
   */
  Level lookupLevel(String uniqueName) throws InvalidSyntaxException;

  /**
   * typespecific lookup because Mondrians unique names are not unique. 
   * [Measures] is the unique name for both, Hierarchy and Dimension
   * much faster than lookupMember or parse(uname) because it does not have to look into the DB.
   */
  Hierarchy lookupHierarchy(String uniqueName) throws InvalidSyntaxException;

  /**
   * typespecific lookup because Mondrians unique names are not unique. 
   * [Measures] is the unique name for both, Hierarchy and Dimension
   * much faster than lookupMember or parse(uname) because it does not have to look into the DB.
   */
  Dimension lookupDimension(String uniqueName) throws InvalidSyntaxException;
}

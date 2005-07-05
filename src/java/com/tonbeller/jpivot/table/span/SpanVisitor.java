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
package com.tonbeller.jpivot.table.span;

import com.tonbeller.jpivot.olap.model.Visitor;

/**
 * Visitor for Span.getObject()
 * @author av
 */
public interface SpanVisitor extends Visitor {

  void visitPropertyHeading(PropertyHeading heading);

}

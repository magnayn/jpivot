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
package com.tonbeller.jpivot.table;

import org.w3c.dom.Element;

import com.tonbeller.jpivot.olap.model.Member;

/**
 * Created on 18.10.2002
 * 
 * @author av
 */
public abstract class SlicerBuilderDecorator extends PartBuilderDecorator implements SlicerBuilder {
  
  public SlicerBuilderDecorator(SlicerBuilder delegate) {
    super(delegate);
  }
  
  public Element build(Member m) {
    return ((SlicerBuilder)delegate).build(m);
  }

}

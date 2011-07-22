package com.tonbeller.jpivot.table;

import org.w3c.dom.Element;

import com.tonbeller.jpivot.table.span.Span;

public interface AxisHeaderBuilder {

  void build(Element row, Span span, int rowspan, int colspan, boolean even, boolean memberIndent);

}

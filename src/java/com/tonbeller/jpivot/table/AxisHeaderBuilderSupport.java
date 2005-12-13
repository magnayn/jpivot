package com.tonbeller.jpivot.table;

import org.w3c.dom.Element;

import com.tonbeller.jpivot.olap.model.Alignable;
import com.tonbeller.jpivot.table.span.Span;

public class AxisHeaderBuilderSupport implements AxisHeaderBuilder {

  private SpanBuilder spanBuilder;

  public AxisHeaderBuilderSupport(SpanBuilder spanBuilder) {
    this.spanBuilder = spanBuilder;
  }

  public void build(Element row, Span span, int rowspan, int colspan, boolean even, boolean memberIndent) {
    Element elem = spanBuilder.build(span, even);
    elem.setAttribute("rowspan", Integer.toString(rowspan));
    elem.setAttribute("colspan", Integer.toString(colspan));

    // no special formatting present?
    if (elem.getAttribute("style").length() == 0) {
      // valid styles are { span, even, odd, span-right, even-right, odd-right}
      String style;
      if (colspan > 1 || rowspan > 1)
        style = "span";
      else if (even)
        style = "even";
      else
        style = "odd";
      if (isRightAligned(span))
        style = style + "-right";
      elem.setAttribute("style", style);
    }

    // indent level for hierarchy style view
    if (memberIndent && span.isMember()) {
      elem.setAttribute("indent", Integer.toString(span.getIndent()));
    }

    row.appendChild(elem);
  }

  private boolean isRightAligned(Span span) {
    Object obj = span.getObject();
    if (obj instanceof Alignable)
      return ((Alignable) obj).getAlignment() == Alignable.Alignment.RIGHT;
    return false;
  }

}

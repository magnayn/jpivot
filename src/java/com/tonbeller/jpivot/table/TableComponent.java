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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.MissingResourceException;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tonbeller.jpivot.core.ModelChangeEvent;
import com.tonbeller.jpivot.core.ModelChangeListener;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Cell;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.ClickableExtension;
import com.tonbeller.jpivot.table.span.PropertyConfig;
import com.tonbeller.tbutils.res.Resources;
import com.tonbeller.wcf.component.Component;
import com.tonbeller.wcf.component.ComponentSupport;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.controller.RequestListener;
import com.tonbeller.wcf.utils.XmlUtils;

/**
 * Contains a reference to the olap data plus additional gui settings.
 * Renders the pivot table
 */
public class TableComponent extends ComponentSupport implements ModelChangeListener {
  private static Logger logger = Logger.getLogger(TableComponent.class);

  // configurable options from config.xml
  CellBuilder cellBuilder;
  CornerBuilder cornerBuilder;
  SlicerBuilder slicerBuilder;
  RowAxisBuilder rowAxisBuilder;
  ColumnAxisBuilder columnAxisBuilder;
  
  Resources resources;
  
  // extensions, map for scripting, list for ordered initializing
  List extensionList = new ArrayList();
  Map extensionMap = new HashMap();

  // initialized from tag  
  OlapModel olapModel;

  Document document;

  /** valid between startBuild() and stopBuild() */   
  Result result;
  /** valid between startBuild() and stopBuild() */   
  Iterator cellIterator;
  /** valid between startBuild() and stopBuild() */   
  int dimCount;
  /** valid between startBuild() and stopBuild() */   
  Element rootElement;

  public TableComponent(String id, Component parent) {
    super(id, parent);
  }

  public TableComponent(String id, Component parent, OlapModel newOlapModel) {
    super(id, parent);
    logger.info("TableComponent");
    this.olapModel = newOlapModel;
    olapModel.addModelChangeListener(this);
  }

  /**
   * for instantiation via reflection api.
   * @see #setOlapModel
   */
  public TableComponent() {
    super(null, null);
  }

  public void setOlapModel(OlapModel newOlapModel) {
    logger.info("setOlapModel");
    if (olapModel != null)
      olapModel.removeModelChangeListener(this);
    olapModel = newOlapModel;
    olapModel.addModelChangeListener(this);
  }

  /**
   * deferred ctor called once by the creating tag
   */
  public void initialize(RequestContext context) throws Exception {
    logger.info("initialize");
    super.initialize(context);
    resources = context.getResources(TableComponent.class);
    for (Iterator it = extensionList.iterator(); it.hasNext();)
       ((TableComponentExtension) it.next()).initialize(context, this);
    columnAxisBuilder.initialize(context, this);
    rowAxisBuilder.initialize(context, this);
    cellBuilder.initialize(context, this);
    cornerBuilder.initialize(context, this);
    slicerBuilder.initialize(context, this);
  }

  /**
   * deferred ctor called once by the creating tag
   */
  public void destroy(HttpSession session) throws Exception {
    logger.info("destroy");
    slicerBuilder.destroy(session);
    cornerBuilder.destroy(session);
    cellBuilder.destroy(session);
    rowAxisBuilder.destroy(session);
    columnAxisBuilder.destroy(session);
    for (Iterator it = extensionList.iterator(); it.hasNext();)
       ((TableComponentExtension) it.next()).destroy(session);
    super.destroy(session);
  }

  public Object retrieveBookmarkState(int levelOfDetail) {
    logger.info("retrieveBookmarkState");
    Map map = (Map) super.retrieveBookmarkState(levelOfDetail);
    map.put("slicerBuilder", slicerBuilder.retrieveBookmarkState(levelOfDetail));
    map.put("cornerBuilder", cornerBuilder.retrieveBookmarkState(levelOfDetail));
    map.put("cellBuilder", cellBuilder.retrieveBookmarkState(levelOfDetail));
    map.put("rowAxisBuilder", rowAxisBuilder.retrieveBookmarkState(levelOfDetail));
    map.put("columnAxisBuilder", columnAxisBuilder.retrieveBookmarkState(levelOfDetail));
    for (Iterator it = extensionList.iterator(); it.hasNext();) {
      TableComponentExtension tce = (TableComponentExtension) it.next();
      map.put(tce.getId(), tce.retrieveBookmarkState(levelOfDetail));
    }
    return map;
  }

  public void setBookmarkState(Object state) {
    if (state == null)
      return;
    logger.info("setBookmarkState");
    super.setBookmarkState(state);
    Map map = (Map) state;
    slicerBuilder.setBookmarkState(map.get("slicerBuilder"));
    cornerBuilder.setBookmarkState(map.get("cornerBuilder"));
    cellBuilder.setBookmarkState(map.get("cellBuilder"));
    rowAxisBuilder.setBookmarkState(map.get("rowAxisBuilder"));
    columnAxisBuilder.setBookmarkState(map.get("columnAxisBuilder"));
    for (Iterator it = extensionList.iterator(); it.hasNext();) {
      TableComponentExtension tce = (TableComponentExtension) it.next();
      tce.setBookmarkState(map.get(tce.getId()));
    }
  }
  
  public Iterator clickableIterator() {
    ClickableExtension ce = (ClickableExtension) olapModel.getExtension(ClickableExtension.ID);
    if (ce == null)
      return Collections.EMPTY_LIST.iterator();
    return ce.getClickables().iterator();
  }

  public void request(RequestContext context) throws Exception {
    super.request(context);
    for (Iterator it = clickableIterator(); it.hasNext();)
      ((RequestListener)it.next()).request(context);
  }
  
  /**
   * notifies PartBuilders that a new DOM will be created
   */
  private void startBuild(RequestContext context) {
    logger.info("enter startBuild");
    columnAxisBuilder.startBuild(context);
    rowAxisBuilder.startBuild(context);
    cellBuilder.startBuild(context);
    cornerBuilder.startBuild(context);
    slicerBuilder.startBuild(context);
    for (Iterator it = extensionList.iterator(); it.hasNext();)
       ((TableComponentExtension) it.next()).startBuild(context);
    for (Iterator it = clickableIterator(); it.hasNext();)
      ((ClickableMember) it.next()).startRendering(context, this);
    logger.info("leave startBuild");
  }

  /**
   * notifies PartBuilders that a new DOM has been created
   */
  private void stopBuild() {
    logger.info("enter stopBuild");
    for (Iterator it = extensionList.iterator(); it.hasNext();)
       ((TableComponentExtension) it.next()).stopBuild();
    slicerBuilder.stopBuild();
    cornerBuilder.stopBuild();
    cellBuilder.stopBuild();
    rowAxisBuilder.stopBuild();
    columnAxisBuilder.stopBuild();
    for (Iterator it = clickableIterator(); it.hasNext();)
      ((ClickableMember) it.next()).stopRendering();
    
    // reduce memory usage
    result = null;
    cellIterator = null;
    
    logger.info("leave stopBuild");
    
  }

  /**
   * main entry point
   */
  public Document render(RequestContext context) throws Exception {
    logger.info("render");
    if (document == null) {
      logger.info("creating document");
      long t1 = System.currentTimeMillis();
      document = XmlUtils.createDocument();
      Element elem = render2(context);
      document.appendChild(elem);
      if (logger.isInfoEnabled()) {
        long t2 = System.currentTimeMillis();
        logger.info("Rendering of Table took " + (t2 - t1) + " millisec");
      }
    }
    return document;
  }
  
  protected Result updateOlapModel() throws Exception {
    return olapModel.getResult();
  }

  private Element render2(RequestContext context) throws Exception {
    logger.info("render2");
    this.result = updateOlapModel();
    this.cellIterator = result.getCells().iterator();
    this.dimCount = result.getAxes().length;

    rootElement = document.createElement("mdxtable");
    Element head = append("head", rootElement);
    Element body = append("body", rootElement);
    
    startBuild(context);

    switch (dimCount) {
      case 0 :
        logger.info("0-dim data");
        buildRows0Dim(body);
        break;
      case 1 :
        logger.info("1-dim data");
        buildColumns1Dim(head);
        buildRows1Dim(body);
        break;
      case 2 :
        logger.info("2-dim data");
        buildColumns2Dim(head);
        buildRows2Dim(body);
        break;
      default :
        logger.error("more than 2 dimensions");
        throw new IllegalArgumentException("TableRenderer requires 0, 1 or 2 dimensional result");
    }

    rootElement.appendChild(buildSlicer());

    stopBuild();

    return rootElement;
  }

  private Element buildSlicer() {
    logger.info("buildSlicer");
    Element slicer = elem("slicer");
    // Was there at least one slicer
    boolean gotOne = false;
    Iterator pi = getResult().getSlicer().getPositions().iterator();
    while (pi.hasNext()) {
      Position p = (Position) pi.next();
      Member[] members = p.getMembers();
      for (int i = 0; i < members.length; i++) {
        Element e = slicerBuilder.build(members[i]);
        slicer.appendChild(e);
        gotOne = true;
      }
    }
    if (! gotOne) {
        // No slicer
        Element empty = elem("empty");
        String v = null;
        try {
            v = resources.getString("table.slicer.empty"); 
        } catch (MissingResourceException ex) {
            v = "EMPTY";
        }
        empty.setAttribute("value", v);
        slicer.appendChild(empty);
    }
    return slicer;
  }

  /* ---------------------- 0 dim ------------------------------- */

  private void buildRows0Dim(Element parent) {
    logger.info("buildRows0Dim");
    // if result is empty, dont show anything
    if (!cellIterator.hasNext())
      return;
    
    Element row = append("row", parent);
    Cell cell = (Cell) cellIterator.next();
    Element cellElem = cellBuilder.build(cell, true);
    row.appendChild(cellElem);
  }

  /* ---------------------- 1 dim ------------------------------- */

  private void buildRows1Dim(Element parent) {
    logger.info("buildRows1Dim");
    Element row = append("row", parent);
    buildCells(row, false);
  }

  private void buildColumns1Dim(Element parent) {
    logger.info("buildColumns1Dim");
    final int N = columnAxisBuilder.getRowCount();
    for (int i = 0; i < N; i++) {
      Element row = append("row", parent);
      columnAxisBuilder.buildRow(row, i);
    }
  }

  /* ---------------------- 2 dim ------------------------------- */

  private void buildCornerElement(Element parent, int colSpan, int rowSpan) {
    Element corner = cornerBuilder.build(colSpan, rowSpan);
    parent.appendChild(corner);
  }

  /**
   * <pre>
   * C = column axis
   * R = row axis
   * H = row axis heading
   * X = corner element
   *
   * Case 1 (C &lt; H), corner element on top of column axis
   *  
   * H H H X X X
   * H H H C C C
   * R R R 1 2 3
   * R R R 3 4 5
   *
   * Case 2 (C &gt; H), corner element in the left upper corner
   *  
   * X X X C C C
   * H H H C C C
   * R R R 1 2 3
   * R R R 3 4 5
   *
   * Case 3 (C == H), no corner element
   *  
   * H H H C C C
   * H H H C C C
   * R R R 1 2 3
   * R R R 3 4 5
   * </pre>
   */

  private void buildColumns2Dim(Element parent) {
    logger.info("enter buildColumns2Dim");
    int colAxisCount = columnAxisBuilder.getRowCount();
    int rowAxisCount = rowAxisBuilder.getHeaderRowCount();
    int colAxisIndex = 0;
    int rowAxisIndex = 0;
    
    if (logger.isInfoEnabled())
      logger.info("colAxisCount = " + colAxisCount + ", rowAxisCount = " + rowAxisCount);

    if (rowAxisCount > colAxisCount) {
      logger.info("rowAxisCount > colAxisCount");
      // case 1
      int N = rowAxisCount - colAxisCount;
      Element row = append("row", parent);
      rowAxisBuilder.buildHeaderRow(row, rowAxisIndex++);
      buildCornerElement(row, columnAxisBuilder.getColumnCount(), N);
      for (int i = 1; i < N; i++) {
        row = append("row", parent);
        rowAxisBuilder.buildHeaderRow(row, rowAxisIndex++);
      }
      // number of rows left to add
      rowAxisCount -= N;
    } else if (colAxisCount > rowAxisCount) {
      logger.info("colAxisCount > rowAxisCount");
      // case 2
      int N = colAxisCount - rowAxisCount;
      Element row = append("row", parent);
      buildCornerElement(row, rowAxisBuilder.getColumnCount(), N);
      columnAxisBuilder.buildRow(row, colAxisIndex++);
      for (int i = 1; i < N; i++) {
        row = append("row", parent);
        columnAxisBuilder.buildRow(row, colAxisIndex++);
      }
      // number of rows left to add
      colAxisCount -= N;
    }
    
    logger.info("building cells");
    // case 3
    // assert(colAxisCount == rowAxisCount)
    for (int i = 0; i < colAxisCount; i++) {
      Element row = append("row", parent);
      rowAxisBuilder.buildHeaderRow(row, rowAxisIndex++);
      columnAxisBuilder.buildRow(row, colAxisIndex++);
    }
    logger.info("leave buildColumns2Dim");
  }

  private void buildRows2Dim(Element parent) {
    logger.info("enter buildRows2Dim");
    final int cellCountLimit = Integer.getInteger(
        com.tonbeller.jpivot.mondrian.MondrianModel.CELL_LIMIT_PROP,
        com.tonbeller.jpivot.mondrian.MondrianModel.CELL_LIMIT_DEFAULT).intValue();

    final int nosRows = rowAxisBuilder.getRowCount();
    final int nosColumns = columnAxisBuilder.getColumnCount();
    if (logger.isDebugEnabled()) {
        StringBuffer buf = new StringBuffer();
        buf.append("buildRows2Dim: cellCountLimit=");
        buf.append(cellCountLimit);
        buf.append(", nosColumns=");
        buf.append(nosColumns);
        buf.append(", nosRows=");
        buf.append(nosRows);
        logger.debug(buf.toString());
    }
    if ((cellCountLimit > 0) && (cellCountLimit < nosColumns*nosRows)) {
        int nr = (cellCountLimit / nosColumns) + 1;
        if (logger.isDebugEnabled()) {
            StringBuffer buf = new StringBuffer();
            buf.append("buildRows2Dim: number of rows=");
            buf.append(nr);
            logger.debug(buf.toString());
        }

        for (int i = 0; i < nr; i++) {
          boolean even = (i % 2 == 0);
          Element row = append("row", parent);
          rowAxisBuilder.buildRow(row, i);
          buildCells(row, even);
        }

        Element row = append("row", parent);
        rowAxisBuilder.buildRow(row, nr);
        Element cellElem = elem("cellspan");
        String v = null;
        try {
            v = resources.getString("table.cell.limit", 
                                   new Integer(cellCountLimit),
                                   new Integer(nosColumns*nosRows)
                                   );
        } catch (MissingResourceException ex) {
            v = "Too many cells (cell limit:" + cellCountLimit + ")";
        }
        cellElem.setAttribute("value", v);
        cellElem.setAttribute("colspan", Integer.toString(nosColumns));
        row.appendChild(cellElem);

    } else {

        for (int i = 0; i < nosRows; i++) {
          boolean even = (i % 2 == 0);
          Element row = append("row", parent);
          rowAxisBuilder.buildRow(row, i);
          buildCells(row, even);
        }

    }

    logger.info("leave buildRows2Dim");
  }

  /* ---------------------- common ------------------------------- */

  private void buildCells(Element row, boolean even) {
    final int N = columnAxisBuilder.getColumnCount();
    for (int i = 0; i < N; i++) {
      try {
        Cell cell = (Cell) cellIterator.next();
        Element cellElem = cellBuilder.build(cell, even);
        row.appendChild(cellElem);
      } catch (NoSuchElementException e) {
        logger.error("not enough cells", e);
        e.printStackTrace();
      }

    }
  }

  /* ----------------------- utilities -------------------------- */

  /**
   * utility - creates an element with the given name
   */
  public Element elem(String name) {
    return document.createElement(name);
  }

  /**
   * utility - creates an element and appends it
   */
  public Element append(String name, Element parent) {
    Element elem = document.createElement(name);
    parent.appendChild(elem);
    return elem;
  }

  private void firstChild(Element child, Element parent) {
    Node before = parent.getFirstChild();
    if (before != null)
      parent.insertBefore(child, before);
    else
      parent.appendChild(child);
  }

  /**
   * utility - creates an element an inserts it before the first child
   */
  public Element insert(String name, Element parent) {
    Element elem = document.createElement(name);
    firstChild(elem, parent);
    return elem;
  }

  /**
   * utility - creates a CDATA section
   */
  public Object cdata(String content, Element parent) {
    CDATASection section = document.createCDATASection(content);
    parent.appendChild(section);
    return section;
  }

  /* ----------------------- properties -------------------------------- */

  public OlapModel getOlapModel() {
    return olapModel;
  }

  /**
   * registers an extension. Used at creation time before initialize() is
   * called
   */
  public void addExtension(TableComponentExtension extension) {
    extensionList.add(extension);
    extensionMap.put(extension.getId(), extension);
  }

  /**
   * provides access to the extensions thru JSP scripting
   */
  public Map getExtensions() {
    return extensionMap;
  }

  /**
   * true means that render() will create a new DOM
   */
  public boolean isDirty() {
    return document == null;
  }

  public void setDirty(boolean dirty) {
    document = null;
    // avoid memory leak
    result = null;
    cellIterator = null;
    rootElement = null;
  }

  public void modelChanged(ModelChangeEvent e) {
    setDirty(true);
  }

  public void structureChanged(ModelChangeEvent e) {
    setDirty(true);
  }

  /**
   * Returns the cellBuilder.
   * @return CellBuilder
   */
  public CellBuilder getCellBuilder() {
    return cellBuilder;
  }

  /**
   * Returns the columnAxisBuilder.
   * @return ColumnAxisBuilder
   */
  public ColumnAxisBuilder getColumnAxisBuilder() {
    return columnAxisBuilder;
  }

  /**
   * Returns the cornerBuilder.
   * @return CornerBuilder
   */
  public CornerBuilder getCornerBuilder() {
    return cornerBuilder;
  }

  /**
   * Returns the rowAxisBuilder.
   * @return RowAxisBuilder
   */
  public RowAxisBuilder getRowAxisBuilder() {
    return rowAxisBuilder;
  }

  /**
   * Returns the slicerBuilder.
   * @return SlicerBuilder
   */
  public SlicerBuilder getSlicerBuilder() {
    return slicerBuilder;
  }

  /**
   * Sets the cellBuilder.
   * @param cellBuilder The cellBuilder to set
   */
  public void setCellBuilder(CellBuilder cellBuilder) {
    this.cellBuilder = cellBuilder;
  }

  /**
   * Sets the columnAxisBuilder.
   * @param columnAxisBuilder The columnAxisBuilder to set
   */
  public void setColumnAxisBuilder(ColumnAxisBuilder columnAxisBuilder) {
    this.columnAxisBuilder = columnAxisBuilder;
  }

  /**
   * Sets the cornerBuilder.
   * @param cornerBuilder The cornerBuilder to set
   */
  public void setCornerBuilder(CornerBuilder cornerBuilder) {
    this.cornerBuilder = cornerBuilder;
  }

  /**
   * Sets the rowAxisBuilder.
   * @param rowAxisBuilder The rowAxisBuilder to set
   */
  public void setRowAxisBuilder(RowAxisBuilder rowAxisBuilder) {
    this.rowAxisBuilder = rowAxisBuilder;
  }

  /**
   * Sets the slicerBuilder.
   * @param slicerBuilder The slicerBuilder to set
   */
  public void setSlicerBuilder(SlicerBuilder slicerBuilder) {
    this.slicerBuilder = slicerBuilder;
  }

  /**
   * returns the current result
   */
  public Result getResult() {
    return result;
  }

  /**
   * returns the dimension count of the current result
   */
  public int getDimCount() {
    return dimCount;
  }

  /**
   * returns the root DOM element that is rendered 
   */
  public Element getRootElement() {
    return rootElement;
  }

  /**
   * returns the row axis or null
   */
  public Axis getRowAxis() {
    if (dimCount < 2)
      return null;
    return result.getAxes()[1];
  }

  /**
   * returns the column axis or null
   */
  public Axis getColumnAxis() {
    if (dimCount < 1)
      return null;
    return result.getAxes()[0];
  }

  /**
   * returns the property config object that allows
   * to adjust visible properties
   */
  public PropertyConfig getPropertyConfig() {
    return rowAxisBuilder.getAxisConfig().getPropertyConfig();
  }

}

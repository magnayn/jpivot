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
package com.tonbeller.jpivot.navigator.hierarchy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tonbeller.jpivot.core.ModelChangeEvent;
import com.tonbeller.jpivot.core.ModelChangeListener;
import com.tonbeller.jpivot.navigator.member.MemberSelectionModel;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.OlapException;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.Result;
import com.tonbeller.jpivot.olap.navi.ChangeSlicer;
import com.tonbeller.jpivot.olap.navi.MemberDeleter;
import com.tonbeller.jpivot.olap.navi.PlaceHierarchiesOnAxes;
import com.tonbeller.jpivot.olap.navi.PlaceMembersOnAxes;
import com.tonbeller.jpivot.ui.Available;
import com.tonbeller.tbutils.res.Resources;
import com.tonbeller.wcf.catedit.CategoryEditor;
import com.tonbeller.wcf.catedit.CategoryModelSupport;
import com.tonbeller.wcf.component.Component;
import com.tonbeller.wcf.controller.Dispatcher;
import com.tonbeller.wcf.controller.DispatcherSupport;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.controller.RequestListener;

/**
 * Navigation dialog
 *
 * @author av
 */
public class HierarchyNavigator extends CategoryEditor implements ModelChangeListener, Available {

  public class CancelHandler implements RequestListener {
    private boolean hide;

    public CancelHandler(boolean hide) {
      this.hide = hide;
    }

    public void request(RequestContext context) throws Exception {
      editing = false;
      // we will recreate everything on the next render()
      revert(context);
      if (hide)
        setVisible(false);
    }
  }

  public class OkHandler implements RequestListener {
    private boolean hide;

    public OkHandler(boolean hide) {
      this.hide = hide;
    }

    public void request(RequestContext context) throws Exception {
      editing = false;
      boolean valid = validate(context);

      // the following will fire multiple ModelChangeEvents
      for (Iterator it = categories.iterator(); it.hasNext();)
        ((AbstractCategory) it.next()).deleteDeleted();
      for (Iterator it = categories.iterator(); it.hasNext();)
        ((AbstractCategory) it.next()).prepareApplyChanges();
      for (Iterator it = categories.iterator(); it.hasNext();)
        ((AbstractCategory) it.next()).applyChanges();

      if (valid && hide)
        setVisible(false);
    }
  }

  private String acceptButtonId;
  private String cancelButtonId;
  private RequestListener acceptHandler;
  private RequestListener revertHandler;
  private String okButtonId;
  private String revertButtonId;
  private List categories = new ArrayList();
  private Resources resources;

  /**
   * after the user has started editing, the CategoryModel is no longer synchronized with the
   * OlapModel. This means, the user may do one or more changes, and then apply these changes
   * at once to the OlapModel.
   */
  private boolean editing = false;

  private HierarchyItemClickHandler hierarchyItemClickHandler;
  private OlapModel olapModel;
  private CategoryModelSupport categoryModel;
  private Dispatcher tempDispatcher = new DispatcherSupport();
  private SlicerCategory slicerCategory;

  private static Logger logger = Logger.getLogger(HierarchyNavigator.class);

  /**
   * Constructor for HierNavigator.
   */
  public HierarchyNavigator(String id, Component parent, OlapModel olapModel) {
    super(id, parent);

    logger.info("creating instance: " + this);

    acceptButtonId = id + ".accept";
    cancelButtonId = id + ".cancel";
    okButtonId = id + ".ok";
    revertButtonId = id + ".revert";

    this.olapModel = olapModel;
    olapModel.addModelChangeListener(this);

    acceptHandler = new OkHandler(false);
    revertHandler = new CancelHandler(false);
    super.getDispatcher().addRequestListener(acceptButtonId, null, acceptHandler);
    super.getDispatcher().addRequestListener(revertButtonId, null, revertHandler);
    super.getDispatcher().addRequestListener(okButtonId, null, new OkHandler(true));
    super.getDispatcher().addRequestListener(cancelButtonId, null, new CancelHandler(true));
    super.getDispatcher().addRequestListener(null, null, tempDispatcher);
    categoryModel = new CategoryModelSupport() {
      public List getCategories() {
        return categories;
      }
    };
    super.setModel(categoryModel);
    super.setItemRenderer(new HierarchyItemRenderer());
  }

  public void initialize(RequestContext context) throws Exception {
    super.initialize(context);
    resources = context.getResources(HierarchyNavigator.class);
  }

  /**
   * Returns the hierExtension.
   * @return PlaceHierarchiesOnAxes
   */
  public PlaceHierarchiesOnAxes getHierarchyExtension() {
    return (PlaceHierarchiesOnAxes) olapModel.getExtension(PlaceHierarchiesOnAxes.ID);
  }

  /**
   * Returns the hierarchyItemClickHandler.
   * @return HierarchyItemClickHandler
   */
  public HierarchyItemClickHandler getHierarchyItemClickHandler() {
    return hierarchyItemClickHandler;
  }

  /**
   * Returns the memberExtension.
   * @return PlaceMembersOnAxes
   */
  public PlaceMembersOnAxes getMemberExtension() {
    return (PlaceMembersOnAxes) olapModel.getExtension(PlaceMembersOnAxes.ID);
  }

  public MemberDeleter getDeleterExtension() {
    return (MemberDeleter) olapModel.getExtension(MemberDeleter.ID);
  }

  /**
   * Returns the olapModel.
   * @return OlapModel
   */
  public OlapModel getOlapModel() {
    return olapModel;
  }

  /**
   * Returns the slicerExtension.
   * @return ChangeSlicer
   */
  public ChangeSlicer getSlicerExtension() {
    return (ChangeSlicer) olapModel.getExtension(ChangeSlicer.ID);
  }

  /**
   * Returns the tempDispatcher.
   * @return Dispatcher
   */
  Dispatcher getTempDispatcher() {
    return tempDispatcher;
  }

  /**
   * initializes the CategoryModel to reflect the OlapModel
   */
  void initializeCategories() throws OlapException {
    categories.clear();

    Result result = olapModel.getResult();
    Axis[] axes = result.getAxes();
    for (int index = 0; index < axes.length; index++) {
      Axis axis = axes[index];
      String name = resources.getString("axis." + index + ".name");
      String icon = resources.getString("axis." + index + ".icon");
      AxisCategory axisCat = new AxisCategory(this, axis, name, icon);
      categories.add(axisCat);
    }

    // the rest is added to the slicer
    String name = resources.getString("slicer.name");
    String icon = resources.getString("slicer.icon");
    slicerCategory = new SlicerCategory(this, name, icon);
    categories.add(slicerCategory);
  }

  /**
   * true if the user has changed the axis/hierarchy mapping.
   * @return boolean
   */
  public boolean isEditing() {
    return editing;
  }

  void itemClicked(RequestContext context, HierarchyItem item, MemberSelectionModel selection,
      boolean allowChangeOrder) {
    if (hierarchyItemClickHandler != null)
      hierarchyItemClickHandler.itemClicked(context, item, selection, allowChangeOrder);
  }

  public Element render(RequestContext context, Document factory) throws Exception {
    if (!editing) {
      tempDispatcher.clear();
      initializeCategories();
    }

    Element elem = super.render(context, factory);

    elem.setAttribute("accept-id", acceptButtonId);
    elem.setAttribute("accept-title", resources.getString("accept.title"));
    elem.setAttribute("revert-id", revertButtonId);
    elem.setAttribute("revert-title", resources.getString("revert.title"));
    elem.setAttribute("ok-id", okButtonId);
    elem.setAttribute("ok-title", resources.getString("ok.title"));
    elem.setAttribute("cancel-id", cancelButtonId);
    elem.setAttribute("cancel-title", resources.getString("cancel.title"));

    return elem;
  }

  public void setEditing(boolean editing) {
    this.editing = editing;
  }

  /**
   * Sets the hierarchyItemClickHandler.
   * @param hierarchyItemClickHandler The hierarchyItemClickHandler to set
   */
  public void setHierarchyItemClickHandler(HierarchyItemClickHandler hierarchyItemClickHandler) {
    this.hierarchyItemClickHandler = hierarchyItemClickHandler;
  }

  public void modelChanged(ModelChangeEvent e) {
    // recreate everything on next render()
    editing = false;
  }

  public void structureChanged(ModelChangeEvent e) {
    logger.info("cleaning up");
    // force reload of members, hierarchies etc
    setEditing(false);
    tempDispatcher.clear();
    categories.clear();
    // invalidate hyperlinks
    categoryModel.fireModelChanged();
  }


  /**
   * finds the HierarchyItem for <code>hier</code>
   * @param hier the Hierarchy
   * @return null or the HierarchyItem
   */
  public HierarchyItem findHierarchyItem(Hierarchy hier) {
    for (Iterator ci = categoryModel.getCategories().iterator(); ci.hasNext();) {
      AbstractCategory ac = (AbstractCategory) ci.next();
      for (Iterator ii = ac.getItems().iterator(); ii.hasNext();) {
        HierarchyItem hi = (HierarchyItem) ii.next();
        if (hi.getHierarchy().equals(hier))
          return hi;
      }
    }
    return null;
  }

  public RequestListener getAcceptHandler() {
    return acceptHandler;
  }

  public RequestListener getRevertHandler() {
    return revertHandler;
  }

  /**
   * returns the set of dimensions that are currently on the
   * slicer axis. This includes those dimensions that the
   * user has moved to the slicer in the navigator but not yet
   * committed by pressing the OK button.
   *
   * @return empty set if this component has not been rendered yet
   */
  public Set getSlicerDimensions() {
    Set set = new HashSet();
    for (Iterator it = slicerCategory.getItems().iterator(); it.hasNext();) {
      HierarchyItem hi = (HierarchyItem) it.next();
      set.add(hi.getHierarchy().getDimension());
    }
    return set;
  }
  
  public Resources getRes() {
    return resources;
  }

  public boolean isAvailable() {
    return getHierarchyExtension() != null;
  }

}
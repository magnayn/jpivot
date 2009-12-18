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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.core.ModelChangeEvent;
import com.tonbeller.jpivot.core.ModelChangeListener;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.MemberPropertyMeta;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.impl.PropertyImpl;
import com.tonbeller.jpivot.olap.navi.MemberProperties;
import com.tonbeller.jpivot.ui.Available;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * adds Span elements to a SpanCalc containing Member Properties.
 * @author av
 */
public class PropertySpanBuilder implements PropertyConfig, ModelChangeListener, Available {

  private MemberProperties extension;
  private OlapModel model;
  
  private List[] propertyColumns;
  private int PCOUNT;
  private int HCOUNT;
  private SpanCalc spanCalc;
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private static Logger logger = Logger.getLogger(PropertySpanBuilder.class);

  /** List of all visible Properties - these may be filled in by some GUI */
  private List visiblePropertyMetas = null;
  private boolean showProperties = false;

  /**
   * for every property name contains an empty property with that name
   */
  private Map emptyPropertyMap = new HashMap();

  /**
   * for every hierarchyIndex contains the names of all properties of all members 
   */
  private ScopedPropertyMetaSet metaSet;

  private AllPropertiesMap allPropertiesMap;

  /** 
   * collects the normalized propertiy instances of all members.
   * <ol>
   *   <li>The map only includes top-level properties, 
   *        nested properties (properties of properties) are <em>not</em> included</li>
   *   <li>Inline properties, that result in decoration of the member 
   *       (like hyperlinks), are <em>not</em> included.</li>
   * </ol>
   */
  static class AllPropertiesMap {
    private Map map = new HashMap();
    private Set set = new HashSet();

    void add(Member member) {
      if (member == null)
        return;

      if (set.contains(member))
        return;
      set.add(member);

      Property[] props = member.getProperties();
      props = PropertyUtils.normalize(props);
      List list = new ArrayList();
      for (int i = 0; i < props.length; i++) {
        Property p = props[i];
        // ignore inline properties
        if (PropertyUtils.isInline(p.getName()))
          continue;
        list.add(p);
      }
      map.put(member, list);
    }

    List getProperties(Member member) {
      return (List) map.get(member);
    }
  }

  class PropertyLookup {
    Map map = new HashMap();
    void clear() {
      map.clear();
    }
    void addAll(Collection properties) {
      for (Iterator it = properties.iterator(); it.hasNext();) {
        Property p = (Property) it.next();
        map.put(p.getName(), p);
      }
    }
    Property getProperty(String name) {
      return (Property) map.get(name);
    }
  }

  /**
   * for testing only
   */
  PropertySpanBuilder(MemberProperties extension) {
    this.model = null;
    this.extension = extension;
  }

  public PropertySpanBuilder(OlapModel model) {
    this.model = model;
    this.extension = (MemberProperties) model.getExtension(MemberProperties.ID);
  }

  public void initialize(RequestContext context) {
    model.addModelChangeListener(this);
  }
  public void destroy(HttpSession session) {
    model.removeModelChangeListener(this);
  }
  
  public void modelChanged(ModelChangeEvent e) {
  }

  public void structureChanged(ModelChangeEvent e) {
    extension = (MemberProperties) model.getExtension(MemberProperties.ID);
    setVisiblePropertyMetas(null);
    setShowProperties(false);
  }

  public boolean isAvailable() {
    return model.getExtension(MemberProperties.ID) != null;
  }
  
  /** initializes fields */
  private void reset(SpanCalc spanCalc) {
    // initialize instance variables
    this.emptyPropertyMap.clear();
    this.allPropertiesMap = new AllPropertiesMap();
    this.metaSet = new ScopedPropertyMetaSet(extension);
    this.spanCalc = spanCalc;
    this.HCOUNT = spanCalc.getHierarchyCount();
    this.PCOUNT = spanCalc.getPositionCount();

    collectProperties();
    initializePropertyColumns();
  }

  /**
   * collects all properties and their metadata from result
   */
  void collectProperties() {
    Span[][] spans = spanCalc.getSpans();

    for (int hi = 0; hi < HCOUNT; hi++) {
      for (int pi = 0; pi < PCOUNT; pi++) {
        Span s = spans[pi][hi];
        if (s.isMember()) {
          Member m = s.getMember();
          metaSet.addMember(m);
          allPropertiesMap.add(m);
        }
      }
    }
  }

  /**
   * initializes the propertyColumns field. 
   * Computes the visible MemberPropertyMetas for
   * every column.
   */
  void initializePropertyColumns() {
    propertyColumns = new List[HCOUNT];
    for (int i = 0; i < HCOUNT; i++)
      propertyColumns[i] = Collections.EMPTY_LIST;

    // no positions -> no properties
    if (PCOUNT == 0)
      return;

    // for every column compute the set of
    // valid scopes, e.g. levels
    Set[] scopes = new Set[HCOUNT];
    Span[][] spans = spanCalc.getSpans();
    for (int hi = 0; hi < HCOUNT; hi++) {
      Set set = new HashSet();
      for (int pi = 0; pi < PCOUNT; pi++)
        set.add(getScope(spans[pi][hi]));
      scopes[hi] = set;
    }

    // for every column compute the visible MemberPropertyMetas
    Set done = new HashSet();
    for (int hi = 0; hi < HCOUNT; hi++) {
      // from all MemberPropertyMetas keep only those that match the scopes
      // of this column. Also remove all metas that denote inline properties.
      // Finally intersect with the list of all visible properties
      MemberPropertyMetaFilter scopesFilter = metaSet.createScopesFilter(scopes[hi]);
      MemberPropertyMetaFilter inlineFilter = metaSet.createIgnoreInlineFilter();

      if (hi == (HCOUNT - 1) || !scopes[hi].equals(scopes[hi + 1])) {
        ScopedPropertyMetaSet sub = metaSet.metaSet(scopesFilter);
        sub.removeAll(done);
        if (visiblePropertyMetas == null)
          propertyColumns[hi] = sub.metaList(inlineFilter);
        else
          propertyColumns[hi] = sub.intersectList(visiblePropertyMetas);
        done.addAll(propertyColumns[hi]);
      }
    }
  }

  Object getScope(Span s) {
    // if its not a member, the span itself is the scope
    if (!s.isMember())
      return s;
    return extension.getPropertyScope((Member) s.getMember().getRootDecoree());
  }

  /**
   * adds property columns to sc
   * @param sc the SpanCalc to modify
   */
  public void addPropertySpans(SpanCalc sc) {
    if (extension == null || !showProperties)
      return;
    logger.info("adding properties");

    // initialize the PropertyConfig model
    reset(sc);

    // compute result size
    int newHierCount = spanCalc.getHierarchyCount();
    for (int hi = 0; hi < HCOUNT; hi++)
      newHierCount += propertyColumns[hi].size();

    // create matrix
    Span[][] dst = new Span[PCOUNT][];
    for (int pi = 0; pi < PCOUNT; pi++)
      dst[pi] = new Span[newHierCount];
    Span[][] src = spanCalc.getSpans();

    // fill matrix
    PropertyLookup lookup = new PropertyLookup();
    for (int pi = 0; pi < PCOUNT; pi++) {
      int dstHierIndex = 0;
      lookup.clear();
      for (int hi = 0; hi < HCOUNT; hi++) {
        Span span = src[pi][hi];
        dst[pi][dstHierIndex++] = span;

        if (span.isMember())
          lookup.addAll(allPropertiesMap.getProperties(span.getMember()));

        // copy the properties
        Object scope = getScope(span);
        for (Iterator it = propertyColumns[hi].iterator(); it.hasNext();) {
          MemberPropertyMeta mpm = (MemberPropertyMeta) it.next();
          Property prop = null;
          if (mpm.getScope().equals(scope))
            prop = lookup.getProperty(mpm.getName());
          if (prop == null)
            prop = emptyProperty(mpm.getName(), mpm.getLabel());
          dst[pi][dstHierIndex++] = new Span(span.getAxis(), span.getPosition(), prop);
        }
      }
    }

    spanCalc.setSpans(dst);
  }

  /**
   * returns an empty Property instance for a given name. 
   * If queried multiple times for the same name, the same
   * object instance is returned (necessary for 
   * span computation when a single property spans multiple cells of the table axis).
   */
  Property emptyProperty(String name, String label) {
    Property p = (Property) emptyPropertyMap.get(name);
    if (p != null)
      return p;
    PropertyImpl pi = new PropertyImpl();
    pi.setName(name);
    pi.setLabel(label);
    pi.setValue("");
    emptyPropertyMap.put(name, pi);
    return pi;
  }

  /* ----------------------- PropertyConfig methods ------------------------------- */
  
  public boolean isShowProperties() {
    return showProperties;
  }

  public void setShowProperties(boolean b) {
    Object oldValue = new Boolean(showProperties);
    this.showProperties = b;
    setVisiblePropertiesExtension();
    Object newValue = new Boolean(showProperties);
    propertyChangeSupport.firePropertyChange("showProperties", oldValue, newValue);
  }

  public void setVisiblePropertyMetas(List metas) {
    Object oldValue = visiblePropertyMetas;
    this.visiblePropertyMetas = metas;
    setVisiblePropertiesExtension();
    Object newValue = visiblePropertyMetas;
    propertyChangeSupport.firePropertyChange("visiblePropertyMetas", oldValue, newValue);
  }

  public List getVisiblePropertyMetas() {
    return visiblePropertyMetas;
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    propertyChangeSupport.addPropertyChangeListener(l);
  }
  public void removePropertyChangeListener(PropertyChangeListener l) {
    propertyChangeSupport.removePropertyChangeListener(l);
  }

  public static class BookmarkState {
    boolean showProperties = false;
    List visibleProperties = null;
    public boolean isShowProperties() {
      return showProperties;
    }
    public List getVisibleProperties() {
      return visibleProperties;
    }
    public void setShowProperties(boolean b) {
      showProperties = b;
    }
    public void setVisibleProperties(List list) {
      visibleProperties = list;
    }
  }

  public Object retrieveBookmarkState(int levelOfDetail) {
    BookmarkState x = new BookmarkState();
    x.setShowProperties(isShowProperties());
    if (visiblePropertyMetas != null) {
      ArrayList metas = new ArrayList();
      metas.addAll(visiblePropertyMetas);
      x.setVisibleProperties(metas);
    }
    return x;
  }

  public void setBookmarkState(Object state) {
    if (!(state instanceof BookmarkState))
      return;
    BookmarkState x = (BookmarkState) state;
    setVisiblePropertyMetas(x.getVisibleProperties());
    setShowProperties(x.isShowProperties());
  }


  /**
   * updates the visibleProperties in the extension
   */
  protected void setVisiblePropertiesExtension() {
    if (extension == null)
      return;
    if (!showProperties || visiblePropertyMetas == null) {
      extension.setVisibleProperties(new MemberPropertyMeta[0]);
      return;
    }
    MemberPropertyMeta[] mps = new MemberPropertyMeta[visiblePropertyMetas.size()];
    mps = (MemberPropertyMeta[]) visiblePropertyMetas.toArray(mps);
    extension.setVisibleProperties(mps);
  }

  public List[] getAvailablePropertiesColumns() {
	if(metaSet == null) {
		return null;
	}
	MemberPropertyMetaFilter inlineFilter = metaSet.createAllFilter();
	List metaList = metaSet.metaList(inlineFilter);

	List[] availablePropertyColumns = new List[1];
	availablePropertyColumns[0] = metaList;
	return availablePropertyColumns;
  }

}

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

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;

/**
 * Calculates table spans for an axis. An axis is seen as a matrix of 
 * positions and hierarchies. For a row-axis, the positions are the rows, the hierarchies
 * are the columns, for a column-axis its vice versa. 
 * <p>
 * The cells of the matrix are <code>Span</code> instances.
 * 
 * @author av
 */
public class SpanCalc {
  private static final Logger logger = Logger.getLogger(SpanCalc.class);
  
  int positionCount, hierarchyCount;
  // index order is spans[positionIndex][hierarchyIndex]
  Span[][] spans;

  // true when spans have been calculated
  boolean initialized = false;

  SpanConfig config = new NoSpanConfig();

  /**
   * creates an instance
   */
  public SpanCalc(Span[][] spans) {
    this.spans = spans;
  }

  /**
   * creates an instance from an axis
   */
  public SpanCalc(Axis axis) {
    positionCount = axis.getPositions().size();
    if (positionCount > 0)
      hierarchyCount = ((Position) axis.getPositions().get(0)).getMembers().length;
    else
      hierarchyCount = 0;
    if (logger.isInfoEnabled())
      logger.info("creating SpanCalc, positionCount = " + positionCount + ", hierarchyCount = " + hierarchyCount);
    spans = new Span[positionCount][];
    for (int i = 0; i < positionCount; i++)
      spans[i] = new Span[hierarchyCount];

    Iterator it = axis.getPositions().iterator();
    for (int posIndex = 0; posIndex < positionCount; posIndex++) {
      Position p = (Position) it.next();
      createSpansFromAxis(axis, p, posIndex, spans[posIndex]);
    }
  }

  void createSpansFromAxis(Axis axis, Position position, int posIndex, Span[] spans) {
    if (logger.isDebugEnabled())
      logger.debug("creating Span for position " + posIndex);
    Member[] members = position.getMembers();
    for (int hierIndex = 0; hierIndex < hierarchyCount; hierIndex++) {
      Member member = members[hierIndex];
      spans[hierIndex] = new Span(axis, position, member);
    }
  }

  void initialize() {
    if (!initialized) {
      positionCount = spans.length;
      if (positionCount > 0)
        hierarchyCount = spans[0].length;
      else
        hierarchyCount = 0;
      initSpans();
      calcSpans();
      calcIndent();
      initialized = true;
    }
  }

  /**
   * initializes the spans to 1 column and 1 row.
   */
  void initSpans() {
    for (int posIndex = 0; posIndex < positionCount; posIndex++) {
      for (int hierIndex = 0; hierIndex < hierarchyCount; hierIndex++) {
        spans[posIndex][hierIndex].initialize(posIndex, hierIndex);
      }
    }
  }

  /**
   * flag indicating that a break is forced. Example row-axis:
   * <pre>
   * Product 1 | Revenue
   * Product 2 | Revenue
   * </pre>
   * The revenues shall not be combined to a single span because a break occured
   * at higher level.
   */
  boolean[][] forcePositionBreak;

  void calcSpans() {
    logger.info("calcSpans");
    forcePositionBreak = new boolean[positionCount][hierarchyCount];

    for (int hierIndex = 0; hierIndex < hierarchyCount; hierIndex++) {
      for (int posIndex = 0; posIndex < positionCount; posIndex++) {
        Span span = spans[posIndex][hierIndex];

        // if the span is already part of another, continue
        if (!span.isSignificant())
          continue;

        int dir = config.chooseSpanDirection(span);

        if (dir == SpanConfig.HIERARCHY_SPAN) {
          makeHierSpan(span, 1);
          addForcePositionBreak(span);
        } else if (dir == SpanConfig.POSITION_SPAN) {
          makePosSpan(span, 1);
          addForcePositionBreak(span);
        } else if (dir == SpanConfig.HIERARCHY_THEN_POSITION_SPAN) {
          int count = makeHierSpan(span, 1);
          makePosSpan(span, count);
          addForcePositionBreak(span);
        } else if (dir == SpanConfig.POSITION_THEN_HIERARCHY_SPAN) {
          int count = makePosSpan(span, 1);
          makeHierSpan(span, count);
          addForcePositionBreak(span);
        }

        // else do nothing because spans are initialized to 1/1 row/col
      }
    }
  }

  /** returns the number of hierarchy spans created */
  int makeHierSpan(Span span, int posSpans) {
    logger.debug("makeHierSpan");
    int pi = span.positionIndex;
    int spanCount = 1;
    loop : for (int hi = span.hierarchyIndex + 1; hi < hierarchyCount; hi++) {
      // check if all positions at hierarchy hi level are equal
      boolean equal = true;
      for (int i = 0; i < posSpans; i++) {
        Span s = spans[pi + i][hi];
        equal = equal && config.equals(span, s);
      }

      // add another row of spans      
      if (equal) {
        span.hierarchySpan += 1;
        spanCount += 1;
        for (int i = 0; i < posSpans; i++) {
          Span s = spans[pi + i][hi];
          s.significant = false;
          s.hierarchySpan = s.positionSpan = 0;
        }
      } else
        break loop;
    }
    return spanCount;
  }

  /** returns the number of position spans created */
  int makePosSpan(Span span, int hierSpans) {
    logger.debug("makePosSpan");
    int hi = span.hierarchyIndex;
    int spanCount = 1;
    loop : for (int pi = span.positionIndex + 1; pi < positionCount; pi++) {
      // artificial break?
      if (forcePositionBreak[pi][hi])
        break loop;

      // check if all hierarchies at position pi are equal
      boolean equal = true;
      for (int i = 0; i < hierSpans; i++) {
        Span s = spans[pi][hi + i];
        equal = equal && config.equals(span, s);
      }

      // add another row of spans      
      if (equal) {
        span.positionSpan += 1;
        spanCount += 1;
        for (int i = 0; i < hierSpans; i++) {
          Span s = spans[pi][hi + i];
          s.significant = false;
          s.hierarchySpan = s.positionSpan = 0;
        }
      } else
        break loop;
    }
    return spanCount;
  }

  void addForcePositionBreak(Span span) {
    // spans[pi][hi] == last element of span
    int pi = span.positionIndex + span.positionSpan;
    int hi = span.hierarchyIndex + span.hierarchySpan;
    for (; pi < positionCount && hi < hierarchyCount; hi++)
      forcePositionBreak[pi][hi] = true;
  }

  /* --------------------------------------------------------------------- */

  /**
   * creates a SpanCalc for row axis headers. Searches all positions
   * for a "significant" span that is used to create the header via 
   * <code>shf</code>. Example: the row axis
   * <pre>
   * a a
   * a b
   * </pre>
   * will get the header (upper case letters)
   * <pre>
   * A B
   * ---
   * a a
   * a b
   * </pre>
   * because the second <code>a</code> is not significant. Here <code>A</code>
   * is created by <code>shf.create(a)</code>, and <code>B = shf.create(b)</code>.
   */
  public SpanCalc createPositionHeader(SpanHeaderFactory shf) {
    logger.info("createPositionHeader");
    if (!initialized)
      initialize();

    if (hierarchyCount == 0 || positionCount == 0)
      return null;

    Span[][] header = new Span[1][hierarchyCount];

    header[0][0] = shf.create(spans[0][0]);

    for (int hi = 1; hi < hierarchyCount; hi++) {
      int pi;
      inner : for (pi = 0; pi < positionCount; pi++) {
        Span curSpan = spans[pi][hi];
        Span prevSpan = spans[pi][hi - 1];
        if (!config.equals(prevSpan, curSpan)) {
          prevSpan = curSpan;
          header[0][hi] = shf.create(curSpan);
          break inner;
        }
      }
      if (pi == positionCount) {
        // throw new IllegalArgumentException("no header found");
        // create a non-significant header
        header[0][hi] = shf.create(spans[0][hi]);
      }
    }

    return new SpanCalc(header);
  }

  /* --------------------------------------------------------------------- */

  public void addHierarchyHeader(SpanHeaderFactory shf, boolean removeDuplicates) {
    logger.info("addHierarchyHeader");
    boolean[] keep = new boolean[hierarchyCount * 2];
    createHeaderSpans(shf, keep);
    int newHierarchyCount = 0;
    for (int i = 0; i < keep.length; i++)
      if (keep[i])
        newHierarchyCount += 1;
    removeDuplicateHeaders(keep, newHierarchyCount);
    initialized = false;
  }

  void removeDuplicateHeaders(boolean[] keep, int newHierarchyCount) {
    logger.info("removeDuplicateHeaders");
    for (int posIndex = 0; posIndex < positionCount; posIndex++) {
      Span[] oldSpans = spans[posIndex];
      Span[] newSpans = new Span[newHierarchyCount];
      int newHierIndex = 0;
      for (int oldHierIndex = 0; oldHierIndex < oldSpans.length; oldHierIndex++) {
        if (keep[oldHierIndex]) {
          Span span = oldSpans[oldHierIndex];
          newSpans[newHierIndex++] = span;
        }
      }
      spans[posIndex] = newSpans;
    }
  }

  /**
   * doubles the number of spans per hierarchy (i.e. hierarchyCount).
   * Adds either a header span or the original span to every hierarchyIndex.
   * A header is added if its different from the previous one.
   */
  void createHeaderSpans(SpanHeaderFactory shf, boolean[] keep) {
    logger.info("createHeaderSpans");
    for (int posIndex = 0; posIndex < positionCount; posIndex++) {
      Span[] newSpans = new Span[hierarchyCount * 2];
      int newIndex = 0;
      Span prevHeaderSpan = null;
      for (int hierIndex = 0; hierIndex < hierarchyCount; hierIndex++) {
        Span span = spans[posIndex][hierIndex];
        // create a header span
        Span curHeaderSpan = shf.create(span);
        if (prevHeaderSpan == null || !config.equals(prevHeaderSpan, curHeaderSpan)) {
          keep[newIndex] = true;
          newSpans[newIndex++] = curHeaderSpan;
          prevHeaderSpan = curHeaderSpan;
        } else {
          // we dont have to keep this one
          newSpans[newIndex++] = (Span) span.clone();
        }
        // copy the original span
        keep[newIndex] = true;
        newSpans[newIndex++] = span;
      }
      spans[posIndex] = newSpans;
    }
  }

  /* --------------------------------------------------------------------- */

  /**
   * return span info for the element at positionIndex, hierarchyIndex.
   * @param positionIndex - index for axis.getPositions()
   * @param hierarchyIndex - index for axis.getPositions().getMembers()
   * @return the Span info if for (positionIndex, hierarchyIndex) needs
   * a &lt;td&gt; to be generated, returns null otherwise. The return value
   * is non-null, if (positionIndex, hierarchyIndex) are minimal for this
   * cell.
   */
  public Span getSpan(int positionIndex, int hierarchyIndex) {
    if (!initialized)
      initialize();
    return spans[positionIndex][hierarchyIndex];
  }

  /**
   * Returns the hierarchyCount. 
   * @return int
   */
  public int getHierarchyCount() {
    if (!initialized)
      initialize();
    return hierarchyCount;
  }

  /**
   * Returns the positionCount.
   * @return int
   */
  public int getPositionCount() {
    if (!initialized)
      initialize();
    return positionCount;
  }

  /**
   * Returns the config.
   * @return SpanConfig
   */
  public SpanConfig getConfig() {
    return config;
  }

  /**
   * Sets the config.
   * @param config The config to set
   */
  public void setConfig(SpanConfig config) {
    initialized = false;
    this.config = config;
  }

  /**
   * returns a matrix of spans[positionIndex][hierarchyIndex] for faster access.
   */
  public Span[][] getSpans() {
    return spans;
  }

  /**
   * set the matrix of spans[positionIndex][hierarchyIndex] for faster access.
   */
  public void setSpans(Span[][] spans) {
    this.spans = spans;
    initialized = false;
  }

  /**
   * sets the indent attribute of all spans
   */
  void calcIndent() {
    logger.info("calcIndent");
    for (int hi = 0; hi < hierarchyCount; hi++) {

      // find minimal root distance for this hierIndex
      int minRootDistance = Integer.MAX_VALUE;
      for (int pi = 0; pi < positionCount; pi++) {
        Span s = spans[pi][hi];
        if (s.isMember()) {
          Member m = s.getMember();
          if (m.getRootDistance() < minRootDistance)
            minRootDistance = m.getRootDistance();
        }
      }

      // set the indent for this hierIndex
      for (int pi = 0; pi < positionCount; pi++) {
        Span s = spans[pi][hi];
        if (s.isMember()) {
          Member m = s.getMember();
          s.setIndent(m.getRootDistance() - minRootDistance);
        } else
          s.setIndent(0);
      }
    }
  }

  /* -------------------------------------------------------------------- */

  public static SpanCalc appendBelow(SpanCalc above, SpanCalc below) {
    logger.info("appendBelow");
    if (above == null)
      return below;
    if (below == null)
      return above;
    if (above.getHierarchyCount() != below.getHierarchyCount())
      throw new IllegalArgumentException("sizes dont match");
    final int HI = above.getHierarchyCount();
    Span[][] a = above.spans;
    Span[][] b = below.spans;
    Span[][] s = new Span[a.length + b.length][];
    for (int pi = 0; pi < a.length; pi++) {
      s[pi] = new Span[HI];
      for (int hi = 0; hi < HI; hi++)
        s[pi][hi] = a[pi][hi];
    }
    for (int pi = 0; pi < b.length; pi++) {
      s[pi + a.length] = new Span[HI];
      for (int hi = 0; hi < HI; hi++)
        s[pi + a.length][hi] = b[pi][hi];
    }
    return new SpanCalc(s);
  }

}

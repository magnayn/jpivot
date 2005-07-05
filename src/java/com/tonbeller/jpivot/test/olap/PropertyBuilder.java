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
package com.tonbeller.jpivot.test.olap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tonbeller.jpivot.olap.model.Alignable;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.impl.PropertyImpl;

class PropertyBuilder {

  void build(TestDimension dim) {
    TestHierarchy hier = (TestHierarchy) dim.getHierarchies()[0];
    TestMember[] roots = hier.getRootMembers();
    for (int i = 0; i < roots.length; i++)
      addProperties(roots[i], 0, i);
  }

  private void addProperties(TestMember member, int level, int index) {

    List pl = new ArrayList();

    // to test right alignment, we construct a "big" number together with "small" ones
    pl.add(new PropertyImpl("P", "" + ((index == 1) ? 10000 : index), Alignable.Alignment.RIGHT));
    pl.add(new PropertyImpl("P" + level, "V" + level + index));

    if (level == 1) {
      pl.add(new PropertyImpl("link", "http://www.tonbeller.com"));
    }

    if (level == 2) {

      String[] arrows = new String[] { "down", "none", "up" };
      pl.add(new PropertyImpl("arrow", arrows[index % 3]));

      String[] styles = new String[] { "red", "yellow", "green" };
      pl.add(new PropertyImpl("style", styles[index % 3]));

      /*
      String[] arrows = new String[] { "auf", "ohne", "ab" };
      pl.add(new PropertyImpl("pfeil", arrows[index % 3]));

      String[] styles = new String[] { "rot", "gelb", "gruen" };
      pl.add(new PropertyImpl("stil", styles[index % 3]));
      */

      //pl.add(new PropertyImpl("My Image", "TONBELLER"));
      pl.add(new PropertyImpl("My Image.image", "/wcf/form/ok.png"));
      pl.add(new PropertyImpl("My Image.link", "http://www.tonbeller.com"));
      
    }

    if (level == 3) {
      pl.add(new PropertyImpl("MyLabel", "MyValue " + index));
      pl.add(new PropertyImpl("MyLabel.link", "http://jpivot.sourceforge.net"));
      pl.add(new PropertyImpl("MyLabel.arrow", "up"));
      pl.add(new PropertyImpl("MyLabel.image", "http://sourceforge.net/sflogo.php?group_id=58645&type=4"));
    }

    Property[] p = (Property[]) pl.toArray(new Property[0]);
    member.setProperties(p);

    index = 0;
    for (Iterator it = member.getChildMember().iterator(); it.hasNext();)
      addProperties(((TestMember) it.next()), level + 1, index++);
  }

}

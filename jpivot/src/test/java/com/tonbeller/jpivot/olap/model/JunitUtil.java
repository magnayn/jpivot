package com.tonbeller.jpivot.olap.model;

import com.tonbeller.jpivot.olap.model.Cell;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Result;

/**
 * Utilities for Junit tests
 */
public class JunitUtil {


  /**
   * assert position by members and values
   */
  static public void assertPosition(Result result, int iAxis, int iPos, String[] members,
      String[] formattedValues) {
    Position pos = (Position) result.getAxes()[iAxis].getPositions().get(iPos);
    int nPos0 = result.getAxes()[0].getPositions().size();
    for (int i = 0; i < pos.getMembers().length; i++) {
      Member m = pos.getMembers()[i];
      String s = m.getLabel();
      junit.framework.Assert.assertEquals(s, members[i]);
    }

    // cells of position ipos on axis 1 have the numbers ipos*nPos0 ... 
    int nCell = iPos * nPos0;
    for (int i = 0; i < formattedValues.length; i++) {
      Cell cell = (Cell) result.getCells().get(nCell + i);
      // we remove any non digit char 
      String cellval = cell.getFormattedValue().replaceAll("[^0-9]", "");
      String fval = formattedValues[i].replaceAll("[^0-9]", "");
      junit.framework.Assert.assertEquals(cellval, fval);
    }
  }

}

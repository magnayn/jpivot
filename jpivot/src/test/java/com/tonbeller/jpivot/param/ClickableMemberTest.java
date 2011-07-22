package com.tonbeller.jpivot.param;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.TestCase;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.impl.MemberImpl;
import com.tonbeller.jpivot.olap.model.impl.PropertyImpl;
import com.tonbeller.wcf.param.SessionParam;

/**
 * @author av
 */
public class ClickableMemberTest extends TestCase {

  class TestSqlAccess extends ExtensionSupport implements SqlAccess {
    public DataSource getDataSource() {
      return null;
    }
    public SessionParam createParameter(Member m, String paramName) {
      SessionParam p = new SessionParam();
      p.setName(paramName);
      p.setSqlValue(paramName);
      return p;
    }

    public SessionParam createParameter(Member m, String paramName, String propertyName) {
      SessionParam p = new SessionParam();
      p.setName(paramName);
      p.setSqlValue(propertyName);
      return p;
    }
  }
  public void testMemberInstance() {

    MemberImpl m = new MemberImpl();
    Property[] props = new Property[5];
    for (int i = 0; i < props.length; i++) {
      PropertyImpl pi = new PropertyImpl("prefix" + i, "value" + i);
      props[i] = pi;
    }
    m.setProperties(props);
    SqlAccess sa = new TestSqlAccess();

    List c = new ArrayList();
    AbstractParamProvider pp = new MemberParamProvider("param1");
    pp.addMemberParams(c, sa, null);
    assertEquals(1, c.size());
    SessionParam sp = (SessionParam)c.iterator().next();
    assertEquals("param1", sp.getName());
    assertEquals("param1", sp.getSqlValue());

    c.clear();
    pp = new PropertyParamProvider("param2", "prop2");
    pp.addMemberParams(c, sa, null);
    assertEquals(1, c.size());
    sp = (SessionParam)c.iterator().next();
    assertEquals("param2", sp.getName());
    assertEquals("prop2", sp.getSqlValue());

    c.clear();
    pp = new PropertyPrefixParamProvider("prefix");
    pp.addMemberParams(c, sa, m);
    assertEquals(props.length, c.size());
    Iterator it = c.iterator();
    sp = (SessionParam)it.next();
    assertEquals("0", sp.getName());
    assertEquals("prefix0", sp.getSqlValue());
    sp = (SessionParam)it.next();
    assertEquals("1", sp.getName());
    assertEquals("prefix1", sp.getSqlValue());
    
  }
}

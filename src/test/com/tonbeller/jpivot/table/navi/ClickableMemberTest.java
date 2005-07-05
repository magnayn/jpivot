package com.tonbeller.jpivot.table.navi;

import java.util.Collection;
import java.util.Iterator;

import javax.sql.DataSource;

import junit.framework.TestCase;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.olap.model.impl.MemberImpl;
import com.tonbeller.jpivot.olap.model.impl.PropertyImpl;
import com.tonbeller.jpivot.param.DefaultParamProvider;
import com.tonbeller.jpivot.param.SqlAccess;
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
    DefaultParamProvider pp2 = (DefaultParamProvider) DefaultParamProvider.createPropertyInstance("parm2", "prop2");
    DefaultParamProvider pp3 = (DefaultParamProvider) DefaultParamProvider.createPropertyPrefixInstance("prefix");

    MemberImpl m = new MemberImpl();
    Property[] props = new Property[5];
    for (int i = 0; i < props.length; i++) {
      PropertyImpl pi = new PropertyImpl("prefix" + i, "value" + i);
      props[i] = pi;
    }
    m.setProperties(props);
    SqlAccess sa = new TestSqlAccess();

    DefaultParamProvider pp = (DefaultParamProvider) DefaultParamProvider.createMemberInstance("param1");
    Collection c = pp.createSessionParams(sa, null);
    assertEquals(1, c.size());
    SessionParam sp = (SessionParam)c.iterator().next();
    assertEquals("param1", sp.getName());
    assertEquals("param1", sp.getSqlValue());

    pp = (DefaultParamProvider) DefaultParamProvider.createPropertyInstance("param2", "prop2");
    c = pp.createSessionParams(sa, null);
    assertEquals(1, c.size());
    sp = (SessionParam)c.iterator().next();
    assertEquals("param2", sp.getName());
    assertEquals("prop2", sp.getSqlValue());

    pp = (DefaultParamProvider) DefaultParamProvider.createPropertyPrefixInstance("prefix");
    c = pp.createSessionParams(sa, m);
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

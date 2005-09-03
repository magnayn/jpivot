<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ page language="java" extends="org.jboss.portal.core.servlet.jsp.PortalJsp" %>
<%@ taglib uri="/WEB-INF/portal-lib.tld" prefix="n" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib uri="http://www.tonbeller.com/wcf" prefix="wcf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%@ page isELIgnored ="false" %>
<portlet:defineObjects/>

<h1>Parameters 2 - receiving</h1>

<!-- this sets the "MdxParameter" parameter of paramquery01 -->
<jp:setParam query="${paramquery01}" httpParam="param" mdxParam="ProductMember">
<jp:mondrianQuery id="paramquery01" jdbcDriver="sun.jdbc.odbc.JdbcOdbcDriver" jdbcUrl="jdbc:odbc:MondrianFoodMart" catalogUri="/WEB-INF/queries/FoodMart.xml">
select
  {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns,
  {Parameter("ProductMember", [Product], [Product].[All Products].[Food], "wat willste?").children} ON rows
from Sales
where ([Time].[1997])
</jp:mondrianQuery>
</jp:setParam>

<portlet:renderURL var="post4">
	<portlet:param name="screen" value="/test/param4.jsp"/>
</portlet:renderURL>

<form action="${post4}" method="post" id="form01">

<!-- display the current Parameter value -->
Show <b>Children</b> of Parameter Member <c:out value="${paramquery01.extensions.setParameter.displayValues['ProductMember']}"/>
<p>
<jp:table id="paramtable01" query="${paramquery01}" visible="true"/>
<wcf:render ref="paramtable01" xslUri="/WEB-INF/jpivot/table/mdxtable.xsl" xslCache="true"/>

</form>
<p>

<portlet:renderURL var="indexURL">
	<portlet:param name="op" value="index"/>
</portlet:renderURL>

<a href="${indexURL}">Back to index</a>


<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ page language="java" extends="org.jboss.portal.core.servlet.jsp.PortalJsp" %>
<%@ taglib uri="/WEB-INF/portal-lib.tld" prefix="n" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib uri="http://www.tonbeller.com/wcf" prefix="wcf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%@ page isELIgnored ="false" %>
<portlet:defineObjects/>

<h1>Parameter 1 - sending</h1>

Click on a Region.
<p>

<portlet:renderURL var="post">
	<portlet:param name="screen" value="/test/param1.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="post2">
	<portlet:param name="screen" value="/test/param2.jsp"/>
</portlet:renderURL>

<form action="${post}" method="post" id="form01">


<!-- make all members of the Region dimension clickable -->
<jp:table id="clicktable01" query="#{query01}" visible="true">
  <jp:clickable urlPattern="${post2}" uniqueName="Region"/>
</jp:table>

<wcf:render ref="clicktable01" xslUri="/WEB-INF/jpivot/table/mdxtable.xsl" xslCache="true"/>

</form>

<p>

<portlet:renderURL var="indexURL">
	<portlet:param name="op" value="index"/>
</portlet:renderURL>

<a href="${indexURL}">Back to index</a>

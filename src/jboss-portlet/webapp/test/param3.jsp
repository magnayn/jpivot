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

Click on a Product.
<p>

<portlet:renderURL var="post3">
	<portlet:param name="screen" value="/test/param3.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="post4">
	<portlet:param name="screen" value="/test/param4.jsp"/>
	<portlet:param name="param" value="{0}"/>
</portlet:renderURL>

<form action="${post3}" method="post" id="form01">


<%-- make all members of the Region dimension clickable --%>
<jp:table id="clicktable02" query="#{query01}" visible="true">

  <jp:clickable urlPattern="${post4}" uniqueName="[Product]"/>

</jp:table>

<wcf:render ref="clicktable02" xslUri="/WEB-INF/jpivot/table/mdxtable.xsl" xslCache="true"/>

</form>

<p>

<portlet:renderURL var="indexURL">
	<portlet:param name="op" value="index"/>
</portlet:renderURL>

<a href="${indexURL}">Back to index</a>

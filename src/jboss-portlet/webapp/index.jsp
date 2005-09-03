<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0//EN"
     "http://www.w3.org/TR/REC-html40/strict.dtd">
<%@ page language="java" extends="org.jboss.portal.core.servlet.jsp.PortalJsp" %>
<%@ taglib uri="/WEB-INF/portal-lib.tld" prefix="n" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib uri="http://www.tonbeller.com/wcf" prefix="wcf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ page isELIgnored ="false" %>
<portlet:defineObjects/>

<p>
<h2>JPivot Portlet Demo</h2>

<n:errors/>
<n:success/>


<portlet:renderURL var="testData">
	<portlet:param name="query" value="/WEB-INF/queries/testquery.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="mondrian">
	<portlet:param name="query" value="/WEB-INF/queries/mondrian.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="fourhier">
	<portlet:param name="query" value="/WEB-INF/queries/fourhier.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="testDataParameters">
	<portlet:param name="screen" value="/test/param1.jsp"/>
	<portlet:param name="query" value="/WEB-INF/queries/testquery.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="mondrianParameters">
	<portlet:param name="screen" value="/test/param3.jsp"/>
	<portlet:param name="query" value="/WEB-INF/queries/mondrian.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="arrows">
	<portlet:param name="query" value="/WEB-INF/queries/arrows.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="colors">
	<portlet:param name="query" value="/WEB-INF/queries/colors.jsp"/>
</portlet:renderURL>

<c:if test="${query01 != null}">
  <jp:destroyQuery id="query01"/>
</c:if>

<ul>
  <li><a href="${testData}">Testpage using testdata</a></li>
  <li><a href="${mondrian}">Testpage using mondrian</a></li>
  <li><a href="${fourhier}">Mondrian fourhier test</a></li>
  <li><a href="${testDataParameters}">Parameters with builtin Testdata</a></li>
  <li><a href="${mondrianParameters}">Parameters with Mondrian</a></li>
  <li><a href="${arrows}">Arrows in Cells</a></li>
  <li><a href="${colors}">Colors in Cells</a></li>
</ul>
<p>


</body>
</html>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0//EN"
     "http://www.w3.org/TR/REC-html40/strict.dtd">
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib uri="http://www.tonbeller.com/wcf" prefix="wcf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
<head>
  <title>JPivot Demo</title>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <link rel="stylesheet" type="text/css" href="css/mdxtable.css">
  <link rel="stylesheet" type="text/css" href="css/mdxnavi.css">
</head>
<body>
<p>
<h2>JPivot Demonstration</h2>
<c:if test="${query01 != null}">
  <jp:destroyQuery id="query01"/>
</c:if>

<h3>Using the Mondrian OLAP engine</h3>

<ul>
  <li><a href="testpage.jsp?query=mondrian">Slice and Dice with two hierarchies</a></li>
  <li><a href="testpage.jsp?query=fourhier">...and with four hierachies</a></li>
  <li><a href="test/param3.jsp?query=mondrian">Dynamic parameters with Mondrian</a></li>
  <li><a href="testpage.jsp?query=arrows">Arrows in Cells</a></li>
  <li><a href="testpage.jsp?query=colors">Colors in Cells</a></li>
  <li><a href="testpage.jsp?query=testquery">Test data</a></li>
  <li><a href="test/param1.jsp?query=testquery">Dynamic parameters with test data</a></li>
</ul>
<p/>

<h3>Using XML/A</h3>
<p/>
XML/A is a Web services protocol and standard that allows JPivot to connect to Microsoft 
Analysis Services and Mondrian on other machines. This example connects to Mondrian via
XML/A where you are running JPivot.
<p/>
<ul>
  <li><a href="testpage.jsp?query=mondrianXMLA">Slice and Dice with two hierarchies</a></li>
</ul>
<p/>


</body>
</html>

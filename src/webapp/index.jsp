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
<h2>JPivot Demo</h2>
<c:if test="${query01 != null}">
  <jp:destroyQuery id="query01"/>
</c:if>

<ul>
  <li><a href="testpage.jsp?query=testquery">Testpage using testdata</a></li>
  <li><a href="testpage.jsp?query=mondrian">Testpage using mondrian</a></li>
  <li><a href="testpage.jsp?query=fourhier">Mondrian fourhier test</a></li>
  <li><a href="test/param1.jsp?query=testquery">Parameters with builtin Testdata</a></li>
  <li><a href="test/param3.jsp?query=mondrian">Parameters with Mondrian</a></li>
  <li><a href="testpage.jsp?query=arrows">Arrows in Cells</a></li>
  <li><a href="testpage.jsp?query=colors">Colors in Cells</a></li>
</ul>
<p>


</body>
</html>

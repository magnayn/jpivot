<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ taglib uri="/WEB-INF/theme/portal-layout.tld" prefix="p" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en"><head>
	<meta http-equiv="content-type" content="text/html; charset=iso-8859-1"/>

   <!-- insert the dynamically determined theme elements here -->
   <p:theme themeName='jpivotTheme' />

<body>

<!-- header table -->
<table class="headerTable">
<tr>
<td align="left">
<img src="/jpivotTheme/images/Mondrian_logo_top.jpg" />
</td>
</tr>
</table>

<!-- center table with columns -->
<table width="100%" bgcolor="#FFFFFF">
<tr>
<td class="leftColumn" width="10%"><p:region regionName='left'/></td>
<td class="centerColumn"><p:region regionName='center'/></td>
</tr>
</table>

<!-- footer table -->
<table class="footerTable">
<tr>
<td align="center">
<a href="http://jpivot.sourceforge.net">JPivot</a> and <a href="http://mondrian.sourceforge.net">Mondrian</a> are open source projects. JPivot Theme
</td>
</tr>
</table>

</body>
</html>
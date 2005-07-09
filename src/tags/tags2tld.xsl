<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" version="1.0" encoding="iso-8859-1" indent="yes"
  doctype-public="-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN"
  doctype-system="http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd"/>

<xsl:strip-space elements="taglib tag descr attr"/>

<xsl:template match="/taglib">
  <taglib>
    <tlib-version>1.0</tlib-version>
    <jsp-version>1.2</jsp-version>
    <short-name><xsl:value-of select="@name"/></short-name>
    <uri><xsl:value-of select="@uri"/></uri>
    <display-name><xsl:value-of select="@name"/></display-name>
    <description><xsl:value-of select="descr[@lang='en']"/></description>
    <xsl:apply-templates select="tag">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
  </taglib>
</xsl:template>

<xsl:template match="tag">
  <tag>
    <name><xsl:value-of select="@name"/></name>
    <tag-class><xsl:value-of select="@class"/></tag-class>
    <body-content><xsl:value-of select="@body"/></body-content>
    <description><xsl:value-of select="descr[@lang='en']"/></description>
    <xsl:apply-templates select="attr"/>
  </tag>
</xsl:template>


<xsl:template match="attr">
  <attribute>
    <name><xsl:value-of select="@name"/></name>
    <required><xsl:value-of select="@required"/></required>
    <rtexprvalue><xsl:value-of select="@rtexpr"/></rtexprvalue>
    <description><xsl:value-of select="descr[@lang='en']"/></description>
  </attribute>
</xsl:template>


</xsl:stylesheet>

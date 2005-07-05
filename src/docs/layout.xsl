<?xml version="1.0" encoding="ISO-8859-1"?>


<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:lxslt="http://xml.apache.org/xslt"
  xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
  version="1.0"

  extension-element-prefixes="redirect"
  xmlns:tl="http://www.tonbeller.com/jpivot/documentation/layout"
  xmlns:java="http://xml.apache.org/xslt/java"
  exclude-result-prefixes="java"
>

<xsl:output method="html" indent="yes" encoding="iso-8859-1"/>

<xsl:variable name="content" select="/"/>
<xsl:variable name="layout" select="document('layout.xml')"/>
<xsl:variable name="now" select="java:java.util.Date.new()"/>


<xsl:template match="/">
  <xsl:apply-templates select="$content/document/section"/>
</xsl:template>


<xsl:template match="section">
  <xsl:apply-templates/>
</xsl:template>


<xsl:template match="page[@id]">
  <redirect:write select="concat(@id, '.html')">
    <xsl:apply-templates select="$layout/html">
      <xsl:with-param name="page" select="."/>
    </xsl:apply-templates>
  </redirect:write>
</xsl:template>


<xsl:template match="page">
  <redirect:write select="concat('temp-', generate-id(), '.html')">
    <xsl:apply-templates select="$layout/html">
      <xsl:with-param name="page" select="."/>
    </xsl:apply-templates>
  </redirect:write>
</xsl:template>

<xsl:template match="link"/>


<xsl:template match="tl:changed">
  <xsl:value-of select="$now"/>
</xsl:template>


<xsl:template match="tl:version">
  <xsl:value-of select="$content/document/@version"/>
</xsl:template>

<xsl:template match="tl:document-title">
  <xsl:value-of select="$content/document/@title"/>
</xsl:template>

<xsl:template match="tl:menu">
  <xsl:param name="page"/>
  <xsl:apply-templates mode="menu" select="$content/document/section">
    <xsl:with-param name="page" select="$page"/>
  </xsl:apply-templates>
</xsl:template>


<xsl:template mode="menu" match="section">
  <xsl:param name="page"/>
  <div>
    <strong><xsl:value-of select="@title"/></strong>
    <xsl:apply-templates mode="menu">
      <xsl:with-param name="page" select="$page"/>
    </xsl:apply-templates>
  </div>
</xsl:template>


<xsl:template mode="menu" match="page">
  <xsl:param name="page"/>
  <div class="menuentry">
    <xsl:choose>
      <xsl:when test="generate-id($page) = generate-id(.)">
        <b><xsl:value-of select="@title"/></b>
      </xsl:when>

      <xsl:when test="@id">
        <a href="{@id}.html">
          <xsl:value-of select="@title"/>
        </a>
      </xsl:when>

      <xsl:otherwise>
        <a href="temp-{generate-id()}.html">
          <xsl:value-of select="@title"/>
        </a>
      </xsl:otherwise>
    </xsl:choose>
  </div>
</xsl:template>


<xsl:template mode="menu" match="link">
  <xsl:param name="page"/>
  <div class="menuentry">
    <a href="{@href}">
      <xsl:value-of select="@title"/>
    </a>
  </div>
</xsl:template>



<xsl:template match="tl:page-title">
  <xsl:param name="page"/>
  <xsl:value-of select="$page/@title"/>
</xsl:template>


<xsl:template match="tl:body">
  <xsl:param name="page"/>
  <xsl:apply-templates select="$page/* | $page/node()"/>
</xsl:template>


<!-- identity transformation -->
<xsl:template match="*|@*|node()">
  <xsl:param name="page"/>
  <xsl:copy>
    <xsl:apply-templates select="*|@*|node()">
      <xsl:with-param name="page" select="$page"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>

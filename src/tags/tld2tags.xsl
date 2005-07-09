<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" version="1.0" encoding="iso-8859-1" indent="no"/>

<xsl:strip-space elements="taglib tag attribute name description body-content tag-class"/>

<xsl:template match="/taglib">
  <taglib name="{short-name}" uri="{uri}">
    <xsl:apply-templates select="description"/>
    <xsl:apply-templates select="tag"/>
  </taglib>
</xsl:template>

<xsl:template match="tag">

  <xsl:comment> ******************************************************************** </xsl:comment>

  <tag name="{name}" class="{tag-class}" body="{body-content}">
    <xsl:apply-templates select="description"/>
    <xsl:apply-templates select="attribute"/>
    <example/>
  </tag>
</xsl:template>


<xsl:template match="attribute">
  <attr name="{name}" required="{required}" rtexpr="{rtexprvalue}" type="String">
    <xsl:apply-templates select="description"/>
  </attr>
</xsl:template>


<xsl:template match="description">
  <descr lang="en">
    <xsl:value-of select="."/>
  </descr>
  <descr lang="de">
    <!-- german docs go here -->
  </descr>
</xsl:template>

</xsl:stylesheet>

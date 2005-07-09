<?xml version="1.0"?>

<!--
  
  creates documentation from *-tags.xml
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="lang"/>

<xsl:output method="html" version="1.0" encoding="iso-8859-1" indent="yes"
  omit-xml-declaration="yes"/>

<xsl:template match="/taglib">
  <html>
    <head>
      <title><xsl:value-of select="@name"/></title>
      <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
      <style type="text/css">
          body {
            background-color: #ffffff;
            font-family: Verdana, SansSerif;
            font-size: 10 pt;
          }
          td,th {
            font-size: 10 pt;
            text-align: left;
            vertical-align: top;
          }
          th {
            font-weight: bold;
          }
        </style>
      </head>
    <body>
      <h2><xsl:value-of select="@name"/> (<xsl:value-of select="$lang"/>)</h2>
      <xsl:value-of select="descr[@lang=$lang]"/>
      <h3>Content</h3>
      <ul>
        <xsl:apply-templates mode="toc" select="tag">
          <xsl:sort select="@name"/>
        </xsl:apply-templates>
      </ul>

      <xsl:apply-templates select="tag">
        <xsl:sort select="@name"/>
      </xsl:apply-templates>

    </body>
 </html>
</xsl:template>


<xsl:template mode="toc" match="tag">
  <li>
    <a href="#{@name}">
      <xsl:value-of select="@name"/>
    </a>
  </li>
</xsl:template>


<xsl:template match="tag">
  <hr/>
  <p/>
  <h3>
    <a name="{@name}"><xsl:value-of select="@name"/></a>
  </h3>
  <table width="100%">
    <col width="1%" align="left" valign="top"/>
    <col width="99%" align="left" valign="top"/>
    <tr>
      <th nowrap="nowrap">Body Content</th>
      <td><xsl:value-of select="@body"/></td>
    </tr>
    <tr>
      <th>Description</th>
      <td><xsl:apply-templates select="descr[@lang=$lang]"/></td>
    </tr>
  </table>
  <p/>
  <table border="1" cellpadding="3" cellspacing="0" width="100%">
    <col width="1%" align="left" valign="top"/>
    <col width="1%" align="left" valign="top"/>
    <col width="1%" align="left" valign="top"/>
    <col width="1%" align="left" valign="top"/>
    <col width="97%" align="left" valign="top"/>
    <tr>
      <th>Attribute</th>
      <th>Required</th>
      <th>Type</th>
      <th>rtexpr</th>
      <th>Description</th>
    </tr>
    <xsl:apply-templates select="attr"/>
  </table>
  <xsl:apply-templates select="example"/>
</xsl:template>

<xsl:template match="attr">
  <tr>
    <td><xsl:value-of select="@name"/></td>
    <td><xsl:value-of select="@required"/></td>
    <td><xsl:value-of select="@type"/></td>
    <td><xsl:value-of select="@rtexpr"/></td>
    <td><xsl:apply-templates select="descr[@lang=$lang]"/></td>
  </tr>
</xsl:template>


<xsl:template match="example">
  <h4>Example</h4>
  <pre><xsl:apply-templates/></pre>
</xsl:template>

<xsl:template match="*|@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="*|@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>

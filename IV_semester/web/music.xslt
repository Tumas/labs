<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:u="http://github.com/Tumas"> 

  <xsl:template match="/">
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Music database</title> 
      </head>
      <body>
        <h1>Music database</h1>
        <xsl:apply-templates />
      </body>
    </html>
  </xsl:template>

  <xsl:template match="u:music_database">
    <ul>
      <xsl:for-each select="u:album">
      <li>
        <h2>
        <xsl:number value="position()" format="1" /> 
        <xsl:text> Album: </xsl:text>
        <xsl:value-of select="u:title" /></h2>
        <xsl:if test="(u:title)=(//u:most_popular/u:title)">
          <em>MOST POPULAR!</em>
        </xsl:if>
      </li>
      <li>
        <xsl:apply-templates select="u:file">
          <xsl:sort select="u:title" />
        </xsl:apply-templates>
      </li>
    </xsl:for-each>
  </ul>
  </xsl:template>

  <xsl:template match="u:file">
    <ul>
        <li>
          <xsl:value-of select="u:title" /> by 
          <strong><xsl:value-of select="u:author" /></strong>
          in format: <em><xsl:value-of select="@format" /></em>
        </li>
    </ul>
  </xsl:template>
</xsl:stylesheet>

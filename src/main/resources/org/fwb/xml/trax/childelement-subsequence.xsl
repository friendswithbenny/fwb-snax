<?xml version="1.0" encoding="UTF-8"?>
<!--
copies an XML document exactly (identity),
but selectively filters certain level-2 elements (child-elements of root-element).
@param $skip: non-negative number of leading level-2 elements to skip (0 to skip none)
@param $limit: non-negative maximum number of level-2 elements to print (0 for no-limit)
-->
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<!-- default to skipping 1/3 of the data -->
	<xsl:param name="skip">
		<xsl:value-of select="count(/*/*) div 3" />
	</xsl:param>
	<!-- default to showing 1/3 of the data -->
	<xsl:param name="limit">
		<xsl:value-of select="count(/*/*) div 3" />
	</xsl:param>
	
	<!-- identity -->
	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
	
	<!-- special -->
	<xsl:template match="/*/*">
		<xsl:variable name="index" select="1 + count(preceding-sibling::*)" />
		
		<xsl:choose>
			<xsl:when test="$skip >= $index">
				<xsl:comment>
					<xsl:text>skipped </xsl:text>
					<xsl:value-of select="$index" />
				</xsl:comment>
			</xsl:when>
			
			<xsl:when test="0 &lt; $limit and $limit &lt; $index - $skip">
				<xsl:comment>
					<xsl:text>truncated </xsl:text>
					<xsl:value-of select="$index" />
				</xsl:comment>
			</xsl:when>
			
			<xsl:otherwise>
				<xsl:comment>
					<xsl:text>#</xsl:text>
					<xsl:value-of select="$index" />
					<xsl:text>:</xsl:text>
				</xsl:comment>
				
				<!-- identity -->
				<xsl:copy>
					<xsl:apply-templates select="@*|node()" />
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>

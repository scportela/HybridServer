<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://www.esei.uvigo.es/dai/hybridserver">

<xsl:output method="html" indent="yes" encoding="utf-8"/>

	<xsl:template match="/">
	
		<html>
		
			<body>
			
			<ul>
			
				<li>db.url=<xsl:value-of select="c:configuration/c:database/c:url"/></li>
				<li>port=<xsl:value-of select="c:configuration/c:connections/c:http"/></li>
				<li>db.users=<xsl:value-of select="c:configuration/c:database/c:user"/></li>
				<li>db.password=<xsl:value-of select="c:configuration/c:database/c:password"/></li>
				<li>numClients=<xsl:value-of select="c:configuration/c:connections/c:numClients"/></li>
				
			</ul>
			</body>
			
		</html>
		
	</xsl:template>
	
</xsl:stylesheet>
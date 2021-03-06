<?xml version="1.0"?>

<!--

   Copyright 2000-2001 The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

-->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

  <xsl:param name="image"/>
  <xsl:param name="color"/>

  <xsl:template match="context">
    <image source="{$image}">
      <xsl:apply-templates/>
    </image>
  </xsl:template>

  <xsl:template match="parameter">
    <xsl:if test="@name='label'">
      <text font="Arial" size="12" x="14" y="1" halign="left"
            valign="top" color="{$color}" style="italic" text="{@value}"/>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>

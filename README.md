Visuwall
========

Visuwall is a web application showing build and quality states of development projects in a very visual way that you can put it on a wall in your open space.


Description
===========

Visuwall connects to softwares to get informations about projects and will display it in a web based client

Visuwall Currently support :

 * Jenkins
 * Hudson
 * Sonar
 * Bamboo
 * Teamcity
 * Continuum


$ java -jar visuwall.war --help


Available goals

    * license:check: verify if some files miss license header
    * license:format: add the license header when missing. If a header is existing, it is updated to the new one.

Java file generation form XSD

    * xjc -xmlschema SurefireAggregatedReport.xsd -pfr.norad.visuwall.generated.hudson.surefireaggregatedreport
    * xjc -xmlschema MavenModuleSet.xsd -pfr.norad.visuwall.generated.hudson.mavenmoduleset
    * xjc -xmlschema MavenModuleSetBuild.xsd -pfr.norad.visuwall.generated.hudson.mavenmodulesetbuild
    * xjc -xmlschema Hudson.xsd -pfr.norad.visuwall.generated.hudson.hudsonmodel




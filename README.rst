Available goals

    * license:check: verify if some files miss license header
    * license:format: add the license header when missing. If a header is existing, it is updated to the new one.

Java file generation form XSD

    * xjc -xmlschema SurefireAggregatedReport.xsd -p com.jsmadja.wall.projectwall.generated.hudson.surefireaggregatedreport
    * xjc -xmlschema MavenModuleSet.xsd -p com.jsmadja.wall.projectwall.generated.hudson.mavenmoduleset    
    * xjc -xmlschema MavenModuleSetBuild.xsd -p com.jsmadja.wall.projectwall.generated.hudson.mavenmodulesetbuild
    * xjc -xmlschema Hudson.xsd -p com.jsmadja.wall.projectwall.generated.hudson.hudsonmodel

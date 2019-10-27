# Maven plugin to setup a Talend component

This plugin is bound to the phase `package` and performes for a talend component following tasks:
* can copy the resources of an component from a source dir to a target dir before starting the processing
* adds the necessary libraries according to the project dependencies to the component and deletes obsolete libs
* Setup the IMPORT tags in the component XML configuration file
* Setup the release date tag of the component and adds in the advanced settings a label showing release date and version
* Setup the component version according to the project version
* Checks the message properties (only the default) if all necessary message keys exist.
* Checks the messages if the LONG_NAME property is present - needed to show a meaningful tool-tip for the component in the palette.

To use it in your own Talend component project use this plugin configuration. You can set configuration parameters in context of an execution of in for all executions.
If you have more than one component in your project, use multiple executions, this example assumes 2 components. It is helpful to use the component name as id
```xml
	<plugin>
		<groupId>de.cimt.talendcomp</groupId>
		<artifactId>cimt-talendcomp-maven-plugin</artifactId>
		<version>2.1</version>
		<configuration>
			<checkMessageProperties>true</checkMessageProperties>
			<copyFromSourceBaseDir>src/talend_component/</copyFromSourceBaseDir>
			<componentBaseDir>${project.basedir}/talend_component</componentBaseDir>
		</configuration>
		<executions>
			<execution>
				<id>tComponent1</id>
				<goals>
					<goal>component</goal>
				</goals>
				<configuration>
					<componentName>tComponent1</componentName>
					<componentReleaseDate>20170106</componentReleaseDate>
					<noJars>false</noJars>
					<jarExcludePattern>log4j</jarExcludePattern>
				</configuration>
			</execution>
			<execution>
				<id>tComponent2</id>
				<goals>
					<goal>component</goal>
				</goals>
				<configuration>
					<componentName>tComponent2</componentName>
					<noJars>true</noJars>
				</configuration>
			</execution>
		</executions>
	</plugin>

```
Only the configuration parameter `componentName` is mandatory.

The other parameters will be filled with default values (the current values here).

The parameter `componentReleaseDate` will be filled with the current date if it is missing.

The parameter `noJars` prevents the plugin from adding jar files and adding IMPORT tags. This is useful if you have multiple components depending from another component which carries all libraries.

If some components needs dedicated jars and other don't you can use the `jarExcludePattern` to exclude with regex expressions jars from being used with the current executed component.

This is a typical maven log output:
```
[INFO] --- cimt-talendcomp-maven-plugin:1.8:component (tFileExcelWorkbookOpen) @ cimt-talendcomp-excel ---
[INFO] ############ Setup component: tFileExcelWorkbookOpen with base dir: /Volumes/Data/Talend/workspace_talend_comp/talendcomp_tFileExcel/talend_component
[INFO] Check dependencies and collect artifact jar files...
[INFO]     file: /Volumes/Data/Talend/workspace_talend_comp/talendcomp_tFileExcel/target/cimt-talendcomp-excel-8.0.jar
[INFO]     file: /Users/jan/.m2/repository/org/apache/poi/poi-ooxml/3.15/poi-ooxml-3.15.jar scope: compile
[INFO]     file: /Users/jan/.m2/repository/org/apache/poi/poi/3.15/poi-3.15.jar scope: compile
[INFO]     file: /Users/jan/.m2/repository/commons-codec/commons-codec/1.10/commons-codec-1.10.jar scope: compile
[INFO]     file: /Users/jan/.m2/repository/org/apache/commons/commons-collections4/4.1/commons-collections4-4.1.jar scope: compile
[INFO]     file: /Users/jan/.m2/repository/org/apache/poi/poi-ooxml-schemas/3.15/poi-ooxml-schemas-3.15.jar scope: compile
[INFO]     file: /Users/jan/.m2/repository/org/apache/xmlbeans/xmlbeans/2.6.0/xmlbeans-2.6.0.jar scope: compile
[INFO]     file: /Users/jan/.m2/repository/com/github/virtuald/curvesapi/1.04/curvesapi-1.04.jar scope: compile
[INFO]     file: /Users/jan/.m2/repository/xalan/xalan/2.7.2/xalan-2.7.2.jar scope: compile
[INFO]     file: /Users/jan/.m2/repository/xalan/serializer/2.7.2/serializer-2.7.2.jar scope: compile
[INFO] Clean target and copy resources from source base dir: /Volumes/Data/Talend/workspace_talend_comp/talendcomp_tFileExcel/talend_component
[INFO]     5 files copied.
[INFO] Read component XML configuration...
[INFO]     XML configuration file: /Volumes/Data/Talend/workspace_talend_comp/talendcomp_tFileExcel/talend_component/tFileExcelWorkbookOpen/tFileExcelWorkbookOpen_java.xml sucessfully read
[INFO] Remove previous jars from component...
[INFO]     10 old jars deleted.
[INFO] Copy jars into component...
[INFO]     10 jars copied.
[INFO] Process component XML configuration...
[INFO]     setup imports...
[INFO]     setup release and version info...
[INFO] Done.
[INFO] Write back component XML configuration...
[INFO]     XML configuration file: /Volumes/Data/Talend/workspace_talend_comp/talendcomp_tFileExcel/talend_component/tFileExcelWorkbookOpen/tFileExcelWorkbookOpen_java.xml sucessfully written.
[INFO] Check message properties...
[INFO]     Read message properties file: /Volumes/Data/Talend/workspace_talend_comp/talendcomp_tFileExcel/talend_component/tFileExcelWorkbookOpen/tFileExcelWorkbookOpen_messages.properties
[INFO] Finished.
```

The result e.g. a list of the necessary modules within a Talend component XML configuration.
Example:
```xml
  <CODEGENERATION> 
    <IMPORTS> 
      <IMPORT NAME="jlo-talendcomp-excel" MODULE="jlo-talendcomp-excel-11.3.jar" MVN="mvn:org.talend.libraries/jlo-talendcomp-excel-11.3/6.0.0-SNAPSHOT" REQUIRED="true"/>
      <IMPORT NAME="poi-ooxml" MODULE="poi-ooxml-4.1.0.jar" MVN="mvn:org.talend.libraries/poi-ooxml-4.1.0/6.0.0-SNAPSHOT" REQUIRED="true"/>
      <IMPORT NAME="poi" MODULE="poi-4.1.0.jar" MVN="mvn:org.talend.libraries/poi-4.1.0/6.0.0-SNAPSHOT" REQUIRED="true"/>
      <IMPORT NAME="commons-codec" MODULE="commons-codec-1.12.jar" MVN="mvn:org.talend.libraries/commons-codec-1.12/6.0.0-SNAPSHOT" REQUIRED="true"/>
      <IMPORT NAME="commons-collections4" MODULE="commons-collections4-4.3.jar" MVN="mvn:org.talend.libraries/commons-collections4-4.3/6.0.0-SNAPSHOT" REQUIRED="true"/>
      <IMPORT NAME="commons-math3" MODULE="commons-math3-3.6.1.jar" MVN="mvn:org.talend.libraries/commons-math3-3.6.1/6.0.0-SNAPSHOT" REQUIRED="true"/>
      <IMPORT NAME="poi-ooxml-schemas" MODULE="poi-ooxml-schemas-4.1.0.jar" MVN="mvn:org.talend.libraries/poi-ooxml-schemas-4.1.0/6.0.0-SNAPSHOT" REQUIRED="true"/>
      <IMPORT NAME="xmlbeans" MODULE="xmlbeans-3.1.0.jar" MVN="mvn:org.talend.libraries/xmlbeans-3.1.0/6.0.0-SNAPSHOT" REQUIRED="true"/>
      <IMPORT NAME="commons-compress" MODULE="commons-compress-1.18.jar" MVN="mvn:org.talend.libraries/commons-compress-1.18/6.0.0-SNAPSHOT" REQUIRED="true"/>
      <IMPORT NAME="curvesapi" MODULE="curvesapi-1.06.jar" MVN="mvn:org.talend.libraries/curvesapi-1.06/6.0.0-SNAPSHOT" REQUIRED="true"/>
      <IMPORT NAME="xalan" MODULE="xalan-2.7.2.jar" MVN="mvn:org.talend.libraries/xalan-2.7.2/6.0.0-SNAPSHOT" REQUIRED="true"/>
      <IMPORT NAME="serializer" MODULE="serializer-2.7.2.jar" MVN="mvn:org.talend.libraries/serializer-2.7.2/6.0.0-SNAPSHOT" REQUIRED="true"/>
    </IMPORTS> 
  </CODEGENERATION>  
```

This plugin is available via Maven Central since 2018-07-20.

# Maven plugin to setup a Talend component

This plugin is bound to the phase `package` and performes for a talend component following tasks:
* can copy the resources of an component from a source dir to a target dir before starting the processing
* adds the necessary libraries according to the project dependencies to the component and deletes obsolete libs
* Setup the IMPORT tags in the component XML configuration file
* Setup the release date tag of the component and adds in the advanced settings a label showing release date and version
* Setup the component version according to the project version
* Checks the message properties (only the default) if all necessary message keys exist.
* Checks the messages if the LONG_NAME property is present - needed to show a meaningful tool-tip for the component in the palette.
* Checks the JET files for inconsistency of JET code marks (<% , %>)
* Can now setup the module maven location in the native maven way instead of the terrible org.talend.libraries maven location. Please set the tag useTalendLibrariesMavenLocation to false. It works since Talend version 8!

To use it in your own Talend component project use this plugin configuration. You can set configuration parameters in context of an execution of in for all executions.
If you have more than one component in your project, use multiple executions, this example assumes 2 components. It is helpful to use the component name as id

If you leaf out the tag **copyFromSourceBaseDir** the plugin will use the path ```src/main/components``` for the component JET code sources and if you leaf out the tag **componentBaseDir** the plugin will use the path ```target/components``` for the final component files.

```xml
	<plugin>
		<groupId>de.cimt.talendcomp</groupId>
		<artifactId>cimt-talendcomp-maven-plugin</artifactId>
		<version>4.0</version>
		<configuration>
			<checkMessageProperties>true</checkMessageProperties>
			<copyFromSourceBaseDir>src/talend_component/</copyFromSourceBaseDir>
			<useTalendLibrariesMavenLocation>false</useTalendLibrariesMavenLocation> <!-- this is the new attribute to prevent org.talend.libraries location-->
			<studioUserComponentFolder>/Data/Talend/Studio/talend_user_components</studioUserComponentFolder>
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
[INFO] --- cimt-talendcomp-maven-plugin:4.0:component (tFileExcelWorkbookOpen) @ jlo-talendcomp-excel ---
[INFO] ############ Setup component: tFileExcelWorkbookOpen with base dir: /Users/jan/development/eclipse-workspace-talendcomp/talendcomp_tFileExcel/talend_component
[INFO] Check dependencies and collect artifact jar files...
[INFO]     file: /Users/jan/development/eclipse-workspace-talendcomp/talendcomp_tFileExcel/target/jlo-talendcomp-excel-13.5.jar
[INFO] Collect project artifacts without scope [system, test, provided]
[INFO]       Add file: /Users/jan/.m2/repository/org/apache/poi/poi-ooxml/4.1.2/poi-ooxml-4.1.2.jar
[INFO]       Add file: /Users/jan/.m2/repository/org/apache/poi/poi/4.1.2/poi-4.1.2.jar
[INFO]       Add file: /Users/jan/.m2/repository/commons-codec/commons-codec/1.13/commons-codec-1.13.jar
[INFO]       Add file: /Users/jan/.m2/repository/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar
[INFO]       Add file: /Users/jan/.m2/repository/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar
[INFO]       Add file: /Users/jan/.m2/repository/com/zaxxer/SparseBitSet/1.2/SparseBitSet-1.2.jar
[INFO]       Add file: /Users/jan/.m2/repository/org/apache/poi/poi-ooxml-schemas/4.1.2/poi-ooxml-schemas-4.1.2.jar
[INFO]       Add file: /Users/jan/.m2/repository/org/apache/xmlbeans/xmlbeans/3.1.0/xmlbeans-3.1.0.jar
[INFO]       Add file: /Users/jan/.m2/repository/org/apache/commons/commons-compress/1.19/commons-compress-1.19.jar
[INFO]       Add file: /Users/jan/.m2/repository/com/github/virtuald/curvesapi/1.06/curvesapi-1.06.jar
[INFO]       Add file: /Users/jan/.m2/repository/xalan/xalan/2.7.2/xalan-2.7.2.jar
[INFO]       Add file: /Users/jan/.m2/repository/xalan/serializer/2.7.2/serializer-2.7.2.jar
[INFO] Clean target and copy resources from source base dir: /Users/jan/development/eclipse-workspace-talendcomp/talendcomp_tFileExcel/talend_component
[INFO]     Source and target component folder are the same. No cleanup proceeded and no files copied.
[INFO] Read component XML configuration...
[INFO]     XML configuration file: /Users/jan/development/eclipse-workspace-talendcomp/talendcomp_tFileExcel/talend_component/tFileExcelWorkbookOpen/tFileExcelWorkbookOpen_java.xml sucessfully read
[INFO] Remove previous jars from component...
[INFO]     13 old jars deleted.
[INFO] Copy jars into component...
[INFO]     13 jars copied.
[INFO] Process component XML configuration...
[INFO]     setup imports removing  existing values ...
[INFO]     setup release and version info...
[INFO]     Done.
[INFO] Write back component XML configuration...
[INFO]     XML configuration file: /Users/jan/development/eclipse-workspace-talendcomp/talendcomp_tFileExcel/talend_component/tFileExcelWorkbookOpen/tFileExcelWorkbookOpen_java.xml successfully written.
[INFO] Check message properties...
[INFO]     Read message properties file: /Users/jan/development/eclipse-workspace-talendcomp/talendcomp_tFileExcel/talend_component/tFileExcelWorkbookOpen/tFileExcelWorkbookOpen_messages.properties
[INFO] Check JET files from component dir: /Users/jan/development/eclipse-workspace-talendcomp/talendcomp_tFileExcel/src/main/components/tFileExcelWorkbookOpen
[INFO]     check file: /Users/jan/development/eclipse-workspace-talendcomp/talendcomp_tFileExcel/src/main/components/tFileExcelWorkbookOpen/tFileExcelWorkbookOpen_begin.javajet
[INFO] Copy component files to studio custom component dir: /Users/jan/development/talend_user_components/
[INFO]     20 files copied.
[INFO] Finished.
```

The result e.g. a list of the necessary modules within a Talend component XML configuration.
Example:
```xml
  <CODEGENERATION> 
    <IMPORTS> 
      <IMPORT NAME="jlo-talendcomp-excel" MODULE="jlo-talendcomp-excel-13.5.jar" MVN="mvn:de.jlo.talendcomp/jlo-talendcomp-excel/13.5" REQUIRED="true"/>
      <IMPORT NAME="poi-ooxml" MODULE="poi-ooxml-4.1.2.jar" MVN="mvn:org.apache.poi/poi-ooxml/4.1.2" REQUIRED="true"/>
      <IMPORT NAME="poi" MODULE="poi-4.1.2.jar" MVN="mvn:org.apache.poi/poi/4.1.2" REQUIRED="true"/>
      <IMPORT NAME="commons-codec" MODULE="commons-codec-1.13.jar" MVN="mvn:commons-codec/commons-codec/1.13" REQUIRED="true"/>
      <IMPORT NAME="commons-collections4" MODULE="commons-collections4-4.4.jar" MVN="mvn:org.apache.commons/commons-collections4/4.4" REQUIRED="true"/>
      <IMPORT NAME="commons-math3" MODULE="commons-math3-3.6.1.jar" MVN="mvn:org.apache.commons/commons-math3/3.6.1" REQUIRED="true"/>
      <IMPORT NAME="SparseBitSet" MODULE="SparseBitSet-1.2.jar" MVN="mvn:com.zaxxer/SparseBitSet/1.2" REQUIRED="true"/>
      <IMPORT NAME="poi-ooxml-schemas" MODULE="poi-ooxml-schemas-4.1.2.jar" MVN="mvn:org.apache.poi/poi-ooxml-schemas/4.1.2" REQUIRED="true"/>
      <IMPORT NAME="xmlbeans" MODULE="xmlbeans-3.1.0.jar" MVN="mvn:org.apache.xmlbeans/xmlbeans/3.1.0" REQUIRED="true"/>
      <IMPORT NAME="commons-compress" MODULE="commons-compress-1.19.jar" MVN="mvn:org.apache.commons/commons-compress/1.19" REQUIRED="true"/>
      <IMPORT NAME="curvesapi" MODULE="curvesapi-1.06.jar" MVN="mvn:com.github.virtuald/curvesapi/1.06" REQUIRED="true"/>
      <IMPORT NAME="xalan" MODULE="xalan-2.7.2.jar" MVN="mvn:xalan/xalan/2.7.2" REQUIRED="true"/>
      <IMPORT NAME="serializer" MODULE="serializer-2.7.2.jar" MVN="mvn:xalan/serializer/2.7.2" REQUIRED="true"/>
    </IMPORTS> 
  </CODEGENERATION>  
```

This plugin is available via Maven Central since 2018-07-20 and update at 2022-12-13

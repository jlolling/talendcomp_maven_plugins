#Maven plugin to setup a Talend component

This plugin is bound to the phase `install` and performes for a talend component following tasks:
* adds the necessary libraries according to the project dependencies to the component and deletes obsolete libs
* Setup the IMPORT tags in the component XML configuration file
* Setup the release date tag of the component and adds in the advanced settings a label showing release date and version
* Setup the component version according to the project version
* Checks the message properties (only the default) if all necessary message keys are present

To use it in your own Talend component project use this plugin configuration:
(If you have more than one component in your project, use multiple executions, this example assumes 2 components)
```
	<plugin>
		<groupId>de.cimt.talendcomp</groupId>
		<artifactId>cimt-talendcomp-maven-plugin</artifactId>
		<version>1.0</version>
		<executions>
			<execution>
				<id>execution-1</id>
				<phase>install</phase>
				<goals>
					<goal>component</goal>
				</goals>
				<configuration>
					<componentName>tComponent1</componentName>
					<componentBaseDir>${project.basedir}/talend_component</componentBaseDir>
					<componentVersion>${project.version}</componentVersion>
					<componentReleaseDate>20170106</componentReleaseDate>
					<noJars>false</noJars>
					<checkMessageProperties>true</checkMessageProperties>
				</configuration>
			</execution>
			<execution>
				<id>execution-2</id>
				<phase>install</phase>
				<goals>
					<goal>component</goal>
				</goals>
				<configuration>
					<componentName>tComponent2</componentName>
					<noJars>true</noJars>
					<checkMessageProperties>true</checkMessageProperties>
				</configuration>
			</execution>
		</executions>
	</plugin>

```
Only the configuration parameter `componentName` is mandatory.

The other parameters will be filled with default values (the current values here).

The parameter `componentReleaseDate` will be filled with the current date if it is missing.

The parameter `noJars` prevents the plugin from adding jar files and adding IMPORT tags. This is useful if you have multiple components depending from another component which carries all libraries.

The result e.g. a list of the necessary modules within a Talend component XML configuration.
Example:
```
  <CODEGENERATION> 
    <IMPORTS> 
      <IMPORT NAME="poi-ooxml" MODULE="poi-ooxml-3.15.jar" REQUIRED="true"/>
      <IMPORT NAME="xalan" MODULE="xalan-2.7.2.jar" REQUIRED="true"/>
    </IMPORTS> 
  </CODEGENERATION>  
```

This plugin is not yet available via Maven Central. I am going to publish it there.
For the moment you can download it here:
http://jan-lolling.de/talend/cimt-talendcomp-maven-plugin-1.1.jar

and install it with this command (please setup your file path here according to your download location)
```
mvn install:install-file -Dfile=Downloads/cimt-talendcomp-maven-plugin-1.1.jar -DgroupId=de.cimt.talendcomp \
    -DartifactId=cimt-talendcomp-maven-plugin -Dversion=1.1 -Dpackaging=jar
```

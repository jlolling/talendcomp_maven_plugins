#Maven plugin to setup a Talend component

This plugin does for a talend component:
* adds the necessary libraries according to the project dependencies to the component
* Setup the IMPORT tags in the component XML configuration file
* Setup the release date tag of the component
* Setup the component version cording to the project version

To use it in your own Talend component project use this plugin configuration:
```
	<plugin>
		<groupId>de.cimt.talendcomp</groupId>
		<artifactId>cimt-talendcomp-maven-plugin</artifactId>
		<version>1.0</version>
		<configuration>
			<componentName>tTest</componentName>
			<componentBaseDir>${project.basedir}/talend_component</componentBaseDir>
			<componentVersion>${project.version}</componentVersion>
			<componentReleaseDate>20170106</componentReleaseDate>
		</configuration>
		<executions>
			<execution>
				<phase>install</phase>
				<goals>
					<goal>component</goal>
				</goals>
			</execution>
		</executions>
	</plugin>

```
Only the configuration parameter `componentName` is mandatory.
The other parameters will be filled with default values (the current values here).
The parameter `componentReleaseDate` will be filled with the current date.
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

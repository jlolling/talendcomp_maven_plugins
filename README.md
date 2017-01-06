#Maven plugin to setup a Talend component

This plugin configures for a talend component:
1. adds the necessary libraries according to the project dependencies to the component
2. Setup the IMPORT tags in the component XML configuration file
3. Setup the release date tag of the component
4. Setup the component version cording to the project version

To use it in your own Talend component project use this plugin configuration:
```
	<plugin>
		<groupId>de.cimt.talendcomp</groupId>
		<artifactId>cimt-talendcomp-maven-plugin</artifactId>
		<version>1.0</version>
		<configuration>
			<componentName>tTest</componentName>
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
It assumes you have a folder: `talend_component`  foolder and here a folder of your Talend component.


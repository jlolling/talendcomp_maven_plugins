package de.cimt.talendcomp.dev.maven;

import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import de.cimt.talendcomp.dev.ComponentUtil;

@Mojo( name = "component", requiresDependencyResolution = ResolutionScope.COMPILE, defaultPhase = LifecyclePhase.INSTALL )
@Execute( goal = "component", phase = LifecyclePhase.INSTALL )
public class ComponentDeploymentMojo extends AbstractMojo {

	@Parameter
	private String componentName;
	@Parameter(defaultValue="${project.basedir}/talend_component")
	private String componentBaseDir;
	@Parameter(defaultValue="${project.version}")
	private String componentVersion;
	@Parameter
	private String componentReleaseDate;
	@Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (componentName == null) {
			throw new MojoFailureException("componentName is not set!");
		}
		if (componentBaseDir == null) {
			throw new MojoFailureException("componentBaseDir is not set!");
		}
		getLog().info("Setup component: " + componentName + " with base dir: " + componentBaseDir);
		ComponentUtil util = new ComponentUtil();
		util.setComponentBaseDir(componentBaseDir);
		util.setComponentName(componentName);
		util.setComponentVersion(componentVersion);
		util.setComponentReleaseDate(componentReleaseDate);
		getLog().info("Check dependencies and collect artifact jar files...");
		Artifact mainArtifact = project.getArtifact();
		if (mainArtifact != null) {
			try {
				util.addJarFile(mainArtifact.getFile().getAbsolutePath());
			} catch (Exception e) {
				throw new MojoExecutionException("Main artifact: " + mainArtifact + ": failed get jar file: " + mainArtifact.getFile().getAbsolutePath());
			}
		}
		@SuppressWarnings("unchecked")
		Set<Artifact> artifacts = project.getArtifacts();
		for (Artifact a : artifacts) {
			if ("provided".equals(a.getScope()) == false) {
				String path = a.getFile().getAbsolutePath();
				try {
					util.addJarFile(path);
					getLog().info("    file: " + path + " scope: " + a.getScope());
				} catch (Exception e) {
					throw new MojoExecutionException("Artifact: " + a + ": failed get jar file: " + path);
				}
			}
		}
		getLog().info("Read component XML configuration...");
		try {
			String xmlFilePath = util.readXmlConfiguration();
			getLog().info("XML configuration file: " + xmlFilePath + " sucessfully read");
		} catch (Exception e) {
			MojoFailureException me = new MojoFailureException("Read XML configuration failed: " + e.getMessage(), e);
			throw me;
		}
		getLog().info("Remove previous jars from component...");
		try {
			int count = util.clearComponentJars();
			getLog().info(count + " old jars deleted.");
		} catch (Exception e) {
			MojoFailureException me = new MojoFailureException("Remove previous jars from component failed: " + e.getMessage(), e);
			throw me;
		}
		getLog().info("Copy jars into component...");
		try {
			int count = util.copyJars();
			getLog().info(count + " jars copied.");
		} catch (Exception e) {
			MojoFailureException me = new MojoFailureException("Copy jars into component failed: " + e.getMessage(), e);
			throw me;
		}
		getLog().info("Setup component XML coniguration...");
		try {
			util.setupXML();
			getLog().info("Done.");
		} catch (Exception e) {
			MojoFailureException me = new MojoFailureException("Setup component XML coniguration failed: " + e.getMessage(), e);
			throw me;
		}
		getLog().info("Write back component XML coniguration...");
		try {
			String xmlFilePath = util.writeXmlConfiguration();
			getLog().info("XML configuration file: " + xmlFilePath + " sucessfully written.");
		} catch (Exception e) {
			MojoFailureException me = new MojoFailureException("Write back component XML coniguration failed: " + e.getMessage(), e);
			throw me;
		}
		getLog().info("Done.");
	}
	
}

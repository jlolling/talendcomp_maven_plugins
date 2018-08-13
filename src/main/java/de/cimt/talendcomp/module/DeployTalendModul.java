package de.cimt.talendcomp.module;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.deploy.AbstractDeployMojo;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "deployModule", requiresDependencyResolution = ResolutionScope.COMPILE, defaultPhase = LifecyclePhase.DEPLOY, threadSafe = false)
@Execute(goal = "deployModule", phase = LifecyclePhase.DEPLOY)
public class DeployTalendModul extends AbstractDeployMojo {
	
	private String nexusRepoTalendCustomLibsSnapshot = "talend-custom-libs-snapshot";

	@Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;
	@Parameter( defaultValue = "org.talend.libraries", readonly = false, required = false )
    private String talendGroupId;
	@Parameter( defaultValue = "6.0.0-SNAPSHOT", readonly = false, required = false )
    private String talendVersion;
	@Parameter( defaultValue = "http://localhost:8081/nexus/content/repositories", readonly = false, required = true )
    private String talendNexusUrl;
	
	private Artifact getTalendArtifact() {
        Artifact artifact = project.getArtifact();
        String packaging = project.getPackaging();
        File pomFile = project.getFile();
        String talendArtifactId = artifact.getArtifactId() + "-" + artifact.getVersion();
		Artifact talendArtifact = new DefaultArtifact(talendGroupId, talendArtifactId, VersionRange.createFromVersion(talendVersion), Artifact.SCOPE_SYSTEM, "jar", null, null);
		return talendArtifact;
	}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
	}
	
}

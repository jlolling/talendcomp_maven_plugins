package de.cimt.talendcomp.module;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.deploy.AbstractDeployMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.validation.ModelValidator;

@Mojo(name = "deployModule", requiresDependencyResolution = ResolutionScope.COMPILE, defaultPhase = LifecyclePhase.INSTALL, threadSafe = false)
@Execute(goal = "deployModule", phase = LifecyclePhase.INSTALL)
public class DeployTalendModul extends AbstractDeployMojo {
	
	@Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;
    @Parameter( property = "repositoryLayout", defaultValue = "default" )
    private String repositoryLayout;
	@Parameter( defaultValue = "talend-custom-libs-snapshot", readonly = false, required = false )
	private String nexusRepoTalendCustomLibsSnapshot = null;
	@Parameter( defaultValue = "org.talend.libraries", readonly = false, required = false )
    private String talendGroupId;
	@Parameter( defaultValue = "6.0.0-SNAPSHOT", readonly = false, required = false )
    private String talendVersion;
	@Parameter( defaultValue = "http://localhost:8081/nexus/content/repositories", readonly = false, required = true )
    private String url; 
    @Component
    private ModelValidator modelValidator;
    @Component
    protected ArtifactInstaller installer;
    
	private Artifact getTalendArtifact() {
        Artifact artifact = project.getArtifact();
        String packaging = project.getPackaging();
        String talendArtifactId = artifact.getArtifactId() + "-" + artifact.getVersion();
		Artifact talendArtifact = new DefaultArtifact(talendGroupId, talendArtifactId, VersionRange.createFromVersion(talendVersion), Artifact.SCOPE_SYSTEM, "jar", null, artifact.getArtifactHandler());
		talendArtifact.setFile(artifact.getFile());
		return talendArtifact;
	}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// install file
		if (getLocalRepository() == null) {
			throw new MojoFailureException("Local repository is not set");
		}
		Artifact artifact = getTalendArtifact();
		getLog().info("############ Install artifact as Talend module: " + artifact);
		try {
			File file = artifact.getFile();
			if (file == null) {
				throw new MojoFailureException("Artifact " + artifact + " file is null");
				//file = new File(getLocalRepository().getBasedir()+ "/" + getLocalRepository().pathOf(artifact));
			}
			if (file.exists() == false) {
				throw new MojoFailureException("Artifact " + artifact + " file-path: " + file.getAbsolutePath() + " does not exist");
			}
			installer.install( artifact.getFile(), artifact, getLocalRepository() );
		} catch (ArtifactInstallationException e) {
			throw new MojoFailureException(e.getMessage(), e);
		}
		

	}
	
}

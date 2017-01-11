package de.cimt.talendcomp.dev.maven;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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

@Mojo(name = "component", requiresDependencyResolution = ResolutionScope.COMPILE, defaultPhase = LifecyclePhase.PACKAGE)
@Execute(goal = "component", phase = LifecyclePhase.PACKAGE)
public class ComponentDeploymentMojo extends AbstractMojo {

    @Parameter
    private String componentName;
    @Parameter(defaultValue = "${project.basedir}/talend_component")
    private String componentBaseDir;
    @Parameter(defaultValue = "${project.version}")
    private String componentVersion;
    @Parameter
    private String componentReleaseDate;
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;
    @Parameter(defaultValue = "true")
    private boolean addReleaseLabel;
    @Parameter(defaultValue = "true")
    private boolean checkMessageProperties;
    @Parameter(defaultValue = "false")
    private boolean noJars;
    @Parameter
    private String jarExcludePattern;
    private Pattern pattern = null;

    private boolean filterJarFile(String jarFileName) {
        if (jarExcludePattern != null && jarExcludePattern.trim().isEmpty() == false) {
            if (pattern == null) {
                pattern = Pattern.compile(jarExcludePattern, Pattern.CASE_INSENSITIVE);
            }
            return pattern
                    .matcher(jarFileName)
                    .find() == false;
        } else {
            return true;
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (componentName == null) { 
            throw new MojoFailureException("componentName is not set!");
        }
        if (componentBaseDir == null) { // TODO: not sure but null is not possible because of default, or? How about exists?
            throw new MojoFailureException("componentBaseDir is not set!");
        }
        getLog().info("############ Setup component: " + componentName + " with base dir: " + componentBaseDir);
        ComponentUtil util = new ComponentUtil();
        util.setComponentBaseDir(componentBaseDir);
        util.setComponentName(componentName);
        util.setComponentVersion(componentVersion);
        util.setComponentReleaseDate(componentReleaseDate);
        if (noJars == false) {
            getLog().info("Check dependencies and collect artifact jar files...");
            if (jarExcludePattern != null && jarExcludePattern.trim().isEmpty() == false) {
                getLog().info("   use exclude regex pattern: " + jarExcludePattern);
            }
            Artifact mainArtifact = project.getArtifact();
            if (mainArtifact != null) {
                String path = mainArtifact.getFile().getAbsolutePath();
                if (filterJarFile(path)) {
                    try {
                        util.addJarFile(path);
                        getLog().info("    file: " + path);
                    } catch (Exception e) {
                        throw new MojoExecutionException("Main artifact: " + mainArtifact + ": failed get jar file: " + mainArtifact.getFile().getAbsolutePath());
                    }
                }
            }
            @SuppressWarnings("unchecked")
            Set<Artifact> artifacts = project.getArtifacts(); 
            for (Artifact a : artifacts) {
                if ("provided".equals(a.getScope()) == false && "test".equals(a.getScope()) == false) {
                    String path = a.getFile().getAbsolutePath();
                    if (filterJarFile(path)) {
                        try {
                            util.addJarFile(path);
                            getLog().info("    file: " + path);
                        } catch (Exception e) {
                            throw new MojoExecutionException("Artifact: " + a + ": failed get jar file: " + path);
                        }
                    }
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
        if (noJars == false) {
            try {
                getLog().info("Copy jars into component...");
                int count = util.copyJars();
                getLog().info(count + " jars copied.");
            } catch (Exception e) {
                MojoFailureException me = new MojoFailureException("Copy jars into component failed: " + e.getMessage(), e);
                throw me;
            }
        }
        getLog().info("Process component XML configuration...");
        try {
            if (noJars == false) {
                getLog().info("    setup imports...");
                util.setupXMLImports();
            }
            if (addReleaseLabel) {
                getLog().info("    setup release and version info...");
                util.setupXMLReleaseLabel();
            }
            getLog().info("Done.");
        } catch (Exception e) {
            MojoFailureException me = new MojoFailureException("Process component XML configuration failed: " + e.getMessage(), e);
            throw me;
        }
        getLog().info("Write back component XML configuration...");
        try {
            String xmlFilePath = util.writeXmlConfiguration();
            getLog().info("XML configuration file: " + xmlFilePath + " sucessfully written.");
        } catch (Exception e) {
            MojoFailureException me = new MojoFailureException("Write back component XML configuration failed: " + e.getMessage(), e);
            throw me;
        }
        if (checkMessageProperties) {
            getLog().info("Check message properties...");
            try {
                String fileName = util.checkMissingMessageProperties();
                getLog().info("Read message properties file: " + fileName);
                List<String> missingProperties = util.getListMissingMessageProperties();
                if (missingProperties != null && missingProperties.isEmpty() == false) {
                    StringBuilder sb = new StringBuilder();
                    for (String key : missingProperties) {
                        sb.append(key);
                        sb.append("\n");
                    }
                    throw new MojoFailureException("Found " + missingProperties.size() + " missing message properties:\n" + sb.toString());
                }
            } catch (Exception e) {
                if (e instanceof MojoFailureException) {
                    throw (MojoFailureException) e;
                } else {
                    MojoFailureException me = new MojoFailureException("Check message message properties failed: " + e.getMessage(), e);
                    throw me;
                }
            }
        }
        getLog().info("Finished.");
    }

}

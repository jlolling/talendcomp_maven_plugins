/**
 * Copyright 2022 Jan Lolling jan.lolling@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cimt.talendcomp.dev.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import de.cimt.talendcomp.dev.JETFileChecker;

@Mojo(name = "component", requiresDependencyResolution = ResolutionScope.COMPILE, defaultPhase = LifecyclePhase.PACKAGE)
@Execute(goal = "component", phase = LifecyclePhase.PACKAGE)
public class ComponentDeploymentMojo extends AbstractMojo {

    @Parameter
    private String componentName;

    @Parameter(defaultValue = "${project.basedir}/target/components/")
    private String componentBaseDir;
    
    @Parameter
    private String studioUserComponentFolder;

    @Parameter(defaultValue = "${project.basedir}/src/main/components/")
    private String copyFromSourceBaseDir;
    
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

    @Parameter(defaultValue = "false")
    private boolean keepImports;
    
    @Parameter(defaultValue = "true")
    private boolean useTalendLibrariesMavenLocation;

    @Parameter(defaultValue = "true")
    private boolean checkJetFiles;
    
    @Parameter(defaultValue = "org.talend.libraries")
    private String talendLibrariesGroupId;

    @Parameter(defaultValue = "6.0.0")
    private String talendLibrariesVersion;
    
    /**
     * Comma separated list of scopes to be excluded. Default to compile, test, system, provided
     */
    @Parameter(defaultValue = "system,test,provided")
    private String excludeScopes;

    /**
     * Collection of Components Dependency needed at runtime. These elements will NOT be bundled with component
     * but registered as dependency
     */
    @Parameter
    private List<CompDependency> compDependencies;

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
        util.setLog(getLog());
        util.setUseTalendLibrariesMavenLocation(useTalendLibrariesMavenLocation);
        util.setTalendLibrariesGroupId(talendLibrariesGroupId);
        util.setTalendLibrariesVersion(talendLibrariesVersion);
        File baseDir = new File(componentBaseDir);
        if (baseDir.isAbsolute() == false) {
        	baseDir = new File(project.getBasedir(), componentBaseDir);
        }
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
                try {
                    String path = mainArtifact.getFile().getAbsolutePath();
                    if (filterJarFile(path)) {
                        try {
                            util.addJarFile(path, mainArtifact.getGroupId(), mainArtifact.getArtifactId(), mainArtifact.getVersion());
                            getLog().info("    file: " + path);
                        } catch (Exception e) {
                            throw new MojoExecutionException("Main artifact: " + mainArtifact + ": failed get jar file: " + mainArtifact.getFile().getAbsolutePath());
                        }
                    }
                } catch (java.lang.NullPointerException npe) {
                    if (!project.getPackaging().equalsIgnoreCase("jar")) {
                        getLog().warn("No local jar is created!");
                    } else {
                        throw new MojoExecutionException("failed to create jar file.");
                    }
                }
            }
        	getLog().debug("Dependencies found:");
            @SuppressWarnings("unchecked")
            Set<Artifact> artifacts = project.getArtifacts(); 
            for (Artifact a : artifacts) {
            	getLog().debug("    " + a.getArtifactId() + ":" + a.getVersion() + " scope: " + a.getScope());
            }
   
            List<String> excludeScopesList = new ArrayList<String>();
            if (excludeScopes != null && !excludeScopes.trim().isEmpty()) {
                excludeScopesList.addAll( Arrays.<String>asList( excludeScopes.toLowerCase().split("\\s*,\\s*") ) );
            }
            getLog().info("Collect project artifacts without scope " + excludeScopesList);
            for (Artifact a : artifacts) {
                if ( !excludeScopesList.contains( a.getScope()) ) {
                    String path = a.getFile().getAbsolutePath();
                    if (filterJarFile(path)) {
                        try {
                            getLog().info("      Add file: " + path);
                            util.addJarFile(path, a.getGroupId(), a.getArtifactId(), a.getVersion());
                        } catch (Exception e) {
                            throw new MojoExecutionException("Artifact: " + a + ": failed get jar file: " + path);
                        }
                    }
                }
            }
        }
        
        try {
            File sourceDir = new File(copyFromSourceBaseDir);
            if (sourceDir.isAbsolute() == false) {
                sourceDir = new File(project.getBasedir().getAbsolutePath(), copyFromSourceBaseDir);
            }
            getLog().info("Clean target and copy resources from source base dir: " + sourceDir.getAbsolutePath());
            util.setComponentSourceBaseDir(sourceDir.getAbsolutePath());
            int count = util.copyResources();
            if (count == -1) {
                getLog().info("    Source and target component folder are the same. No cleanup proceeded and no files copied.");
            } else {
                getLog().info("    " + count + " files copied.");
            }
        } catch (Exception e) {
            MojoFailureException me = new MojoFailureException("Copy resources from source failed: " + e.getMessage(), e);
            throw me;
        }
        getLog().info("Read component XML configuration...");
        try {
            String xmlFilePath = util.readXmlConfiguration();
            getLog().info("    XML configuration file: " + xmlFilePath + " sucessfully read");
        } catch (Exception e) {
            MojoFailureException me = new MojoFailureException("Read XML configuration failed: " + e.getMessage(), e);
            throw me;
        }
        getLog().info("Remove previous jars from component...");
        try {
            int count = util.clearComponentJars();
            getLog().info("    " + count + " old jars deleted.");
        } catch (Exception e) {
            MojoFailureException me = new MojoFailureException("Remove previous jars from component failed: " + e.getMessage(), e);
            throw me;
        }
        getLog().info("Process component XML configuration...");
        try {
            getLog().info("    Setup imports "+ (keepImports ? "keeping" : "removing") +"  existing values ...");
            util.setupXMLImports( keepImports, compDependencies  );
            
            if (addReleaseLabel) {
                getLog().info("    Setup release and version info...");
                util.setupXMLReleaseLabel();
            }
            getLog().info("    Done.");
        } catch (Exception e) {
            MojoFailureException me = new MojoFailureException("Process component XML configuration failed: " + e.getMessage(), e);
            throw me;
        }
        getLog().info("Write back component XML configuration...");
        try {
            String xmlFilePath = util.writeXmlConfiguration();
            getLog().info("    XML configuration file: " + xmlFilePath + " successfully written.");
        } catch (Exception e) {
            MojoFailureException me = new MojoFailureException("Write back component XML configuration failed: " + e.getMessage(), e);
            throw me;
        }
        if (noJars == false) {
            try {
                getLog().info("Copy jars into component...");
                int count = util.copyJars();
                getLog().info("    " + count + " jars copied.");
            } catch (Exception e) {
                MojoFailureException me = new MojoFailureException("Copy jars into component failed: " + e.getMessage(), e);
                throw me;
            }
        }
        if (checkMessageProperties) {
            getLog().info("Check message properties...");
            try {
                String fileName = util.checkMissingMessageProperties();
                getLog().info("    Read message properties file: " + fileName);
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
        if (checkJetFiles) {
            File sourceDir = new File(copyFromSourceBaseDir, componentName);
            if (sourceDir.isAbsolute() == false) {
                sourceDir = new File(project.getBasedir().getAbsolutePath(), copyFromSourceBaseDir);
            }
            getLog().info("Check JET files from component dir: " + sourceDir.getAbsolutePath());
        	JETFileChecker checker = new JETFileChecker(sourceDir.getAbsolutePath());
        	try {
        		File[] jetFiles = checker.listJetFiles();
        		for (File jetFile : jetFiles) {
        			getLog().info("    check file: " + jetFile);
        			String checkResult = checker.checkJetFile(jetFile);
        			if (checkResult != null) {
        				throw new MojoFailureException("Check JET file: " + jetFile.getAbsolutePath() + " failed: " + checkResult);
        			}
        		}
            } catch (Exception e) {
                if (e instanceof MojoFailureException) {
                    throw (MojoFailureException) e;
                } else {
                    MojoFailureException me = new MojoFailureException("Check JET files failed: " + e.getMessage(), e);
                    throw me;
                }
            }
        }
        if (studioUserComponentFolder != null && studioUserComponentFolder.trim().isEmpty() == false) {
        	File dir = new File(studioUserComponentFolder);
        	if (dir.exists() == false) {
        		throw new MojoFailureException("Copy component to target studio folder failed: Directory " + dir.getAbsolutePath() + " does not exist.");
        	} else if (dir.isDirectory() == false) {
        		throw new MojoFailureException("Copy component to target studio folder failed: Path " + dir.getAbsolutePath() + " is not a directory.");
        	}
        	try {
        		getLog().info("Copy component files to studio custom component dir: " + studioUserComponentFolder);
				int numFilesCopied = util.copyComponentFilesToStudio(studioUserComponentFolder);
				getLog().info("    " + numFilesCopied + " files copied.");
			} catch (Exception e) {
				throw new MojoFailureException("Copy component to target studio folder failed.", e);
			}
        }
        getLog().info("Finished.");
    }

}

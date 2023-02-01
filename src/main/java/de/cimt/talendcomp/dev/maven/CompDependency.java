/*
 * Copyright 2018 dkoch.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cimt.talendcomp.dev.maven;

import java.io.File;
import java.util.Objects;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * @author dkoch
 */
public class CompDependency   {
  /**
     * Group Id of Artifact
     *
     * @parameter
     * @required
     */
    private String groupId;

    /**
     * Name of Artifact
     *
     * @parameter
     * @required
     */
    private String artifactId;

    /**
     * Version of Artifact
     *
     * @parameter
     */
    private String version = null;

    /**
     * Type of Artifact (War,Jar,etc)
     *
     * @parameter
     * @required
     */
    private String type = "jar";

    /**
     * Classifier for Artifact (tests,sources,etc)
     *
     * @parameter
     */
    private String classifier;

    /**
     * Location to use for this Artifact. Overrides default location.
     *
     * @parameter
     */
    private File outputDirectory;

    /**
     * Provides ability to change destination file name
     *
     * @parameter
     */
    private String destFileName;

    /**
     * A comma separated list of file patterns to include when unpacking the artifact.
     */
    private String includes;

    /**
     * A comma separated list of file patterns to exclude when unpacking the artifact.
     */
    private String excludes;

    /**
     * Text containing a condition used without modification for attibute required_if.
     * 
     * @parameter
     */
    private String requiredIf;

    /**
     * Marks dependency to be reqired, otherwise missing dedendency will not cause any error.
     * 
     * @parameter
     */
    private boolean required=false;

    /**
     * Default ctor.
     */
    public CompDependency()
    {
        // default constructor
    }

    /**
     * clean up configuration string before it can be tokenized
     * @param str The str which should be cleaned.
     * @return cleaned up string.
     */
    public static String cleanToBeTokenizedString( String str )
    {
        String ret = "";
        if ( !StringUtils.isEmpty( str ) )
        {
            // remove initial and ending spaces, plus all spaces next to commas
            ret = str.trim().replaceAll( "[\\s]*,[\\s]*", "," );
        }

        return ret;
    }

    /**
     * @param artifact {@link Artifact}
     */
    public CompDependency( Artifact artifact )
    {
        this.artifactId = artifact.getArtifactId();
        this.classifier = artifact.getClassifier();
        this.groupId = artifact.getGroupId();
        this.type = artifact.getType();
        this.version = artifact.getVersion();
        try{
            this.destFileName = artifact.getFile().getName();
        }catch(Throwable t){}
    }

    private String filterEmptyString( String in )
    {
        if ( "".equals( in ) )
        {
            return null;
        }
        return in;
    }

    /**
     * @return Returns the artifactId.
     */
    public String getArtifactId()
    {
        return artifactId;
    }

    /**
     * @param theArtifact The artifactId to set.
     */
    public void setArtifactId( String theArtifact )
    {
        this.artifactId = filterEmptyString( theArtifact );
    }

    /**
     * @return Returns the groupId.
     */
    public String getGroupId()
    {
        return groupId;
    }

    /**
     * @param groupId The groupId to set.
     */
    public void setGroupId( String groupId )
    {
        this.groupId = filterEmptyString( groupId );
    }

    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType( String type )
    {
        this.type = filterEmptyString( type );
    }

    /**
     * @return Returns the version.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version The version to set.
     */
    public void setVersion( String version )
    {
        this.version = filterEmptyString( version );
    }

    /**
     * @return Returns the base version.
     */
    public String getBaseVersion()
    {
        return ArtifactUtils.toSnapshotVersion( version );
    }

    /**
     * @return Classifier.
     */
    public String getClassifier()
    {
        return classifier;
    }

    /**
     * @param classifier Classifier.
     */
    public void setClassifier( String classifier )
    {
        this.classifier = filterEmptyString( classifier );
    }

    @Override
    public String toString()
    {
        if ( this.classifier == null )
        {
            return groupId + ":" + artifactId + ":" + Objects.toString( version, "?" ) + ":" + type;
        }
        else
        {
            return groupId + ":" + artifactId + ":" + classifier + ":" + Objects.toString( version, "?" ) + ":"
                + type;
        }
    }

    /**
     * @return Returns the location.
     */
    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * @param outputDirectory The outputDirectory to set.
     */
    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @return Returns the location.
     */
    public String getDestFileName()
    {
        return destFileName!=null ? destFileName : this.artifactId + "-" + this.version + ".jar";
    }

    /**
     * @param destFileName The destFileName to set.
     */
    public void setDestFileName( String destFileName )
    {
        this.destFileName = filterEmptyString( destFileName );
    }

    /**
     * @return Returns a comma separated list of excluded items
     */
    public String getExcludes()
    {
        return cleanToBeTokenizedString( this.excludes );
    }

    /**
     * @param excludes A comma separated list of items to exclude i.e. <code>**\/*.xml, **\/*.properties</code>
     */
    public void setExcludes( String excludes )
    {
        this.excludes = excludes;
    }

    /**
     * @return Returns a comma separated list of included items
     */
    public String getIncludes()
    {
        return cleanToBeTokenizedString( this.includes );
    }

    /**
     * @param includes A comma separated list of items to include i.e. <code>**\/*.xml, **\/*.properties</code>
     */
    public void setIncludes( String includes )
    {
        this.includes = includes;
    }    

    /**
     * 
     * @return attibute value
     */
    public String getRequiredIf() {
        return requiredIf;
    }

    /**
     * 
     * @param requiredIf 
     */
    public void setRequiredIf(String requiredIf) {
        this.requiredIf = requiredIf;
    }

    /**
     * @return when false, missing dependency will not cause any error
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * @param required flag setting dependeny to be required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    
}

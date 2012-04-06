/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.maven.plugins.test.stubs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.ReaderFactory;

/**
 * @author kamesh.sampath
 */
public abstract class LiferayPluginsMavenProjectStub extends MavenProjectStub
{

	private Build build;
	
	

	public LiferayPluginsMavenProjectStub()
	{
		
		File liferayPlugUTDir =
			new File( PlexusTestCase.getBasedir() + "/src/test/resources/unit/" + getProjetPath() + "/" );

		MavenXpp3Reader pomReader = new MavenXpp3Reader();
		Model model;
		try
		{
			File pomFile = new File( liferayPlugUTDir, "pom.xml" );
			model = pomReader.read( ReaderFactory.newXmlReader( pomFile ) );
			model.addProperty( "basedir", PlexusTestCase.getBasedir() + "/target" );
			setModel( model );

			setGroupId( model.getGroupId() );
			setArtifactId( model.getArtifactId() );
			setVersion( model.getVersion() );
			setName( model.getName() == null ? model.getArtifactId() : model.getName() );
			setUrl( model.getUrl() );
			setPackaging( model.getPackaging() );

			ArtifactStub artifact =
				new LiferayPluginsWebArtifact(
					model.getGroupId(), model.getArtifactId(), model.getPackaging(), model.getVersion() );
			artifact.setArtifactHandler( new LiferayArtifactHandlerStub() );
			setArtifact( artifact );

			build = new Build();
			build.setFinalName( model.getArtifactId() );
			build.setDirectory( getBasedir() + "/target" );
			build.setSourceDirectory( getBasedir() + "/src/main/java" );

			build.setOutputDirectory( getBasedir() + "/target/classes" );
			build.setTestSourceDirectory( getBasedir() + "/src/test/java" );
			build.setTestOutputDirectory( getBasedir() + "/target/test-classes" );
			setBuild( build );

			List<String> compileSourceRoots = new ArrayList<String>();
			compileSourceRoots.add( getBasedir() + "/src/main/java" );
			setCompileSourceRoots( compileSourceRoots );

			List<String> testCompileSourceRoots = new ArrayList<String>();
			testCompileSourceRoots.add( getBasedir() + "/src/test/java" );
			setTestCompileSourceRoots( testCompileSourceRoots );

		}
		catch ( Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	protected File getBaseDir()
	{
		File basedir = new File( PlexusTestCase.getBasedir(), "/target/test/unit/" + getProjetPath() + "/" );
		if ( !basedir.exists() )
		{
			basedir.mkdirs();
		}
		return basedir;

	}

	/**
	 * @return the build
	 */
	public Build getBuild()
	{
		return build;
	}

	protected abstract String getProjetPath();

	/*
	 * (non-Javadoc)
	 * @see
	 * org.apache.maven.plugin.testing.stubs.MavenProjectStub#addProjectReference(org.apache.maven.project.MavenProject)
	 */
	@Override
	public void addProjectReference( MavenProject mavenProject )
	{
		mavenProject = getExecutionProject();
		super.addProjectReference( mavenProject );
	}
}

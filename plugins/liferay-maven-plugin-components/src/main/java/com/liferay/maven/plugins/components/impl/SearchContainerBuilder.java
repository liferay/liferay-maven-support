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

package com.liferay.maven.plugins.components.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

import com.liferay.maven.plugins.components.LiferayPortalCodeBuilder;
import com.liferay.maven.plugins.components.TemplateProcessor;
import com.liferay.maven.plugins.util.EnumSearchJavaTemplates;
import com.liferay.maven.plugins.util.EnumSearchJspTemplates;
import com.liferay.maven.plugins.util.JSPUtil;
import com.liferay.maven.plugins.util.JavaClassUtil;
import com.liferay.portal.tools.SourceFormatter;

import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;
import de.hunsicker.jalopy.storage.Convention;
import de.hunsicker.jalopy.storage.ConventionKeys;
import de.hunsicker.jalopy.storage.Environment;

/**
 * The actual code builder class that will generate the code using the {@link TemplateProcessor} and the model passed to the code generation methods
 * @author kamesh.sampath
 */
public class SearchContainerBuilder implements LiferayPortalCodeBuilder
{

	final Logger logger = new ConsoleLogger( Logger.LEVEL_INFO, "com.liferay.maven.plugins.components" );

	private TemplateProcessor templateProcessor;

	/*
	 * (non-Javadoc)
	 * @see
	 * com.liferay.maven.plugins.components.LiferayPortalCodeBuilder#generateJavaFiles(org.apache.maven.project.MavenProject
	 * , java.io.File, java.util.Map)
	 */

	public void generateJavaFiles( MavenProject project, File genSrcDir, Map<String, Object> variableMap )
		throws Exception
	{
		logger.debug( "Template-Processor:" + templateProcessor );

		if ( !genSrcDir.exists() )
		{
			genSrcDir.mkdirs();
		}

		logger.info( "Java files will be generated in " + genSrcDir.getAbsolutePath() );

		// Generate the package
		File fPackage = null;

		String packageName =
			(String) variableMap.get( "packageName" ) == null
				? "search.container" : (String) variableMap.get( "packageName" );

		packageName = JavaClassUtil.checkJavaPackage( packageName );
		fPackage = new File( genSrcDir, packageName );
		if ( !fPackage.exists() )
		{
			fPackage.mkdirs();
		}
		logger.info( "Created package:" + fPackage.getAbsolutePath() );

		if ( fPackage.exists() )
		{
			String strFileName = (String) variableMap.get( "searchName" );

			EnumSearchJavaTemplates[] seachTemplates = EnumSearchJavaTemplates.values();
			for ( EnumSearchJavaTemplates enumSearchTemplate : seachTemplates )
			{
				String className =
					JavaClassUtil.javaFileName( strFileName, enumSearchTemplate.prefix(), enumSearchTemplate.suffix() );
				File javaFile = new File( fPackage, className );
				logger.info( "Generating file:" + javaFile.getAbsolutePath() );
				String content = templateProcessor.processTemplate( enumSearchTemplate.templateKey(), variableMap );
				formatJava( project, packageName, className, javaFile, content, null );
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.liferay.maven.plugins.components.LiferayPortalCodeBuilder#generateJspFiles(org.apache.maven.project.MavenProject
	 * , java.io.File, java.util.Map)
	 */
	public void generateJspFiles( MavenProject project, File jspSourceDir, Map<String, Object> variableMap )
		throws Exception
	{
		boolean overwrite = variableMap.get( "overwrite" ) != null ? true : false;

		String strFileName = (String) variableMap.get( "searchName" );

		logger.debug( "Template-Processor:" + templateProcessor );

		if ( !jspSourceDir.exists() )
		{
			jspSourceDir.mkdirs();
		}

		logger.info( "JSP files will be generated in " + jspSourceDir.getAbsolutePath() );

		EnumSearchJspTemplates[] seachTemplates = EnumSearchJspTemplates.values();
		for ( EnumSearchJspTemplates enumSearchTemplate : seachTemplates )
		{
			String templateKey = enumSearchTemplate.templateKey();
			String jspFileName = JSPUtil.jspFileName( strFileName, "search", templateKey );
			File jspFile = new File( jspSourceDir, jspFileName );
			String content = templateProcessor.processTemplate( templateKey, variableMap );
			if ( !jspFile.exists() || overwrite )
			{
				logger.info( "Generating file:" + jspFile.getAbsolutePath() );
				FileUtils.writeStringToFile( jspFile, content );
			}
			else
			{
				logger.info( "File:" + jspFile.getAbsolutePath() + " already exists, skipping generation" );
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.liferay.maven.plugins.components.LiferayPortalCodeBuilder#formatJava(org.apache.maven.project.MavenProject,
	 * java.lang.String, java.lang.String, java.io.File, java.lang.String, java.util.Map)
	 */
	public void formatJava(
		MavenProject project, String packageName, String className, File javaFile, String content,
		Map<String, Object> jalopySettings ) throws IOException
	{
		String buildOutputDirectory = null;
		if ( project == null )
		{
			buildOutputDirectory = "target";
		}
		else
		{
			buildOutputDirectory = project.getBuild().getOutputDirectory();
		}
		content = SourceFormatter.stripJavaImports( content, packageName, className );
		File tempDirRoot = new File( buildOutputDirectory + "gen-src-temp" );
		File tempDir = new File( tempDirRoot, packageName );
		File tempClassFile = new File( tempDir, className + ".temp" );
		FileUtils.writeStringToFile( tempClassFile, content );

		// Beautify
		StringBuffer buffer = new StringBuffer();
		Jalopy jalopy = new Jalopy();
		jalopy.setFileFormat( FileFormat.UNIX );
		jalopy.setInput( tempClassFile );
		jalopy.setOutput( buffer );

		// Try to see if we have jalopy.xml anwhere in classapath
		File jalopyXmlFile = new File( "tools/jalopy.xml" );

		if ( !jalopyXmlFile.exists() )
		{
			jalopyXmlFile = new File( "../tools/jalopy.xml" );
		}

		if ( !jalopyXmlFile.exists() )
		{
			jalopyXmlFile = new File( "misc/jalopy.xml" );
		}

		if ( !jalopyXmlFile.exists() )
		{
			jalopyXmlFile = new File( "../misc/jalopy.xml" );
		}

		if ( !jalopyXmlFile.exists() )
		{
			jalopyXmlFile = new File( "../../misc/jalopy.xml" );
		}

		try
		{
			Jalopy.setConvention( jalopyXmlFile );
		}
		catch ( FileNotFoundException e )
		{
			// ignore
		}

		if ( jalopySettings == null )
		{
			jalopySettings = new HashMap<String, Object>();
		}

		Environment env = Environment.getInstance();

		// Author

		String author =
			jalopySettings.get( "author" ) != null
				? (String) jalopySettings.get( "author" ) : System.getProperty( "user.name" );

		env.set( "author", author );

		// File name

		env.set( "fileName", javaFile.getName() );

		Convention convention = Convention.getInstance();

		String classMask = "/**\n" +
						" * @author $author$\n" +
						"*/";

		convention.put( ConventionKeys.COMMENT_JAVADOC_TEMPLATE_CLASS, env.interpolate( classMask ) );

		convention.put( ConventionKeys.COMMENT_JAVADOC_TEMPLATE_INTERFACE, env.interpolate( classMask ) );

		jalopy.format();

		String newContent = buffer.toString();

		// Remove double blank lines after the package or last import

		newContent = newContent.replaceFirst( "(?m)^[ \t]*((?:package|import) .*;)\\s*^[ \t]*/\\*\\*", "$1\n\n/**" );

		FileUtils.writeStringToFile( javaFile, newContent );

		if ( tempDir.exists() )
		{
			logger.debug( "Deleting directory:" + tempDirRoot );
			tempDirRoot.deleteOnExit();
		}
	}

}

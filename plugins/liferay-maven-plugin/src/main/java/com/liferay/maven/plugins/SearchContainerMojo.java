/*******************************************************************************
 *   Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *  
 *   This library is free software; you can redistribute it and/or modify it under
 *   the terms of the GNU Lesser General Public License as published by the Free
 *   Software Foundation; either version 2.1 of the License, or (at your option)
 *   any later version.
 *  
 *   This library is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *   FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *   details.
 *  
 *******************************************************************************/

package com.liferay.maven.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.ReaderFactory;

import com.liferay.maven.plugins.components.LiferayPortalCodeBuilder;
import com.liferay.maven.plugins.model.SearchFieldModel;
import com.liferay.maven.plugins.util.SearchTemplateConfiguration;
import com.liferay.maven.plugins.xml.SearchContainerModelParser;
import com.liferay.maven.plugins.xml.SearchContainerModelParser.SearchContainer;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import freemarker.ext.beans.BeansWrapper;

/**
 * This plugin will help generating the liferay-ui:search-container implementation JSP and its related Java classes
 * 
 * @author Kamesh Sampath
 * @goal build-search-container
 * @phase generate-sources
 * @requiresDependencyCollection test
 */
public class SearchContainerMojo extends AbstractMojo implements SearchTemplateConfiguration
{

	/**
	 * The maven project
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The Liferay Search Container UI builder
	 * 
	 * @component role="com.liferay.maven.plugins.components.LiferayPortalCodeBuilder" hint="search-container"
	 */
	private LiferayPortalCodeBuilder searchContainerBuilder;

	/**
	 * The directory where the generated JSP files will be placed
	 * 
	 * @parameter default-value="${basedir}/src/main/webapp/html/portlet/"
	 */
	private File webContentDirectory;

	/**
	 * The directory where the files will be generated
	 * 
	 * @parameter alias="gen-src-dir" default-value="${basedir}/src/main/java"
	 */
	private File warSourceDirectory;

	/**
	 * The search definition file from where the search container definitions will be read
	 * 
	 * @parameter default-value="${basedir}/src/main/resources/search-containers.xml"
	 */
	private File searchDefinition;

	/**
	 * The flag to indicate on whether to overwrite the files generated if the already exists
	 * 
	 * @parameter default-value="false"
	 */
	private Boolean overwrite;

	public void execute() throws MojoExecutionException
	{
		try
		{
			doExecute();
		}
		catch ( Exception e )
		{
			throw new MojoExecutionException( e.getMessage(), e );
		}
	}

	protected void doExecute() throws MojoExecutionException
	{
		getLog().debug( "Om Shakti! Om Ganesha! Jai Hanuman!" );

		try
		{
			getLog().info( "Search Definition File:" + searchDefinition );

			if ( searchDefinition != null && searchDefinition.exists() )
			{
				SearchContainerModelParser modelParser = new SearchContainerModelParser();
				List<SearchContainerModelParser.SearchContainer> containers =
					modelParser.read( ReaderFactory.newXmlReader( searchDefinition ) );
				for ( SearchContainer sModel : containers )
				{

					webContentDirectory = new File( webContentDirectory, sModel.getName() );
					if ( !webContentDirectory.exists() )
					{
						webContentDirectory.mkdirs();
					}
					if ( !warSourceDirectory.exists() )
					{
						warSourceDirectory.mkdirs();
					}

					Map<String, Object> variableMap = new HashMap<String, Object>();
					buildVariableMap( sModel, variableMap );
					searchContainerBuilder.generateJavaFiles( project, warSourceDirectory, variableMap );
					searchContainerBuilder.generateJspFiles( project, webContentDirectory, variableMap );
				}
			}
		}
		catch ( Exception e )
		{
			throw new MojoExecutionException( "Error while generating search container code", e );
		}
	}

	/**
	 * @param sModel
	 * @return
	 */
	private void buildVariableMap( SearchContainer sModel, Map<String, Object> variableMap )
		throws MojoExecutionException
	{
		try
		{

			variableMap.put( "statics", BeansWrapper.getDefaultInstance().getStaticModels() );
			variableMap.put( "searchName", sModel.getName() );
			variableMap.put( "advancedSearch", sModel.getAdvancedSearch() == null ? "false" : "true" );
			for ( SearchFieldModel searchFieldModel : sModel.getSearchFields() )
			{
				String type = searchFieldModel.getFieldType();
				if ( "date".equalsIgnoreCase( type ) )
				{
					// TODO: need to add any other common imports that might be useful
					addDateRelatedImports( sModel );

				}
			}

			variableMap.put( TPL_MAP_PROJECT_KEY, project );
			addDateRelatedImports( sModel );
			sModel.getAdditionalImports().add( sModel.getModelClass() );
			variableMap.put( TPL_MAP_IMPORTS_KEY, sModel.getAdditionalImports() );
			variableMap.put( TPL_MAP_WEBAPP_BASE_DIR_KEY, webContentDirectory );
			variableMap.put( TPL_MAP_PACKAGE_DIR_KEY, sModel.getPackageDirectory() );
			variableMap.put( TPL_MAP_GEN_SRC_DIR_KEY, warSourceDirectory );
			variableMap.put( TPL_MAP_MODEL_CLASS_KEY, sModel.getModelClass() );
			variableMap.put( TPL_MAP_MODEL_PRIMARY_KEY, sModel.getModelPK() );
			variableMap.put( TPL_MAP_OVERWRITE_KEY, overwrite );
			variableMap.put( TPL_MAP_SEARCH_PARAMETERS_KEY, sModel.getSearchFields() );
			variableMap.put( TPL_MAP_DELTA_KEY, sModel.getDelta() );
			variableMap.put( TPL_MAP_EMPTY_MESSAGE_KEY, sModel.getEmptyResultsMessage() );

			getLog().debug( "Variable Map:" + variableMap );
		}
		catch ( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param sModel
	 */
	private void addDateRelatedImports( SearchContainer sModel )
	{
		List<String> additionalImports = sModel.getAdditionalImports();

		if ( !additionalImports.contains( "java.util.Date" ) )
		{
			additionalImports.add( "java.util.Date" );

		}

		if ( !additionalImports.contains( "java.text.DateFormat" ) )
		{
			additionalImports.add( "java.text.DateFormat" );

		}

		if ( !additionalImports.contains( "java.util.Locale" ) )
		{
			additionalImports.add( "java.util.Locale" );

		}

		if ( !additionalImports.contains( "java.util.TimeZone" ) )
		{
			additionalImports.add( "java.util.TimeZone" );

		}
		if ( !additionalImports.contains( "com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" ) )
		{
			additionalImports.add( "com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" );
		}
	}
}

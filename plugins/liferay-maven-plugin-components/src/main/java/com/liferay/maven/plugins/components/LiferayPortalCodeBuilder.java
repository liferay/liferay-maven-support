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

package com.liferay.maven.plugins.components;

import java.io.File;
import java.util.Map;

import org.apache.maven.project.MavenProject;

/**
 * The interface that will needs to be implemented by the various Code Builders which wil be used by the mojos to generate Liferay
 * Portal specific code
 * 
 * @author kamesh.sampath
 */
public interface LiferayPortalCodeBuilder
{

	String ROLE = LiferayPortalCodeBuilder.class.getName();

	/**
	 * This method is used to generate Java files based on Templates
	 * 
	 * @param project
	 *            - the maven project reference
	 * @param genSrcDir
	 *            - the java source directory where the files will be generated
	 * @param variableMap
	 *            - the model data to templates
	 * @throws Exception
	 *             - an exception that might occur during template to code generation
	 */
	public void generateJavaFiles( MavenProject project, File genSrcDir, Map<String, Object> variableMap )
		throws Exception;

	/**
	 * This method is used to generate JSP files based on Templates
	 * 
	 * @param project
	 *            - the maven project reference
	 * @param jspSoureDirectory
	 *            the web application directory where the jsp files be generated
	 * @param variableMap
	 *            - the model data to templates
	 * @throws Exception
	 *             - an exception that might occur during template to code generation
	 */
	public void generateJspFiles( MavenProject project, File jspSoureDirectory, Map<String, Object> variableMap )
		throws Exception;

	/**
	 * This method is used to format the java code base using Jalopy
	 * 
	 * @param project
	 *            the maven project reference
	 * @param packageName
	 *            - the java package name
	 * @param className
	 *            - the java class file
	 * @param javaFile
	 *            - the File System location of the generated Java File
	 * @param content
	 *            - the content of the Java class
	 * @param jalopySettings
	 *            - Jalopy settings to use
	 * @throws Exception
	 *             - any error that might occur during formatting
	 */
	public void formatJava(
		MavenProject project, String packageName, String className, File javaFile, String content,
		Map<String, Object> jalopySettings ) throws Exception;
}

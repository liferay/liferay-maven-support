/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.maven.plugins;

import com.liferay.maven.plugins.util.CopyTask;
import com.liferay.maven.plugins.util.FileUtil;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;

import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

/**
 * @author Mika Koivisto
 * @goal   build-ext
 */
public class ExtBuilderMojo extends AbstractLiferayMojo {

	protected void copyJarAndClasses(
			Artifact artifact, File jarDir, String jarName)
		throws Exception {

		File serviceJarFile = new File(jarDir, jarName);

		FileUtil.copyFile(artifact.getFile(), serviceJarFile);

		File classesDir = new File(jarDir, "classes");

		classesDir.mkdirs();

		String[] excludes = {
			"META-INF/**", "portal-*.properties", "system-*.properties"
		};

		unpack(artifact.getFile(), classesDir, excludes, null);
	}

	protected void copyUtilLibrary(
			Artifact artifact, File utilDir, File implClassesDir,
			String utilJarName)
		throws Exception {

		File utilJarFile = new File(utilDir, "ext-" + utilJarName);

		FileUtil.copyFile(artifact.getFile(), utilJarFile);

		File dependencyUtilJarFile = new File(
			implClassesDir, "ext-" + pluginName + "-" + utilJarName);

		FileUtil.copyFile(artifact.getFile(), dependencyUtilJarFile);
	}

	protected void doExecute() throws Exception {
		if (dependencyAddVersionAndClassifier) {
			dependencyAddVersion = true;
			dependencyAddClassifier = true;
		}

		File implDir = new File(webappDir, "WEB-INF/ext-impl");

		implDir.mkdirs();

		File implClassesDir = new File(
			implDir, "classes/com/liferay/portal/deploy/dependencies");

		implClassesDir.mkdirs();

		File globalLibDir = new File(webappDir, "WEB-INF/ext-lib/global");

		globalLibDir.mkdirs();

		File portalLibDir = new File(webappDir, "WEB-INF/ext-lib/portal");

		portalLibDir.mkdirs();

		File serviceDir = new File(webappDir, "WEB-INF/ext-service");

		serviceDir.mkdirs();

		File sqlDir = new File(webappDir, "WEB-INF/sql");

		sqlDir.mkdirs();

		File utilBridgesDir = new File(webappDir, "WEB-INF/ext-util-bridges");

		utilBridgesDir.mkdirs();

		File utilJavaDir = new File(webappDir, "WEB-INF/ext-util-java");

		utilJavaDir.mkdirs();

		File utilTaglibDir = new File(webappDir, "WEB-INF/ext-util-taglib");

		utilTaglibDir.mkdirs();

		File webDir = new File(webappDir, "WEB-INF/ext-web/docroot");

		webDir.mkdirs();

		workDir.mkdirs();

		String groupId = project.getGroupId();

		File extImplFile = null;

		for (Object dependencyObj : project.getDependencies()) {
			Dependency dependency = (Dependency)dependencyObj;

			if (!groupId.equals(dependency.getGroupId())) {
				continue;
			}

			Artifact artifact = resolveArtifact(dependency);

			String artifactId = artifact.getArtifactId();

			if (artifactId.endsWith("ext-impl")) {
				extImplFile = artifact.getFile();

				copyJarAndClasses(artifact, implDir, "ext-impl.jar");

				copyLibraryDependencies(
					portalLibDir, artifact, dependencyAddVersion,
					dependencyAddClassifier, dependencyCopyTransitive);
			}
			else if (artifactId.endsWith("ext-lib-global")) {
				copyLibraryDependencies(
					globalLibDir, artifact, dependencyAddVersion,
					dependencyAddClassifier, dependencyCopyTransitive);
			}
			else if (artifactId.endsWith("ext-lib-portal")) {
				copyLibraryDependencies(
					portalLibDir, artifact, dependencyAddVersion,
					dependencyAddClassifier, dependencyCopyTransitive);
			}
			else if (artifactId.endsWith("ext-service")) {
				copyJarAndClasses(artifact, serviceDir, "ext-service.jar");

				copyLibraryDependencies(
					globalLibDir, artifact, dependencyAddVersion,
					dependencyAddClassifier, dependencyCopyTransitive);
			}
			else if (artifactId.endsWith("ext-util-bridges")) {
				copyUtilLibrary(
					artifact, utilBridgesDir, implClassesDir,
					"util-bridges.jar");

				copyLibraryDependencies(
					portalLibDir, artifact, dependencyAddVersion,
					dependencyAddClassifier, dependencyCopyTransitive);
			}
			else if (artifactId.endsWith("ext-util-java")) {
				copyUtilLibrary(
					artifact, utilJavaDir, implClassesDir, "util-java.jar");

				copyLibraryDependencies(
					portalLibDir, artifact, dependencyAddVersion,
					dependencyAddClassifier, dependencyCopyTransitive);
			}
			else if (artifactId.endsWith("ext-util-taglib")) {
				copyUtilLibrary(
					artifact, utilTaglibDir, implClassesDir, "util-taglib.jar");

				copyLibraryDependencies(
					portalLibDir, artifact, dependencyAddVersion,
					dependencyAddClassifier, dependencyCopyTransitive);
			}
			else if (artifactId.endsWith("ext-web")) {
				String[] excludes = new String[] {"META-INF/**"};

				unpack(artifact.getFile(), webDir, excludes, null);

				copyLibraryDependencies(
					portalLibDir, artifact, dependencyAddVersion,
					dependencyAddClassifier, dependencyCopyTransitive);
			}
		}

		unpack(
			extImplFile, workDir, null,
			new String[] {"portal-*.properties", "system-*.properties"});

		CopyTask.copyDirectory(
			workDir, new File(webDir, "WEB-INF/classes"),
			"portal-*.properties,system-*.properties", null, true, true);

		if (sqlSourceDir.exists() && sqlSourceDir.isDirectory()) {
			FileUtil.copyDirectory(sqlSourceDir, sqlDir);
		}

		String dirName = webappDir.getAbsolutePath() + "/WEB-INF";

		String[] args = {dirName, dirName, pluginName};

		executeTool(
			"com.liferay.portal.tools.ExtInfoBuilder", getToolsClassLoader(),
			args);
	}

	protected void unpack(
			File srcFile, File destDir, String[] excludes, String[] includes)
		throws Exception {

		UnArchiver unArchiver = archiverManager.getUnArchiver(srcFile);

		unArchiver.setDestDirectory(destDir);
		unArchiver.setSourceFile(srcFile);

		IncludeExcludeFileSelector includeExcludeFileSelector =
			new IncludeExcludeFileSelector();

		includeExcludeFileSelector.setExcludes(excludes);
		includeExcludeFileSelector.setIncludes(includes);

		unArchiver.setFileSelectors(
			new FileSelector[] {includeExcludeFileSelector});

		unArchiver.extract();
	}

	/**
	 * @parameter default-value="false"
	 */
	private boolean dependencyAddClassifier;

	/**
	 * @parameter default-value="false"
	 */
	private boolean dependencyAddVersion;

	/**
	 * @parameter default-value="false"
	 */
	private boolean dependencyAddVersionAndClassifier;

	/**
	 * @parameter default-value="false"
	 */
	private boolean dependencyCopyTransitive;

	/**
	 * @parameter default-value="${project.artifactId}" expression="${pluginName}"
	 * @required
	 */
	private String pluginName;

	/**
	 * @parameter default-value="${basedir}/src/main/webapp/WEB-INF/sql"
	 * @required
	 */
	private File sqlSourceDir;

	/**
	 * @parameter default-value="${project.build.directory}/${project.build.finalName}"
	 * @required
	 */
	private File webappDir;

}
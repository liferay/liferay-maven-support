/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ExtInfoBuilder;
import com.liferay.util.ant.CopyTask;

import java.io.File;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

/**
 * @author Mika Koivisto
 * @goal   build-ext
 */
public class ExtBuilderMojo extends AbstractLiferayMojo {

	protected void doExecute() throws Exception {
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
			}
			else if (artifactId.endsWith("ext-lib-global")) {
				copyLibraryDependencies(globalLibDir, artifact);
			}
			else if (artifactId.endsWith("ext-lib-portal")) {
				copyLibraryDependencies(portalLibDir, artifact);
			}
			else if (artifactId.endsWith("ext-service")) {
				copyJarAndClasses(artifact, serviceDir, "ext-service.jar");
			}
			else if (artifactId.endsWith("ext-util-bridges")) {
				copyUtilLibrary(
					artifact, utilBridgesDir, implClassesDir,
					"util-bridges.jar");
			}
			else if (artifactId.endsWith("ext-util-java")) {
				copyUtilLibrary(
					artifact, utilJavaDir, implClassesDir,
					"util-java.jar");
			}
			else if (artifactId.endsWith("ext-util-taglib")) {
				copyUtilLibrary(
					artifact, utilTaglibDir, implClassesDir,
					"util-taglib.jar");
			}
			else if (artifactId.endsWith("ext-web")) {
				String[] excludes = new String[] {"META-INF/**"};

				unpack(artifact.getFile(), webDir, excludes, null);
			}
		}

		unpack(
			extImplFile, workDir, null,
			new String[] {"portal-*.properties", "system-*.properties"});

		CopyTask.copyDirectory(
			workDir, new File(webDir, "WEB-INF/classes"),
			"portal-*.properties,system-*.properties", null);

		FileUtil.copyDirectory(sqlSourceDir, sqlDir);

		String dirName = webappDir.getAbsolutePath() + "/WEB-INF";

		ExtInfoBuilder infoBuilder = new ExtInfoBuilder(
			dirName, dirName, pluginName);
	}

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

	protected void copyLibraryDependencies(File libDir, Artifact artifact)
		throws Exception {

		MavenProject libProject = resolveProject(artifact);

		List<Dependency> dependencies = libProject.getDependencies();

		for (Dependency dependency : dependencies) {
			String scope = dependency.getScope();

			if (Validator.isNotNull(scope) &&
				(scope.equalsIgnoreCase("provided") ||
				 scope.equalsIgnoreCase("test"))) {

				continue;
			}

			String type = dependency.getType();

			if (type.equalsIgnoreCase("pom")) {
				continue;
			}

			Artifact libArtifact = resolveArtifact(dependency);

			String libJarFileName = libArtifact.getArtifactId();

			if (dependencyAddVersion || dependencyAddVersionAndClassifier) {
				if (Validator.isNotNull(libArtifact.getVersion())) {
					libJarFileName += "-" + libArtifact.getVersion();
				}
			}

			if (dependencyAddClassifier || dependencyAddVersionAndClassifier) {
				if (Validator.isNotNull(libArtifact.getClassifier())) {
					libJarFileName += "-" + libArtifact.getClassifier();
				}
			}

			File libArtifactFile = libArtifact.getFile();

			libJarFileName +=
				"." + FileUtil.getExtension(libArtifactFile.getName());

			File libJarFile = new File(libDir, libJarFileName);

			FileUtil.copyFile(libArtifact.getFile(), libJarFile);
		}
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

	/**
	 * @parameter default-value="${project.build.directory}/liferay-work"
	 * @required
	 */
	private File workDir;

}
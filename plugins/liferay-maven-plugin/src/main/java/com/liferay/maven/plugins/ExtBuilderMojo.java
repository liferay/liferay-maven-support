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

package com.liferay.maven.plugins;

import com.liferay.portal.tools.ExtInfoBuilder;
import com.liferay.portal.util.FileImpl;
import com.liferay.util.ant.CopyTask;

import java.io.File;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

/**
 * @author Mika Koivisto
 * @goal   build-ext
 */
public class ExtBuilderMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException {
		try {
			doExecute();
		}
		catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

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

			if (artifact.getArtifactId().endsWith("ext-impl")) {
				extImplFile = artifact.getFile();

				copyJarAndClasses(artifact, implDir, "ext-impl.jar");
			}
			else if (artifact.getArtifactId().endsWith("ext-lib-global")) {
				copyLibraryDependencies(globalLibDir, artifact);
			}
			else if (artifact.getArtifactId().endsWith("ext-lib-portal")) {
				copyLibraryDependencies(portalLibDir, artifact);
			}
			else if (artifact.getArtifactId().endsWith("ext-service")) {
				copyJarAndClasses(artifact, serviceDir, "ext-service.jar");
			}
			else if (artifact.getArtifactId().endsWith("ext-util-bridges")) {
				copyUtilLibrary(
					artifact, utilBridgesDir, implClassesDir,
					"util-bridges.jar");
			}
			else if (artifact.getArtifactId().endsWith("ext-util-java")) {
				copyUtilLibrary(
					artifact, utilJavaDir, implClassesDir,
					"util-java.jar");
			}
			else if (artifact.getArtifactId().endsWith("ext-util-taglib")) {
				copyUtilLibrary(
					artifact, utilTaglibDir, implClassesDir,
					"util-taglib.jar");
			}
			else if (artifact.getArtifactId().endsWith("ext-web")) {
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

		_fileUtil.copyDirectory(sqlSourceDir, sqlDir);

		String dirName = webappDir.getAbsolutePath() + "/WEB-INF";

		ExtInfoBuilder infoBuilder = new ExtInfoBuilder(
			dirName, dirName, pluginName);
	}

	protected void copyJarAndClasses(
			Artifact artifact, File jarDir, String jarName)
		throws Exception {

		File serviceJarFile = new File(jarDir, jarName);

		_fileUtil.copyFile(artifact.getFile(), serviceJarFile);

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
			if (dependency.getScope().equalsIgnoreCase("provided") ||
				dependency.getScope().equalsIgnoreCase("test") ||
				dependency.getType().equalsIgnoreCase("pom")) {

				continue;
			}

			Artifact libArtifact = resolveArtifact(dependency);

			File libJarFile = new File(
				libDir, libArtifact.getArtifactId() + ".jar");

			_fileUtil.copyFile(libArtifact.getFile(), libJarFile);
		}
	}

	protected void copyUtilLibrary(
			Artifact artifact, File utilDir, File implClassesDir,
			String utilJarName)
		throws Exception {

		File utilJarFile = new File(utilDir, "ext-" + utilJarName);

		_fileUtil.copyFile(artifact.getFile(), utilJarFile);

		File dependencyUtilJarFile = new File(
			implClassesDir, "ext-" + pluginName + "-" + utilJarName);

		_fileUtil.copyFile(artifact.getFile(), dependencyUtilJarFile);
	}

	protected Dependency createDependency(
		String groupId, String artifactId, String version, String type) {

		Dependency dependency = new Dependency();

		dependency.setArtifactId(artifactId);
		dependency.setGroupId(groupId);
		dependency.setType(type);
		dependency.setVersion(version);

		return dependency;
	}

	protected Artifact resolveArtifact(Dependency dependency) throws Exception {
		Artifact artifact = artifactFactory.createArtifact(
			dependency.getGroupId(), dependency.getArtifactId(),
			dependency.getVersion(), dependency.getClassifier(),
			dependency.getType());

		artifactResolver.resolve(
			artifact, remoteArtifactRepositories, localArtifactRepository);

		return artifact;
	}

	protected MavenProject resolveProject(Artifact artifact) throws Exception {
		Artifact pomArtifact = artifact;

		String type = artifact.getType();

		if (!type.equals("pom")) {
			pomArtifact = artifactFactory.createArtifact(
				artifact.getGroupId(), artifact.getArtifactId(),
				artifact.getVersion(), "", "pom");
		}

		return projectBuilder.buildFromRepository(
			pomArtifact, remoteArtifactRepositories, localArtifactRepository);
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

	private static FileImpl _fileUtil = new FileImpl();

	/**
	 * @component
	 */
	private ArchiverManager archiverManager;

	/**
	 * @component
	 */
	private ArtifactFactory artifactFactory;

	/**
	 * @component
	 */
	private ArtifactResolver artifactResolver;

	/**
	 * @parameter expression="${localRepository}"
	 * @readonly
	 * @required
	 */
	private ArtifactRepository localArtifactRepository;

	/**
	 * @parameter expression="${project.artifactId}"
	 * @required
	 */
	private String pluginName;

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @component role="org.apache.maven.project.MavenProjectBuilder"
	 * @required
	 * @readonly
	 */
	protected MavenProjectBuilder projectBuilder;

	/**
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @readonly
	 * @required
	 */
	private List remoteArtifactRepositories;

	/**
	 * @parameter expression="${basedir}/src/main/webapp/WEB-INF/sql"
	 * @required
	 */
	private File sqlSourceDir;

	/**
	 * @parameter expression="${project.build.directory}/${project.build.finalName}"
	 * @required
	 */
	private File webappDir;

	/**
	 * @parameter expression="${project.build.directory}/liferay-work"
	 * @required
	 */
	private File workDir;

}
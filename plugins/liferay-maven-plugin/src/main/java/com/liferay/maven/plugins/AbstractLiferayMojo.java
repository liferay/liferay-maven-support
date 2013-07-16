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
import com.liferay.maven.plugins.util.StringUtil;
import com.liferay.maven.plugins.util.Validator;

import java.io.File;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.security.Permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
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

/**
 * @author Mika Koivisto
 */
public abstract class AbstractLiferayMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException {
		try {
			if (!isLiferayProject()) {
				return;
			}

			String[] versionParts = StringUtil.split(liferayVersion, ".");

			int major = Integer.parseInt(versionParts[0]);
			int minor = Integer.parseInt(versionParts[1]);

			if ((major < 6) || ((major == 6) && (minor < 1))) {
				throw new MojoExecutionException(
					"Liferay versions below 6.1.0 are not supported");
			}

			initPortalProperties();

			doExecute();
		}
		catch (Throwable t) {
			try {
				List<String> toolsClassPath = getToolsClassPath();

				getLog().debug("Tools classpath:");

				for (String path : toolsClassPath) {
					getLog().debug("\t" + path);
				}

				List<String> projectClassPath = getProjectClassPath();

				getLog().debug("Project classpath:");

				for (String path : projectClassPath) {
					getLog().debug("\t" + path);
				}
			}
			catch (Exception e) {
			}

			if (t instanceof MojoExecutionException) {
				throw (MojoExecutionException)t;
			}
			else {
				throw new MojoExecutionException(t.getMessage(), t);
			}
		}
	}

	protected void copyLibraryDependencies(File libDir, Artifact artifact)
		throws Exception {

		copyLibraryDependencies(libDir, artifact, false, false, false);
	}

	protected void copyLibraryDependencies(
			File libDir, Artifact artifact, boolean dependencyAddVersion,
			boolean dependencyAddClassifier, boolean copyTransitive)
		throws Exception {

		MavenProject mavenProject = resolveProject(artifact);

		List<Dependency> dependencies = mavenProject.getDependencies();

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

			if (dependencyAddVersion) {
				if (Validator.isNotNull(libArtifact.getVersion())) {
					libJarFileName += "-" + libArtifact.getVersion();
				}
			}

			if (dependencyAddClassifier) {
				if (Validator.isNotNull(libArtifact.getClassifier())) {
					libJarFileName += "-" + libArtifact.getClassifier();
				}
			}

			File libArtifactFile = libArtifact.getFile();

			libJarFileName +=
				"." + FileUtil.getExtension(libArtifactFile.getName());

			CopyTask.copyFile(
				libArtifactFile, libDir, libJarFileName, null, true, true);

			if (copyTransitive) {
				copyLibraryDependencies(
					libDir, libArtifact, dependencyAddVersion,
					dependencyAddClassifier, copyTransitive);
			}
		}
	}

	protected Dependency createDependency(
		String groupId, String artifactId, String version, String classifier,
		String type) {

		Dependency dependency = new Dependency();

		dependency.setArtifactId(artifactId);
		dependency.setClassifier(classifier);
		dependency.setGroupId(groupId);
		dependency.setType(type);
		dependency.setVersion(version);

		return dependency;
	}

	protected abstract void doExecute() throws Exception;

	protected void executeTool(
			String toolClassName, ClassLoader classLoader, String[] args)
		throws Exception {

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		currentThread.setContextClassLoader(classLoader);

		SecurityManager currentSecurityManager = System.getSecurityManager();

		// Required to prevent premature exit by DBBuilder. See LPS-7524.

		SecurityManager securityManager = new SecurityManager() {

			public void checkPermission(Permission permission) {
			}

			public void checkExit(int status) {
				throw new SecurityException();
			}

		};

		System.setSecurityManager(securityManager);

		try {
			System.setProperty(
				"external-properties",
				"com/liferay/portal/tools/dependencies" +
					"/portal-tools.properties");
			System.setProperty(
				"org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.Log4JLogger");

			Class<?> clazz = classLoader.loadClass(toolClassName);

			Method method = clazz.getMethod("main", String[].class);

			method.invoke(null, (Object)args);
		}
		catch (InvocationTargetException ite) {
			if (ite.getCause() instanceof SecurityException) {
			}
			else {
				throw ite;
			}
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);

			System.setSecurityManager(currentSecurityManager);
		}
	}

	protected ClassLoader getProjectClassLoader() throws Exception {
		return toClassLoader(getProjectClassPath());
	}

	protected List<String> getProjectClassPath() throws Exception {
		List<String> projectClassPath = new ArrayList<String>();

		for (Object object : project.getCompileClasspathElements()) {
			String path = (String)object;

			File file = new File(path);

			URI uri = file.toURI();

			URL url = uri.toURL();

			projectClassPath.add(url.toString());
		}

		projectClassPath.addAll(getToolsClassPath());

		return projectClassPath;
	}

	protected ClassLoader getToolsClassLoader() throws Exception {
		return toClassLoader(getToolsClassPath());
	}

	protected List<String> getToolsClassPath() throws Exception {
		List<String> toolsClassPath = new ArrayList<String>();

		String[][] dependencyPartsArray = new String[][] {
			new String[] {
				"com.liferay.portal", "portal-service", liferayVersion, "",
				"jar"
			},
			new String[] {
				"com.thoughtworks.qdox", "qdox", "1.12", "", "jar"
			},
			new String[] {
				"jalopy", "jalopy", "1.5rc3", "", "jar"
			},
			new String[] {
				"javax.activation", "activation", "1.1", "", "jar"
			},
			new String[] {
				"javax.mail", "mail", "1.4", "", "jar"
			},
			new String[] {
				"javax.portlet", "portlet-api", "2.0", "", "jar"
			},
			new String[] {
				"javax.servlet", "servlet-api", "2.5", "", "jar"
			},
			new String[] {
				"javax.servlet.jsp", "jsp-api", "2.1", "", "jar"
			}
		};

		for (String[] dependencyParts : dependencyPartsArray) {
			Dependency dependency = createDependency(
				dependencyParts[0], dependencyParts[1], dependencyParts[2],
				dependencyParts[3], dependencyParts[4]);

			URI uri = resolveArtifactFileURI(dependency);

			URL url = uri.toURL();

			toolsClassPath.add(url.toString());
		}

		if ((appServerLibGlobalDir != null) && appServerLibGlobalDir.exists()) {
			Collection<File> globalJarFiles = FileUtils.listFiles(
				appServerLibPortalDir, new String[] {"jar"}, false);

			for (File file : globalJarFiles) {
				URI uri = file.toURI();

				URL url = uri.toURL();

				toolsClassPath.add(url.toString());
			}
		}

		Collection<File> portalJarFiles = FileUtils.listFiles(
			appServerLibPortalDir, new String[] {"jar"}, false);

		for (File file : portalJarFiles) {
			URI uri = file.toURI();

			URL url = uri.toURL();

			toolsClassPath.add(url.toString());
		}

		return toolsClassPath;
	}

	protected void initPortalProperties() throws Exception {
		if (((appServerPortalDir == null) || !appServerPortalDir.exists()) &&
			Validator.isNotNull(liferayVersion)) {

			appServerPortalDir = new File(workDir, "appServerPortalDir");

			if (!appServerPortalDir.exists()) {
				appServerPortalDir.mkdirs();
			}

			Dependency dependency = createDependency(
				"com.liferay.portal", "portal-web", liferayVersion, "", "war");

			Artifact artifact = resolveArtifact(dependency);

			UnArchiver unArchiver = archiverManager.getUnArchiver(
				artifact.getFile());

			unArchiver.setDestDirectory(appServerPortalDir);

			unArchiver.setOverwrite(false);
			unArchiver.setSourceFile(artifact.getFile());

			unArchiver.extract();
		}

		if ((appServerPortalDir != null) && appServerPortalDir.exists()) {
			if (appServerClassesPortalDir == null) {
				appServerClassesPortalDir = new File(
					appServerPortalDir, "WEB-INF/classes");
			}

			if (appServerLibPortalDir == null) {
				appServerLibPortalDir = new File(
					appServerPortalDir, "WEB-INF/lib");
			}

			if (appServerTldPortalDir == null) {
				appServerTldPortalDir = new File(
					appServerPortalDir, "WEB-INF/tld");
			}
		}
	}

	protected boolean isLiferayProject() {
		String packaging = project.getPackaging();

		if (packaging.equals("pom")) {
			getLog().info("Skipping " + project.getArtifactId());

			return false;
		}

		return true;
	}

	protected Artifact resolveArtifact(Dependency dependency) throws Exception {
		Artifact artifact = null;

		if (Validator.isNull(dependency.getClassifier())) {
			artifact = artifactFactory.createArtifact(
				dependency.getGroupId(), dependency.getArtifactId(),
				dependency.getVersion(), dependency.getScope(),
				dependency.getType());
		}
		else {
			artifact = artifactFactory.createArtifactWithClassifier(
				dependency.getGroupId(), dependency.getArtifactId(),
				dependency.getVersion(), dependency.getType(),
				dependency.getClassifier());
		}

		artifactResolver.resolve(
			artifact, remoteArtifactRepositories, localArtifactRepository);

		return artifact;
	}

	protected URI resolveArtifactFileURI(Dependency dependency)
		throws Exception {

		Artifact artifact = resolveArtifact(dependency);

		File file = artifact.getFile();

		return file.toURI();
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

	protected ClassLoader toClassLoader(List<String> classPath)
		throws Exception {

		List<URL> urls = new ArrayList<URL>();

		for (String path : classPath) {
			urls.add(new URL(path));
		}

		return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
	}

	protected static boolean initialized;

	/**
	 * @parameter expression="${appServerClassesPortalDir}"
	 */
	protected File appServerClassesPortalDir;

	/**
	 * @parameter expression="${appServerLibGlobalDir}"
	 */
	protected File appServerLibGlobalDir;

	/**
	 * @parameter expression="${appServerLibPortalDir}"
	 */
	protected File appServerLibPortalDir;

	/**
	 * @parameter expression="${appServerPortalDir}"
	 */
	protected File appServerPortalDir;

	/**
	 * @parameter expression="${appServerTldPortalDir}"
	 */
	protected File appServerTldPortalDir;

	/**
	 * @component
	 */
	protected ArchiverManager archiverManager;

	/**
	 * @component
	 */
	protected ArtifactFactory artifactFactory;

	/**
	 * @component
	 */
	protected ArtifactResolver artifactResolver;

	/**
	 * @parameter expression="${liferayVersion}"
	 */
	protected String liferayVersion;

	/**
	 * @parameter expression="${localRepository}"
	 * @readonly
	 * @required
	 */
	protected ArtifactRepository localArtifactRepository;

	/**
	 * @parameter default-value="portlet" expression="${pluginType}"
	 * @required
	 */
	protected String pluginType;

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

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
	protected List remoteArtifactRepositories;

	/**
	 * @parameter default-value="${project.build.directory}/liferay-work"
	 * @required
	 */
	protected File workDir;

}
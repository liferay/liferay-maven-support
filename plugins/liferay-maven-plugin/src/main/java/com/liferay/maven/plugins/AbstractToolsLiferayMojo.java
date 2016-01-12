/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
import com.liferay.maven.plugins.util.GetterUtil;
import com.liferay.maven.plugins.util.SAXReaderUtil;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;

import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;

import org.xml.sax.EntityResolver;

/**
 * @author Mika Koivisto
 */
public abstract class AbstractToolsLiferayMojo extends AbstractLiferayMojo {

	public static final float PORTAL_VERSION_6_1 = 6.1f;

	public static final float PORTAL_VERSION_6_2 = 6.2f;

	public void execute() throws MojoExecutionException {
		try {
			if (!isLiferayProject()) {
				return;
			}

			if (getPortalMajorVersion() < PORTAL_VERSION_6_1) {
				throw new MojoExecutionException(
					"Liferay versions below 6.1.0 are not supported");
			}

			initPortalProperties();

			initUtils();

			doExecute();
		}
		catch (Throwable t) {
			if (t instanceof MojoExecutionException) {
				throw (MojoExecutionException)t;
			}
			else {
				throw new MojoExecutionException(t.getMessage(), t);
			}
		}
	}

	protected void addDependencyToClassPath(
			List<String> classPath, Dependency dependency)
		throws Exception {

		URI uri = resolveArtifactFileURI(dependency);

		URL url = uri.toURL();

		classPath.add(url.toString());
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

			System.clearProperty("org.apache.commons.logging.Log");

			System.setSecurityManager(currentSecurityManager);
		}
	}

	protected float getPortalMajorVersion() {
		float majorVersion = 0;

		Matcher matcher = _majorVersionPattern.matcher(liferayVersion);

		if (matcher.find()) {
			majorVersion = GetterUtil.getFloat(matcher.group(1));
		}

		return majorVersion;
	}

	protected ClassLoader getProjectClassLoader() throws Exception {
		return toClassLoader(getProjectClassPath());
	}

	protected List<String> getProjectClassPath() throws Exception {
		List<String> projectClassPath = new ArrayList<String>();

		projectClassPath.addAll(getToolsClassPath());

		List<MavenProject> classPathMavenProjects =
			new ArrayList<MavenProject>();

		classPathMavenProjects.add(project);

		for (Object object : project.getDependencyArtifacts()) {
			Artifact artifact = (Artifact)object;

			ArtifactHandler artifactHandler = artifact.getArtifactHandler();

			if (!artifactHandler.isAddedToClasspath()) {
				continue;
			}

			MavenProject dependencyMavenProject = resolveProject(artifact);

			if (dependencyMavenProject == null) {
				continue;
			}
			else {
				getLog().debug(
					"Resolved dependency project " + dependencyMavenProject);
			}

			List<String> compileSourceRoots =
				dependencyMavenProject.getCompileSourceRoots();

			if (compileSourceRoots.isEmpty()) {
				continue;
			}

			getLog().debug(
				"Adding project to class path " + dependencyMavenProject);

			classPathMavenProjects.add(dependencyMavenProject);
		}

		for (MavenProject classPathMavenProject : classPathMavenProjects) {
			for (Object object :
					classPathMavenProject.getCompileClasspathElements()) {

				String path = (String)object;

				getLog().debug("Class path element " + path);

				File file = new File(path);

				URI uri = file.toURI();

				URL url = uri.toURL();

				projectClassPath.add(url.toString());
			}
		}

		getLog().debug("Project class path:");

		for (String path : projectClassPath) {
			getLog().debug("\t" + path);
		}

		return projectClassPath;
	}

	protected ClassLoader getToolsClassLoader() throws Exception {
		return toClassLoader(getToolsClassPath());
	}

	protected ClassLoader getToolsClassLoader(Dependency[] dependencies)
		throws Exception {

		return toClassLoader(getToolsClassPath(dependencies));
	}

	protected List<String> getToolsClassPath() throws Exception {
		List<String> toolsClassPath = new ArrayList<String>();

		if ((appServerLibGlobalDir != null) && appServerLibGlobalDir.exists()) {
			Collection<File> globalJarFiles = FileUtils.listFiles(
				appServerLibGlobalDir, new String[] {"jar"}, false);

			for (File file : globalJarFiles) {
				URI uri = file.toURI();

				URL url = uri.toURL();

				toolsClassPath.add(url.toString());
			}

			Dependency jalopyDependency = createDependency(
				"jalopy", "jalopy", "1.5rc3", "", "jar");

			addDependencyToClassPath(toolsClassPath, jalopyDependency);

			Dependency qdoxDependency = createDependency(
				"com.thoughtworks.qdox", "qdox", "1.12", "", "jar");

			addDependencyToClassPath(toolsClassPath, qdoxDependency);

			ClassLoader globalClassLoader = toClassLoader(toolsClassPath);

			try {
				globalClassLoader.loadClass("javax.activation.MimeType");
			}
			catch (ClassNotFoundException cnfe) {
				Dependency activationDependency = createDependency(
					"javax.activation", "activation", "1.1", "", "jar");

				addDependencyToClassPath(toolsClassPath, activationDependency);
			}

			try {
				globalClassLoader.loadClass("javax.mail.Message");
			}
			catch (ClassNotFoundException cnfe) {
				Dependency mailDependency = createDependency(
					"javax.mail", "mail", "1.4", "", "jar");

				addDependencyToClassPath(toolsClassPath, mailDependency);
			}

			try {
				globalClassLoader.loadClass(
					"com.liferay.portal.kernel.util.ReleaseInfo");
			}
			catch (ClassNotFoundException cnfe) {
				Dependency portalServiceDependency = createDependency(
					"com.liferay.portal", "portal-service", liferayVersion, "",
					"jar");

				addDependencyToClassPath(
					toolsClassPath, portalServiceDependency);
			}

			try {
				globalClassLoader.loadClass("javax.portlet.Portlet");
			}
			catch (ClassNotFoundException cnfe) {
				Dependency portletApiDependency = createDependency(
					"javax.portlet", "portlet-api", "2.0", "", "jar");

				addDependencyToClassPath(toolsClassPath, portletApiDependency);
			}

			try {
				globalClassLoader.loadClass("javax.servlet.ServletRequest");
			}
			catch (ClassNotFoundException cnfe) {
				Dependency servletApiDependency = createDependency(
					"javax.servlet", "servlet-api", "2.5", "", "jar");

				addDependencyToClassPath(toolsClassPath, servletApiDependency);
			}

			try {
				globalClassLoader.loadClass("javax.servlet.jsp.JspPage");
			}
			catch (ClassNotFoundException cnfe) {
				Dependency jspApiDependency = createDependency(
					"javax.servlet.jsp", "jsp-api", "2.1", "", "jar");

				addDependencyToClassPath(toolsClassPath, jspApiDependency);
			}
		}
		else {
			Dependency jalopyDependency = createDependency(
				"jalopy", "jalopy", "1.5rc3", "", "jar");

			addDependencyToClassPath(toolsClassPath, jalopyDependency);

			Dependency qdoxDependency = createDependency(
				"com.thoughtworks.qdox", "qdox", "1.12", "", "jar");

			addDependencyToClassPath(toolsClassPath, qdoxDependency);

			Dependency activationDependency = createDependency(
				"javax.activation", "activation", "1.1", "", "jar");

			addDependencyToClassPath(toolsClassPath, activationDependency);

			Dependency mailDependency = createDependency(
				"javax.mail", "mail", "1.4", "", "jar");

			addDependencyToClassPath(toolsClassPath, mailDependency);

			Dependency portalServiceDependency = createDependency(
				"com.liferay.portal", "portal-service", liferayVersion, "",
				"jar");

			addDependencyToClassPath(toolsClassPath, portalServiceDependency);

			Dependency portletApiDependency = createDependency(
				"javax.portlet", "portlet-api", "2.0", "", "jar");

			addDependencyToClassPath(toolsClassPath, portletApiDependency);

			Dependency servletApiDependency = createDependency(
				"javax.servlet", "servlet-api", "2.5", "", "jar");

			addDependencyToClassPath(toolsClassPath, servletApiDependency);

			Dependency jspApiDependency = createDependency(
				"javax.servlet.jsp", "jsp-api", "2.1", "", "jar");

			addDependencyToClassPath(toolsClassPath, jspApiDependency);
		}

		Collection<File> portalJarFiles = FileUtils.listFiles(
			appServerLibPortalDir, new String[] {"jar"}, false);

		for (File file : portalJarFiles) {
			URI uri = file.toURI();

			URL url = uri.toURL();

			toolsClassPath.add(url.toString());
		}

		getLog().debug("Tools class path:");

		for (String path : toolsClassPath) {
			getLog().debug("\t" + path);
		}

		return toolsClassPath;
	}

	protected List<String> getToolsClassPath(Dependency[] dependencies)
		throws Exception {

		List<String> toolsClassPath = getToolsClassPath();

		for (Dependency dependency : dependencies) {
			addDependencyToClassPath(toolsClassPath, dependency);
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

	protected void initUtils() throws Exception {
		ClassLoader classLoader = getToolsClassLoader();

		Class<?> clazz = classLoader.loadClass(
			"com.liferay.portal.util.EntityResolver");

		EntityResolver entityResolver = (EntityResolver)clazz.newInstance();

		SAXReaderUtil.setEntityResolver(entityResolver);
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

		ProjectBuildingRequest projectBuildingRequest =
			new DefaultProjectBuildingRequest();

		projectBuildingRequest.setSystemProperties(System.getProperties());

		projectBuildingRequest.setLocalRepository(localArtifactRepository);
		projectBuildingRequest.setRemoteRepositories(
			remoteArtifactRepositories);
		projectBuildingRequest.setRepositorySession(session.getRepositorySession());

		List<String> activeProfileIds = new ArrayList<String>();

		MavenExecutionRequest mavenExecutionRequest = session.getRequest();

		for (String activeProfile : mavenExecutionRequest.getActiveProfiles()) {
			activeProfileIds.add(activeProfile);
		}

		projectBuildingRequest.setActiveProfileIds(activeProfileIds);
		projectBuildingRequest.setProfiles(
			mavenExecutionRequest.getProfiles());

		ProjectBuildingResult projectBuildingResult = projectBuilder.build(
			pomArtifact, true, projectBuildingRequest);

		return projectBuildingResult.getProject();
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
	 * @component role="org.apache.maven.project.ProjectBuilder"
	 * @required
	 * @readonly
	 */
	protected ProjectBuilder projectBuilder;

	/**
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @readonly
	 * @required
	 */
	protected List remoteArtifactRepositories;

	/**
	 * @parameter expression="${session}"
	 * @readonly
	 * @required
	 */
	protected MavenSession session;

	/**
	 * @parameter default-value="${project.build.directory}/liferay-work"
	 * @required
	 */
	protected File workDir;

	private static Pattern _majorVersionPattern = Pattern.compile(
		"(\\d+[.]\\d+)");

}
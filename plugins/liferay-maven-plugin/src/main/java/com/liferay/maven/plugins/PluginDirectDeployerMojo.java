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

import java.io.File;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author Mika Koivisto
 * @author Thiago Moreira
 * @goal   direct-deploy
 */
public class PluginDirectDeployerMojo extends AbstractToolsLiferayMojo {

	protected void deployExt() throws Exception {
		String artifactId = project.getArtifactId();
		Build build = project.getBuild();

		if (artifactId.endsWith("ext-lib-global")) {
			copyLibraryDependencies(
				appServerLibGlobalDir, project.getArtifact(),
				dependencyAddVersion, dependencyAddClassifier,
				dependencyCopyTransitive);
		}

		if (artifactId.endsWith("ext-lib-portal")) {
			copyLibraryDependencies(
				appServerLibPortalDir, project.getArtifact(),
				dependencyAddVersion, dependencyAddClassifier,
				dependencyCopyTransitive);
		}

		if (artifactId.endsWith("ext-service")) {
			File sourceFile = new File(
				build.getDirectory(),
				build.getFinalName() + "." + project.getPackaging());

			deployExtService(sourceFile);

			copyLibraryDependencies(
				appServerLibGlobalDir, project.getArtifact(),
				dependencyAddVersion, dependencyAddClassifier,
				dependencyCopyTransitive);
		}

		if (artifactId.endsWith("ext-impl")) {
			File sourceFile = new File(
				build.getDirectory(),
				build.getFinalName() + "." + project.getPackaging());

			File sourceDir = new File(build.getOutputDirectory());

			deployExtImpl(sourceDir, sourceFile);

			copyLibraryDependencies(
				appServerLibPortalDir, project.getArtifact(),
				dependencyAddVersion, dependencyAddClassifier,
				dependencyCopyTransitive);
		}

		if (artifactId.endsWith("ext-util-bridges") ||
			artifactId.endsWith("ext-util-java") ||
			artifactId.endsWith("ext-util-taglib")) {

			File sourceFile = new File(
				build.getDirectory(),
				build.getFinalName() + "." + project.getPackaging());

			String utilName =
				"util-" + artifactId.substring(artifactId.lastIndexOf('-'));

			deployExtUtil(sourceFile, utilName);

			copyLibraryDependencies(
				appServerLibPortalDir, project.getArtifact(),
				dependencyAddVersion, dependencyAddClassifier,
				dependencyCopyTransitive);
		}

		if (artifactId.endsWith("ext-web")) {
			File sourceDir = new File(
				build.getDirectory(), build.getFinalName());

			copyLibraryDependencies(
				appServerLibPortalDir, project.getArtifact(),
				dependencyAddVersion, dependencyAddClassifier,
				dependencyCopyTransitive);

			deployExtWeb(sourceDir);
		}

		String packaging = project.getPackaging();

		if (artifactId.endsWith("-ext") && packaging.equals("war")) {
			File buildDir = new File(
				build.getDirectory(), build.getFinalName());

			if (fullDeploy) {
				File extImplClassesDir = new File(
					buildDir, "WEB-INF/ext-impl/classes");
				File extImplJarFile = new File(
					buildDir, "WEB-INF/ext-impl/ext-impl.jar");

				deployExtImpl(extImplClassesDir, extImplJarFile);

				File extLibDir = new File(buildDir, "WEB-INF/ext-lib");

				deployExtLib(extLibDir);

				File extServiceJarFile = new File(
					buildDir, "WEB-INF/ext-service/ext-service.jar");

				deployExtService(extServiceJarFile);

				File extUtilBridgesJarFile = new File(
					buildDir, "WEB-INF/ext-util-bridges/ext-util-bridges.jar");

				deployExtUtil(extUtilBridgesJarFile, "util-bridges");

				File extUtilJavaJarFile = new File(
					buildDir, "WEB-INF/ext-util-java/ext-util-java.jar");

				deployExtUtil(extUtilJavaJarFile, "util-java");

				File extUtilTaglibJarFile = new File(
					buildDir, "WEB-INF/ext-util-taglib/ext-util-taglib.jar");

				deployExtUtil(extUtilTaglibJarFile, "util-taglib");

				File extWebDocrootDir = new File(
					buildDir, "WEB-INF/ext-web/docroot");

				deployExtWeb(extWebDocrootDir);
			}

			File sourceFile = new File(
				buildDir, "WEB-INF/ext-" + pluginName + ".xml");

			CopyTask.copyFile(
				sourceFile, new File(appServerPortalDir, "WEB-INF"), true,
				true);
		}
	}

	protected void deployExtImpl(File extImplClassesDir, File extImplJarFile) {
		CopyTask.copyFile(
			extImplJarFile, appServerLibPortalDir,
			"ext-" + pluginName + "-impl.jar", null, true, true);

		CopyTask.copyDirectory(
			extImplClassesDir, appServerClassesPortalDir, null, null);
	}

	protected void deployExtLib(File extLibDir) {
		File extLibGlobalDir = new File(extLibDir, "global");

		CopyTask.copyDirectory(
			extLibGlobalDir, appServerLibGlobalDir, "*.jar", null, true, true);

		File extLibPortalDir = new File(extLibDir, "portal");

		CopyTask.copyDirectory(
			extLibPortalDir, appServerLibPortalDir, "*.jar", null, true, true);
	}

	protected void deployExtService(File extServiceJarFile) {
		CopyTask.copyFile(
			extServiceJarFile, appServerLibGlobalDir,
			"ext-" + pluginName + "-service.jar", null, true, true);
	}

	protected void deployExtUtil(File extUtilFile, String utilName) {
		String fileName = "ext-" + pluginName + "-" + utilName + ".jar";

		CopyTask.copyFile(
			extUtilFile, appServerLibPortalDir, fileName, null, true, true);

		File deployDependenciesDir = new File(
			appServerClassesPortalDir,
			"com/liferay/portal/deploy/dependencies");

		if (!deployDependenciesDir.exists()) {
			deployDependenciesDir.mkdirs();
		}

		CopyTask.copyFile(
			extUtilFile, deployDependenciesDir, fileName, null, true, true);
	}

	protected void deployExtWeb(File extWebDocrootDir) throws Exception {
		CopyTask.copyDirectory(
			extWebDocrootDir, appServerPortalDir, null, "WEB-INF/web.xml", true,
			true);

		File originalWebXml = new File(appServerPortalDir, "WEB-INF/web.xml");
		File mergedWebXml = new File(
			appServerPortalDir, "WEB-INF/web.xml.merged");

		String[] args = {
			originalWebXml.getAbsolutePath(),
			new File(extWebDocrootDir, "/WEB-INF/web.xml").getAbsolutePath(),
			mergedWebXml.getAbsolutePath()
		};

		executeTool(
			"com.liferay.portal.tools.WebXMLBuilder", getToolsClassLoader(),
			args);

		FileUtil.move(mergedWebXml, originalWebXml);
	}

	protected void deployHook() throws Exception {
		executeTool(
			"com.liferay.portal.tools.deploy.HookDeployer",
			getToolsClassLoader(), getRequiredPortalJars());
	}

	protected void deployLayoutTemplate() throws Exception {
		executeTool(
			"com.liferay.portal.tools.deploy.LayoutTemplateDeployer",
			getToolsClassLoader(), getRequiredPortalJars());
	}

	protected void deployPortlet() throws Exception {
		String tldPath = appServerTldPortalDir.getAbsolutePath();

		System.setProperty("deployer.aui.taglib.dtd", tldPath + "/aui.tld");
		System.setProperty(
			"deployer.custom.portlet.xml", String.valueOf(customPortletXml));
		System.setProperty(
			"deployer.portlet.taglib.dtd", tldPath + "/liferay-portlet.tld");
		System.setProperty(
			"deployer.portlet-ext.taglib.dtd",
			tldPath + "/liferay-portlet-ext.tld");
		System.setProperty(
			"deployer.security.taglib.dtd", tldPath + "/liferay-security.tld");
		System.setProperty(
			"deployer.theme.taglib.dtd", tldPath + "/liferay-theme.tld");
		System.setProperty(
			"deployer.ui.taglib.dtd", tldPath + "/liferay-ui.tld");
		System.setProperty(
			"deployer.util.taglib.dtd", tldPath + "/liferay-util.tld");

		executeTool(
			"com.liferay.portal.tools.deploy.PortletDeployer",
			getToolsClassLoader(), getRequiredPortalJars());
	}

	protected void deployTheme() throws Exception {
		String tldPath = appServerTldPortalDir.getAbsolutePath();

		System.setProperty(
			"deployer.theme.taglib.dtd", tldPath + "/liferay-theme.tld");
		System.setProperty(
			"deployer.util.taglib.dtd", tldPath + "/liferay-util.tld");

		executeTool(
			"com.liferay.portal.tools.deploy.ThemeDeployer",
			getToolsClassLoader(), getRequiredPortalJars());
	}

	protected void deployWeb() throws Exception {
		executeTool(
			"com.liferay.portal.tools.deploy.WebDeployer",
			getToolsClassLoader(), getRequiredPortalJars());
	}

	protected void doExecute() throws Exception {
		if ((appServerLibGlobalDir == null) && pluginType.equals("ext")) {
			throw new MojoExecutionException(
				"The parameter appServerLibGlobalDir is required");
		}

		if (appServerLibPortalDir == null) {
			throw new MojoExecutionException(
				"The parameter appServerLibPortalDir is required");
		}

		getLog().info("Directly deploying " + project.getArtifactId());

		getLog().debug("appServerType: " + appServerType);
		getLog().debug("baseDir: " + baseDir);
		getLog().debug("deployDir: " + appServerDeployDir.getAbsolutePath());
		getLog().debug("jbossPrefix: " + jbossPrefix);
		getLog().debug("pluginType: " + pluginType);
		getLog().debug("unpackWar: " + unpackWar);

		System.setProperty("deployer.app.server.type", appServerType);
		System.setProperty("deployer.base.dir", baseDir);
		System.setProperty(
			"deployer.dest.dir", appServerDeployDir.getAbsolutePath());
		System.setProperty("deployer.file.pattern", warFileName);
		System.setProperty("deployer.unpack.war", String.valueOf(unpackWar));

		if (dependencyAddVersionAndClassifier) {
			dependencyAddVersion = true;
			dependencyAddClassifier = true;
		}

		if (pluginType.equals("ext")) {
			deployExt();
		}
		else if (pluginType.equals("hook")) {
			deployHook();
		}
		else if (pluginType.equals("layouttpl")) {
			deployLayoutTemplate();
		}
		else if (pluginType.equals("portlet")) {
			deployPortlet();
		}
		else if (pluginType.equals("theme")) {
			deployTheme();
		}
		else if (pluginType.equals("web")) {
			deployWeb();
		}
	}

	protected String[] getRequiredPortalJars() {
		String path = appServerLibPortalDir.getAbsolutePath();

		return new String[] {
			path + "/util-bridges.jar", path + "/util-java.jar",
			path + "/util-taglib.jar"
		};
	}

	@Override
	protected boolean isLiferayProject() {
		String artifactId = project.getArtifactId();

		if (pluginType.equals("ext") &&
			(artifactId.endsWith("ext-lib-global") ||
			 artifactId.endsWith("ext-lib-portal"))) {

			return true;
		}

		return super.isLiferayProject();
	}

	/**
	 * @parameter default-value="${deployDir}" expression="${appServerDeployDir}"
	 * @required
	 */
	private File appServerDeployDir;

	/**
	 * @parameter default-value="tomcat" expression="${appServerType}"
	 * @required
	 */
	private String appServerType;

	/**
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private String baseDir;

	/**
	 * @parameter default-value="false" expression="${customPortletXml}"
	 * @required
	 */
	private boolean customPortletXml;

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
	 * @deprecated As of 6.1.1
	 * @parameter expression="${deployDir}"
	 */
	private File deployDir;

	/**
	 * @parameter default-value="false"
	 */
	private boolean fullDeploy;

	/**
	 * @parameter expression="${jbossPrefix}"
	 */
	private String jbossPrefix;

	/**
	 * @parameter default-value="${project.artifactId}" expression="${pluginName}"
	 * @required
	 */
	private String pluginName;

	/**
	 * @parameter default-value="true" expression="${unpackWar}"
	 * @required
	 */
	private boolean unpackWar;

	/**
	 * @parameter default-value="${project.build.finalName}.war" expression="${warFileName}
	 * @required
	 */
	private String warFileName;

}
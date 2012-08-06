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
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.tools.WebXMLBuilder;
import com.liferay.portal.tools.deploy.HookDeployer;
import com.liferay.portal.tools.deploy.LayoutTemplateDeployer;
import com.liferay.portal.tools.deploy.PortletDeployer;
import com.liferay.portal.tools.deploy.ThemeDeployer;
import com.liferay.portal.tools.deploy.WebDeployer;
import com.liferay.util.ant.CopyTask;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author Mika Koivisto
 * @author Thiago Moreira
 * @goal   direct-deploy
 */
public class PluginDirectDeployerMojo extends AbstractLiferayMojo {

	protected void deployExt() throws Exception {
		String artifactId = project.getArtifactId();
		Build build = project.getBuild();

		if (artifactId.endsWith("ext-service")) {
			File sourceFile = new File(
				build.getDirectory(),
				build.getFinalName() + StringPool.PERIOD +
					project.getPackaging());

			CopyTask.copyFile(
				sourceFile, appServerLibGlobalDir,
				"ext-" + pluginName + "-service.jar", null, true, true);

			copyLibraryDependencies(
				appServerLibGlobalDir, project.getArtifact(),
				dependencyAddVersion, dependencyAddClassifier,
				dependencyCopyTransitive);
		}

		if (artifactId.endsWith("ext-impl")) {
			File sourceFile = new File(
				build.getDirectory(),
				build.getFinalName() + StringPool.PERIOD +
					project.getPackaging());

			CopyTask.copyFile(
				sourceFile, appServerLibPortalDir,
				"ext-" + pluginName + "-impl.jar", null, true, true);

			File sourceDir = new File(build.getOutputDirectory());

			CopyTask.copyDirectory(
				sourceDir, appServerClassesPortalDir,
				"portal-*.properties,system-*.properties", null);

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
				build.getFinalName() + StringPool.PERIOD +
					project.getPackaging());

			String fileName = "ext-" + pluginName;

			fileName += artifactId.substring(artifactId.lastIndexOf('-'));
			fileName += ".jar";

			CopyTask.copyFile(
				sourceFile, appServerLibPortalDir, fileName, null, true, true);

			copyLibraryDependencies(
				appServerLibPortalDir, project.getArtifact(),
				dependencyAddVersion, dependencyAddClassifier,
				dependencyCopyTransitive);
		}

		if (artifactId.endsWith("ext-web")) {
			File sourceDir = new File(
				build.getDirectory(), build.getFinalName());

			CopyTask.copyDirectory(
				sourceDir, appServerDeployDir, null, "WEB-INF/web.xml", true,
				true);

			copyLibraryDependencies(
				appServerLibPortalDir, project.getArtifact(),
				dependencyAddVersion, dependencyAddClassifier,
				dependencyCopyTransitive);

			File originalWebXml = new File(
				appServerPortalDir, "WEB-INF/web.xml");
			File mergedWebXml = new File(
				appServerPortalDir, "WEB-INF/web.xml.merged");

			new WebXMLBuilder(
				originalWebXml.getAbsolutePath(),
				sourceDir + "/WEB-INF/web.xml", mergedWebXml.getAbsolutePath());

			FileUtil.move(mergedWebXml, originalWebXml);
		}

		String packaging = project.getPackaging();

		if (artifactId.endsWith("-ext") && packaging.equals("war")) {
			File buildDir = new File(
				build.getDirectory(), build.getFinalName());

			File sourceFile = new File(
				buildDir, "WEB-INF/ext-" + pluginName + ".xml");

			CopyTask.copyFile(sourceFile, appServerPortalDir, true, true);
		}
	}

	protected void deployHook() throws Exception {
		List<String> wars = new ArrayList<String>();

		List<String> jars = new ArrayList<String>();

		jars.add(appServerLibPortalDir.getAbsolutePath() + "/util-java.jar");

		new HookDeployer(wars, jars);
	}

	protected void deployLayoutTemplate() throws Exception {
		List<String> wars = new ArrayList<String>();
		List<String> jars = new ArrayList<String>();

		new LayoutTemplateDeployer(wars, jars);
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

		List<String> wars = new ArrayList<String>();

		List<String> jars = new ArrayList<String>();

		String libPath = appServerLibPortalDir.getAbsolutePath() ;

		jars.add(libPath + "/util-bridges.jar");
		jars.add(libPath + "/util-java.jar");
		jars.add(libPath + "/util-taglib.jar");

		new PortletDeployer(wars, jars);
	}

	protected void deployTheme() throws Exception {
		String tldPath = appServerTldPortalDir.getAbsolutePath();

		System.setProperty(
			"deployer.theme.taglib.dtd", tldPath + "/liferay-theme.tld");
		System.setProperty(
			"deployer.util.taglib.dtd", tldPath + "/liferay-util.tld");

		List<String> wars = new ArrayList<String>();

		List<String> jars = new ArrayList<String>();

		String libPath = appServerLibPortalDir.getAbsolutePath();

		jars.add(libPath + "/util-java.jar");
		jars.add(libPath + "/util-taglib.jar");

		new ThemeDeployer(wars, jars);
	}

	protected void deployWeb() throws Exception {
		List<String> wars = new ArrayList<String>();

		List<String> jars = new ArrayList<String>();

		String libPath = appServerLibPortalDir.getAbsolutePath();

		jars.add(libPath + "/util-java.jar");

		new WebDeployer(wars, jars);
	}

	protected void doExecute() throws Exception {
		if (appServerLibGlobalDir == null) {
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
	 * @deprecated
	 * @parameter expression="${deployDir}"
	 * @since 6.1.1
	 */
	private File deployDir;

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
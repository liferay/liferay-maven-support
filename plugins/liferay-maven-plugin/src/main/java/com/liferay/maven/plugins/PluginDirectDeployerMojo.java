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
import com.liferay.portal.kernel.util.Validator;
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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;

import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

/**
 * @author Mika Koivisto
 * @author Thiago Moreira
 * @goal   direct-deploy
 */
public class PluginDirectDeployerMojo extends AbstractLiferayMojo {

	protected void doExecute() throws Exception {
		getLog().info("Directly deploying " + warFileName);

		getLog().debug("appServerType: " + appServerType);
		getLog().debug("baseDir: " + baseDir);
		getLog().debug("deployDir: " + appServerDeployDir.getAbsolutePath());
		getLog().debug("jbossPrefix: " + jbossPrefix);
		getLog().debug("pluginType: " + pluginType);
		getLog().debug("unpackWar: " + unpackWar);

		preparePortalDependencies();

		System.setProperty("deployer.app.server.type", appServerType);
		System.setProperty("deployer.base.dir", baseDir);
		System.setProperty(
			"deployer.dest.dir", appServerDeployDir.getAbsolutePath());
		System.setProperty("deployer.file.pattern", warFileName);
		System.setProperty("deployer.unpack.war", String.valueOf(unpackWar));
		System.setProperty(
			"liferay.lib.portal.dir",
			workDir.getAbsolutePath() + "/WEB-INF/lib");

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

	protected void deployExt() throws Exception {
		if (Validator.isNull(appServerClassesPortalDir)) {
			appServerClassesPortalDir =
				new File(appServerPortalDir, "WEB-INF/classes");
		}
		if (Validator.isNull(appServerLibPortalDir)) {
			appServerLibPortalDir =
				new File(appServerPortalDir, "WEB-INF/lib");
		}

		String artifactId = project.getArtifactId();
		Build build = project.getBuild();

		if (artifactId.endsWith("ext-service")) {
			File sourceFile = new File(
				build.getDirectory(), build.getFinalName()
				+ StringPool.PERIOD + project.getPackaging());

			CopyTask.copyFile(sourceFile, appServerLibGlobalDir, true, true);

			copyLibraryDependencies(
				appServerLibGlobalDir, project.getArtifact());
		}

		if (artifactId.endsWith("ext-impl")) {
			File sourceFile = new File(
				build.getDirectory(), build.getFinalName()
				+ StringPool.PERIOD +  project.getPackaging());
			File sourceDir = new File(build.getOutputDirectory());

			CopyTask.copyFile(sourceFile, appServerLibPortalDir, true, true);
			CopyTask.copyDirectory(
				sourceDir, appServerClassesPortalDir,
				"portal-*.properties,system-*.properties", null);

			copyLibraryDependencies(
				appServerLibPortalDir, project.getArtifact());
		}

		if (artifactId.endsWith("ext-util-bridges")
			|| artifactId.endsWith("ext-util-java")
			|| artifactId.endsWith("ext-util-taglib")) {

			File sourceFile = new File(
				build.getDirectory(), build.getFinalName()
				+ StringPool.PERIOD +  project.getPackaging());

			CopyTask.copyFile(sourceFile, appServerLibPortalDir, true, true);

			copyLibraryDependencies(
				appServerLibPortalDir, project.getArtifact());
		}

		if (artifactId.endsWith("ext-web")) {
			File originalWebXml = new File(
				appServerPortalDir, "WEB-INF/web.xml");
			File mergedWebXml = new File(
				appServerPortalDir, "WEB-INF/web.xml.merged");

			File sourceDir =
				new File(build.getDirectory(), build.getFinalName());
			String customWebXml = sourceDir + "/WEB-INF/web.xml";

			CopyTask.copyDirectory(
				sourceDir, appServerDeployDir, null, "WEB-INF/web.xml", true,
				true);

			new WebXMLBuilder(
				originalWebXml.getAbsolutePath(), customWebXml,
				mergedWebXml.getAbsolutePath());

			FileUtil.move(mergedWebXml, originalWebXml);
		}

		if (artifactId.endsWith("-ext")
			&& project.getPackaging().equals("war")) {

			File sourceFile =
				new File(
					build.getDirectory() + File.separator + build.getFinalName(),
					"WEB-INF/ext-" + pluginName + ".xml");

			CopyTask.copyFile(sourceFile, appServerPortalDir, true, true);
		}
	}

	protected void deployHook() throws Exception {
		List<String> wars = new ArrayList<String>();

		List<String> jars = new ArrayList<String>();

		String libPath = workDir.getAbsolutePath() + "/WEB-INF/lib";

		jars.add(libPath + "/util-java.jar");

		new HookDeployer(wars, jars);
	}

	protected void deployLayoutTemplate() throws Exception {
		List<String> wars = new ArrayList<String>();
		List<String> jars = new ArrayList<String>();

		new LayoutTemplateDeployer(wars, jars);
	}

	protected void deployPortlet() throws Exception {
		String tldPath = workDir.getAbsolutePath() + "/WEB-INF/tld";

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

		String libPath = workDir.getAbsolutePath() + "/WEB-INF/lib";

		jars.add(libPath + "/util-bridges.jar");
		jars.add(libPath + "/util-java.jar");
		jars.add(libPath + "/util-taglib.jar");

		new PortletDeployer(wars, jars);
	}

	protected void deployTheme() throws Exception {
		String tldPath = workDir.getAbsolutePath() + "/WEB-INF/tld";

		System.setProperty(
			"deployer.theme.taglib.dtd", tldPath + "/liferay-theme.tld");
		System.setProperty(
			"deployer.util.taglib.dtd", tldPath + "/liferay-util.tld");

		List<String> wars = new ArrayList<String>();

		List<String> jars = new ArrayList<String>();

		String libPath = workDir.getAbsolutePath() + "/WEB-INF/lib";

		jars.add(libPath + "/util-java.jar");
		jars.add(libPath + "/util-taglib.jar");

		new ThemeDeployer(wars, jars);
	}

	protected void deployWeb() throws Exception {
		List<String> wars = new ArrayList<String>();

		List<String> jars = new ArrayList<String>();

		String libPath = workDir.getAbsolutePath() + "/WEB-INF/lib";

		jars.add(libPath + "/util-java.jar");

		new WebDeployer(wars, jars);
	}

	protected void preparePortalDependencies() throws Exception {
		Artifact artifact = artifactFactory.createArtifact(
			"com.liferay.portal", "portal-web", liferayVersion, "", "war");

		artifactResolver.resolve(
			artifact, remoteArtifactRepositories, localArtifactRepository);

		if (!workDir.exists()) {
			workDir.mkdirs();
		}

		UnArchiver unArchiver = archiverManager.getUnArchiver(
			artifact.getFile());

		unArchiver.setDestDirectory(workDir);
		unArchiver.setSourceFile(artifact.getFile());

		IncludeExcludeFileSelector includeExcludeFileSelector =
			new IncludeExcludeFileSelector();

		includeExcludeFileSelector.setExcludes(new String[]{});
		includeExcludeFileSelector.setIncludes(
			new String[] {"WEB-INF/tld/**", "WEB-INF/lib/**"});

		unArchiver.setFileSelectors(
			new FileSelector[] {includeExcludeFileSelector});

		unArchiver.extract();
	}

	/**
	 * @parameter expression="${appServerClassesPortalDir}"
	 */
	private File appServerClassesPortalDir;

	/**
	 * @parameter default-value="${deployDir}" expression="${appServerDeployDir}"
	 * @required
	 */
	private File appServerDeployDir;

	/**
	 * @parameter expression="${appServerLibGlobalDir}"
	 * @required
	 */
	private File appServerLibGlobalDir;

	/**
	 * @parameter expression="${appServerPortalDir}"
	 * @required
	 */
	private File appServerPortalDir;

	/**
	 * @parameter expression="${appServerLibPortalDir}"
	 */
	private File appServerLibPortalDir;

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
	 * @parameter expression="${liferayVersion}"
	 * @required
	 */
	private String liferayVersion;

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
	 * @parameter default-value="${project.build.directory}/${project.build.finalName}.war" expression="${warFile}"
	 * @required
	 */
	private File warFile;

	/**
	 * @parameter default-value="${project.build.finalName}.war" expression="${warFileName}
	 * @required
	 */
	private String warFileName;

	/**
	 * @parameter default-value="${project.build.directory}/liferay-work"
	 * @required
	 */
	private File workDir;

}
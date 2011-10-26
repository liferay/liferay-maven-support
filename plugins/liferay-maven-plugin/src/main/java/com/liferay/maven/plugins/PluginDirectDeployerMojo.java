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

import com.liferay.portal.bean.BeanLocatorImpl;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.tools.deploy.ExtDeployer;
import com.liferay.portal.tools.deploy.HookDeployer;
import com.liferay.portal.tools.deploy.LayoutTemplateDeployer;
import com.liferay.portal.tools.deploy.PortletDeployer;
import com.liferay.portal.tools.deploy.ThemeDeployer;
import com.liferay.portal.tools.deploy.WebDeployer;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import com.liferay.portal.util.FileImpl;
import com.liferay.portal.util.HtmlImpl;
import com.liferay.portal.util.InitUtil;
import com.liferay.portal.util.PortalImpl;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.xml.SAXReaderImpl;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

/**
 * @author Mika Koivisto
 * @goal   direct-deploy
 */
public class PluginDirectDeployerMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException {
		try {
			doExecute();
		}
		catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	protected void doExecute() throws Exception {
		if (!warFile.exists()) {
			getLog().error(warFileName + " does not exist");

			throw new FileNotFoundException(warFileName + " does not exist!");
		}

		getLog().info("Directly deploying " + warFileName);

		getLog().debug("appServerType: " + appServerType);
		getLog().debug("baseDir: " + baseDir);
		getLog().debug("deployDir: " + deployDir.getAbsolutePath());
		getLog().debug("jbossPrefix: " + jbossPrefix);
		getLog().debug("pluginType: " + pluginType);
		getLog().debug("unpackWar: " + unpackWar);

		preparePortalDependencies();

		System.setProperty("deployer.app.server.type", appServerType);
		System.setProperty("deployer.base.dir", baseDir);
		System.setProperty("deployer.dest.dir", deployDir.getAbsolutePath());
		System.setProperty("deployer.file.pattern", warFileName);
		System.setProperty("deployer.unpack.war", String.valueOf(unpackWar));
		System.setProperty(
			"liferay.lib.portal.dir",
			workDir.getAbsolutePath() + "/WEB-INF/lib");

		initPortal();

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
		List<String> wars = new ArrayList<String>();

		List<String> jars = new ArrayList<String>();

		String libPath = workDir.getAbsolutePath() + "/WEB-INF/lib";

		jars.add(libPath + "/util-java.jar");

		new ExtDeployer(wars, jars);
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

		System.setProperty(
			"deployer.aui.taglib.dtd", tldPath + "/liferay-aui.tld");
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

	protected void initPortal() {
		InitUtil.init();

		PortalBeanLocatorUtil.setBeanLocator(new BeanLocatorImpl(null, null));

		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());

		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(new FileImpl());

		HtmlUtil htmlUtil = new HtmlUtil();

		htmlUtil.setHtml(new HtmlImpl());

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());

		SAXReaderUtil saxReaderUtil = new SAXReaderUtil();

		saxReaderUtil.setSAXReader(new SAXReaderImpl());
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
	 * @parameter default-value="tomcat" expression="${appServerType}"
	 * @required
	 */
	private String appServerType;

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
	 * @parameter expression="${deployDir}"
	 * @required
	 */
	private File deployDir;

	/**
	 * @parameter default-value="" expression="${jbossPrefix}"
	 * @required
	 */
	private String jbossPrefix;

	/**
	 * @parameter expression="${liferayVersion}"
	 * @required
	 */
	private String liferayVersion;

	/**
	 * @parameter expression="${localRepository}"
	 * @readonly
	 * @required
	 */
	private ArtifactRepository localArtifactRepository;

	/**
	 * @parameter default-value="portlet" expression="${pluginType}"
	 * @required
	 */
	private String pluginType;

	/**
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @readonly
	 * @required
	 */
	private List remoteArtifactRepositories;

	/**
	 * @parameter expression="${unpackWar}" default-value="true"
	 * @required
	 */
	private boolean unpackWar;

	/**
	 * @parameter expression="${project.build.directory}/${project.build.finalName}.war"
	 * @required
	 */
	private File warFile;

	/**
	 * @parameter expression="${project.build.finalName}.war"
	 * @required
	 */
	private String warFileName;

	/**
	 * @parameter expression="${project.build.directory}/liferay-work"
	 * @required
	 */
	private File workDir;

}
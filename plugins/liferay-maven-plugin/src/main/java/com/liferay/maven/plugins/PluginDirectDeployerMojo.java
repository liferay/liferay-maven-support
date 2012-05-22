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

import com.liferay.portal.cache.MultiVMPoolImpl;
import com.liferay.portal.cache.memory.MemoryPortalCacheManager;
import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.util.PropsKeys;
<<<<<<< Updated upstream
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.tools.WebXMLBuilder;
=======
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.tools.WebXMLBuilder;
import com.liferay.portal.tools.deploy.ExtDeployer;
>>>>>>> Stashed changes
import com.liferay.portal.tools.deploy.HookDeployer;
import com.liferay.portal.tools.deploy.LayoutTemplateDeployer;
import com.liferay.portal.tools.deploy.PortletDeployer;
import com.liferay.portal.tools.deploy.ThemeDeployer;
import com.liferay.portal.tools.deploy.WebDeployer;
import com.liferay.portal.util.InitUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.util.ant.CopyTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

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

			throw new FileNotFoundException(warFileName + " does not exist");
		}

		getLog().info("Directly deploying " + warFileName);

		getLog().debug("appServerType: " + appServerType);
//		getLog().debug("appServerGlobalLibDir: " + appServerGlobalLibDir.getAbsolutePath());
//		getLog().debug("appServerLiferayRootDir: " + appServerLiferayRootDir.getAbsolutePath());
		getLog().debug("baseDir: " + baseDir);
<<<<<<< Updated upstream
		getLog().debug("deployDir: " + appServerDeployDir.getAbsolutePath());
=======
		getLog().debug("deployDir: " + deployDir.getAbsolutePath());
		getLog().debug("extDir: " + extDir.getAbsolutePath());
>>>>>>> Stashed changes
		getLog().debug("jbossPrefix: " + jbossPrefix);
		getLog().debug("pluginType: " + pluginType);
		getLog().debug("projectName: " + projectName);
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
<<<<<<< Updated upstream
		workDir.mkdirs();

		UnArchiver unArchiver = archiverManager.getUnArchiver(warFile);

		unArchiver.setDestDirectory(workDir);
		unArchiver.setSourceFile(warFile);

		unArchiver.extract();

		CopyTask.copyDirectory(
			new File(workDir, "WEB-INF/ext-lib/global"), appServerLibGlobalDir,
			"*.jar", null, true, true);

		CopyTask.copyFile(
			new File(workDir, "WEB-INF/ext-service/ext-service.jar"),
			appServerLibGlobalDir, "ext-" + pluginName + "-service.jar", null,
			true, true);

		CopyTask.copyDirectory(
			new File(workDir, "WEB-INF/ext-lib/portal"), appServerLibPortalDir,
			"*.jar", null, true, true);

		CopyTask.copyFile(
			new File(workDir, "WEB-INF/ext-impl/ext-impl.jar"),
			appServerLibGlobalDir, "ext-" + pluginName + "-impl.jar", null, true,
			true);

		CopyTask.copyFile(
			new File(workDir, "WEB-INF/ext-util-bridges/ext-util-bridges.jar"),
			appServerLibGlobalDir, "ext-" + pluginName + "-util-bridges.jar",
			null, true, true);

		CopyTask.copyFile(
			new File(workDir, "WEB-INF/ext-util-java/ext-util-java.jar"),
			appServerLibGlobalDir, "ext-" + pluginName + "-util-java.jar", null,
			true, true);

		CopyTask.copyFile(
			new File(workDir, "WEB-INF/ext-util-taglib/ext-util-taglib.jar"),
			appServerLibGlobalDir, "ext-" + pluginName + "-util-taglib.jar",
			null, true, true);

		CopyTask.copyDirectory(
			new File(workDir, "WEB-INF/ext-web/docroot"), appServerPortalDir,
			null, "WEB-INF/web.xml", true, true);

		File webXml = new File(
			workDir, "WEB-INF/ext-web/docroot/WEB-INF/web.xml");

		if (webXml.exists()) {
			File originalWebXml = new File(
				appServerPortalDir, "WEB-INF/web.xml");
			File mergedWebXml = new File(
				appServerPortalDir, "WEB-INF/web.xml.merged");

			new WebXMLBuilder(
				originalWebXml.getAbsolutePath(), webXml.getAbsolutePath(),
				mergedWebXml.getAbsolutePath());

			FileUtil.move(mergedWebXml, originalWebXml);
		}

		CopyTask.copyFile(
			new File(workDir, "WEB-INF/ext-" + pluginName + ".xml"),
			appServerPortalDir, null, true, true);

		CopyTask.copyDirectory(
			new File(workDir, "WEB-INF/ext-web/docroot/WEB-INF/classes"),
			appServerClassesPortalDir,
			"portal-*.properties,system-*.properties", null, true, true);
=======
		
		File appServerLiferayWebInfDir = new File(appServerLiferayRootDir + 
				StringPool.FORWARD_SLASH + "WEB-INF");

		File appServerLiferayWebInfLibDir = new File(appServerLiferayRootDir + 
				StringPool.FORWARD_SLASH + "WEB-INF" + 
				StringPool.FORWARD_SLASH + "lib");

		FileUtils.copyDirectory(new File(extDir + "/WEB-INF/ext-lib/global"),
				appServerGlobalLibDir,
				FileFilterUtils.suffixFileFilter(".jar"));

		FileUtils.copyDirectory(new File(extDir + "/WEB-INF/ext-lib/portal"),
				appServerLiferayWebInfLibDir,
				FileFilterUtils.suffixFileFilter(".jar"));
		
		FileUtils.copyFile(new File(extDir + "/WEB-INF/ext-service/ext-service.jar"),
				new File(appServerGlobalLibDir, StringPool.FORWARD_SLASH +
						"ext-" + projectName + "-service.jar"));

		FileUtils.copyFile(new File(extDir + "/WEB-INF/ext-impl/ext-impl.jar"),
				new File(appServerLiferayWebInfLibDir, StringPool.FORWARD_SLASH +
						"ext-" + projectName + "-impl.jar"));

		FileUtils.copyFile(new File(extDir + "/WEB-INF/ext-impl/ext-impl.jar"),
				new File(appServerLiferayWebInfLibDir, StringPool.FORWARD_SLASH +
						"ext-" + projectName + "-impl.jar"));

		FileUtils.copyFile(new File(extDir + "/WEB-INF/ext-util-bridges/ext-util-bridges.jar"),
				new File(appServerLiferayWebInfLibDir, StringPool.FORWARD_SLASH +
						"ext-" + projectName + "-util-bridges.jar"));

		FileUtils.copyFile(new File(extDir + "/WEB-INF/ext-util-java/ext-util-java.jar"),
				new File(appServerLiferayWebInfLibDir, StringPool.FORWARD_SLASH +
						"ext-" + projectName + "-util-java.jar"));

		FileUtils.copyFile(new File(extDir + "/WEB-INF/ext-util-taglib/ext-util-taglib.jar"),
				new File(appServerLiferayWebInfLibDir, StringPool.FORWARD_SLASH +
						"ext-" + projectName + "-util-taglib.jar"));

		File extWebWorkDir = new File(extDir + "/WEB-INF/ext-web/docroot");
		
		FileUtils.copyDirectory(extWebWorkDir, appServerLiferayRootDir, 
				FileFilterUtils.andFileFilter(FileFilterUtils.trueFileFilter(), 
						FileFilterUtils.notFileFilter(
								FileFilterUtils.nameFileFilter("web.xml"))));

		WebXMLBuilder webXMLBuilder = new WebXMLBuilder(
				appServerLiferayWebInfDir + "/web.xml", 
				extDir + "/WEB-INF/ext-web/docroot/WEB-INF/web.xml", 
				appServerLiferayRootDir + "/WEB-INF/web.xml.merged");
		
		FileUtils.copyFile(new File(appServerLiferayRootDir + "/WEB-INF/web.xml.merged"),
				new File(appServerLiferayWebInfDir + "/web.xml"));
		
		FileUtils.deleteQuietly(new File(appServerLiferayRootDir + "/WEB-INF/web.xml.merged"));
		
		FileUtils.copyFile(new File(extDir + "/WEB-INF/ext-" + projectName + ".xml"),
				new File(appServerLiferayWebInfDir, StringPool.FORWARD_SLASH +
						"ext-" + projectName + ".xml"));
		
>>>>>>> Stashed changes
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

	protected void initPortal() {
		PropsUtil.set(
			PropsKeys.RESOURCE_ACTIONS_READ_PORTLET_RESOURCES,
			Boolean.FALSE.toString());

		PropsUtil.set("spring.configs", "META-INF/service-builder-spring.xml");

		InitUtil.initWithSpring();

		MemoryPortalCacheManager memoryPortalCacheManager =
			new MemoryPortalCacheManager();

		memoryPortalCacheManager.afterPropertiesSet();

		MultiVMPoolImpl multiVMPoolImpl = new MultiVMPoolImpl();

		multiVMPoolImpl.setPortalCacheManager(memoryPortalCacheManager);

		MultiVMPoolUtil multiVMPoolUtil = new MultiVMPoolUtil();

		multiVMPoolUtil.setMultiVMPool(multiVMPoolImpl);
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
	 * @parameter default-value="${appServerPortalDir}/WEB-INF/classes" expression="${appServerClassesPortalDir}"
	 * @required
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
	 * @parameter default-value="${appServerPortalDir}/WEB-INF/lib" expression="${appServerLibPortalDir}"
	 */
	private File appServerLibPortalDir;

	/**
	 * @parameter default-value="tomcat" expression="${appServerType}"
	 * @required
	 */
	private String appServerType;

	/**
	 * @parameter default-value="" expression="${appServerGlobalLibDir}"
	 */
	private File appServerGlobalLibDir;

	/**
	 * @parameter default-value="" expression="${appServerLiferayRootDir}"
	 */
	private File appServerLiferayRootDir;

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
	 * @deprecated
	 * @parameter expression="${deployDir}"
	 * @since 6.1.1
	 */
	private File deployDir;

	/**
<<<<<<< Updated upstream
	 * @parameter expression="${jbossPrefix}"
=======
	 * @parameter expression="${project.build.directory}/${project.build.finalName}"
	 * @required
	 */
	private File extDir;

	/**
	 * @parameter default-value="" expression="${jbossPrefix}"
>>>>>>> Stashed changes
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
	 * @parameter default-value="${project.artifactId}" expression="${pluginName}"
	 * @required
	 */
	private String pluginName;

	/**
	 * @parameter default-value="portlet" expression="${pluginType}"
	 * @required
	 */
	private String pluginType;

	/**
	 * @parameter expression="${project.build.finalName}"
	 * @required
	 */
	private String projectName;

	/**
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @readonly
	 * @required
	 */
	private List remoteArtifactRepositories;

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
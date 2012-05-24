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
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.servicebuilder.ServiceBuilder;
import com.liferay.portal.util.InitUtil;
import com.liferay.portal.util.PropsUtil;

import java.io.File;

import java.lang.reflect.Method;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenCommandLineBuilder;

/**
 * Builds Liferay Service Builder services.
 *
 * @author Mika Koivisto
 * @author Thiago Moreira
 * @goal   build-service
 */
public class ServiceBuilderMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException {
		try {
			if (pluginType.equals("ext")) {
				StringBuilder sb = new StringBuilder();

				sb.append("WARNING: Support for ServiceBuilder in EXT ");
				sb.append("plugins will be deprecated in future versions. ");
				sb.append("EXT plugins are designed to override the portal's ");
				sb.append("core code that cannot be done with hooks, layout ");
				sb.append("templates, portlets, or themes. EXT plugins are ");
				sb.append("not meant to contain new custom services. Please ");
				sb.append("migrate your service.xml to a portlet plugin.");

				getLog().warn(sb.toString());
			}

			initClassLoader();

			doExecute();
		}
		catch (Exception e) {
			if (e instanceof MojoExecutionException) {
				throw (MojoExecutionException)e;
			}
			else {
				throw new MojoExecutionException(
					"Unable to execute Service Builder: " + e.getMessage(), e);
			}
		}
	}

	protected void copyServicePropertiesFile() throws Exception {
		File servicePropertiesFile = new File(
			implResourcesDir, "service.properties");

		if (servicePropertiesFile.exists()) {
			FileUtil.copyFile(
				servicePropertiesFile, new File(implDir, "service.properties"));
		}
	}

	protected void doExecute() throws Exception {
		initProperties();

		File inputFile = new File(serviceFileName);

		if (!inputFile.exists()) {
			throw new MojoExecutionException(
				"Unable to find service.xml with path: " +
					inputFile.getAbsolutePath());
		}

		getLog().info("Building from " + serviceFileName);

		PropsUtil.set("spring.configs", "META-INF/service-builder-spring.xml");
		PropsUtil.set(
			PropsKeys.RESOURCE_ACTIONS_READ_PORTLET_RESOURCES, "false");

		InitUtil.initWithSpring();

		copyServicePropertiesFile();

		FileUtil.mkdirs(sqlDir);

		File tempServiceFile = null;

		if (pluginType.equals("ext")) {
			if (serviceFileName.contains("/main/resources/")) {
				File serviceFile = new File(serviceFileName);

				tempServiceFile = new File(
					StringUtil.replace(
						serviceFileName, "/main/resources/", "/main/java/"));

				FileUtil.copyFile(serviceFile, tempServiceFile);
			}
		}

		new ServiceBuilder(
			serviceFileName, hbmFileName, ormFileName, modelHintsFileName,
			springFileName, springBaseFileName, springClusterFileName,
			springDynamicDataSourceFileName, springHibernateFileName,
			springInfrastructureFileName, springShardDataSourceFileName,
			apiDir, implDir, jsonFileName, remotingFileName, sqlDir,
			sqlFileName, sqlIndexesFileName, sqlIndexesPropertiesFileName,
			sqlSequencesFileName, autoNamespaceTables, beanLocatorUtil,
			propsUtil, pluginName, null);

		if (tempServiceFile != null) {
			FileUtil.delete(tempServiceFile);
		}

		moveServicePropertiesFile();

		invokeDependencyBuild();
	}

	protected void initClassLoader() throws Exception {
		synchronized (ServiceBuilderMojo.class) {
			Class<?> clazz = getClass();

			URLClassLoader classLoader = (URLClassLoader)clazz.getClassLoader();

			Method method = URLClassLoader.class.getDeclaredMethod(
				"addURL", URL.class);

			method.setAccessible(true);

			for (Object object : project.getCompileClasspathElements()) {
				String path = (String)object;

				File file = new File(path);

				URI uri = file.toURI();

				method.invoke(classLoader, uri.toURL());
			}
		}
	}

	protected void initProperties() {
		if (Validator.isNotNull(apiBaseDir) ||
			Validator.isNotNull(implBaseDir) ||
			Validator.isNotNull(webappBaseDir)) {

			if (Validator.isNull(apiBaseDir)) {
				apiBaseDir = baseDir;
			}

			if (Validator.isNull(implBaseDir) &&
				Validator.isNotNull(webappBaseDir)) {

				implBaseDir = webappBaseDir;
			}
			else if (Validator.isNull(implBaseDir) &&
					 Validator.isNotNull(apiBaseDir)) {

				implBaseDir = baseDir;
			}

			if (Validator.isNull(webappBaseDir) &&
				Validator.isNotNull(implBaseDir)) {

				webappBaseDir = implBaseDir;
			}
			else if (Validator.isNull(webappBaseDir) &&
					 Validator.isNotNull(apiBaseDir)) {

				webappBaseDir = baseDir;
			}
		}

		if (Validator.isNotNull(apiBaseDir)) {
			apiDir = apiBaseDir.concat("/src/main/java");
		}

		if (Validator.isNotNull(implBaseDir)) {
			implDir = implBaseDir.concat("/src/main/java");
			implResourcesDir = implBaseDir.concat("/src/main/resources");

			if (pluginType.equals("ext")) {
				hbmFileName = implResourcesDir.concat("/META-INF/ext-hbm.xml");
				modelHintsFileName = implResourcesDir.concat(
					"/META-INF/ext-model-hints.xml");
				ormFileName = implResourcesDir.concat("/META-INF/ext-orm.xml");
				springFileName = implResourcesDir.concat(
					"/META-INF/ext-spring.xml");
			}
		}

		if (Validator.isNotNull(webappBaseDir)) {
			String webappDir = webappBaseDir.concat("/src/main/webapp");
			String webappResourcesDir = webappBaseDir.concat(
				"/src/main/resources");

			if (pluginType.equals("ext")) {
				jsonFileName = webappDir.concat("/html/js/liferay/service.js");
				remotingFileName = webappDir.concat(
					"/WEB-INF/remoting-servlet-ext.xml");
			}
			else {
				hbmFileName = webappResourcesDir.concat(
					"/META-INF/portlet-hbm.xml");
				jsonFileName = webappDir.concat("/js/service.js");
				modelHintsFileName = webappResourcesDir.concat(
					"/META-INF/portlet-model-hints.xml");
				ormFileName = webappResourcesDir.concat(
					"/META-INF/portlet-orm.xml");
				serviceFileName = webappDir.concat("/WEB-INF/service.xml");
				springBaseFileName = webappResourcesDir.concat(
					"/META-INF/base-spring.xml");
				springClusterFileName = webappResourcesDir.concat(
					"/META-INF/cluster-spring.xml");
				springDynamicDataSourceFileName = webappResourcesDir.concat(
					"/META-INF/dynamic-data-source-spring.xml");
				springFileName = webappResourcesDir.concat(
					"/META-INF/portlet-spring.xml");
				springHibernateFileName = webappResourcesDir.concat(
					"/META-INF/hibernate-spring.xml");
				springInfrastructureFileName = webappResourcesDir.concat(
					"/META-INF/infrastructure-spring.xml");
				springShardDataSourceFileName = webappResourcesDir.concat(
					"/META-INF/shard-data-source-spring.xml");
				sqlDir = webappDir.concat("/WEB-INF/sql");

				if (Validator.isNull(serviceFileName)) {
					serviceFileName = webappDir.concat("/WEB-INF/service.xml");
				}
			}
		}

		if (Validator.isNull(sqlDir)) {
			sqlDir = baseDir.concat("/src/main/webapp/WEB-INF/sql");
		}

		if (pluginType.equals("ext")) {
			if (Validator.isNull(beanLocatorUtil)) {
				beanLocatorUtil =
					"com.liferay.portal.kernel.bean.PortalBeanLocatorUtil";
			}

			if (Validator.isNull(propsUtil)) {
				propsUtil = "com.liferay.portal.util.PropsUtil";
			}

			if (Validator.isNull(sqlFileName)) {
				sqlFileName = "portal-tables.sql";
			}

			pluginName = null;
			springBaseFileName = null;
			springClusterFileName = null;
			springDynamicDataSourceFileName = null;
			springHibernateFileName = null;
			springInfrastructureFileName = null;
			springShardDataSourceFileName = null;
		}
		else {
			String webappDir = baseDir.concat("/src/main/webapp");
			String webappResourcesDir = baseDir.concat("/src/main/resources");

			if (Validator.isNull(beanLocatorUtil)) {
				beanLocatorUtil =
					"com.liferay.util.bean.PortletBeanLocatorUtil";
			}

			if (Validator.isNull(hbmFileName)) {
				hbmFileName = webappResourcesDir.concat(
					"/META-INF/portlet-hbm.xml");
			}

			if (Validator.isNull(implDir)) {
				implDir = baseDir.concat("/src/main/java");
				implResourcesDir = baseDir.concat("/src/main/resources");
			}

			if (Validator.isNull(jsonFileName)) {
				jsonFileName = webappDir.concat("/js/service.js");
			}

			if (Validator.isNull(modelHintsFileName)) {
				modelHintsFileName = webappResourcesDir.concat(
					"/META-INF/portlet-model-hints.xml");
			}

			if (Validator.isNull(ormFileName)) {
				ormFileName = webappResourcesDir.concat(
					"/META-INF/portlet-orm.xml");
			}

			if (Validator.isNull(propsUtil)) {
				propsUtil = "com.liferay.util.service.ServiceProps";
			}

			if (Validator.isNull(serviceFileName)) {
				serviceFileName = webappDir.concat("/WEB-INF/service.xml");
			}

			if (Validator.isNull(springBaseFileName)) {
				springBaseFileName = webappResourcesDir.concat(
					"/META-INF/base-spring.xml");
			}

			if (Validator.isNull(springClusterFileName)) {
				springClusterFileName = webappResourcesDir.concat(
					"/META-INF/cluster-spring.xml");
			}

			if (Validator.isNull(springDynamicDataSourceFileName)) {
				springDynamicDataSourceFileName = webappResourcesDir.concat(
					"/META-INF/dynamic-data-source-spring.xml");
			}

			if (Validator.isNull(springFileName)) {
				springFileName = webappResourcesDir.concat(
					"/META-INF/portlet-spring.xml");
			}

			if (Validator.isNull(springHibernateFileName)) {
				springHibernateFileName = webappResourcesDir.concat(
					"/META-INF/hibernate-spring.xml");
			}

			if (Validator.isNull(springInfrastructureFileName)) {
				springInfrastructureFileName = webappResourcesDir.concat(
					"/META-INF/infrastructure-spring.xml");
			}

			if (Validator.isNull(springShardDataSourceFileName)) {
				springShardDataSourceFileName = webappResourcesDir.concat(
					"/META-INF/shard-data-source-spring.xml");
			}

			if (Validator.isNull(sqlFileName)) {
				sqlFileName = "tables.sql";
			}
		}
	}

	/**
	 * @deprecated 
	 * @since 6.1.0
	 */
	protected void invokeDependencyBuild() throws Exception {
		if (!postBuildDependencyModules) {
			return;
		}

		getLog().warn(
			"Invoker is no longer supported by Maven 3 and will be removed " +
				"in future builds.");

		List<Dependency> dependencies = new ArrayList<Dependency>();

		MavenProject parentProject = project.getParent();

		if (parentProject == null) {
			return;
		}

		String groupId = project.getGroupId();

		List<String> modules = parentProject.getModules();

		List<String> reactorIncludes = new ArrayList<String>();

		for (Object dependencyObj : project.getDependencies()) {
			Dependency dependency = (Dependency)dependencyObj;

			if (groupId.equals(dependency.getGroupId()) &&
				modules.contains(dependency.getArtifactId())) {

				reactorIncludes.add(dependency.getArtifactId() + "/pom.xml");
			}
		}

		if (reactorIncludes.isEmpty()) {
			return;
		}

		InvocationRequest invocationRequest = new DefaultInvocationRequest();

		invocationRequest.activateReactor(
			reactorIncludes.toArray(new String[0]), null);

		invocationRequest.setBaseDirectory(parentProject.getBasedir());

		if (postBuildGoals == null) {
			postBuildGoals = new ArrayList<String>();

			postBuildGoals.add("install");
		}

		invocationRequest.setGoals(postBuildGoals);
		invocationRequest.setRecursive(false);

		MavenCommandLineBuilder mavenCommandLineBuilder =
			new MavenCommandLineBuilder();

		getLog().info(
			"Executing " + mavenCommandLineBuilder.build(invocationRequest));

		InvocationResult invocationResult = invoker.execute(invocationRequest);

		if (invocationResult.getExecutionException() != null) {
			throw invocationResult.getExecutionException();
		}
		else if (invocationResult.getExitCode() != 0) {
			throw new MojoExecutionException(
				"Exit code " + invocationResult.getExitCode());
		}
	}

	protected void moveServicePropertiesFile() {
		File servicePropertiesFile = new File(implDir, "service.properties");

		if (servicePropertiesFile.exists()) {
			FileUtil.move(
				servicePropertiesFile,
				new File(implResourcesDir, "service.properties"));
		}
	}

	/**
	 * @parameter
	 */
	private String apiBaseDir;

	/**
	 * @parameter
	 */
	private String apiDir;

	/**
	 * @parameter default-value="true"
	 * @required
	 */
	private boolean autoNamespaceTables;

	/**
	 * @parameter default-value="${basedir}"
	 * @required
	 */
	private String baseDir;

	/**
	 * @parameter
	 */
	private String beanLocatorUtil;

	/**
	 * @parameter
	 */
	private String hbmFileName;

	/**
	 * @parameter
	 */
	private String implBaseDir;

	/**
	 * @parameter
	 */
	private String implDir;

	/**
	 * @parameter
	 */
	private String implResourcesDir;

	/**
	 * @component
	 */
	private Invoker invoker;

	/**
	 * @parameter
	 */
	private String jsonFileName;

	/**
	 * @parameter
	 */
	private String modelHintsFileName;

	/**
	 * @parameter
	 */
	private String ormFileName;

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
	 * @deprecated
	 * @parameter default-value="false" expression="${postBuildDependencyModules}"
	 * @since 6.1.0
	 */
	private boolean postBuildDependencyModules;

	/**
	 * @deprecated
	 * @parameter
	 * @since 6.1.0
	 */
	private List<String> postBuildGoals;

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @parameter
	 */
	private String propsUtil;

	/**
	 * @parameter
	 */
	private String remotingFileName;

	/**
	 * @parameter default-value="" expression="${serviceFileName}"
	 */
	private String serviceFileName;

	/**
	 * @parameter
	 */
	private String springBaseFileName;

	/**
	 * @parameter
	 */
	private String springClusterFileName;

	/**
	 * @parameter
	 */
	private String springDynamicDataSourceFileName;

	/**
	 * @parameter
	 */
	private String springFileName;

	/**
	 * @parameter
	 */
	private String springHibernateFileName;

	/**
	 * @parameter
	 */
	private String springInfrastructureFileName;

	/**
	 * @parameter
	 */
	private String springShardDataSourceFileName;

	/**
	 * @parameter
	 */
	private String sqlDir;

	/**
	 * @parameter
	 */
	private String sqlFileName;

	/**
	 * @parameter default-value="indexes.sql"
	 * @required
	 */
	private String sqlIndexesFileName;

	/**
	 * @parameter default-value="indexes.properties"
	 * @required
	 */
	private String sqlIndexesPropertiesFileName;

	/**
	 * @parameter default-value="sequences.sql"
	 * @required
	 */
	private String sqlSequencesFileName;

	/**
	 * @parameter
	 */
	private String webappBaseDir;

}
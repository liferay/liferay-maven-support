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

import com.liferay.maven.plugins.util.FileUtil;
import com.liferay.maven.plugins.util.StringUtil;
import com.liferay.maven.plugins.util.Validator;

import java.io.File;

import java.net.URI;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
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
public class ServiceBuilderMojo extends AbstractLiferayMojo {

	protected void copyServicePropertiesFile() throws Exception {
		File servicePropertiesFile = new File(
			implResourcesDir, "service.properties");

		if (servicePropertiesFile.exists()) {
			FileUtil.copyFile(
				servicePropertiesFile, new File(implDir, "service.properties"));
		}
	}

	protected void doExecute() throws Exception {
		String packaging = project.getPackaging();

		if (packaging.equals("pom")) {
			getLog().info("Skipping " + project.getArtifactId());

			return;
		}

		String artifactId = project.getArtifactId();

		if (pluginType.equals("ext") &&
			(artifactId.endsWith("ext-util-bridges") ||
			 artifactId.endsWith("ext-util-java") ||
			 artifactId.endsWith("ext-util-taglib"))) {

			getLog().info("Skipping " + artifactId);

			return;
		}

		if (pluginType.equals("ext")) {
			StringBuilder sb = new StringBuilder();

			sb.append("WARNING: Support for ServiceBuilder in EXT plugins ");
			sb.append("will be deprecated in future versions. EXT plugins ");
			sb.append("are designed to override the portal's core code that ");
			sb.append("cannot be done with hooks, layout templates, ");
			sb.append("portlets, or themes. EXT plugins are not meant to ");
			sb.append("contain new custom services. Please migrate your ");
			sb.append("service.xml to a portlet plugin.");

			getLog().warn(sb.toString());
		}

		initProperties();

		if (Validator.isNull(serviceFileName)) {
			throw new MojoExecutionException(
				"Unable to find service.xml with path " + serviceFileName);
		}

		File inputFile = new File(serviceFileName);

		if (!inputFile.exists()) {
			throw new MojoExecutionException(
				"Unable to find service.xml with path " +
					inputFile.getAbsolutePath());
		}

		getLog().info("Building from " + serviceFileName);

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

		String[] args = new String[28];

		args[0] = "service.input.file=" + serviceFileName;
		args[1] = "service.hbm.file=" + hbmFileName;
		args[2] = "service.orm.file=" + ormFileName;
		args[3] = "service.model.hints.file=" + modelHintsFileName;
		args[4] = "service.spring.file=" + springFileName;
		args[5] = "service.spring.base.file=" + springBaseFileName;
		args[6] = "service.spring.cluster.file=" + springClusterFileName;
		args[7] =
			"service.spring.dynamic.data.source.file=" +
				springDynamicDataSourceFileName;
		args[8] = "service.spring.hibernate.file=" + springHibernateFileName;
		args[9] =
			"service.spring.infrastructure.file=" +
				springInfrastructureFileName;
		args[10] =
			"service.spring.shard.data.source.file=" +
				springShardDataSourceFileName;
		args[11] = "service.api.dir=" + apiDir;
		args[12] = "service.impl.dir=" + implDir;
		args[13] = "service.json.file=" + jsonFileName;
		args[14] = "service.remoting.file=" + remotingFileName;
		args[15] = "service.sql.dir=" + sqlDir;
		args[16] = "service.sql.file=" + sqlFileName;
		args[17] = "service.sql.indexes.file=" + sqlIndexesFileName;
		args[18] =
			"service.sql.indexes.properties.file=" +
				sqlIndexesPropertiesFileName;
		args[19] = "service.sql.sequences.file=" + sqlSequencesFileName;
		args[20] = "service.auto.namespace.tables=" + autoNamespaceTables;
		args[21] = "service.bean.locator.util=" + beanLocatorUtil;
		args[22] = "service.props.util=" + propsUtil;
		args[23] = "service.plugin.name=" + pluginName;
		args[24] = "service.target.entity.name=" + targetEntityName;
		args[25] = "service.test.dir=";
		args[26] = "service.build.number=" + serviceBuildNumber;
		args[27] =
			"service.build.number.increment=" + serviceBuildNumberIncrement;

		executeTool(
			"com.liferay.portal.tools.servicebuilder.ServiceBuilder",
			getProjectClassLoader(), args);

		if (tempServiceFile != null) {
			FileUtil.delete(tempServiceFile);
		}

		moveServicePropertiesFile();

		invokeDependencyBuild();
	}

	@Override
	protected List<String> getProjectClassPath() throws Exception {
		List<String> projectClassPath = super.getProjectClassPath();

		File file = new File(implResourcesDir);

		URI uri = file.toURI();

		URL url = uri.toURL();

		projectClassPath.add(0, url.toString());

		return projectClassPath;
	}

	protected void initPortalProperties() throws Exception {
		super.initPortalProperties();

		initProperties();
	}

	protected void initProperties() throws Exception {
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

			if (Validator.isNull(webappBaseDir)) {
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

			if (Validator.isNull(apiDir)) {
				apiDir = baseDir.concat("/src/main/java");
			}

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
	 * @deprecated As of 6.1.0
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

	protected void moveServicePropertiesFile() throws Exception {
		FileUtil.move(
			new File(implDir, "service.properties"),
			new File(implResourcesDir, "service.properties"));
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
	 * @deprecated As of 6.2.0
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
	 * @deprecated As of 6.1.0
	 * @parameter default-value="false" expression="${postBuildDependencyModules}"
	 */
	private boolean postBuildDependencyModules;

	/**
	 * @deprecated As of 6.1.0
	 * @parameter
	 */
	private List<String> postBuildGoals;

	/**
	 * @parameter
	 */
	private String propsUtil;

	/**
	 * @parameter
	 */
	private String remotingFileName;

	/**
	 * @parameter default-value="1" expression="${serviceBuildNumber}"
	 */
	private long serviceBuildNumber;

	/**
	 * @parameter default-value="true" expression="${serviceBuildNumberIncrement}"
	 */
	private boolean serviceBuildNumberIncrement;

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
	 * @deprecated As of 6.1.0
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
	private String targetEntityName;

	/**
	 * @parameter
	 */
	private String webappBaseDir;

}
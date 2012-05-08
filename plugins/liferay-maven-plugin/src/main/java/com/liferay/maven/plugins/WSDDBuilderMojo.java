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

import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.tools.WSDDBuilder;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import com.liferay.portal.util.FileImpl;
import com.liferay.portal.util.HtmlImpl;
import com.liferay.portal.xml.SAXReaderImpl;

import java.io.File;

import java.lang.reflect.Method;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * @author Mika Koivisto
 * @goal   build-wsdd
 */
public class WSDDBuilderMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException {
		try {
			initClassLoader();

			initPortal();

			doExecute();
		}
		catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	protected void doExecute() throws Exception {
		WSDDBuilder wsddBuilder = new WSDDBuilder();

		wsddBuilder.setFileName(serviceFileName);
		wsddBuilder.setOutputPath(resourcesDir + "/");
		wsddBuilder.setServerConfigFileName(serverConfigFileName);
		wsddBuilder.setServiceNamespace(serviceNamespace);

		wsddBuilder.build();
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

	protected void initPortal() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());

		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(new FileImpl());

		HtmlUtil htmlUtil = new HtmlUtil();

		htmlUtil.setHtml(new HtmlImpl());

		SAXReaderUtil saxReaderUtil = new SAXReaderUtil();

		saxReaderUtil.setSAXReader(new SAXReaderImpl());
	}

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @parameter default-value="${basedir}/src/main/resources"
	 * @required
	 */
	private String resourcesDir;

	/**
	 * @parameter default-value="${basedir}/src/main/webapp/WEB-INF/server-config.wsdd" expression="${serverConfigFileName}"
	 * @required
	 */
	private String serverConfigFileName;

	/**
	 * @parameter default-value="${basedir}/src/main/webapp/WEB-INF/service.xml" expression="${serviceFileName}"
	 * @required
	 */
	private String serviceFileName;

	/**
	 * @parameter default-value="Plugin" expression="${serviceNamespace}"
	 */
	private String serviceNamespace;

}
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.SassToCssBuilder;

import java.io.File;

import java.lang.reflect.Method;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author Mika Koivisto
 * @goal   build-css
 * @phase  process-sources
 */
public class SassToCssBuilderMojo extends AbstractLiferayMojo {

	protected void doExecute() throws Exception {
		if (appServerPortalDir == null) {
			throw new MojoExecutionException(
				"The parameter appServerPortalDir is required");
		}

		List<String> dirNames = new ArrayList<String>();

		for (String dirName : StringUtil.split(sassDirNames)) {
			dirNames.add(dirName);
		}

		new SassToCssBuilder(dirNames);
	}

	protected void initClassLoader() throws Exception {
		super.initClassLoader();

		synchronized (SassToCssBuilderMojo.class) {
			Class<?> clazz = getClass();

			URLClassLoader urlClassLoader =
				(URLClassLoader)clazz.getClassLoader();

			Method method = URLClassLoader.class.getDeclaredMethod(
				"addURL", URL.class);

			method.setAccessible(true);

			String[] fileNames = FileUtil.listFiles(appServerLibPortalDir);

			for (String fileName : fileNames) {
				File file = new File(appServerLibPortalDir, fileName);

				URI uri = file.toURI();

				method.invoke(urlClassLoader, uri.toURL());
			}
		}
	}

	/**
	 * @parameter default-value="${project.build.directory}/${project.build.finalName}"
	 * @required
	 */
	private String sassDirNames;

}
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

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author Mika Koivisto
 * @goal   build-css
 * @phase  process-sources
 */
public class SassToCssBuilderMojo extends AbstractLiferayMojo {

	protected void doExecute() throws Exception {
		FileFilter fileFilter = FileFilterUtils.orFileFilter(
			DirectoryFileFilter.DIRECTORY,
			FileFilterUtils.andFileFilter(
				FileFileFilter.FILE, FileFilterUtils.suffixFileFilter(".css")));

		FileUtils.copyDirectory(webappSourceDir, webappDir, fileFilter, true);

		String[] dirNames = StringUtils.split(sassDirNames);

		String[] args = null;

		if (dirNames.length > 1) {
			args = new String[dirNames.length];

			for (int i = 0; i < dirNames.length; i++) {
				args[i] = "sass.dir." + i + "=" + dirNames[i];
			}
		}
		else {
			args = new String[] { "sass.dir=" + sassDirNames };
		}

		executeTool(_SASS_TO_CSS_BUILDER, getProjectClassLoader(), args);
	}

	private static final String _SASS_TO_CSS_BUILDER =
		"com.liferay.portal.tools.SassToCssBuilder";

	/**
	 * @parameter default-value="${project.build.directory}/${project.build.finalName}"
	 * @required
	 */
	private String sassDirNames;

	/**
	 * @parameter default-value="${project.build.directory}/${project.build.finalName}"
	 * @required
	 */
	private File webappDir;

	/**
	 * @parameter default-value="${basedir}/src/main/webapp"
	 * @required
	 */
	private File webappSourceDir;

}
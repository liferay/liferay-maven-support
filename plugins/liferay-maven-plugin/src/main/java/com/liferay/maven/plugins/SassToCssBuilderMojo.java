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

import com.liferay.maven.plugins.util.StringUtil;
import com.liferay.maven.plugins.util.Validator;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;

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

		if (Validator.isNull(sassPortalCommonDir)) {
			File file = new File(appServerPortalDir, "html/css/common");

			sassPortalCommonDir = file.getAbsolutePath();
		}

		String[] args = null;

		String[] dirNames = StringUtil.split(sassDirNames);

		if (dirNames.length > 1) {
			if (getPortalMajorVersion() < PORTAL_VERSION_6_2) {
				args = new String[dirNames.length];
			}
			else {
				args = new String[dirNames.length + 2];

				args[dirNames.length] = "sass.docroot.dir=" + sassDocrootDir;
				args[dirNames.length + 1] =
					"sass.portal.common.dir=" + sassPortalCommonDir;
			}

			for (int i = 0; i < dirNames.length; i++) {
				if (getPortalMajorVersion() < PORTAL_VERSION_6_2) {
					args[i] = "sass.dir." + i + "=" + dirNames[i];
				}
			}
		}
		else {
			if (getPortalMajorVersion() < PORTAL_VERSION_6_2) {
				if (sassDirNames.equals("/")) {
					sassDirNames = "";
				}

				args = new String[] {
					"sass.dir=" + sassDocrootDir + sassDirNames
				};
			}
			else {
				args = new String[] {
					"sass.dir=" + sassDirNames,
					"sass.docroot.dir=" + sassDocrootDir,
					"sass.portal.common.dir=" + sassPortalCommonDir
				};
			}
		}

		executeTool(
			"com.liferay.portal.tools.SassToCssBuilder",
			getProjectClassLoader(), args);
	}

	/**
	 * @parameter default-value="/"
	 * @required
	 */
	private String sassDirNames;

	/**
	 * @parameter default-value="${project.build.directory}/${project.build.finalName}"
	 * @required
	 */
	private String sassDocrootDir;

	/**
	 * @parameter
	 */
	private String sassPortalCommonDir;

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
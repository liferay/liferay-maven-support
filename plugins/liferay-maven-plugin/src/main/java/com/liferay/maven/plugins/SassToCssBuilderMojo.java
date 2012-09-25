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

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.SassToCssBuilder;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * @author Mika Koivisto
 * @goal   build-css
 * @phase  process-sources
 */
public class SassToCssBuilderMojo extends AbstractLiferayMojo {

	protected void doExecute() throws Exception {
		IOFileFilter cssSuffixFilter = FileFilterUtils.suffixFileFilter(".css");
		IOFileFilter cssFiles = FileFilterUtils.andFileFilter(
			FileFileFilter.FILE, cssSuffixFilter);
		FileFilter filter = FileFilterUtils.orFileFilter(
			DirectoryFileFilter.DIRECTORY, cssFiles);

		FileUtils.copyDirectory(
			webappSourceDir, webappDir, filter, true);

		List<String> dirNames = new ArrayList<String>();

		for (String dirName : StringUtil.split(sassDirNames)) {
			dirNames.add(dirName);
		}

		new SassToCssBuilder(dirNames);
	}

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
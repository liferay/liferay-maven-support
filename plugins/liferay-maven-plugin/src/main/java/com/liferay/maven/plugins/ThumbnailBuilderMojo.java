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

/**
 * @author Mika Koivisto
 * @goal   build-thumbnail
 * @phase  process-sources
 */
public class ThumbnailBuilderMojo extends AbstractLiferayMojo {

	protected void doExecute() throws Exception {
		String[] args = new String[5];

		args[0] = "thumbnail.original.file=" + originalFile.getAbsolutePath();
		args[1] = "thumbnail.thumbnail.file=" + thumbnailFile.getAbsolutePath();
		args[2] = "thumbnail.height=" + height;
		args[3] = "thumbnail.width=" + width;
		args[4] = "thumbnail.overwrite=" + overwrite;

		executeTool(
			"com.liferay.portal.tools.ThumbnailBuilder", getToolsClassLoader(),
			args);
	}

	/**
	 * @parameter default-value="120"
	 * @required
	 */
	private int height;

	/**
	 * @parameter default-value="${basedir}/src/main/webapp/images/screenshot.png"
	 * @required
	 */
	private File originalFile;

	/**
	 * @parameter default-value="false"
	 * @required
	 */
	private boolean overwrite;

	/**
	 * @parameter default-value="${basedir}/src/main/webapp/images/thumbnail.png"
	 * @required
	 */
	private File thumbnailFile;

	/**
	 * @parameter default-value="160"
	 * @required
	 */
	private int width;

}
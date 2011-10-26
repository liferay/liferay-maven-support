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

import com.liferay.portal.tools.ThumbnailBuilder;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author Mika Koivisto
 * @goal   build-thumbnail
 * @phase  process-sources
 */
public class ThumbnailBuilderMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException {
		new ThumbnailBuilder(
			originalFile, thumbnailFile, height, width, overwrite);
	}

	/**
	 * @parameter default-value="120"
	 * @required
	 */
	private int height;

	/**
	 * @parameter expression="${basedir}/src/main/webapp/images/screenshot.png"
	 * @required
	 */
	private File originalFile;

	/**
	 * @parameter default-value="false"
	 * @required
	 */
	private boolean overwrite;

	/**
	 * @parameter expression="${basedir}/src/main/webapp/images/thumbnail.png"
	 * @required
	 */
	private File thumbnailFile;

	/**
	 * @parameter default-value="160"
	 * @required
	 */
	private int width;

}
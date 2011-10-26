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

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author Mika Koivisto
 * @author Thiago Moreira
 * @goal   deploy
 */
public class PluginDeployerMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException {
		try {
			doExecute();
		}
		catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	protected void doExecute() throws Exception {
		if (warFile.exists()) {
			getLog().info(
				"Deploying " + warFileName + " to " +
					autoDeployDir.getAbsolutePath());

			FileUtils.copyFile(warFile, new File(autoDeployDir, warFileName));
		}
		else {
			getLog().warn(warFileName + " does not exist");
		}
	}

	/**
	 * @parameter
	 * @required
	 */
	private File autoDeployDir;

	/**
	 * @parameter expression="${project.build.directory}/${project.build.finalName}.war"
	 * @required
	 */
	private File warFile;

	/**
	 * @parameter expression="${project.build.finalName}.war"
	 * @required
	 */
	private String warFileName;

}
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
 * @goal   build-lang
 */
public class LangBuilderMojo extends AbstractLiferayMojo {

	protected void doExecute() throws Exception {
		File file = new File(langDir, langFile + ".properties");

		if (!file.exists()) {
			return;
		}

		String[] args = new String[4];

		args[0] = "lang.dir=" + langDir;
		args[1] = "lang.file=" + langFile;
		args[2] = "lang.plugin=" + langPlugin;
		args[3] = "lang.translate=" + langTranslate;

		executeTool(
			"com.liferay.portal.tools.LangBuilder", getToolsClassLoader(),
			args);
	}

	/**
	 * @parameter default-value="${basedir}/src/main/resources/content" expression="${langDir}"
	 * @required
	 */
	private String langDir;

	/**
	 * @parameter default-value="Language" expression="${langFile}"
	 */
	private String langFile;

	/**
	 * @parameter default-value="true" expression="${langPlugin}"
	 */
	private boolean langPlugin;

	/**
	 * @parameter default-value="true" expression="${langTranslate}"
	 */
	private boolean langTranslate;

}
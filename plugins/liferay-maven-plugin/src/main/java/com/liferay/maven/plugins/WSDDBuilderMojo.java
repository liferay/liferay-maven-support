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

import com.liferay.portal.tools.WSDDBuilder;

/**
 * @author Mika Koivisto
 * @goal   build-wsdd
 */
public class WSDDBuilderMojo extends AbstractLiferayMojo {

	protected void doExecute() throws Exception {
		WSDDBuilder wsddBuilder = new WSDDBuilder();

		wsddBuilder.setFileName(serviceFileName);
		wsddBuilder.setOutputPath(resourcesDir + "/");
		wsddBuilder.setServerConfigFileName(serverConfigFileName);
		wsddBuilder.setServiceNamespace(serviceNamespace);

		wsddBuilder.build();
	}

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
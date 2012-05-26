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
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.DBBuilder;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author Mika Koivisto
 * @goal   build-db
 */
public class DBBuilderMojo extends AbstractLiferayMojo {

	protected void doExecute() throws MojoExecutionException {
		if (pluginType.equals("ext")) {
			StringBuilder sb = new StringBuilder();

			sb.append("WARNING: Support for ServiceBuilder in EXT ");
			sb.append("plugins will be deprecated in future versions. ");
			sb.append("EXT plugins are designed to override the portal's ");
			sb.append("core code that cannot be done with hooks, layout ");
			sb.append("templates, portlets, or themes. EXT plugins are ");
			sb.append("not meant to contain new custom services. Please ");
			sb.append("migrate your service.xml to a portlet plugin.");

			getLog().warn(sb.toString());
		}

		if ((Validator.isNotNull(apiBaseDir) ||
			Validator.isNotNull(implBaseDir)) &&
			Validator.isNull(webappBaseDir)) {

			webappBaseDir = baseDir;
		}

		if (Validator.isNull(sqlDir)) {
			if (pluginType.equals("ext") || Validator.isNull(webappBaseDir)) {
				sqlDir = baseDir.concat("/src/main/webapp/WEB-INF/sql");
			}
			else {
				sqlDir = webappBaseDir.concat("/src/main/webapp/WEB-INF/sql");
			}
		}

		getLog().debug("DatabaseName: " + databaseName);
		getLog().debug("DatabaseTypes: " + databaseTypes);
		getLog().debug("SQLDir: " + sqlDir);

		new DBBuilder(databaseName, StringUtil.split(databaseTypes), sqlDir);
	}

	/**
	 * @parameter
	 */
	public String apiBaseDir;

	/**
	 * @parameter default-value="${basedir}"
	 * @required
	 */
	public String baseDir;

	/**
	 * @parameter default-value="lportal" expression="${databaseName}"
	 * @required
	 */
	private String databaseName;

	/**
	 * @parameter default-value="db2,derby,firebird,hypersonic,informix,ingres,interbase,jdatastore,mysql,oracle,postgresql,sap,sqlserver,sybase" expression="${databaseTypes}"
	 * @required
	 */
	private String databaseTypes;

	/**
	 * @parameter
	 */
	public String implBaseDir;

	/**
	 * @parameter
	 */
	public String sqlDir;

	/**
	 * @parameter
	 */
	public String webappBaseDir;

}

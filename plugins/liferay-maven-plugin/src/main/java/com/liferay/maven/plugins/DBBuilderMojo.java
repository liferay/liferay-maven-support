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

			sb.append("WARNING: Support for ServiceBuilder in EXT plugins ");
			sb.append("will be deprecated in future versions. EXT plugins ");
			sb.append("are designed to override the portal's core code that ");
			sb.append("cannot be done with hooks, layout templates, ");
			sb.append("portlets, or themes. EXT plugins are not meant to ");
			sb.append("contain new custom services. Please migrate your ");
			sb.append("service.xml to a portlet plugin.");

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

		getLog().debug("Database name " + databaseName);
		getLog().debug("Database types " + databaseTypes);
		getLog().debug("SQL directory " + sqlDir);

		new DBBuilder(databaseName, StringUtil.split(databaseTypes), sqlDir);
	}

	/**
	 * @parameter
	 */
	private String apiBaseDir;

	/**
	 * @parameter default-value="${basedir}"
	 * @required
	 */
	private String baseDir;

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
	private String implBaseDir;

	/**
	 * @parameter
	 */
	private String sqlDir;

	/**
	 * @parameter
	 */
	private String webappBaseDir;

}
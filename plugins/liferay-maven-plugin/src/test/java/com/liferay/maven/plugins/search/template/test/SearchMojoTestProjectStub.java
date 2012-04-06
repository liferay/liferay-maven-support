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

package com.liferay.maven.plugins.search.template.test;

import java.io.File;
import java.util.Properties;

import com.liferay.maven.plugins.test.stubs.LiferayPluginsMavenProjectStub;

/**
 * @author kamesh.sampath
 */
public class SearchMojoTestProjectStub extends LiferayPluginsMavenProjectStub
{

	public SearchMojoTestProjectStub()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.maven.plugins.test.stubs.LiferayPluginsMavenProjectStub#getProjetPath()
	 */
	@Override
	protected String getProjetPath()
	{
		return "search-container-test";
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.maven.plugin.testing.stubs.MavenProjectStub#readModel(java.io.File)
	 */
	@Override
	protected void readModel( File pomFile )
	{
		super.readModel( pomFile );
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.maven.plugin.testing.stubs.MavenProjectStub#getProperties()
	 */
	public Properties getProperties()
	{
		Properties properties = getModel().getProperties();
		System.out.println( "props:" + properties );
		return properties;
	}

}

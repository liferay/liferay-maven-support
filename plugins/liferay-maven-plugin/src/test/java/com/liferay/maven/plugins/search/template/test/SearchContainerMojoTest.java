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

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import com.liferay.maven.plugins.SearchContainerMojo;

/**
 * @author kamesh.sampath
 */

public class SearchContainerMojoTest extends AbstractMojoTestCase
{

	private File testPom;

	/*
	 * (non-Javadoc)
	 * @see org.apache.maven.plugin.testing.AbstractMojoTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		testPom = new File( getBasedir(), "src/test/resources/unit/search-container-test/pom.xml" );

	}

	/**
	 * /**
	 * 
	 * @throws Exception
	 */
	public void testBuildSearchContainer() throws Exception
	{

		assertNotNull( testPom );
		assertTrue( testPom.exists() );

		SearchContainerMojo searchContainerMojo = (SearchContainerMojo) lookupMojo( "build-search-container", testPom );

		assertNotNull( searchContainerMojo );

		searchContainerMojo.execute();
	}

}

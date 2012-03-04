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
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.liferay.maven.plugins.xml.SearchContainerModelParser;

/**
 * @author kamesh.sampath
 */
public class SearchContainerModelParserTest extends TestCase
{

	File searchContainersFile = new File( "src/test/resources/unit/search-container-test/search-containers.xml" );

	public void testParsing() throws XmlPullParserException, IOException
	{
		assertNotNull( searchContainersFile );
		SearchContainerModelParser parser = new SearchContainerModelParser();
		List<SearchContainerModelParser.SearchContainer> containers =
			parser.read( ReaderFactory.newXmlReader( searchContainersFile ) );
		assertEquals( 1, containers.size() );
		for ( SearchContainerModelParser.SearchContainer searchContainer : containers )
		{
			assertEquals( "customer", searchContainer.getName() );
			assertEquals( "com.lifery.demo", searchContainer.getPackageDirectory() );
			assertEquals( 10, searchContainer.getDelta().intValue() );
			assertEquals( 0, searchContainer.getAdditionalImports().size() );
			assertEquals( 4, searchContainer.getSearchFields().size() );
			assertEquals( "com.test.model.Customer", searchContainer.getModelClass() );
			assertEquals( "customerId", searchContainer.getModelPK() );
			assertEquals( "there-are-no-records", searchContainer.getEmptyResultsMessage() );
		}

	}
}

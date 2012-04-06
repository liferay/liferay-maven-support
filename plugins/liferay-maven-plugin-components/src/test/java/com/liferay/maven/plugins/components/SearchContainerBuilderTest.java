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

package com.liferay.maven.plugins.components;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryEvent;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;

import com.liferay.maven.plugins.model.SearchFieldModel;
import com.liferay.maven.plugins.util.SearchTemplateConfiguration;

import freemarker.ext.beans.BeansWrapper;

/**
 * @author kamesh.sampath
 */
public class SearchContainerBuilderTest extends PlexusTestCase
{

	Map<String, Object> map = new HashMap<String, Object>();
	LiferayPortalCodeBuilder codeBuilder;

	/*
	 * (non-Javadoc)
	 * @see org.codehaus.plexus.PlexusTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		codeBuilder = lookup( LiferayPortalCodeBuilder.class, "search-container" );

		File webappDir = new File( getBasedir() + "/target/webapp" );
		if ( !webappDir.exists() )
		{
			webappDir.mkdirs();
		}

		map.put("statics", BeansWrapper.getDefaultInstance().getStaticModels());  

		

		map.put( SearchTemplateConfiguration.TPL_MAP_NAME_KEY, "tributeToGod" );
		map.put( SearchTemplateConfiguration.TPL_MAP_PACKAGE_DIR_KEY, "com.liferay.demo" );

		map.put( SearchTemplateConfiguration.TPL_MAP_ADVANCED_SEARCH_KEY, "false" );
		map.put( SearchTemplateConfiguration.TPL_MAP_DELTA_KEY, "10" );
		map.put( SearchTemplateConfiguration.TPL_MAP_EMPTY_MESSAGE_KEY, "there-are-no-records" );
		map.put( SearchTemplateConfiguration.TPL_MAP_MODEL_CLASS_KEY, "com.test.model.Customer" );
		map.put( SearchTemplateConfiguration.TPL_MAP_MODEL_PRIMARY_KEY, "customerId" );

		List<String> imports = new ArrayList<String>();
		imports.add( "com.test.model.Customer" );
		imports.add( "java.util.Date" );
		imports.add( "java.util.Locale" );
		imports.add( "java.util.TimeZone" );
		imports.add( "java.text.Format" );
		imports.add( "com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" );
		map.put( SearchTemplateConfiguration.TPL_MAP_IMPORTS_KEY, imports );

		List<SearchFieldModel> fields = new ArrayList<SearchFieldModel>();
		fields.add( new SearchFieldModel( "customer-id", "customerId", "int", true ) );
		fields.add( new SearchFieldModel( "first-name", "firstName", null, true ) );
		fields.add( new SearchFieldModel( "last-name", "lastName", null, true ) );
		fields.add( new SearchFieldModel( "date-of-birth", "dateOfBirth", "date", false ) );

		// map.put( SearchTemplateConfiguration.TPL_MAP_HEADER_NAMES_KEY, fields );
		map.put( SearchTemplateConfiguration.TPL_MAP_SEARCH_PARAMETERS_KEY, fields );

	}

	/*
	 * (non-Javadoc)
	 * @see org.codehaus.plexus.PlexusTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testGenerateJavaFiles() throws Exception
	{
		assertNotNull( codeBuilder );
		codeBuilder.generateJavaFiles( null, new File( getBasedir() + "/target/gen-src" ), map );
	}

	public void testGenerateJspFiles() throws Exception
	{
		assertNotNull( codeBuilder );
		codeBuilder.generateJspFiles( null, new File( getBasedir() + "/target/webapp" ), map );
	}

}

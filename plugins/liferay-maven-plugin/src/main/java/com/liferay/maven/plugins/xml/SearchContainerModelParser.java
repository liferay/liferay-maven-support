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

package com.liferay.maven.plugins.xml;

import java.io.Reader;
import java.util.List;

import com.liferay.maven.plugins.model.SearchFieldModel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * This class is used to parse the Search Container model xml and return {@link SearchContainer} model object to the consumer
 * <br/> An example of the Search container model will be<br/> 
 * <pre>
 * &lt;searchContainers&gt;
 *  &lt;searchContainer&gt;
 *    &lt;name&gt;customer&lt;/name&gt;
 *    &lt;delta&gt;10&lt;/delta&gt;
 *		&lt;packageDirectory&gt;com.lifery.demo&lt;/packageDirectory&gt;
 *		&lt;additionalImports /&gt;
 *		&lt;searchFields&gt;
 *			&lt;searchField&gt;
 *				&lt;columnHeader&gt;customer-id&lt;/columnHeader&gt;
 *				&lt;fieldName&gt;customerId&lt;/fieldName&gt;
 *				&lt;fieldType&gt;int&lt;/fieldType&gt;
 *				&lt;showable&gt;true&lt;/showable&gt;
 *			&lt;/searchField&gt;
 *			&lt;searchField&gt;
 *				&lt;columnHeader&gt;customer-first-name&lt;/columnHeader&gt;
 *				&lt;fieldName&gt;firstName&lt;/fieldName&gt;
 *				&lt;showable&gt;true&lt;/showable&gt;
 *			&lt;/searchField&gt;
 *			&lt;searchField&gt;
 *				&lt;columnHeader&gt;customer-last-name&lt;/columnHeader&gt;
 *				&lt;fieldName&gt;lastName&lt;/fieldName&gt;
 *				&lt;showable&gt;true&lt;/showable&gt;
 *			&lt;/searchField&gt;
 *			&lt;searchField&gt;
 *				&lt;columnHeader&gt;date-of-birth&lt;/columnHeader&gt;
 *				&lt;fieldName&gt;dateOfBirth&lt;/fieldName&gt;
 *				&lt;fieldType&gt;date&lt;/fieldType&gt;
 *				&lt;showable&gt;true&lt;/showable&gt;
 *			&lt;/searchField&gt;
 *		&lt;/searchFields&gt;
 *		&lt;modelClass&gt;com.test.model.Customer&lt;/modelClass&gt;
 *		&lt;modelPK&gt;customerId&lt;/modelPK&gt;
 *		&lt;emptyResultsMessage&gt;there-are-no-records&lt;/emptyResultsMessage&gt;
 *	&lt;/searchContainer&gt;
 *  &lt;!-- More search-container elements if required  --&gt;
 *&lt;/searchContainers&gt;
 * </pre>
 * @author kamesh.sampath
 */
public class SearchContainerModelParser
{

	XStream xstream;

	public SearchContainerModelParser()
	{
		xstream = new XStream( new StaxDriver() );
		xstream.alias( "searchContainers", List.class );
		xstream.alias( "searchContainer", SearchContainer.class );
		xstream.alias( "additionalImports", List.class );
		xstream.alias( "searchField", SearchFieldModel.class );
	}

	@SuppressWarnings( "unchecked" )
	public List<SearchContainer> read( Reader xml )
	{
		List<SearchContainer> searchContainers = (List<SearchContainer>) xstream.fromXML( xml );
		return searchContainers;
	}

	/**
	 * @author kamesh.sampath
	 */
	public static final class SearchContainer
	{

		/**
		 * The default package for generated sources
		 */
		private String packageDirectory;

		/**
		 * The search container logical name, this will prefixed to generated class names
		 */
		private String name;

		/**
		 * Whether to add advanced search to the search container
		 */
		private Boolean advancedSearch;

		/**
		 * The search fields model which will hold the parameters to search, its header name and whether i needs to be
		 * displayed in the list
		 */
		private List<SearchFieldModel> searchFields;

		/**
		 * The addtional imports that might be required
		 */
		private List<String> additionalImports;

		/**
		 * The default number of records to be displayed
		 */
		private Integer delta;

		/**
		 * The model class that will be used, fully qualified name
		 */
		private String modelClass;

		/**
		 * The model property field that will be used to identify the row in the DB
		 */
		private String modelPK;

		/**
		 * The empty message that needs to be displayed when there are no records
		 */
		private String emptyResultsMessage;

		/**
		 * @return the packageDirectory
		 */
		public String getPackageDirectory()
		{
			return packageDirectory;
		}

		/**
		 * @param packageDirectory
		 *            the packageDirectory to set
		 */
		public void setPackageDirectory( String packageDirectory )
		{
			this.packageDirectory = packageDirectory;
		}

		/**
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName( String name )
		{
			this.name = name;
		}

		/**
		 * @return the advancedSearch
		 */
		public Boolean getAdvancedSearch()
		{
			return advancedSearch;
		}

		/**
		 * @param advancedSearch
		 *            the advancedSearch to set
		 */
		public void setAdvancedSearch( Boolean advancedSearch )
		{
			this.advancedSearch = advancedSearch;
		}

		/**
		 * @return the searchFields
		 */
		public List<SearchFieldModel> getSearchFields()
		{
			return searchFields;
		}

		/**
		 * @param searchFields
		 *            the searchFields to set
		 */
		public void add( SearchFieldModel searchField )
		{
			this.searchFields.add( searchField );
		}

		/**
		 * @return the additionalImports
		 */
		public List<String> getAdditionalImports()
		{
			return additionalImports;
		}

		/**
		 * add the import to additonal imports collection
		 * 
		 * @param additionalImport
		 */
		public void add( String additionalImport )
		{
			this.additionalImports.add( additionalImport );
		}

		/**
		 * @return the delta
		 */
		public Integer getDelta()
		{
			return delta;
		}

		/**
		 * @param delta
		 *            the delta to set
		 */
		public void setDelta( Integer delta )
		{
			this.delta = delta;
		}

		/**
		 * @return the modelClass
		 */
		public String getModelClass()
		{
			return modelClass;
		}

		/**
		 * @param modelClass
		 *            the modelClass to set
		 */
		public void setModelClass( String modelClass )
		{
			this.modelClass = modelClass;
		}

		/**
		 * @return the modelPK
		 */
		public String getModelPK()
		{
			return modelPK;
		}

		/**
		 * @param modelPK
		 *            the modelPK to set
		 */
		public void setModelPK( String modelPK )
		{
			this.modelPK = modelPK;
		}

		/**
		 * @return the emptyResultsMessage
		 */
		public String getEmptyResultsMessage()
		{
			return emptyResultsMessage;
		}

		/**
		 * @param emptyResultsMessage
		 *            the emptyResultsMessage to set
		 */
		public void setEmptyResultsMessage( String emptyResultsMessage )
		{
			this.emptyResultsMessage = emptyResultsMessage;
		}

	}
}

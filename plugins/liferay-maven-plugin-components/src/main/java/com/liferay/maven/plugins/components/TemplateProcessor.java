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

import java.util.Map;

/**
 * The generic Template processor interface that will be implemented by spefic template processing engines to provide template based code generation
 * @author kamesh.sampath
 */
public interface TemplateProcessor
{

	String ROLE = TemplateProcessor.class.getName();

	/**
	 * The method that will be used by the TemplateProcessor impementations to intialize and store the templates locally
	 * 
	 * @throws Exception
	 *             - any error that might occur during template inializations
	 */
	public void init() throws Exception;

	/**
	 * The method that will do the actual processing of the templates, by substituting the values from the map
	 * 
	 * @param templateName
	 *            - the name of the template to process
	 * @param rootMap
	 *            - the map of values which will be used as template data
	 * @return the string representation of the generated content
	 * @throws Exception
	 *             - any error that might occur during processing
	 */
	public String processTemplate( String templateName, Map<String, Object> rootMap ) throws Exception;
}

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

package com.liferay.maven.plugins.test.stubs;

import org.apache.maven.artifact.handler.DefaultArtifactHandler;

/**
 * @author kamesh.sampath
 */
public class LiferayArtifactHandlerStub extends DefaultArtifactHandler
{

	private String language;

	/**
	 * @return the language
	 */
	public String getLanguage()
	{
		return language == null ? "java" : language;
	}

	/**
	 * @param language
	 *            the language to set
	 */
	public void setLanguage( String language )
	{
		this.language = language;
	}

}

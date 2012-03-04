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

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;

/**
 * @author kamesh.sampath
 */
public class LiferayPluginsWebArtifact extends ArtifactStub
{

	private String groupId;
	private String artifactId;
	private String packaging;
	private String version;
	private VersionRange versionRange;
	private ArtifactHandler handler;

	/**
	 * @param groupId
	 * @param artifactId
	 * @param packaging
	 * @param version
	 */
	public LiferayPluginsWebArtifact( String groupId, String artifactId, String packaging, String version )
	{
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.packaging = packaging;
		this.version = version;
		this.versionRange = VersionRange.createFromVersion( version );
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId()
	{
		return groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId( String groupId )
	{
		this.groupId = groupId;
	}

	/**
	 * @return the artifactId
	 */
	public String getArtifactId()
	{
		return artifactId;
	}

	/**
	 * @param artifactId
	 *            the artifactId to set
	 */
	public void setArtifactId( String artiactId )
	{
		this.artifactId = artiactId;
	}

	/**
	 * @return the packaging
	 */
	public String getPackaging()
	{
		return packaging;
	}

	/**
	 * @param packaging
	 *            the packaging to set
	 */
	public void setPackaging( String packaging )
	{
		this.packaging = packaging;
	}

	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion( String version )
	{
		this.version = version;
	}

	/**
	 * @return the handler
	 */
	public ArtifactHandler getHandler()
	{
		return handler;
	}

	/**
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler( ArtifactHandler handler )
	{
		this.handler = handler;
	}

	/**
	 * @return the versionRange
	 */
	public VersionRange getVersionRange()
	{
		return versionRange;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.maven.plugin.testing.stubs.ArtifactStub#getType()
	 */
	@Override
	public String getType()
	{

		return "war";
	}

}

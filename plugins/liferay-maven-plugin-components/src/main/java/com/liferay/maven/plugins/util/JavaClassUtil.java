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

package com.liferay.maven.plugins.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author kamesh.sampath
 */
public class JavaClassUtil
{

	/**
	 * @param name
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static String javaFileName( String name, String prefix, String suffix )
	{
		StringBuffer buffer = new StringBuffer();

		if ( prefix != null )
		{
			buffer.append( StringUtils.capitalize( prefix ) );
		}
		buffer.append( StringUtils.capitalize( name ) );

		if ( suffix != null )
		{
			buffer.append( StringUtils.capitalize( suffix ) );
		}
		if ( buffer.length() > 1 )
		{
			buffer.append( ".java" );
		}
		return buffer.toString();
	}

	/**
	 * @param name
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static String javaMethodorFieldName( String name, String prefix, String suffix )
	{
		StringBuffer buffer = new StringBuffer();

		if ( prefix != null )
		{
			buffer.append( StringUtils.lowerCase( prefix ) );
		}

		if ( buffer.length() == 0 )
		{
			buffer.append( StringUtils.lowerCase( name ) );
		}
		else
		{
			buffer.append( StringUtils.capitalize( name ) );
		}

		if ( suffix != null )
		{
			buffer.append( StringUtils.capitalize( suffix ) );
		}

		return buffer.toString();
	}

	/**
	 * @param packageName
	 * @return
	 */
	public static String checkJavaPackage( String packageName )
	{
		if ( packageName.contains( "." ) )
		{
			packageName = StringUtils.replace( packageName, ".", "/" );
		}
		return packageName;
	}
}

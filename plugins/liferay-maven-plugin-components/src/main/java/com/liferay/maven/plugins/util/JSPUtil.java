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
public class JSPUtil
{

	/**
	 * 
	 * @param name
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static String jspFileName( String name, String prefix, String suffix )
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append( StringUtils.lowerCase( name ) );
		buffer.append( "_" );
		buffer.append( suffix.substring( 0, ( suffix.lastIndexOf( "_" )) ) );
		if ( buffer.length() > 1 )
		{
			buffer.append( ".jsp" );
		}
		return buffer.toString();
	}
}

/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 * @author Brian Wing Shun Chan
 */
public class StringUtil {

	public static String replace(String s, String oldSub, String newSub) {
		return StringUtils.replace(s, oldSub, newSub);
	}

	public static String safePath(String path) {
		return StringUtils.replace(path, "//", "/");
	}

	public static String[] split(String s) {
		return split(s, ",");
	}

	public static String[] split(String s, String delimiter) {
		return StringUtils.split(s, delimiter);
	}

}
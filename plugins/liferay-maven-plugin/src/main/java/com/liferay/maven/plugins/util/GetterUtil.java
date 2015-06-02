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
public class GetterUtil {

	public static float getFloat(String value) {
		return getFloat(value, 0f);
	}

	public static float getFloat(String value, float defaultValue) {
		if (value == null) {
			return defaultValue;
		}

		try {
			return Float.parseFloat(value.trim());
		}
		catch (Exception e) {
		}

		return defaultValue;
	}

	public static String getString(String value, String defaultValue) {
		if (value == null) {
			return defaultValue;
		}

		value = value.trim();

		if (value.indexOf('\r') != -1) {
			value = StringUtils.replace(value, "\r\n", "\n");
		}

		return value;
	}

}
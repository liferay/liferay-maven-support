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

/**
 * @author Mika Koivisto
 */
public class JS {

	public static String getSafeName(String name) {
		if (name == null) {
			return null;
		}

		StringBuilder sb = null;

		int index = 0;

		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);

			switch (c) {
				case ' ':

				case '-':

				case '.':
					if (sb == null) {
						sb = new StringBuilder(name.length() - 1);

						sb.append(name, index, i);
					}

					break;

				default:
					if (sb != null) {
						sb.append(c);
					}
			}
		}

		if (sb == null) {
			return name;
		}
		else {
			return sb.toString();
		}
	}

}
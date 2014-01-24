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

import java.io.File;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import org.xml.sax.EntityResolver;

/**
 * @author Brian Wing Shun Chan
 */
public class SAXReaderUtil {

	public static Document read(File file, boolean validate) throws Exception {
		SAXReader saxReader = new SAXReader(validate);

		saxReader.setEntityResolver(_entityResolver);

		return saxReader.read(file);
	}

	public static void setEntityResolver(EntityResolver entityResolver) {
		_entityResolver = entityResolver;
	}

	private static EntityResolver _entityResolver;

}
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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Brian Wing Shun Chan
 */
public class FileUtil {

	public static void copyDirectory(File source, File destination)
		throws IOException {

		FileUtils.copyDirectory(source, destination);
	}

	public static void copyFile(File source, File destination)
		throws IOException {

		FileUtils.copyFile(source, destination);
	}

	public static void delete(File file) throws IOException {
		FileUtils.forceDelete(file);
	}

	public static String getExtension(String fileName) {
		return FilenameUtils.getExtension(fileName);
	}

	public static void mkdirs(String pathName) {
		File file = new File(pathName);

		file.mkdirs();
	}

	public static boolean move(File source, File destination) {
		if (!source.exists()) {
			return false;
		}

		destination.delete();

		try {
			if (source.isDirectory()) {
				FileUtils.moveDirectory(source, destination);
			}
			else {
				FileUtils.moveFile(source, destination);
			}
		}
		catch (IOException ioe) {
			return false;
		}

		return true;
	}

}
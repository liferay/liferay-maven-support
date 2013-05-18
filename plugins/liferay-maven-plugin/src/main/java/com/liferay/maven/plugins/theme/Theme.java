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

package com.liferay.maven.plugins.theme;

/**
 * @author Mika Koivisto
 */
public class Theme {

	public Theme(String id) {
		_id = id;
	}

	public String getCssPath() {
		return _cssPath;
	}

	public String getImagesPath() {
		return _imagesPath;
	}

	public String getJavaScriptPath() {
		return _javaScriptPath;
	}

	public String getRootPath() {
		return _rootPath;
	}

	public String getTemplateExtension() {
		return _templateExtension;
	}

	public String getTemplatesPath() {
		return _templatesPath;
	}

	public void setCssPath(String cssPath) {
		_cssPath = cssPath;
	}

	public void setImagesPath(String imagesPath) {
		_imagesPath = imagesPath;
	}

	public void setJavaScriptPath(String javaScriptPath) {
		_javaScriptPath = javaScriptPath;
	}

	public void setRootPath(String rootPath) {
		_rootPath = rootPath;
	}

	public void setTemplateExtension(String templateExtension) {
		_templateExtension = templateExtension;
	}

	public void setTemplatesPath(String templatesPath) {
		_templatesPath = templatesPath;
	}

	private String _cssPath;
	private String _id;
	private String _imagesPath;
	private String _javaScriptPath;
	private String _rootPath;
	private String _templateExtension;
	private String _templatesPath;

}
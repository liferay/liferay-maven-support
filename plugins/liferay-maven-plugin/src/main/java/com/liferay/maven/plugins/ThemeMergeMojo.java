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

package com.liferay.maven.plugins;

import com.liferay.maven.plugins.theme.Theme;
import com.liferay.maven.plugins.util.ContextReplace;
import com.liferay.maven.plugins.util.GetterUtil;
import com.liferay.maven.plugins.util.PortalUtil;
import com.liferay.maven.plugins.util.SAXReaderUtil;
import com.liferay.maven.plugins.util.StringUtil;
import com.liferay.maven.plugins.util.Validator;

import java.io.File;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;

import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Mika Koivisto
 * @goal   theme-merge
 * @phase  process-sources
 */
public class ThemeMergeMojo extends AbstractLiferayMojo {

	protected void cleanUpTemplates(File templatesDir) {
		File initFile = new File(templatesDir, "init." + themeType);

		FileUtils.deleteQuietly(initFile);

		String[] extensions = null;

		if (themeType.equals("ftl")) {
			extensions = new String[] {"vm", "jsp"};
		}
		else if (themeType.equals("jsp")) {
			extensions = new String[] {"vm", "ftl"};
		}
		else {
			extensions = new String[] {"ftl", "jsp"};
		}

		Iterator<File> iterator = FileUtils.iterateFiles(
			templatesDir, extensions, false);

		while (iterator.hasNext()) {
			File file = iterator.next();

			FileUtils.deleteQuietly(file);
		}
	}

	protected void doExecute() throws Exception {
		workDir = new File(workDir, "liferay-theme");

		if (!workDir.exists()) {
			workDir.mkdirs();
		}

		if (Validator.isNotNull(parentTheme)) {
			if (parentTheme.indexOf(":") > 0) {
				String[] parentThemeArray = parentTheme.split(":");

				parentThemeArtifactGroupId = parentThemeArray[0];
				parentThemeArtifactId = parentThemeArray[1];
				parentThemeArtifactVersion = parentThemeArray[2];
				parentThemeId = null;
			}
			else {
				parentThemeId = parentTheme;
			}
		}

		getLog().info("Parent theme group ID " + parentThemeArtifactGroupId);
		getLog().info("Parent theme artifact ID " + parentThemeArtifactId);
		getLog().info("Parent theme version " + parentThemeArtifactVersion);
		getLog().info("Parent theme ID " + parentThemeId);

		String[] excludes = null;
		String[] includes = null;

		boolean portalTheme = false;

		if (parentThemeArtifactGroupId.equals("com.liferay.portal") &&
			parentThemeArtifactId.equals("portal-web")) {

			portalTheme = true;
		}

		if (!portalTheme) {
			Dependency dependency = createDependency(
				parentThemeArtifactGroupId, parentThemeArtifactId,
				parentThemeArtifactVersion, "", "war");

			Artifact artifact = resolveArtifact(dependency);

			UnArchiver unArchiver = archiverManager.getUnArchiver(
				artifact.getFile());

			unArchiver.setDestDirectory(workDir);
			unArchiver.setSourceFile(artifact.getFile());

			IncludeExcludeFileSelector includeExcludeFileSelector =
				new IncludeExcludeFileSelector();

			includeExcludeFileSelector.setExcludes(excludes);
			includeExcludeFileSelector.setIncludes(includes);

			unArchiver.setFileSelectors(
				new FileSelector[] {includeExcludeFileSelector});

			unArchiver.extract();
		}

		File liferayLookAndFeelXml = new File(
			webappSourceDir, "WEB-INF/liferay-look-and-feel.xml");

		if (liferayLookAndFeelXml.exists()) {
			Document document = SAXReaderUtil.read(
				liferayLookAndFeelXml, false);

			Element rootElement = document.getRootElement();

			List<Element> themeElements = rootElement.elements("theme");

			for (Element themeElement : themeElements) {
				String id = themeElement.attributeValue("id");

				if (Validator.isNotNull(themeId) && !themeId.equals(id)) {
					continue;
				}

				Theme targetTheme = readTheme(themeElement);

				if (portalTheme) {
					mergePortalTheme(targetTheme);
				}
				else {
					File sourceLiferayLookAndFeelXml = new File(
						workDir, "WEB-INF/liferay-look-and-feel.xml");

					Theme sourceTheme = readTheme(
						parentThemeId, sourceLiferayLookAndFeelXml);

					mergeTheme(sourceTheme, targetTheme);
				}
			}
		}
		else {
			String id = PortalUtil.getJsSafePortletId(project.getArtifactId());

			Theme targetTheme = readTheme(id, null);

			if (portalTheme) {
				mergePortalTheme(targetTheme);
			}
			else {
				File sourceLiferayLookAndFeelXml = new File(
					workDir, "WEB-INF/liferay-look-and-feel.xml");

				Theme sourceTheme = readTheme(
					parentThemeId, sourceLiferayLookAndFeelXml);

				mergeTheme(sourceTheme, targetTheme);
			}
		}
	}

	protected void mergePortalTheme(Theme targetTheme) throws Exception {
		File templatesDir = new File(webappDir, targetTheme.getTemplatesPath());

		templatesDir.mkdirs();

		FileUtils.copyDirectory(
			new File(appServerPortalDir, "html/themes/_unstyled/templates"),
			templatesDir);

		getLog().info(
			"Copying html/themes/_unstyled/templates to " + templatesDir);

		File cssDir = new File(webappDir, targetTheme.getCssPath());

		cssDir.mkdirs();

		FileUtils.copyDirectory(
			new File(appServerPortalDir, "html/themes/_unstyled/css"), cssDir);

		getLog().info("Copying html/themes/_unstyled/css to " + cssDir);

		File imagesDir = new File(webappDir, targetTheme.getImagesPath());

		imagesDir.mkdirs();

		FileUtils.copyDirectory(
			new File(appServerPortalDir, "html/themes/_unstyled/images"),
			imagesDir);

		getLog().info("Copying html/themes/_unstyled/images to " + imagesDir);

		File javaScriptDir = new File(
			webappDir, targetTheme.getJavaScriptPath());

		javaScriptDir.mkdirs();

		FileUtils.copyDirectory(
			new File(appServerPortalDir, "html/themes/_unstyled/js"),
			javaScriptDir);

		getLog().info("Copying html/themes/_unstyled/js to " + javaScriptDir);

		if (parentThemeId.equals("_unstyled")) {
			mergeTheme(webappSourceDir, targetTheme, targetTheme);

			return;
		}

		FileUtils.copyDirectory(
			new File(appServerPortalDir, "html/themes/_styled/css"), cssDir);

		getLog().info("Copying html/themes/_styled/css to " + cssDir);

		FileUtils.copyDirectory(
			new File(appServerPortalDir, "html/themes/_styled/images"),
			imagesDir);

		getLog().info("Copying html/themes/_styled/images to " + imagesDir);

		if (parentThemeId.equals("_styled")) {
			mergeTheme(webappSourceDir, targetTheme, targetTheme);

			return;
		}

		File liferayLookAndFeelXml = new File(
			appServerPortalDir, "WEB-INF/liferay-look-and-feel.xml");

		Theme sourceTheme = readTheme(parentThemeId, liferayLookAndFeelXml);

		mergeTheme(appServerPortalDir, sourceTheme, targetTheme);
		mergeTheme(webappSourceDir, targetTheme, targetTheme);
	}

	protected void mergeTheme(
			File sourceDir, Theme sourceTheme, Theme targetTheme)
		throws Exception {

		File sourceCssDir = new File(sourceDir, sourceTheme.getCssPath());

		if (sourceCssDir.exists()) {
			File targetCssDir = new File(webappDir, targetTheme.getCssPath());

			targetCssDir.mkdirs();

			FileUtils.copyDirectory(sourceCssDir, targetCssDir);

			getLog().info("Copying " + sourceCssDir + " to " + targetCssDir);
		}

		File sourceImagesDir = new File(sourceDir, sourceTheme.getImagesPath());

		if (sourceImagesDir.exists()) {
			File targetImagesDir = new File(
				webappDir, targetTheme.getImagesPath());

			targetImagesDir.mkdirs();

			FileUtils.copyDirectory(sourceImagesDir, targetImagesDir);

			getLog().info(
				"Copying " + sourceImagesDir + " to " + targetImagesDir);
		}

		File sourceJavaScriptDir = new File(
			sourceDir, sourceTheme.getJavaScriptPath());

		if (sourceJavaScriptDir.exists()) {
			File targetJavaScriptDir = new File(
				webappDir, targetTheme.getJavaScriptPath());

			targetJavaScriptDir.mkdirs();

			FileUtils.copyDirectory(sourceJavaScriptDir, targetJavaScriptDir);

			getLog().info(
				"Copying " + sourceJavaScriptDir + " to " +
					targetJavaScriptDir);
		}

		File sourceTemplatesDir = new File(
			sourceDir, sourceTheme.getTemplatesPath());

		if (sourceTemplatesDir.exists()) {
			File targetTemplatesDir = new File(
				webappDir, targetTheme.getTemplatesPath());

			targetTemplatesDir.mkdirs();

			FileUtils.copyDirectory(sourceTemplatesDir, targetTemplatesDir);

			getLog().info(
				"Copying " + sourceTemplatesDir + " to " + targetTemplatesDir);

			cleanUpTemplates(targetTemplatesDir);
		}
	}

	protected void mergeTheme(Theme sourceTheme, Theme targetTheme)
		throws Exception {

		mergeTheme(workDir, sourceTheme, targetTheme);
		mergeTheme(webappSourceDir, targetTheme, targetTheme);
	}

	protected Theme readTheme(Element themeElement) {
		String id = themeElement.attributeValue("id");

		Theme theme = new Theme(id);

		ContextReplace themeContextReplace = new ContextReplace();

		themeContextReplace.addValue("themes-path", null);

		String rootPath = GetterUtil.getString(
			themeElement.elementText("root-path"), "/");

		rootPath = themeContextReplace.replace(rootPath);

		themeContextReplace.addValue("root-path", rootPath);

		theme.setRootPath(rootPath);

		String templatesPath = GetterUtil.getString(
			themeElement.elementText("templates-path"),
			rootPath.concat("/templates"));

		templatesPath = themeContextReplace.replace(templatesPath);
		templatesPath = StringUtil.safePath(templatesPath);

		themeContextReplace.addValue("templates-path", templatesPath);

		theme.setTemplatesPath(templatesPath);

		String cssPath = GetterUtil.getString(
			themeElement.elementText("css-path"), rootPath.concat("/css"));

		cssPath = themeContextReplace.replace(cssPath);
		cssPath = StringUtil.safePath(cssPath);

		themeContextReplace.addValue("css-path", cssPath);

		theme.setCssPath(cssPath);

		String imagesPath = GetterUtil.getString(
			themeElement.elementText("images-path"),
			rootPath.concat("/images"));

		imagesPath = themeContextReplace.replace(imagesPath);
		imagesPath = StringUtil.safePath(imagesPath);

		themeContextReplace.addValue("images-path", imagesPath);

		theme.setImagesPath(imagesPath);

		String javaScriptPath = GetterUtil.getString(
			themeElement.elementText("javascript-path"),
			rootPath.concat("/js"));

		javaScriptPath = themeContextReplace.replace(javaScriptPath);
		javaScriptPath = StringUtil.safePath(javaScriptPath);

		themeContextReplace.addValue("javascript-path", javaScriptPath);

		theme.setJavaScriptPath(javaScriptPath);

		String templateExtension = GetterUtil.getString(
			themeElement.elementText("template-extension"), themeType);

		theme.setTemplateExtension(templateExtension);

		return theme;
	}

	protected Theme readTheme(String themeId, File liferayLookAndFeelXml)
		throws Exception {

		if ((liferayLookAndFeelXml != null) && liferayLookAndFeelXml.exists()) {
			Document document = SAXReaderUtil.read(
				liferayLookAndFeelXml, false);

			Element rootElement = document.getRootElement();

			List<Element> themeElements = rootElement.elements("theme");

			for (Element themeElement : themeElements) {
				String id = themeElement.attributeValue("id");

				if (Validator.isNotNull(themeId) && !themeId.equals(id)) {
					continue;
				}

				return readTheme(themeElement);
			}
		}

		Theme theme = new Theme(themeId);

		theme.setCssPath("/css");
		theme.setImagesPath("/images");
		theme.setJavaScriptPath("/js");
		theme.setRootPath("/");
		theme.setTemplateExtension(themeType);
		theme.setTemplatesPath("/templates");

		return theme;
	}

	/**
	 * The parent theme can be _styled, _unstyled, classic, control_panel, or artifactGroupId:artifactId:artifactVersion.
	 *
	 * @parameter
	 * @deprecated As of 6.2.0
	 */
	private String parentTheme;

	/**
	 * @parameter default-value="com.liferay.portal"
	 */
	private String parentThemeArtifactGroupId;

	/**
	 * @parameter default-value="portal-web"
	 */
	private String parentThemeArtifactId;

	/**
	 * @parameter default-value="${liferay.version}"
	 */
	private String parentThemeArtifactVersion;

	/**
	 * @parameter default-value="_styled"
	 */
	private String parentThemeId;

	/**
	 * @parameter
	 */
	private String themeId;

	/**
	 * @parameter default-value="vm"
	 * @required
	 */
	private String themeType;

	/**
	 * @parameter default-value="${project.build.directory}/${project.build.finalName}"
	 * @required
	 */
	private File webappDir;

	/**
	 * @parameter default-value="${basedir}/src/main/webapp"
	 * @required
	 */
	private File webappSourceDir;

}
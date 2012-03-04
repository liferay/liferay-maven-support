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

package com.liferay.maven.plugins.components.impl;

import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.liferay.maven.plugins.components.TemplateProcessor;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * The concrete implementation of the {@link TemplateProcessor} that will use
 * Freemarker Template engine to generate the code
 * 
 * @author kamesh.sampath
 */
@Component(instantiationStrategy = "singleton", isolatedRealm = false, role = TemplateProcessor.class, hint = "freemarker", version = "0.0.1")
public class FreemarkerTemplateProcessor implements Initializable,
		TemplateProcessor {

	final Logger logger = new ConsoleLogger(Logger.LEVEL_INFO,
			"com.liferay.maven.plugins.components");

	private Configuration ftlConfiguration;

	private String templatesPath;

	private Properties templates;

	private final Hashtable<String, Template> templatesTable = new Hashtable<String, Template>();

	public FreemarkerTemplateProcessor() throws Exception {
		logger.debug("Am getting constructed : Om Shakti!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liferay.maven.plugins.components.TemplateProcessor#init(java.util
	 * .Properties)
	 */
	public void init() throws Exception {

		logger.debug("Adding templates to processor");

		for (Object obj : templates.keySet()) {
			String templateKey = (String) obj;
			String templateName = templates.getProperty(templateKey);
			logger.debug("Template:"
					+ ftlConfiguration.getTemplate(templateName));
			templatesTable.put(templateKey,
					ftlConfiguration.getTemplate(templateName));
		}
		logger.debug("Template map:" + templatesTable);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liferay.maven.plugins.components.TemplateProcessor#processTemplate
	 * (java.lang.String, java.util.Map)
	 */
	public String processTemplate(String templateName,
			Map<String, Object> rootMap) throws Exception {
		logger.debug("Processing template:" + templateName);
		StringWriter out = new StringWriter();
		Template template = templatesTable.get(templateName);
		template.process(rootMap, out);
		out.flush();
		String content = out.toString();
		out.close();
		return content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable#
	 * initialize()
	 */
	public void initialize() throws InitializationException {
		logger.debug("Templates-Path :" + templatesPath);
		try {
			if (templatesPath != null) {
				ftlConfiguration = new Configuration();
				ftlConfiguration.setObjectWrapper(new DefaultObjectWrapper());
				ftlConfiguration.setSetting(Configuration.CACHE_STORAGE_KEY,
						"strong:20, soft:250");
				ftlConfiguration.setClassForTemplateLoading(
						FreemarkerTemplateProcessor.class, templatesPath);
				init();
			} else {
				throw new Exception("Invalid template path:[" + templatesPath
						+ "]");
			}

		} catch (Exception e) {
			logger.error("Error loading freemarker templates", e);
			throw new InitializationException(
					"Error loading freemarker templates");
		}

	}

	/**
	 * @param templateKey - the key correspoding to the template file
	 * @return - the Freemarker {@link Template} corresponding to the key
	 */
	public Template getTemplate(String templateKey) {
		return templatesTable.get(templateKey);
	}

}

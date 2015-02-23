/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;

/**
 * @author Gregory Amerson
 */
public class PluginDeployerMojoTest extends AbstractMojoTestCase {

	public void testDeployGoalExists() throws Exception {
		PluginDeployerMojo pluginDeployerMojo = _getPluginDeployerMojo(
			"plugin-deployer-mojo-pom.xml");

		assertNotNull(pluginDeployerMojo);
	}

	public void testMojoExecution() throws Exception {
		PluginDeployerMojo pluginDeployerMojo = _getPluginDeployerMojo(
			"plugin-deployer-mojo-pom.xml");

		pluginDeployerMojo.execute();

		assertTrue(new File("target/deploy/empty.war").exists());
	}

	public void testMojoSkipsExecution() throws Exception {
		PluginDeployerMojo pluginDeployerMojo = _getPluginDeployerMojo(
			"plugin-deployer-mojo-pom.xml");

		MavenProject project = (MavenProject)getVariableValueFromObject(
			pluginDeployerMojo, "project");

		project.setPackaging("pom");

		pluginDeployerMojo.execute();

		assertFalse(new File("target/deploy/empty.war").exists());
	}

	public void testMojoVariablesConfiguration() throws Exception {
		PluginDeployerMojo pluginDeployerMojo = _getPluginDeployerMojo(
			"plugin-deployer-mojo-pom.xml");

		String liferayVersion = (String)getVariableValueFromObject(
			pluginDeployerMojo, "liferayVersion");

		assertEquals("7.0.0", liferayVersion);

		File autoDeployDir = (File)getVariableValueFromObject(
			pluginDeployerMojo, "autoDeployDir");

		assertEquals(new File("target/deploy"), autoDeployDir);

		String warFileName = (String)getVariableValueFromObject(
			pluginDeployerMojo, "warFileName");

		assertEquals("empty.war", warFileName);

		File warFile = (File) getVariableValueFromObject(
			pluginDeployerMojo, "warFile");

		assertEquals(
			new File("src/test/resources/unit/wars/empty.war"), warFile);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		new File("target/deploy/empty.war").delete();
	}

	private PluginDeployerMojo _getPluginDeployerMojo(String filename) throws Exception {
		File testPom = new File(
			getBasedir(), "src/test/resources/unit/poms/" + filename);

		return (PluginDeployerMojo)lookupMojo("deploy", testPom);
	}

}
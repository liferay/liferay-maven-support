/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.maven.plugins;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;

/**
 * @author Gregory Amerson
 */
public class PluginDeployerMojoTest extends AbstractMojoTestCase {

	public void testDeployGoalExists() throws Exception {
		PluginDeployerMojo mojo = _getMojo("plugin-deployer-mojo-pom.xml");

		assertNotNull(mojo);
	}

	public void testMojoExecution() throws Exception {
		PluginDeployerMojo mojo = _getMojo("plugin-deployer-mojo-pom.xml");

		mojo.execute();

		assertTrue(new File("target/deploy/empty.war").exists());
	}

	public void testMojoSkipsExecution() throws Exception {
		PluginDeployerMojo mojo = _getMojo("plugin-deployer-mojo-pom.xml");

		MavenProject project = (MavenProject)getVariableValueFromObject(mojo,
				"project");
		project.setPackaging("pom");

		mojo.execute();

		assertFalse(new File("target/deploy/empty.war").exists());
	}

	public void testMojoVariablesConfiguration() throws Exception {
		PluginDeployerMojo mojo = _getMojo("plugin-deployer-mojo-pom.xml");

		String liferayVersion = (String)getVariableValueFromObject(mojo,
				"liferayVersion");

		assertEquals("7.0.0", liferayVersion);

		File autoDeployDir = (File)getVariableValueFromObject(mojo,
				"autoDeployDir");

		assertEquals(new File("target/deploy"), autoDeployDir);

		String warFileName = (String)getVariableValueFromObject(mojo,
				"warFileName");

		assertEquals("empty.war", warFileName);

		File warFile = (File) getVariableValueFromObject(mojo, "warFile");

		assertEquals(new File("src/test/resources/unit/wars/empty.war"),
				warFile);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		new File("target/deploy/empty.war").delete();
	}

	private PluginDeployerMojo _getMojo(String filename) throws Exception {
		File testPom = new File(getBasedir(), "src/test/resources/unit/poms/" +
				filename);

		return (PluginDeployerMojo) lookupMojo("deploy", testPom);
	}

}
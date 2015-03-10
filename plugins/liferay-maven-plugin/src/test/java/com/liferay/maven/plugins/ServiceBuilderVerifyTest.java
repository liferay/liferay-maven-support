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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

/**
 * @author Gregory Amerson
 */
public class ServiceBuilderVerifyTest extends TestCase {

	public void testBuildServiceResolveProject() throws Exception {
		File testDir = ResourceExtractor.simpleExtractResources(
			getClass(), "/projects/servicebuilder/6.2/resolveProject");

		assertTrue(testDir.exists());

		Verifier verifier = new Verifier(testDir.getAbsolutePath());

		verifier.deleteArtifact("it", "resolveProject", "1.0", "pom");
		verifier.deleteArtifact("it", "resolveProject-portlet", "1.0", "war");
		verifier.deleteArtifact(
			"it", "resolveProject-portlet-service", "1.0", "jar");
		verifier.setMavenDebug(true);

		List<String> cliOptions = new ArrayList<String>();

		cliOptions.add("-pl");
		cliOptions.add("resolveProject-portlet");

		verifier.setCliOptions(cliOptions);

		verifier.executeGoal("liferay:build-service");

		verifier.verifyTextInLog(
			"Resolved dependency project MavenProject: " +
				"it:resolveProject-portlet-service");

		verifier.resetStreams();
	}

}
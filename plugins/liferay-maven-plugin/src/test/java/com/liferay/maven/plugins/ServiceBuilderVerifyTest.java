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
 * @author Simon Jiang
 */
public class ServiceBuilderVerifyTest extends TestCase {

	public void testBuildServiceGeneratedClass61() throws Exception {
		buildServiceGeneratedClass("6.1.2");
	}

	public void testBuildServiceGeneratedClass62() throws Exception {
		buildServiceGeneratedClass("6.2.2");
	}

	public void testBuildServiceResolveProject() throws Exception {
		File testDir = ResourceExtractor.simpleExtractResources(
			getClass(), "/projects/servicebuilder/testProject");

		assertTrue(testDir.exists());

		Verifier verifier = new Verifier(testDir.getAbsolutePath());

		verifier.deleteArtifact("it", "testProject", "1.0", "pom");
		verifier.deleteArtifact("it", "testProject-portlet", "1.0", "war");
		verifier.deleteArtifact(
			"it", "testProject-portlet-service", "1.0", "jar");

		verifier.setMavenDebug(true);

		List<String> cliOptions = new ArrayList<String>();

		cliOptions.add("-P");
		cliOptions.add("6.2.2");
		cliOptions.add("-pl");
		cliOptions.add("testProject-portlet");

		verifier.setCliOptions(cliOptions);

		verifier.executeGoal("liferay:build-service");

		verifier.verifyTextInLog(
			"Resolved dependency project MavenProject: it:" +
				"testProject-portlet-service");

		verifier.resetStreams();
	}

	private void buildServiceGeneratedClass(String profileId) throws Exception {
		File testDir = ResourceExtractor.simpleExtractResources(
			getClass(), "/projects/servicebuilder/testProject");

		assertTrue(testDir.exists());

		Verifier verifier = new Verifier(testDir.getAbsolutePath());

		File fooServiceUtilJavaFile = new File(
			verifier.getBasedir() + "/testProject-portlet-service/src/main" +
				"/java/it/service/FooServiceUtil.java");

		if (fooServiceUtilJavaFile.exists()) {
			fooServiceUtilJavaFile.delete();
		}

		assertFalse(fooServiceUtilJavaFile.exists());

		verifier.deleteArtifact("it", "testProject", "1.0", "pom");
		verifier.deleteArtifact("it", "testProject-portlet", "1.0", "war");
		verifier.deleteArtifact(
			"it", "testProject-portlet-service", "1.0", "jar");

		verifier.setMavenDebug(true);

		List<String> cliOptions = new ArrayList<String>();

		cliOptions.add("-P");
		cliOptions.add(profileId);
		cliOptions.add("-pl");
		cliOptions.add("testProject-portlet");

		verifier.setCliOptions(cliOptions);

		verifier.executeGoal("liferay:build-service");

		assertTrue(fooServiceUtilJavaFile.exists());

		verifier.resetStreams();
	}

}
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
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class ServiceBuilderVerifyTest extends TestCase {

	public void testServiceBuilderResolveProject() throws Exception {
		File testDir = ResourceExtractor.simpleExtractResources(getClass(),
				"/projects/servicebuilder/testProject");

		assertTrue(testDir.exists());

		Verifier verifier = new Verifier(testDir.getAbsolutePath());

		verifier.deleteArtifact("it", "testProject", "1.0", "pom");
		verifier.deleteArtifact("it", "testProject-portlet", "1.0", "war");
		verifier.deleteArtifact("it", "testProject-portlet-service", "1.0", "jar");

		verifier.setMavenDebug(true);

		List<String> cliOptionsFor62 = new ArrayList<String>();

		cliOptionsFor62.add("-P");
		cliOptionsFor62.add("6.2.2");

		cliOptionsFor62.add("-pl");
		cliOptionsFor62.add("testProject-portlet");

		verifier.setCliOptions(cliOptionsFor62);

		String[] goalsFor622 = { "liferay:build-service" };
		verifier.executeGoals(Arrays.asList(goalsFor622));

		verifier.verifyTextInLog("Resolved dependency project MavenProject: "
				+ "it:testProject-portlet-service");

		verifier.deleteArtifact("it", "testProject", "1.0", "pom");
		verifier.deleteArtifact("it", "testProject-portlet", "1.0", "war");
		verifier.deleteArtifact("it", "testProject-portlet-service", "1.0", "jar");

		List<String> cliOptionsFor61 = new ArrayList<String>();

		cliOptionsFor61.add("-P");
		cliOptionsFor61.add("6.1.2");

		cliOptionsFor61.add("-pl");
		cliOptionsFor61.add("testProject-portlet");

		verifier.setCliOptions(cliOptionsFor61);

		String[] goalsFor612 = { "liferay:build-service" };
		verifier.executeGoals(Arrays.asList(goalsFor612));

		verifier.verifyTextInLog("Resolved dependency project MavenProject: "
				+ "it:testProject-portlet-service");

		verifier.resetStreams();
	}

	public void testServiceBuilderGenerateClass62() throws Exception {
		File testDir = ResourceExtractor.simpleExtractResources(getClass(),
				"/projects/servicebuilder/testProject");

		assertTrue(testDir.exists());

		Verifier verifier = new Verifier(testDir.getAbsolutePath());

		verifier.deleteArtifact("it", "testProject", "1.0", "pom");
		verifier.deleteArtifact("it", "testProject-portlet", "1.0", "war");
		verifier.deleteArtifact("it", "testProject-portlet-service", "1.0", "jar");

		verifier.setMavenDebug(true);

		List<String> cliOptions = new ArrayList<String>();

		cliOptions.add("-P");
		cliOptions.add("6.2.2");

		cliOptions.add("-pl");
		cliOptions.add("testProject-portlet");

		verifier.setCliOptions(cliOptions);

		String[] goals = { "liferay:build-service" };
		verifier.executeGoals(Arrays.asList(goals));

		File fooServiceUtilJavaFile = new File(
				verifier.getBasedir()
						+ "/testProject-portlet-service/src/main/java/it/service/FooServiceUtil.java");
		assertTrue(fooServiceUtilJavaFile.exists());

		verifier.resetStreams();
	}

	public void testServiceBuilderGenerateClass61() throws Exception {
		File testDir = ResourceExtractor.simpleExtractResources(getClass(),
				"/projects/servicebuilder/testProject/");

		assertTrue(testDir.exists());

		Verifier verifier = new Verifier(testDir.getAbsolutePath());

		verifier.deleteArtifact("it", "testProject", "1.0", "pom");
		verifier.deleteArtifact("it", "testProject-portlet", "1.0", "war");
		verifier.deleteArtifact("it", "testProject-portlet-service", "1.0", "jar");

		verifier.setMavenDebug(true);

		List<String> cliOptions = new ArrayList<String>();

		cliOptions.add("-pl");
		cliOptions.add("testProject-portlet");

		cliOptions.add("-P");
		cliOptions.add("6.1.2");

		verifier.setCliOptions(cliOptions);

		verifier.executeGoal("liferay:build-service");

		File fooServiceUtilJavaFile = new File(
				verifier.getBasedir()
						+ "/testProject-portlet-service/src/main/java/it/service/FooServiceUtil.java");
		assertTrue(fooServiceUtilJavaFile.exists());

		verifier.resetStreams();
	}
}
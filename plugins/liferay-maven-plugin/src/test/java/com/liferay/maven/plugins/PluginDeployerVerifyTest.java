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
public class PluginDeployerVerifyTest extends TestCase {

	public void testWarFileWarningMessage() throws Exception {
		File testDir = ResourceExtractor.simpleExtractResources(
			getClass(), "/projects/plugindeployer/missingWarFile");

		assertTrue(testDir.exists());

		Verifier verifier = new Verifier(testDir.getAbsolutePath());

		verifier.deleteArtifact("it", "missingWarFile", "1.0", "war");

		List<String> cliOptions = new ArrayList<String>();

		cliOptions.add("-N");

		verifier.setCliOptions(cliOptions);

		verifier.executeGoal("liferay:deploy");

		verifier.verifyErrorFreeLog();
		verifier.verifyTextInLog("missingWarFile-1.0.war does not exist");

		verifier.resetStreams();
	}

}
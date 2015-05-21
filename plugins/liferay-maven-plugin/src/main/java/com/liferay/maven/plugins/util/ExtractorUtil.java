package com.liferay.maven.plugins.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExtractorUtil {

	private static ExtractorUtil INSTANCE = null;

	protected ArchiverManager archiverManager;
	protected ArtifactFactory artifactFactory;
	protected ArtifactResolver artifactResolver;
	protected File appServerPortalDir;
	protected ArtifactRepository localArtifactRepository;
	protected List remoteArtifactRepositories;
	private static final List<String> alreadyExtracted = new ArrayList<String>();

	public ExtractorUtil(ArchiverManager archiverManager, ArtifactFactory artifactFactory, ArtifactResolver artifactResolver, File appServerPortalDir, ArtifactRepository localArtifactRepository, List remoteArtifactRepositories) {
		this.archiverManager = archiverManager;
		this.artifactFactory = artifactFactory;
		this.artifactResolver = artifactResolver;
		this.appServerPortalDir = appServerPortalDir;
		this.localArtifactRepository = localArtifactRepository;
		this.remoteArtifactRepositories = remoteArtifactRepositories;
	}

	public static void createInstance(ArtifactFactory artifactFactory, ArtifactResolver artifactResolver, ArtifactRepository localArtifactRepository, List remoteArtifactRepositories, File appServerPortalDir, ArchiverManager archiverManager) {
		INSTANCE = new ExtractorUtil(archiverManager, artifactFactory, artifactResolver, appServerPortalDir, localArtifactRepository, remoteArtifactRepositories);
	}

	public static ExtractorUtil getInstance() {
		return INSTANCE;
	}

	public void extractWeb(String liferayVersion, String... includes) throws Exception {
		List<String> newIncludes = new ArrayList<String>();
		for ( String include : includes ) {
			if ( !alreadyExtracted.contains(include) )
				newIncludes.add(include);
			else
				alreadyExtracted.add(include);
		}
		extract("com.liferay.portal", "portal-web", liferayVersion, "", "war", appServerPortalDir,
				newIncludes.toArray(new String[]{}), null);
	}

	protected Dependency createDependency(
			String groupId, String artifactId, String version, String classifier,
			String type) {

		Dependency dependency = new Dependency();

		dependency.setArtifactId(artifactId);
		dependency.setClassifier(classifier);
		dependency.setGroupId(groupId);
		dependency.setType(type);
		dependency.setVersion(version);

		return dependency;
	}

	protected Artifact resolveArtifact(Dependency dependency) throws Exception {
		Artifact artifact = null;

		if (Validator.isNull(dependency.getClassifier())) {
			artifact = artifactFactory.createArtifact(
					dependency.getGroupId(), dependency.getArtifactId(),
					dependency.getVersion(), dependency.getScope(),
					dependency.getType());
		}
		else {
			artifact = artifactFactory.createArtifactWithClassifier(
					dependency.getGroupId(), dependency.getArtifactId(),
					dependency.getVersion(), dependency.getType(),
					dependency.getClassifier());
		}

		artifactResolver.resolve(
				artifact, remoteArtifactRepositories, localArtifactRepository);

		return artifact;
	}

	public void extract(String groupId, String artifactId, String artifactVersion, String classifier,
						String packageType, File workDir, String[] includes, String[] excludes) throws Exception {


		Dependency dependency = createDependency(groupId, artifactId, artifactVersion, classifier, packageType);

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
}

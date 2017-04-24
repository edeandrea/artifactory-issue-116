package org.mycompany.gradle

import spock.lang.Specification

import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectStateInternal
import org.gradle.testfixtures.ProjectBuilder

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class TestPluginSpec extends Specification {
	@Rule
	TemporaryFolder tempFolder

	def getProject(String rootProjectName, String... childProjects) {
		def projBuilder = ProjectBuilder.builder().withProjectDir(tempFolder.root)
		def root = projBuilder.withName(rootProjectName).build()

		if (childProjects) {
			childProjects.each { projBuilder.withName(it).withParent(root).build().projectDir.mkdirs() }
		}

		root
	}

	def runProjectAfterEvaluate(Project project) {
		project.projectEvaluationBroadcaster.afterEvaluate project, new ProjectStateInternal()
	}

	def "Test for ext property"() {
		setup:
			def project = getProject 'project'

		when:
			project.apply plugin: TestPlugin
			runProjectAfterEvaluate project

		then:
			project.plugins.hasPlugin 'com.jfrog.artifactory'
			project.ext.propertyName == 'value'
	}
}
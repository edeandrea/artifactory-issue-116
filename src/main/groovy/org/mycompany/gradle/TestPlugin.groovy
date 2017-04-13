package org.mycompany.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

final class TestPlugin implements Plugin<Project> {
	@Override
	void apply(Project project) {
		project.apply plugin: 'com.jfrog.artifactory'

		project.configure(project) {
			artifactory {
				contextUrl = 'http://jcenter.bintray.com'

				publish {
					repository {
						repoKey = 'mypublishrepo'
						username = 'user'
						password = 'pwd'
					}

					defaults {
						properties = ['Deployed By': System.properties['user.name']]
						publishBuildInfo = true
						publishArtifacts = true
						publishPom = true
						publishIvy = false
					}
				}

				resolve {
					repository {
						repoKey = '/'
						maven = true
					}
				}

				clientConfig.includeEnvVars = true
				clientConfig.envVarsExcludePatterns = '*pwd*,*password*,*PWD*,*PASSWORD*,*secret*,*SECRET*,*key*,*KEY*,sonar.login'
				clientConfig.info.setBuildName('some-artifact')
				clientConfig.info.setBuildNumber(version)
				clientConfig.info.buildUrl = 'build url'
			}
		}

		project.afterEvaluate { proj ->
			proj.ext {
				propertyName = 'value'
			}
		}
	}
}
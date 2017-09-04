/*
 * jenkins 2 Build Pipeline for the jadice server converter client
 *
 * For more documentation / syntax see
 *  - https://jenkins.io/blog/2016/12/19/declarative-pipeline-beta/
 *  - https://jenkins.io/doc/book/pipeline/syntax/
 *
 * This Build Pipeline has the following requirement:
 *  - Maven 3.3.9 (or later) installed as tool "M3"
 *  - JDK 8.112 (or later) installed as tool "JDK8"
 *  - a maven settings file stored in the jenkins credentials provider under key "maven-settings"
 *
 */
pipeline {
    // Run the "classic" way directly on a jenkins agent
    agent any
	tools {
        maven 'M3' // Maven 3.3.9
        jdk 'JDK8' // JDK 8u112
	}
	/* 
	// The official docker image uses OpenJDK which does not provide support for JavaFX
	agent {
		docker 'maven:3.3.9-jdk-8'
	}
	*/
	options {
		buildDiscarder(logRotator(daysToKeepStr: '20')))
		timeout (time: 10, unit: 'MINUTES')
	}
	stages {
		stage('Compile') {
			environment {
				MVN_SETTINGS = credentials('maven-settings')
			}
			steps {
				// Currently we cannot run "mvn install" because launch4j requires 32 Bit libaries which are not provided in the jenkins docker container
				sh "mvn -B -s $MVN_SETTINGS -gs $MVN_SETTINGS test -Dmaven.test.failure.ignore=true"
			}
		}
		stage('Archive') {
			steps {
				junit '**/target/surefire-reports/*.xml'
			}
		}
	}
	post {
		always {
			deleteDir()
		}
	}
}
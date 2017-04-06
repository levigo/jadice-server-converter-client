/*
 * jenkins 2 Build Pipeline
 *
 * For more documentation / syntax see
 *  - https://jenkins.io/blog/2016/12/19/declarative-pipeline-beta/
 *  - https://jenkins.io/doc/book/pipeline/syntax/
 *
 */
pipeline {
	agent any
	tools {
		maven 'M3' // Maven 3.3.9
		jdk 'JDK8' // JDK 8u112
	}
	stages {
		stage('Compile') {
			steps {
				// Currently we cannot run "install" -> launch4j requires 32Bit libaries which are not provided in the jenkins docker container
				sh 'mvn -B test -Dmaven.test.failure.ignore=true'
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
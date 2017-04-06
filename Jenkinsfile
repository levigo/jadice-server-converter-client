pipeline {
	agent any
	tools {
		maven 'M3' // Maven 3.3.9
		jdk 'JDK8' // JDK 8u112
	}
	stages {
		stage('Compile') {
			steps {
				sh 'mvn -B install -Dmaven.test.failure.ignore=true'
			}
		}
		stage('Archive') {
			steps {
				archive "*/target/*.exe"
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
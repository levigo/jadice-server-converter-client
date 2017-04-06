pipeline {
	agent any
	tools {
		maven 'M3' // Maven 3.3.9
		jdk 'JDK8' // JDK 8u112
	}
	stages {
		stage('Clean') {
			steps {
				sh 'mvn -B clean'
			}
		}
	}
}
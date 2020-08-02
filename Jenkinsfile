pipeline {
	 agent any
	 stages {
		 stage("Preparation") {
 			steps {
 				git(
 					url: 'https://github.com/anamartinez97m/prac2-AIS-2020.git',
 					credentialsId: 'developer',
 					branch: 'master'
 				)
 			}
		}
		 stage("Test") {
			 steps {
				 script {
					if(isUnix()) {
						sh "./mvnw test"
 					} else {
 						bat(/mvnw.cmd test/)
 					}
				}
			 }
		 }
	 }
}
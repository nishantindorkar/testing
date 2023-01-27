pipeline {
    agent any
    stages {
        stage("git-pull") {
            steps { 
                sh 'sudo apt-get update -y'
                //sh 'sudo apt-get install git -y'
                git branch: 'J2EE', credentialsId: 'one', url: 'git@github.com:nishantindorkar/onlinebookstore.git'
            }
        }
        stage("build-maven") {
            steps { 
                //sh 'sudo apt-get update -y'
                //sh 'sudo apt-get install maven curl unzip -y'
                sh 'mvn clean package'
            }
        }
        stage("build-artifacts") {
            steps { 
                // sh 'curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"'
                // sh 'unzip awscliv2.zip'
                // sh 'sudo ./aws/install'
                sh 'aws s3 mb s3://studentngpbckt'
                sh 'aws s3 cp **/*.war s3://studentngpbckt/student-${BUILD_ID}.war'
            }
        }
    }
}
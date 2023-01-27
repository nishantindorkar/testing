pipeline {
    agent any
    stages {
        stage("git-pull") {
            steps { 
                sh 'sudo apt update -y'
                //sh 'sudo apt-get install git -y'
                git credentialsId: 'one', url: 'git@github.com:nishantindorkar/onlinebookstore.git'
                sh 'ls'
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
                sh 'aws s3 cp **/*.war s3://studentngpbckt/onlinebookstore-${BUILD_ID}.war'
            }
        }
        stage("tomcat-build") {
            steps { 
                withCredentials([sshUserPrivateKey(credentialsId: 'online', keyFileVariable: 'online', usernameVariable: 'tomcat')]) { 
                sh '''
                ssh -i ${online} -o StrictHostKeyChecking=no ubuntu@100.26.171.150<<EOF
                sudo apt-get update -y
                #sudo apt-get install openjdk-11-jdk -y
                #sudo apt install unzip -y
                #curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                #unzip awscliv2.zip
                #sudo ./aws/install
                aws s3 cp s3://studentngpbckt/onlinebookstore-${BUILD_ID}.war .
                curl -O https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.85/bin/apache-tomcat-8.5.85.tar.gz
                sudo tar -xvf apache-tomcat-8.5.85.tar.gz -C /opt/
                sudo sh /opt/apache-tomcat-8.5.85/bin/shutdown.sh
                sudo cp -rv onlinebookstore-${BUILD_ID}.war onlinebookstore.war
                sudo cp -rv onlinebookstore.war /opt/apache-tomcat-8.5.85/webapps/
                sudo sh /opt/apache-tomcat-8.5.85/bin/startup.sh
                '''
                }
            }
        } 
         stage('slack notification') {
          steps {
    	    slackSend color: "good", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was successful"
          }
        }  
    }
}
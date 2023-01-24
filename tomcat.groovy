pipeline {
    agent any
    stages {
        stage("git-pull") {
            steps { 
                sh 'sudo apt-get update -y'
                //sh 'sudo apt-get install git -y'
                git credentialsId: 'one', url: 'git@github.com:nishantindorkar/student-ui.git'
                //sh 'ls'
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
        stage("tomcat-build") {
            steps { 
                withCredentials([sshUserPrivateKey(credentialsId: 'cat', keyFileVariable: 'tomcat', usernameVariable: 'ubuntu')]) { 
                sh '''
                ssh -i ${tomcat} -o StrictHostKeyChecking=no ubuntu@3.83.136.140<<EOF
                sudo apt-get update -y
                #sudo apt install unzip -y
                #curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                #unzip awscliv2.zip
                #sudo ./aws/install
                aws s3 cp s3://studentngpbckt/student-${BUILD_ID}.war .
                curl -O https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.85/bin/apache-tomcat-8.5.85.tar.gz
                sudo tar -xvf apache-tomcat-8.5.85.tar.gz -C /opt/
                sudo sh /opt/apache-tomcat-8.5.85/bin/shutdown.sh
                sudo cp -rv student-${BUILD_ID}.war studentapp.war
                sudo cp -rv studentapp.war /opt/apache-tomcat-8.5.85/webapps/
                sudo sh /opt/apache-tomcat-8.5.85/bin/startup.sh
                '''
                }
            }
        }   
        stage('Email') {
            steps {
                script {
                    def mailRecipients = 'nindorkar95@gmail.com'
                    def jobName = currentBuild.fullDisplayName
                    emailext body: '''${SCRIPT, template="groovy-html.template"}''',
                    mimeType: 'text/html',
                    subject: "[Jenkins] ${jobName}",
                    to: "${mailRecipients}",
                    replyTo: "${mailRecipients}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider']]
                }
            }
}  
    }
    // post {
    //     always {
    //         //mail to: 'nindorkar95@gmail.com',
    //         emailext body: 'Hello, tomcat server started', 
    //         recipientProviders: [[$class: 'DevelopersRecipientProvider'], 
    //         [$class: 'RequesterRecipientProvider']], 
    //         subject: 'Apache Tomcat started'
    //     }
    // }
}
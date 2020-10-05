pipeline {
         agent any
         tools {
             maven 'Maven'
             jdk 'JDK_8'
         }
         options {
             buildDiscarder(logRotator(artifactNumToKeepStr: '10'))
         }
         stages {
             stage ('Build') {
                 steps {
                    withMaven(options: [pipelineGraphPublisher(lifecycleThreshold: 'install')]) {
                        sh 'mvn clean install'
                    }
                 }
             }
         }
         post {
             always {
                 deleteDir()
             }
             success {
                discordSend(webhookURL: "https://discordapp.com/api/webhooks/681503219821576243/QiyLS2wo2jjptgas_Er9NNf1ob01fRXXFn9ZRSggQy6tJo2grCtlMMZZMMyAD5zczlPN", description: "**Build:** ${env.BUILD_NUMBER}\n**Status:** Success\n\n**Changes:**\n${env.BUILD_URL}", footer: "CubeMC Build System", link: "${env.BUILD_URL}", successful: true, title: "Build Success: CubeRPC", unstable: false, result: "SUCCESS", thumbnail: "https://raw.githubusercontent.com/webpack/media/master/logo/icon-square-big.png")
             }
             failure {
                discordSend(webhookURL: "https://discordapp.com/api/webhooks/681503219821576243/QiyLS2wo2jjptgas_Er9NNf1ob01fRXXFn9ZRSggQy6tJo2grCtlMMZZMMyAD5zczlPN", description: "**Build:** ${env.BUILD_NUMBER}\n**Status:** Failure\n\n**Changes:**\n${env.BUILD_URL}", footer: "CubeMC Build System", link: "${env.BUILD_URL}", successful: true, title: "Build Failed: CubeRPC", unstable: false, result: "FAILURE", thumbnail: "https://raw.githubusercontent.com/webpack/media/master/logo/icon-square-big.png")
             }
         }
     }
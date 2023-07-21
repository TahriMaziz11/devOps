pipeline {
    agent any

    environment{
        SONARSERVER = 'sonarserver'
        SONARSCANNER = 'sonarscanner'
    }

    stages {
        stage("Java Version Check") {
            steps {
                sh 'java -version'
            }
        }
        stage('Pulling from git...') {
            steps {
                git branch: 'main',
                        url: 'https://github.com/TahriMaziz11/devOps'
            }
        }
        
        stage("Build") {
            steps {
                sh 'mvn install -DskipTests=true'
            }
        }
        stage('Deploy') {
            steps {
                        sh 'mvn deploy -DskipTests'
                  }
            }
        // stage('Deploy') {
        //     steps {
        //         sh 'docker-compose up -d'
        //     }
        // }
        
        stage('Build backend docker image') {
            steps {
                sh 'docker build -t spring .'
                sh 'docker images'
            }
        }

        stage('Push images to Dockerhub') {
            steps {
                    script {
                        sh "docker login -u yupii -p svdwi_Still_in_Brown_E"
                        sh "docker tag spring yupii/spring"
                        sh 'docker push yupii/spring'
                    }
            }
        }
        stage('Nexus Deploy ') {
           steps {
                nexusArtifactUploader(
                nexusVersion: 'nexus3',
                protocol: 'http',
                nexusUrl: '193.95.105.45:8081',
                groupId: 'tn.esprit.rh',
                version: '1.0.0',
                repository: 'Achat-release',
                credentialsId: 'nexusid',
                artifacts: [
                    [  artifactId: 'achat',
                        classifier: '',
                        file: 'target/achat.jar',
                        type: 'jar']
                ]
                )
                        }
                    }


         stage('SonarQube analysis') {
            steps {
                        sh 'mvn  sonar:sonar -Dsonar.login=admin -Dsonar.password=admin123 -Dsonar.projectKey=Devops'
                  }
            }
        stage('Running the unit test...') {
            steps {
                sh 'mvn test'
            }
        }
 
        stage("Email notification sender ...") {
            steps {
                emailext attachLog: true, body: "${env.BUILD_URL} has result ${currentBuild.result}", compressLog: true, subject: "Status of pipeline: ${currentBuild.fullDisplayName}", to: 'saadaoui.mohamedaziz@esprit.tn,mohamedaziz.tahri@esprit.tn,houssem.slimani@esprit.tn'
            }
        }
    }
}

pipeline {
    agent any

    environment {
        IMAGE_NAME = "devops-app"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Java App') {
            steps {
                sh 'cd app && mvn clean package'
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                    docker login -u $DOCKER_USER -p $DOCKER_PASS
                    docker build -t $DOCKER_USER/$IMAGE_NAME:$BUILD_NUMBER .
                    docker push ceaser08/devops-app:$BUILD_NUMBER
                    '''
                }
            }
        }

        stage('Deploy to EKS') {
  steps {
    sh '''
    export DOCKER_USER=$DOCKER_USER
    export IMAGE_TAG=$BUILD_NUMBER

    envsubst < k8s/deployment.yml | kubectl apply -f -
    kubectl rollout status deployment/devops-app
    '''
  }
}

    }
}


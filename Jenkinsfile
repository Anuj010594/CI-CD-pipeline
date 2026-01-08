pipeline {
    agent any

    environment {
        // Non-secret values (safe to keep here)
        IMAGE_NAME  = "devops-app"
        DOCKER_USER = "ceaser08"
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build Java Application') {
            steps {
                sh '''
                cd app
                mvn clean package
                '''
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-creds',
                    usernameVariable: 'DOCKER_USER_TMP',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                    echo "Building image: $DOCKER_USER/$IMAGE_NAME:$BUILD_NUMBER"

                    docker login -u $DOCKER_USER_TMP -p $DOCKER_PASS

                    docker build -t $DOCKER_USER/$IMAGE_NAME:$BUILD_NUMBER .
                    docker push $DOCKER_USER/$IMAGE_NAME:$BUILD_NUMBER
                    '''
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh '''
                echo "Deploying image:"
                echo "$DOCKER_USER/$IMAGE_NAME:$BUILD_NUMBER"

                export IMAGE_TAG=$BUILD_NUMBER

                # Render Kubernetes manifest safely
                envsubst < k8s/deployment.yml > /tmp/deployment.rendered.yml

                echo "Rendered deployment manifest:"
                cat /tmp/deployment.rendered.yml

                # Apply and wait for rollout
                kubectl apply -f /tmp/deployment.rendered.yml
                kubectl apply -f k8s/service.yml
                kubectl rollout status deployment/devops-app
                '''
            }
        }
    }
}


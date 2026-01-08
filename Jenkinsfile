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
        echo "Deploying image: $DOCKER_USER/$IMAGE_NAME:$BUILD_NUMBER"

        export IMAGE_TAG=$BUILD_NUMBER

        # Render deployment
        envsubst < k8s/deployment.yml > /tmp/deployment.rendered.yml

        # Apply Kubernetes manifests
        kubectl apply -f k8s/cluster-issuer.yml
        kubectl apply -f k8s/service.yml
        kubectl apply -f /tmp/deployment.rendered.yml
        kubectl apply -f k8s/ingress.yml

        kubectl rollout status deployment/devops-app
        '''
    }
}





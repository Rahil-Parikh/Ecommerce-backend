pipeline {
    agent {
        none
    }

    stages {
        stage('Generate springboot Jar ') {
            agent {
                label 'local-machine'
            }
            steps {
              
                dir('/home/zillions/Hub/Work/ecommerce'){
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        stage('Build Image') {
            agent {
                label 'local-machine'
            }
            steps {
                dir('/home/zillions/Hub/Work/ecommerce'){
                    sh 'docker build -t deploy-springboot:latest .'
                }
            }
        }
        stage('Push Image') {
            agent {
                label 'local-machine'
            }
            steps {
                sh 'docker tag deploy-springboot:latest zillions.jfrog.io/default-docker-virtual/springboot:latest'
                sh 'docker push zillions.jfrog.io/default-docker-virtual/springboot:latest'
            }
        }
        stage('Pull Git') {
            agent {
              label 'ec2-ssh-server-node'
            }
            steps {
                git branch: 'master',credentialsId: 'git-credentials' , url: 'git@github.com:Rahil-Parikh/server-config.git'
            }
        }
        stage('docker-compose down') {
            agent {
              label 'ec2-ssh-server-node'
            }
            steps {
                sh 'sudo docker-compose down'
            }
        }
        stage('Pull Docker registry') {
            agent {
              label 'ec2-ssh-server-node'
            }
            steps {
                sh 'sudo docker pull zillions.jfrog.io/default-docker-virtual/postgres:latest'
                sh 'sudo docker pull zillions.jfrog.io/default-docker-virtual/springboot:latest'
            }
        }
        stage('docker-compose up') {
            steps {
                sh 'sudo docker-compose up -d'
            }
        }
    }
}

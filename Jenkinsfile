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
        stage('Refreshing SSL keystore'){
            agent {
              label 'ec2-ssh-server-node'
            }
            steps{
                sh 'sudo rm /opt/server/keystore.p12'
                sh 'sudo openssl pkcs12 -export -in /etc/letsencrypt/live/app.trepechyjewels.com/fullchain.pem -inkey /etc/letsencrypt/live/app.trepechyjewels.com/privkey.pem -out /opt/server/keystore.p12 -passout pass:jewels -name tomcat'
                sh 'sudo chmod +r /opt/server/keystore.p12'
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
            agent {
              label 'ec2-ssh-server-node'
            }
            steps {
                sh 'sudo docker-compose up -d'
            }
        }
    }
}

pipeline {

    agent any

    options {
        timestamps()
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out Amazon BDD project'
                checkout scm
            }
        }

        stage('Check Environment') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
                bat 'git --version'
            }
        }

        stage('Build') {
            steps {
                echo 'Compiling the Maven project'
                bat 'mvn -B -DskipTests compile'
            }
        }

        stage('Execute BDD Tests') {
            steps {
                echo 'Executing Amazon public BDD scenarios'
                bat 'mvn -B test -Dcucumber.filter.tags="@public"'
            }
        }
    }

    post {

        always {

            echo 'Publishing test results'

            junit(
                testResults: 'target/surefire-reports/*.xml',
                allowEmptyResults: true
            )

            archiveArtifacts(
                artifacts: 'target/cucumber-reports/**, target/screenshots/**',
                allowEmptyArchive: true
            )
        }

        success {
            echo 'Jenkins CI pipeline completed successfully!'
        }

        failure {
            echo 'Jenkins CI pipeline failed. Check Console Output.'
        }

        unstable {
            echo 'Jenkins pipeline completed with unstable results.'
        }
    }
}

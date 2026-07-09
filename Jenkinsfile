// Pipeline alternativo — Jenkins (declarative).
//
// Implementación equivalente al .gitlab-ci.yml para entornos Jenkins. Corre el agente sobre
// una imagen Maven+JDK 21 y reutiliza la cache local de Maven del host. Igual que el pipeline de
// GitLab, no se ejecuta automáticamente sobre el commit en GitHub (donde corre GitHub Actions):
// evita duplicar la misma batería de pruebas sobre el mismo commit.
pipeline {
  agent {
    docker {
      image 'maven:3.9-eclipse-temurin-21'
      args '-v $HOME/.m2:/root/.m2'
    }
  }

  options {
    timestamps()
    timeout(time: 20, unit: 'MINUTES')
  }

  stages {
    stage('Test (unit + BDD)') {
      steps {
        sh 'mvn -B verify'
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
          archiveArtifacts artifacts: 'target/cucumber/**', allowEmptyArchive: true
        }
      }
    }

    stage('Package') {
      steps {
        sh 'mvn -B -DskipTests package'
      }
      post {
        success {
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
      }
    }
  }
}

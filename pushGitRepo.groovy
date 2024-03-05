import java.text.SimpleDateFormat
library 'shared'
def choiceArray = []

pipeline {
    agent any
    options {
        skipDefaultCheckout()
    }

    
    stages {


        //     stage('show available date') {
        //   steps {
        //     sh '''
        //     echo $JENKINS_HOME
        //     echo "2023\r\n2024" > $JENKINS_HOME/workspace/tag/test/lista.txt
        //     echo "Show available date"
        //     cat $JENKINS_HOME/workspace/tag/test/lista.txt
        //     '''
        //   }
        // }
        // stage('Restore') {
        //     steps {
        //         script {
        //           def folders = sh(returnStdout: true, script: "cat $JENKINS_HOME/workspace/tag/test/lista.txt")    
        //           //load the array using the file content (lista.txt)
        //             folders.split().each {
        //                 choiceArray << it
        //             }                  
        //             // wait for user input 
        //           def INPUT_DATE = input message: 'Please select date', ok: 'Next',
        //           //generate the list using the array content
        //           parameters: [ choice(name: 'CHOICES', choices: choiceArray, description: 'Please Select One') ]
        //         }
        //     }
        // }

        // stage('List tags') {
        //     steps {
        //         script {
        //             int x = 1;
  
        //             //lastTag = sh script: """git tag --sort=-version:refname | head -1 | grep -oE '[0-9]+\044'""".trim(), returnStdout: true
        //             //withCredentials([usernamePassword(credentialsId: 'github-app', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
        //                 dir ('savetag'){    
        //                     cloneToLocation("https://github.com/girafrica/release-tags", 'github-app')
        //                     //sh (' git pull https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/girafrica/release-tags ')
        //                     lastTag = sh script: """ls -t | head -1 | grep -oE '[0-9]+\044'""".trim(), returnStdout: true
        //                     sh (' ls -l ')
        //                 }
        //             //}
        //             lt = lastTag.trim()  // the .trim() is necessary
        //             echo "lastTag: " + lt
        //             int lt = lt.toInteger()

        //             newtag = "${env.BUILD_ID}"

        //             echo newtag.toString()
        //         }
        //     }
        // }

        stage('Create tag') {
            // when {
            //     expression { change != '' }
            // }
            steps {
                script {
                    echo 'Setting git safe.directory'
                    sh "git config --global --add safe.directory '*'"
                    sh "git config --global user.name 'noreply@3d.com'"
                    sh "git config --global user.email 'noreply@d.com'"
                    currentDateTime = sh script: """date +"%Y.%V" """.trim(), returnStdout: true
                    version = currentDateTime.trim()  // the .trim() is necessary
                    cloneToLocation('https://github.com/girafrica/release-management.git', 'github-app', 'main', 'release')

                    createTag(version)
                }
            }
        }

        stage('Save tag') {
            // when {
            //     expression { change != '' }
            // }
            steps {
                script {
                    echo 'Setting git safe.directory'
                    sh "git config --global --add safe.directory '*'"
                    sh "git config --global user.name 'noreply@3d.com'"
                    sh "git config --global user.email 'noreply@d.com'"
                    setVersionTag()    
                    saveTag(version)

                    // withCredentials([usernamePassword(credentialsId: 'github-app', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    //     // dir ('foo'){
                    //     //     sh (' ls -l ')
                    //     //     //sh (' git config --global pull.rebase false ')
                    //     //     sh (' git fetch https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/girafrica/release-tags ')
                    //     //     //sh (' git pull https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/girafrica/release-tags main --allow-unrelated-histories')
                    //     //     def readContent = "${version}.sbt"
                    //     //     writeFile file: "${version}.sbt", text: readContent+"\r\nversion := 1.0.${env.BUILD_ID}"
                    //     //     sh (" git add -A")
                    //     //     sh (' git commit -am "Updated version number"')
                    //     //     sh (' git pull https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/girafrica/release-tags main --allow-unrelated-histories')
                    //     //     sh (' git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/girafrica/release-tags HEAD:main')
                    //     // }

                    //     cloneToLocation("https://github.com/girafrica/release-tags", 'github-app')
                    //     //def readContent = "${version}.${newtag}"
                    //     // def newtag = "${env.BUILD_ID}"
                    //     // tag = newtag.toString()

                    //     if (!fileExists('releases')) {
                    //         writeFile file: 'releases', text: "Releases:"
                    //     }

                    //     def readContent = readFile 'releases'

                    //     writeFile file: 'releases', text: readContent+"\r\n${version}.${env.BUILD_ID}"
                    //     sh (" git add -A")
                    //     sh (" git commit -am 'Updated version number to ${version}.${env.BUILD_ID}'")
                    //     sh (' git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/girafrica/release-tags HEAD:main')
                    // }
                }
            }
        }

        stage('Check changes') {
            steps {
                script {
                    dir ("changes"){
                        sh (' ls -l ')
                        cloneToLocation('https://github.com/girafrica/test1.git', 'github-app', 'main', '.')
                        //change = sh(returnStdout: true, script: 'git log $(git describe --tags --abbrev=0)..HEAD --oneline').trim()
                        //change = sh(returnStdout: true, script: "git diff --name-only HEAD ${version}.${env.BUILD_ID}").trim()

                        last_tag = sh(returnStdout: true, script: "git describe --abbrev=0 --tags").trim()
                        prev_tag = sh(returnStdout: true, script: "git tag --sort=-creatordate | grep -A 1 ${last_tag} | tail -n 1").trim()
                        change = sh(returnStdout: true, script: "git diff ${prev_tag} ${last_tag} -- VERSION").trim()

                        sh (' ls -l ')
                        println "Change: ${change}"
                        deleteDir()
                    }
                }
            }
        }

        // stage ('Invoke_pipeline') {
        //     steps {
        //         script {
        //         // Trigger another pipeline and check result of this
        //         ret = build(job: '../tag-2-test/get-version', propagate: true, wait: false)
        //         echo ret.result
        //         currentBuild.result = ret.result

        //         ret2 = build(job: '../tag-2-test/get-version', propagate: true, wait: true)
        //         echo ret2.result
        //         currentBuild.result = ret2.result
        //         }
        //     }
        // }
        stage('testing') {
            steps {
                script{
                    // def branches = [:]

                    // for (int i = 0; i < 4; i++) {
                    // def index = i //if we tried to use i below, it would equal 4 in each job execution.
                    // branches["branch${i}"] = {
                    //     build job: '../tag-2-test/get-version', parameters: [
                    //     string(name: 'param1', value:'test_param'),
                    //     string(name:'dummy', value: "${index}")]
                    // }
                    // }
                    // parallel branches
                    sh("ls -a")
                    buildImages()
                }
            }
        }
    }
}
        def sonarQubeUrl="http://sonarqube:9000"
        def githubOrganization="sarahBuisson"
        def githubRepository="pitest-xebicon-demo"


void setBuildStatus(String url, String context, String message, String state, String backref){
  step([
    $class: "GitHubCommitStatusSetter",
    reposSource: [$class: "ManuallyEnteredRepositorySource", url: url ],
    contextSource: [$class: "ManuallyEnteredCommitContextSource", context: context ],
    errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
    statusBackrefSource: [ $class: "ManuallyEnteredBackrefSource", backref: backref ],
    statusResultSource: [ $class: "ConditionalStatusResultSource", results: [
        [$class: "AnyBuildResult", message: message, state: state]] ]
  ]);
}

String getRepoURL() {
  sh "git config --get remote.origin.url"
  sh "git config --get remote.origin.url > originurl"
  def originurl = readFile("originurl").trim()
  return originurl
}



def getItem(branchName) {
 Jenkins.instance.getItemByFullName("sonar-openedge/${branchName}")
}


def getTitle(json) {
   def slurper = new groovy.json.JsonSlurper()
   def jsonObject = slurper.parseText(json.content)
   jsonObject.title
}

def isUp(url){
 def r = sh script: 'wget -q '+url+' -O /dev/null', returnStatus: true

 echo "up $r"
      return (r==0)
}




void sendCommentToPullRequest(String repoGithub, String prId, String messageContent){

     def SHA1 ="SHA1"
     script {
        SHA1 = sh(returnStdout: true, script: "git rev-parse HEAD").trim()
     }

     def message = """{"body": "$messageContent", "commit_id": "$SHA1", "path": "/", "position": 0}"""
     httpRequest authentication: 'sbuisson-git', httpMode: 'POST', requestBody: "${message}",  url: "https://api.github.com/repos/$repoGithub/issues/${env.CHANGE_ID}/comments"
}

void getBranch(String repoGithub, String authentification, String pullId){
 def response = httpRequest authentication: authentification, httpMode: 'GET',  url: "https://api.github.com/repos/$repoGithub/pulls/$pullId"


 return new groovy.json.JsonSlurper().parseText(response.content).head.ref;
}

def getFromPom(pom, balise) {
    def matcher = readFile(pom) =~ "<$balise>(.+)</$balise>"
    matcher ? matcher[0][1] : null
}

node {
 stage('info') {
       sh "git branch"
 //      sh "git remote"
  //     sh "git log"
   //    sh "git show-ref"


    }
    stage('build') {
       echo "build"
        checkout scm
        sh "git branch"
        sh "mvn clean install -B"
    }
    stage('metrics') {
        sh "git show-refs"

        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sbuisson-sonar', usernameVariable: 'SONAR_LOGIN', passwordVariable: 'SONAR_PASSWORD']]) {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sbuisson-ci', usernameVariable: 'CI_LOGIN', passwordVariable: 'CI_PASSWORD']]) {

        def databaseSonarParam = " -Dsonar.jdbc.username=${env.CI_LOGIN} -Dsonar.jdbc.password=${env.CI_PASSWORD} -Dsonar.jdbc.url=jdbc:postgresql://postgres:5432/ci "
        def sonarParam = " -Dsonar.host.url=$sonarQubeUrl -Dsonar.login=${env.SONAR_LOGIN} -Dsonar.password=${env.SONAR_LOGIN} "
        //TODO : use credential for password sonar
        def jenkinsJobUrl="http://localhost:8080/job/$githubOrganization/job/$githubRepository/view/change-requests/job/${env.BRANCH_NAME}"
        http://localhost:8080/job/sarahbuisson/job/jenkinsCraft/view/change-requests/job/PR-18/HTML_site/pit-reports/index.html

        def githubProject="sarahBuisson/pitest-xebicon-demo"
        def groupId="com.github.sarahbuisson"
        def artifactId="pitest-xebicon-demo"

        echo "for the branch  ${env.BRANCH_NAME}"
        if ("master" == env.BRANCH_NAME) {
            if (isUp(sonarQubeUrl)){

                echo("sonar master")
                sh "mvn sonar:sonar -Dsonar.analysis.mode=issues $sonarParam $databaseSonarParam  -B "

            }
            sh "mvn pitest:mutationCoverage -Pquality -B"
            publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/pit-reports', reportFiles: '*', reportName: 'pitest site', reportTitles: 'pitest'])

            try{
                sh "mvn universal-module-aggregator:aggregate -Pquality -B -U"
            }catch ( e){
                echo e.toString();
            }
            //build site ( for documentation)
            sh "mvn site -Pquality -U site:stage"
            publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/staging', reportFiles: '*', reportName: 'HTML site', reportTitles: 'site'])

        }
        else if(!env.BRANCH_NAME.startsWith("PR-")){

            if(isUp(sonarQubeUrl)){

                echo("sonar branch ${env.BRANCH_NAME}")
                sh "mvn sonar:sonar -Dsonar.analysis.mode=issues $sonarParam $databaseSonarParam  -B "

            }
            sh "mvn pitest:mutationCoverage -Pquality -B"
            publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/pit-reports', reportFiles: '*', reportName: 'pitest site', reportTitles: 'pitest'])

            try{
                sh "mvn universal-module-aggregator:aggregate -Pquality -B -U"
            }catch ( e){
                echo e.toString();
            }
            //build site ( for documentation)
            sh "mvn site -Pquality -U site:stage"
            publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/staging', reportFiles: '*', reportName: 'HTML site', reportTitles: 'site'])


        } else {
            echo "for a PR"

            def githubUrl = "${env.CHANGE_URL}"

            //sh "git remote add originPR https://github.com/$githubOrganization/$githubRepository.git"

            def resume = "Build Infos : <br/>"


            resume+="<a href='${jenkinsJobUrl}/${env.BUILD_NUMBER}/console'>logs</a><br/>"

            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sbuisson-git', usernameVariable: 'GH_LOGIN', passwordVariable: 'GH_PASSWORD']]) {
                withCredentials([[$class: 'StringBinding', credentialsId: ' git-token', variable: 'OATH']]) {
                    def githubSonarParam="-Dsonar.github.pullRequest=${env.CHANGE_ID}\
                                                        -Dsonar.github.repository=$githubProject \
                                                        -Dsonar.github.login=${env.GH_LOGIN} \
                                                        -Dsonar.github.oauth=${env.GH_PASSWORD}  \
                                                        -Dsonar.verbose=true "


                    def branchName = getBranch(githubOrganization+"/"+githubRepository, 'sbuisson-git', env.CHANGE_ID);


                    //sonar
                    if(isUp(sonarQubeUrl)){
                        sh "mvn sonar:sonar -Dsonar.analysis.mode=preview -Dsonar.issuesReport.html.enable=true -Dsonar.issuesReport.json.enable=true $sonarParam $databaseSonarParam $githubSonarParam -B"

                        echo "metrics sonar"

                        publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/issues-report/', reportFiles: '*', reportName: 'sonar site', reportTitles: 'sonar'])
                        resume+="rapport sonar : and <a href='${jenkinsJobUrl}//sonar_site/issues-report.html'>Pull-request</a> et <a href='http://localhost:9000/dashboard?id=$groupId%3A$artifactId'>master</a>(pour comparaison)  <br/>"
                    }



                    // pitest
                      echo "metrics pitest"
                    sh "mvn pitest:mutationCoverage -Ppull-request -DoriginReference=refs/remotes/origin/${env.BRANCH_NAME} -DdestinationReference=refs/remotes/origin/master -B -X"
                    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/pit-reports', reportFiles: '*', reportName: 'pitest site', reportTitles: 'pitest'])
                    resume+="rapport pitest : <a href='${jenkinsJobUrl}//HTML_site//pit-reports/index.html'>here</a> <br/>"

                    try{
                        sh "mvn universal-module-aggregator:aggregate -Pquality -B -U"
                    }catch ( e){
                        echo e
                    }

                    //build site ( for documentation)
                    sh "mvn site -Pquality -U site:stage"
                    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/staging', reportFiles: '*', reportName: 'HTML site', reportTitles: 'site'])
                    resume+="documentation : <a href='${jenkinsJobUrl}//HTML_site/project-info.html'>here</a> <br/>"




                    resume+="job : ${env.JOB_NAME}"

                    sendCommentToPullRequest(githubOrganization+"/"+githubRepository, env.CHANGE_ID, resume)
                }
            }
        }
        }}

    }

}


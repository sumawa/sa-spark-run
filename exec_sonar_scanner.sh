~/projs/scala/sbt/bin/sbt -Dsonar.host.url=http://localhost \
  clean coverage test coverageReport sonarScan
~/tools/sonar-scanner-4.5.0.2216-macosx/bin/sonar-scanner \
  -Dsonar.projectKey=sa-suite \
  -Dsonar.sources=./sa-spark-run \
  -Dsonar.host.url=http://localhost \
  -Dsonar.login=b5301886f4d9de74a00da3992ced0243716ab1b3

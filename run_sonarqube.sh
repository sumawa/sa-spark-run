~/projs/scala/sbt/bin/sbt -Dsonar.host.url=http://localhost \
  clean coverage test coverageReport sonarScan

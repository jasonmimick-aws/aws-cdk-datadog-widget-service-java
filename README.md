# aws-cdk-datadog-widget-service-java

A sample Typescript CDK project which deploys a Java 'widget-service' which
lists the contents of an AWS S3 Bucket and includes out-of-the-box Datadog
integration.


## Useful commands

* `npm run build`   compile typescript to js
* `npm run watch`   watch for changes and compile
* `npm run test`    perform the jest unit tests
* `cdk deploy`      deploy this stack to your default AWS account/region
* `cdk diff`        compare deployed stack with current state
* `cdk synth`       emits the synthesized CloudFormation template


NOTES----

We need this in the `resources/HelloWorldFunction/pom.xml`

https://jar-download.com/artifacts/org.slf4j/slf4j-simple/1.7.24/source-code/org/slf4j/impl/StaticLoggerBinder.java

# aws-cdk-datadog-widget-service-java

import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { WidgetService } from "./widget_service";

export class MyWidgetServiceStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // stackName from context
    // parameters - 
    /*
     * bucketName - default to stackName -"bucket"
     * datadog external id
     * role principal assumed by
     *
     */
    const widgetBucketName = new cdk.CfnParameter(this, "widgetBucketName", {
        type: "String",
        description: "The name of the Amazon S3 bucket the Widget will monitor. Defaults to <stack-name>-bucket",
        default: `${this.stackName}-bucket` 
    })
    const datadogApiKeySecretName = new cdk.CfnParameter(this, "datadogApiKeySecretName", {
        type: "String",
        description: "The name of the AWS Secrets Manager secret storing the Datadog apikey for the service.",
	default: `DatadogApiKey`
    })

    new WidgetService(this, "WidgetService",
        { bucketName : widgetBucketName.valueAsString,
          datadogApiKeySecretName : datadogApiKeySecretName.valueAsString });


  }
}

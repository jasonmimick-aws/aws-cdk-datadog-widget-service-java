import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { RestApi, LambdaIntegration } from "aws-cdk-lib/aws-apigateway";
import { Function, Runtime, Code } from "aws-cdk-lib/aws-lambda";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { Datadog } from "datadog-cdk-constructs-v2";
import { Secret }from 'aws-cdk-lib/aws-secretsmanager';

export interface WidgetServiceProps extends cdk.ResourceProps {
  bucketName: string;
  datadogApiKeySecretName: string;
}

export class WidgetService extends Construct {
    constructor(scope: Construct, id: string, props: WidgetServiceProps) {
    

	/*
	const secret = secretsmanager.Secret.fromSecretAttributes(this, 'ImportedSecret', {
		  secretArn: 'arn:aws:secretsmanager:<region>:<account-id-number>:secret:<secret-name>-<random-6-characters>',
		    // If the secret is encrypted using a KMS-hosted CMK, either import or reference that key:
		  //   encryptionKey,
		  //   });
		  //
        */
	super(scope, id);
        const secret = Secret.fromSecretNameV2(this, props.datadogApiKeySecretName, 'DatadogApiKey');

        const bucket = Bucket.fromBucketName(this, props.bucketName, props.bucketName)
        const handler = new Function(this, "WidgetHandler", {
            runtime: Runtime.JAVA_11, 
            code: Code.fromAsset("resources/HelloWorldFunction/target/HelloWorld-1.0.jar"),
            handler: "helloworld.App::handleRequest",
            environment: {
                BUCKET: bucket.bucketName
            }
        });

        bucket.grantReadWrite(handler);
        const api = new RestApi(this, "widgets-api", {
            restApiName: "Widget Service",
            description: "This service serves widgets."
        });

        const getWidgetsIntegration = new LambdaIntegration(handler, {
            requestTemplates: {
                "application/json": '{ "statusCode": "200" }'
            },
        });

        api.root.addMethod("POST", getWidgetsIntegration);


        const datadog = new Datadog(this, "Datadog", {
	        nodeLayerVersion: 56,
	        extensionLayerVersion: 9,
	        apiKey: secret.secretValue.unsafeUnwrap().toString()
	    });

        datadog.addLambdaFunctions([handler]);

    }
}

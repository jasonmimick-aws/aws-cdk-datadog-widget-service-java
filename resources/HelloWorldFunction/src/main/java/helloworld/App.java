package helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
/**
 * Handler for requests to Lambda function.
 */



public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static void log(LambdaLogger logger, String msg) {
        if (logger != null) {
            logger.log(msg);
        } else {
            System.out.println(msg);
        }
    }
    
    /*
    private S3Client s3Client = S3Client.builder()
        .region(Region.EU_WEST_1)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build();
    */

    private S3Client s3Client = S3Client.builder().build();
    //
    //.httpClient(UrlConnectionHttpClient.builder().build())
   public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");


        LambdaLogger logger = null;
        if ( context != null ) {
            logger = context.getLogger();
            log(logger, "Context was NOT null");
        }

        log(logger, "Hello CloudWatch!");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {

            
	        String bucketName = "foobar";

	        if ( input != null ) {
	    	    String requestString = input.getBody();
	    	    JSONParser parser = new JSONParser();
	    	    JSONObject requestJsonObject = (JSONObject) parser.parse(requestString);
	    	    if (requestJsonObject != null) {
			        if (requestJsonObject.get("bucket") != null) {
				        bucketName = requestJsonObject.get("bucket").toString();
			        } else {
		    		    return response.withStatusCode(404).withBody("Parameter 'bucket' required.");
			        }
	    	    }
	        }
            /*
            return response
                    .withStatusCode(200)
                    .withBody("bucketName="+bucketName);
            */
 
            log(logger,"bucketName="+bucketName);
            //ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
            //log(logger,"credentialsProvider="+credentialsProvider);
            Region region = Region.US_EAST_1;
            log(logger,"region="+region);
            
            //S3Client s3 = S3Client.builder().region(region).build();
            log(logger,"s3client="+s3Client);
            
	        //S3Client s3 = S3Client.builder().region(region).build();
	        List<String> objects = listBucketObjects(s3Client, bucketName, logger);
            objects.add("one");
            /*
            List<String> objects = new ArrayList<String>();
            objects.add(s3Client.toString());
            objects.add("one");
            objects.add("two");
            */
	        String objs = objects.toString();
	        System.out.println("bucket="+bucketName+"Your objects are:"+objs);
	        log(logger,"bucket="+bucketName+"Your objects are:"+objs);

            return response
                    .withStatusCode(200)
                    .withBody(objs);
            
        } catch (Exception e) {
	        System.out.println("ERROR =======> " + e);
            log(logger,"ERROR="+e);
            return response
                    .withBody(String.format("{ \"%s\" }", e))
                    .withStatusCode(500);
        }
    }


    public static List<String> listBucketObjects(S3Client s3, String bucketName, LambdaLogger logger ) {

        log(logger,"listBucketObjects - called");
        List<String> result = new ArrayList<String>();
        //result.add("one");
        //result.add("two");
        //return result;
        /*  */
        try {

            ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build();
            log(logger, "listObjects="+listObjects);
            ListObjectsResponse res = s3.listObjects(listObjects);
            log(logger, "res="+res);
            List<S3Object> objects = res.contents();
            log(logger, "objects="+objects);
            for (S3Object myValue : objects) {
                log(logger, "Object-->"+myValue.key());
                result.add( myValue.key() );
                //System.out.print("\n The name of the key is " + myValue.key());
                //System.out.print("\n The object is " + calKb(myValue.size()) + " KBs");
                //System.out.print("\n The owner is " + myValue.owner());
            }

        } catch (S3Exception e) {
            //System.err.println(e.awsErrorDetails().errorMessage());
            log(logger,"S3ERROR:"+e.awsErrorDetails().errorMessage());
            throw e;
            //System.exit(1);
        } catch(Exception e) {
            log(logger,"ERROR:"+e);


        }
        return result;
        /* */
    }

    //convert bytes to kbs.
    private static long calKb(Long val) {
        return val/1024;
    }


    /*
    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
    */
}

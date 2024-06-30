package com.myorg;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.util.Date;

public class ImportProductsFileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
//    private final String bucketName = System.getenv("BUCKET_NAME");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        System.out.println(request);
        String fileName = request.getPathParameters().get("filename");
        String key = "uploaded/" + fileName;
        System.out.println(fileName);


        // Generate a pre-signed URL valid for 15 minutes
        String existingBucketName = "rsschool-task-5";

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(existingBucketName, key)
                .withMethod(com.amazonaws.HttpMethod.PUT)
                .withExpiration(new Date(System.currentTimeMillis() + 900000));

        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody("{\"url\": \"" + url.toString() + "\"}");

        return response;
    }
}
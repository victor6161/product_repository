package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.apigateway.IResource;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.events.targets.ApiGateway;
import software.amazon.awscdk.services.lambda.AssetCode;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.s3.IBucket;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

public class ProductRepositoryStack extends Stack {
    private static final AssetCode LAMBDA_JAR = Code.fromAsset("lambda/target/lambda-0.1.jar");
    private static final AssetCode IMPORT_SERVICE_JAR = Code.fromAsset("import-service/target/import_service-0.1.jar");


    public ProductRepositoryStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ProductRepositoryStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Function getProductsListFunction = Function.Builder.create(this, "GetProductList")
                .functionName("GetProductList")
                .runtime(Runtime.JAVA_21)
                .code(LAMBDA_JAR)
                .handler("com.myorg.GetProductsListHandler")
                .build();

        // Define the GetProductDetailsHandler Lambda function
        Function getProductByIdFunction = Function.Builder.create(this, "GetProductListById")
                .functionName("GetProductById")
                .runtime(Runtime.JAVA_21)
                .code(LAMBDA_JAR)
                .handler("com.myorg.GetProductsById")
                .build();

        // Reference existing S3 bucket by name
        String existingBucketName = "rsschool-task-5";
        IBucket bucket = Bucket.fromBucketName(this, "ExistingBucket", existingBucketName);

        // Create Lambda function for loading files
        Function importProductsFileFunction = Function.Builder.create(this, "ImportProductsFile")
                .functionName("ImportProductsFile")
                .runtime(Runtime.JAVA_21)
                .code(IMPORT_SERVICE_JAR)
                .handler("com.myorg.ImportProductsFileHandler")
                .build();

        // Grant Lambda permissions to interact with the bucket
        bucket.grantReadWrite(importProductsFileFunction);

        ApiGateway api = ApiGateway.Builder.create(
                        RestApi.Builder
                                .create(this, "ProductServiceApi")
                                .description("This service serves products.")
                                .restApiName("Product Service")
                                .build())
                .build();

        IResource root = api.getIRestApi().getRoot();

        IResource products = root.addResource("products");
        // get all products
        products.addMethod("GET",
                LambdaIntegration.Builder
                        .create(getProductsListFunction)
                        .build()
        );

        // get product by id
        products.addResource("{productId}")
                .addMethod("GET",
                        LambdaIntegration.Builder
                                .create(getProductByIdFunction)
                                .build()
                );

        CfnOutput.Builder.create(this, "URL").value(root.getPath() + "products").build();


        ApiGateway apiS3 = ApiGateway.Builder.create(
                        RestApi.Builder
                                .create(this, "ProductServiceApiS3")
                                .description("This service serves getting files from s3.")
                                .restApiName("Product Service S3")
                                .build())
                .build();

        IResource rootS3 = apiS3.getIRestApi().getRoot();

        IResource productsS3 = rootS3.addResource("import");
        // get file by id
        productsS3.addResource("{filename}")
                .addMethod("GET",
                        LambdaIntegration.Builder
                                .create(importProductsFileFunction)
                                .build()
                );
    }
}

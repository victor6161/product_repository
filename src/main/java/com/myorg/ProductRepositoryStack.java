package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Code;
import software.constructs.Construct;

public class ProductRepositoryStack extends Stack {
    public ProductRepositoryStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ProductRepositoryStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Define the Lambda function
//        Function getProductsListFunction = Function.Builder.create(this, "GetProductsListHandler")
//                .runtime(Runtime.JAVA_21)
//                .code(Code.fromAsset("target/classes")) // load jar
//                .handler("com.myorg.GetProductsListHandler::handleRequest") // try to use without packages
//                .build();

        Function getProductsListFunction = Function.Builder.create(this, "GetProductsListHandler")
                .runtime(Runtime.NODEJS_20_X)
                .code(Code.fromAsset("lambda"))
                .handler("GetProductList.handler") // try to use without packages
                .build();

        // Define the API Gateway REST API
        LambdaRestApi api = LambdaRestApi.Builder.create(this, "ProductsApi")
                .handler(getProductsListFunction)
                .proxy(false)
                .build();

        // Define the /products endpoint
        api.getRoot()
                .addResource("products").addMethod("GET");
    }
}

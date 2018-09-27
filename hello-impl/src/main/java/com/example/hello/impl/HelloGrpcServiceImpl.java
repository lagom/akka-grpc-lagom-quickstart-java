package com.example.hello.impl;

import akka.stream.Materializer;
import example.myapp.helloworld.grpc.AbstractGreeterServiceRouter;
import example.myapp.helloworld.grpc.HelloReply;
import example.myapp.helloworld.grpc.HelloRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class HelloGrpcServiceImpl extends AbstractGreeterServiceRouter {

    @Inject
    public HelloGrpcServiceImpl(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<HelloReply> sayHello(HelloRequest in) {
        HelloReply reply = HelloReply
            .newBuilder()
            .setMessage("Hi " + in.getName() + " (gRPC)")
            .build();
        return CompletableFuture.completedFuture(reply);
    }
}

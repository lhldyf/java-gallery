package io.vertx.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class App extends AbstractVerticle {

    private static final String SERVER = "vertx-web";
    private String date;
    private static int sleep = 10;
    private static int sleepCount = 1;

    public static void main(String[] args) {
        VertxOptions vo = new VertxOptions();
        // vo.setEventLoopPoolSize(16);
        vo.setWorkerPoolSize(16);
        if (args.length > 0) {
            vo.setWorkerPoolSize(Integer.valueOf(args[0]));
        }
        System.out.println("work pool size: " + vo.getWorkerPoolSize());

        DeploymentOptions options = new DeploymentOptions();
        options.setInstances(16);
        if (args.length > 1) {
            options.setInstances(Integer.valueOf(args[1]));
        }
        System.out.println("instances: " + options.getInstances());
        Vertx.vertx(vo).deployVerticle(App.class.getName(), options, ar -> {
            System.out.println(ar.result());
        });

        if (args.length > 2) {
            sleep = Integer.valueOf(args[2]);
        }
        System.out.println("sleep: " + sleep);

        if (args.length > 3) {
            sleepCount = Integer.valueOf(args[3]);
        }
        System.out.println("sleepCount:" + sleepCount);
    }

    @Override
    public void start() {
        final Router app = Router.router(vertx);
        // initialize the date header
        date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());
        // refresh the value as a periodic task
        vertx.setPeriodic(1000, handler -> date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));


        /*
         * This test exercises the framework fundamentals including keep-alive support, request routing, request header
         * parsing, object instantiation, JSON serialization, response header generation, and request count throughput.
         */
        app.get("/json").handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.SERVER, SERVER).putHeader(HttpHeaders.DATE, date)
               .putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(Json.encodeToBuffer("Hello, World!"));
        });

        app.route().handler(BodyHandler.create());
        app.post("/post").handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.SERVER, SERVER).putHeader(HttpHeaders.DATE, date)
               .putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(ctx.getBodyAsString());
        });

        app.post("/sleep").handler(ctx -> {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ctx.response().putHeader(HttpHeaders.SERVER, SERVER).putHeader(HttpHeaders.DATE, date)
               .putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(ctx.getBodyAsString());
        });

        app.post("/blockNothing").blockingHandler(RoutingContext::next).handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.SERVER, SERVER).putHeader(HttpHeaders.DATE, date)
               .putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(ctx.getBodyAsString());
        });

        app.post("/justBlock").blockingHandler(ctx -> {
            sleep();
            ctx.response().putHeader(HttpHeaders.SERVER, SERVER).putHeader(HttpHeaders.DATE, date)
               .putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(ctx.getBodyAsString());
        });

        Route route = app.post("/blockSleep");
        for (int i = 0; i < sleepCount; i++) {
            route.blockingHandler(ctx -> {
                sleep();
                ctx.next();
            });
        }
        route.handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.SERVER, SERVER).putHeader(HttpHeaders.DATE, date)
               .putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(ctx.getBodyAsString());
        });


        /*
         * This test is an exercise of the request-routing fundamentals only, designed to demonstrate the capacity of
         * high-performance platforms in particular. Requests will be sent using HTTP pipelining. The response
         * payload is
         * still small, meaning good performance is still necessary in order to saturate the gigabit Ethernet of the
         * test
         * environment.
         */
        app.get("/plaintext").handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.SERVER, SERVER).putHeader(HttpHeaders.DATE, date)
               .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain").end("Hello, World!");
        });

        vertx.createHttpServer().requestHandler(app).listen(8891);
    }

    void sleep() {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

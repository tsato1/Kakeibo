//package com.kakeibo;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
///**
// * Global executor pools for the whole application.
// *
// * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
// * webservice requests).
// */
//public class AppExecutors {
//    public Executor diskIO = Executors.newSingleThreadExecutor();
//    public Executor networkIO = Executors.newFixedThreadPool(3);
//}
//

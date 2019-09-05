package com.frankiegao123.concurrent.exercise;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleThreadPoolExecutor extends ThreadPoolExecutor {

    public SimpleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler );
    }

    public SimpleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
            RejectedExecutionHandler handler) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler );
    }

    public SimpleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory );
    }

    public SimpleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue );
    }

    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new SimpleThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
    }

    public static ExecutorService newCachedThreadPool() {
        return new SimpleThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        SimpleFuture<T> future = new SimpleFuture<T>( task );
        execute( future );
        return future;
    }
}

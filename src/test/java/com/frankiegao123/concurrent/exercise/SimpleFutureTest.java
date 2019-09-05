package com.frankiegao123.concurrent.exercise;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SimpleFutureTest {

    public static void main(String[] args) throws InterruptedException {

        final int N = 10;

        ExecutorService executor = SimpleThreadPoolExecutor.newFixedThreadPool( N + 1 );

        final Future<String> future = executor.submit( new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep( 3000 );
                return "test";
            }
        });

        final CountDownLatch latch = new CountDownLatch( N );

        final FutureTask task = new FutureTask( future , latch);

        for ( int i = 0 ; i < N ; i++ ) {
            executor.execute( task );
        }

        latch.await();

        executor.shutdown();
    }

    private static void log(String message) {
        System.out.printf( "%tF %<tT,%<tL [%s] %s%n",
                System.currentTimeMillis(),
                Thread.currentThread().getName(),
                message );
    }

    private static final class FutureTask implements Runnable {
        private final Future<String> future;
        private final CountDownLatch latch;
        public FutureTask(Future<String> future, CountDownLatch latch) {
            this.future = future;
            this.latch = latch;
        }
        @Override
        public void run() {
            try {
                log( "Wait..." );
                String value = future.get();
                log( value );
            }
            catch (Exception e) {
                log( "Exception" );
            }
            finally {
                latch.countDown();
            }
        }
    }
}

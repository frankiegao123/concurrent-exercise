package com.frankiegao123.concurrent.exercise;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

public class SimpleFuture<V> implements RunnableFuture<V> {

    private static final class Waiter {
        private Thread thread;
        public Waiter() {
            this.thread = Thread.currentThread();
        }
    }

    private static final int NEW = 0;
    private static final int CANCELLED = 1;
    private static final int DONE = 3;

    private V value;
    private volatile int state = NEW;
    private ConcurrentLinkedQueue<Waiter> waiters = new ConcurrentLinkedQueue<>();

    private Callable<V> callable;

    public SimpleFuture(Callable<V> callable) {
        this.callable = callable;
    }

    @Override
    public void run() {
        try {
            value = callable.call();
            state = DONE;
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            finish();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {

        if ( state > CANCELLED ) {
            return false;
        }

        try {
            state = CANCELLED;
        }
        finally {
            finish();
        }

        return true;
    }

    @Override
    public boolean isCancelled() {
        return state == CANCELLED;
    }

    @Override
    public boolean isDone() {
        return state == DONE;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        await( false, 0 );
        return value;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        await( true, unit.toNanos(timeout) );

        if ( state != DONE ) {
            throw new TimeoutException();
        }

        return value;
    }

    int waiterSize() {
        return waiters.size();
    }

    private void await(boolean timed, long nanos) {

        if ( state > NEW ) {
            return;
        }

        Waiter waiter = new Waiter();

        waiters.add( waiter );

        if ( timed ) {
            LockSupport.parkNanos( this, nanos );
            waiters.remove( waiter );
        }
        else {
            LockSupport.park( this );
        }
    }

    private void finish() {
        for ( Waiter waiter = null ; (waiter = waiters.poll()) != null ; ) {
            if ( waiter.thread != null ) {
                LockSupport.unpark( waiter.thread );
            }
        }
    }
}

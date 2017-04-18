/*
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.concurrent;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AbstractScheduledEventExecutorTest {

    private static final TestScheduledEventExecutor EXECUTOR = new TestScheduledEventExecutor();

    @Test
    public void testScheduleRunnableZero() {
        TestTask task = new TestTask();
        ScheduledFuture<?> future = EXECUTOR.schedule(task, 0 , TimeUnit.DAYS);
        future.syncUninterruptibly();
        Assert.assertTrue(task.executed.get());
    }

    @Test
    public void testScheduleRunnableNegative() {
        TestTask task = new TestTask();
        ScheduledFuture<?> future = EXECUTOR.schedule(task, -1, TimeUnit.DAYS);
        future.syncUninterruptibly();
        Assert.assertTrue(task.executed.get());
    }

    @Test
    public void testScheduleCallableZero() {
        TestTask task = new TestTask();
        ScheduledFuture<?> future = EXECUTOR.schedule(Executors.callable(task), 0 , TimeUnit.DAYS);
        future.syncUninterruptibly();
        Assert.assertTrue(task.executed.get());
    }

    @Test
    public void testScheduleCallableNegative() {
        TestTask task = new TestTask();
        ScheduledFuture<?> future = EXECUTOR.schedule(Executors.callable(task), -1 , TimeUnit.DAYS);
        future.syncUninterruptibly();
        Assert.assertTrue(task.executed.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleAtFixedRateRunnableZero() {
        EXECUTOR.scheduleAtFixedRate(new TestTask(), 0, 0, TimeUnit.DAYS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleAtFixedRateRunnableNegative() {
        EXECUTOR.scheduleAtFixedRate(new TestTask(), 0, -1, TimeUnit.DAYS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleWithFixedDelayZero() {
        EXECUTOR.scheduleWithFixedDelay(new TestTask(), 0, -1, TimeUnit.DAYS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleWithFixedDelayNegative() {
        EXECUTOR.scheduleWithFixedDelay(new TestTask(), 0, -1, TimeUnit.DAYS);
    }

    private static final class TestScheduledEventExecutor extends AbstractScheduledEventExecutor {
        @Override
        public boolean isShuttingDown() {
            return false;
        }

        @Override
        public boolean inEventLoop(Thread thread) {
            return true;
        }

        @Override
        public void shutdown() {
            // NOOP
        }

        @Override
        public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Future<?> terminationFuture() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) {
            return false;
        }

        @Override
        public void execute(Runnable command) {
            safeExecute(command);
        }
    }

    private static final class TestTask implements Runnable {

        final AtomicBoolean executed = new AtomicBoolean();

        @Override
        public void run() {
            executed.set(true);
        }
    }
}

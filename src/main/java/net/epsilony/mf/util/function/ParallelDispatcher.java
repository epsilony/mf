/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.epsilony.mf.util.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Spliterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ParallelDispatcher<T> {
    private final ArrayList<Consumer<? super T>> consumers = new ArrayList<>();
    private final ArrayList<Future<?>>           futures   = new ArrayList<>();
    private Spliterator<? extends T>             spliterator;
    private ExecutorService                      threadPool;

    public ParallelDispatcher(Spliterator<? extends T> spliterator, Collection<? extends Consumer<? super T>> consumers) {
        this.spliterator = spliterator;
        registerAll(consumers);
    }

    public void setSpliterator(Spliterator<? extends T> spliterator) {
        this.spliterator = spliterator;
    }

    public boolean register(Consumer<? super T> e) {
        return consumers.add(e);
    }

    public void clear() {
        consumers.clear();
    }

    public boolean registerAll(Collection<? extends Consumer<? super T>> c) {
        return consumers.addAll(c);
    }

    public void run() {
        futures.clear();
        threadPool = Executors.newFixedThreadPool(consumers.size());
        for (int i = 0; i < consumers.size(); i++) {
            futures.add(threadPool.submit(newTask(i)));
        }
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            try {
                threadPool.awaitTermination(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new IllegalStateException(e);
            }
        }

    }

    synchronized private boolean tryAdvance(Consumer<? super T> action) {
        return spliterator.tryAdvance(action);
    }

    synchronized public long estimateSize() {
        return spliterator.estimateSize();
    }

    private Runnable newTask(int i) {
        return new Runnable() {
            Consumer<? super T> consumer = consumers.get(i);

            @Override
            public void run() {
                boolean tryAdvance;
                do {
                    tryAdvance = tryAdvance(consumer);
                } while (tryAdvance);
            }
        };
    }
}

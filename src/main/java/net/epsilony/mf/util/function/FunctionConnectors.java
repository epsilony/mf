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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 *
 */
public class FunctionConnectors {
    public static <A, B, C> Function<A, Stream<C>> oneStreamOneStream(
            Function<? super A, ? extends Stream<? extends B>> upper,
            Function<? super B, ? extends Stream<? extends C>> lower) {
        return new OneStreamOneStream<A, B, C>(upper, lower);
    }

    public static <A, B> Function<Stream<? extends A>, Stream<B>> streamedOneOne(
            Function<? super A, ? extends B> function) {
        return new StreamedOneOne<A, B>(function);
    }

    public static <A, B> Function<Stream<? extends A>, Stream<B>> streamedOneStream(
            Function<? super A, ? extends Stream<? extends B>> oneStream) {
        return new StreamedOneStream<A, B>(oneStream);
    }

    public static <A, B, C> Function<A, Stream<C>> oneStreamOneOne(
            Function<? super A, ? extends Stream<? extends B>> oneStream, Function<? super B, ? extends C> oneOne) {
        return new OneStreamOneOne<A, B, C>(oneStream, oneOne);
    }

    public static <A, B> Consumer<A> oneOneConsumer(Function<? super A, ? extends B> oneOne,
            Consumer<? super B> consumer) {
        return new OneOneConsumer<>(oneOne, consumer);
    }

    public static <A, B> Consumer<A> oneStreamConsumer(Function<? super A, ? extends Stream<? extends B>> oneStream,
            Consumer<? super B> consumer) {
        Consumer<Stream<? extends B>> streamedConsumer = streamedConsumer(consumer);
        return oneOneConsumer(oneStream, streamedConsumer);
    }

    public static <A> Consumer<Stream<? extends A>> streamedConsumer(Consumer<? super A> consumer) {
        return new StreamedConsumer<>(consumer);
    }

    public static <A> Consumer<A> fakeConsumer(final Runnable runnable) {
        return new Consumer<A>() {

            @Override
            public void accept(A t) {
                runnable.run();
            }
        };
    }

    private static class OneStreamOneStream<A, B, C> implements Function<A, Stream<C>> {
        private final Function<? super A, ? extends Stream<? extends B>> upper;
        private final Function<? super B, ? extends Stream<? extends C>> lower;

        @Override
        public Stream<C> apply(A t) {
            return upper.apply(t).flatMap(lower);
        }

        private OneStreamOneStream(Function<? super A, ? extends Stream<? extends B>> upper,
                Function<? super B, ? extends Stream<? extends C>> lower) {
            super();
            this.upper = upper;
            this.lower = lower;
        }
    }

    private static class StreamedOneOne<T, R> implements Function<Stream<? extends T>, Stream<R>> {

        private final Function<? super T, ? extends R> oneOne;

        @Override
        public Stream<R> apply(Stream<? extends T> t) {
            return t.map(oneOne);
        }

        private StreamedOneOne(Function<? super T, ? extends R> oneOne) {
            this.oneOne = oneOne;
        }
    }

    private static class StreamedOneStream<T, R> implements Function<Stream<? extends T>, Stream<R>> {
        private final Function<? super T, ? extends Stream<? extends R>> oneStream;

        @Override
        public Stream<R> apply(Stream<? extends T> t) {
            return t.flatMap(oneStream);
        }

        private StreamedOneStream(Function<? super T, ? extends Stream<? extends R>> oneStream) {
            super();
            this.oneStream = oneStream;
        }
    }

    private static class OneStreamOneOne<A, B, C> implements Function<A, Stream<C>> {
        private final Function<? super A, ? extends Stream<? extends B>> oneMany;
        private final Function<? super B, ? extends C>                   oneOne;

        @Override
        public Stream<C> apply(A t) {
            return oneMany.apply(t).map(oneOne);
        }

        private OneStreamOneOne(Function<? super A, ? extends Stream<? extends B>> oneMany,
                Function<? super B, ? extends C> oneOne) {
            super();
            this.oneMany = oneMany;
            this.oneOne = oneOne;
        }
    }

    private static class OneOneConsumer<A, B> implements Consumer<A> {
        private final Function<? super A, ? extends B> oneOne;
        private final Consumer<? super B>              consumer;

        @Override
        public void accept(A t) {
            consumer.accept(oneOne.apply(t));
        }

        private OneOneConsumer(Function<? super A, ? extends B> oneOne, Consumer<? super B> consumer) {
            super();
            this.oneOne = oneOne;
            this.consumer = consumer;
        }

    }

    private static class StreamedConsumer<A> implements Consumer<Stream<? extends A>> {
        private final Consumer<? super A> consumer;

        @Override
        public void accept(Stream<? extends A> t) {
            t.forEach(consumer);
        }

        private StreamedConsumer(Consumer<? super A> consumer) {
            super();
            this.consumer = consumer;
        }

    }
}

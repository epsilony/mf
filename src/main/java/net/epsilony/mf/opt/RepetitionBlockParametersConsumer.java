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
package net.epsilony.mf.opt;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class RepetitionBlockParametersConsumer implements Consumer<double[]> {
    private double[] lastParameters;
    private Consumer<double[]> innerConsumer;

    public void reset() {
        lastParameters = null;
    }

    public RepetitionBlockParametersConsumer(Consumer<double[]> innerConsumer) {
        this.innerConsumer = innerConsumer;
    }

    public RepetitionBlockParametersConsumer() {
    }

    public Consumer<double[]> getInnerConsumer() {
        return innerConsumer;
    }

    public void setInnerConsumer(Consumer<double[]> innerConsumer) {
        this.innerConsumer = innerConsumer;
    }

    @Override
    public void accept(double[] parameters) {
        Objects.requireNonNull(parameters);

        if (null != lastParameters && Arrays.equals(lastParameters, parameters)) {
            return;
        }

        if (lastParameters == null || lastParameters.length != parameters.length) {
            lastParameters = parameters.clone();
        } else {
            System.arraycopy(parameters, 0, lastParameters, 0, parameters.length);
        }

        innerConsumer.accept(parameters);
    }
}

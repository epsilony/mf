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
package net.epsilony.mf.model.sample.config;

import java.util.function.Function;

import net.epsilony.mf.util.math.ArrayPartialValueTuple;
import net.epsilony.mf.util.math.ArrayPartialValueTuple.SingleArray;
import net.epsilony.mf.util.math.PartialValueTuple;
import net.epsilony.mf.util.math.V1S2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PoissonLinearSampleConfig extends SampleConfigBase {
    double a = 1, b = 2, c = 0;

    private final SingleArray result = new ArrayPartialValueTuple.SingleArray(1, 2, 2);

    @Override
    @Bean
    public Function<double[], PartialValueTuple> field() {
        return xy -> {
            double x = xy[0];
            double y = xy[1];
            result.fill(0);
            result.setByIndexAndPartial(0, V1S2.U, a * x + b * y + c);
            result.setByIndexAndPartial(0, V1S2.U_x, a);
            result.setByIndexAndPartial(0, V1S2.U_y, b);
            return result;
        };
    }
}
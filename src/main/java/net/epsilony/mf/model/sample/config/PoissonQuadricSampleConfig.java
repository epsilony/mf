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

import net.epsilony.mf.util.math.ArrayPartialTuple;
import net.epsilony.mf.util.math.ArrayPartialTuple.SingleArray;
import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.mf.util.math.Pds2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PoissonQuadricSampleConfig extends PoissonSampleConfigBase {

    private final double a = 0.8, b = 1.2, c = 0.6, d = 0.1, e = 0.3, f = 0;
    private final SingleArray result = new ArrayPartialTuple.SingleArray(1, 2, 2);

    @Override
    @Bean
    public Function<double[], PartialTuple> field() {
        return xy -> {
            double x = xy[0];
            double y = xy[1];
            result.fill(0);
            result.set(0, Pds2.U, a * x * x + b * x * y + c * y * y + d * x + e * y + f);
            result.set(0, Pds2.U_x, 2 * a * x + b * y + d);
            result.set(0, Pds2.U_y, b * x + 2 * c * y + e);
            result.set(0, Pds2.U_xx, 2 * a);
            result.set(0, Pds2.U_xy, b);
            result.set(0, Pds2.U_yy, 2 * c);
            return result;
        };
    }
}
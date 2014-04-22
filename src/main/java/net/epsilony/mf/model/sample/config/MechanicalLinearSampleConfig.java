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
import net.epsilony.mf.util.math.convention.Pds2;
import net.epsilony.mf.util.math.PartialTuple;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class MechanicalLinearSampleConfig extends MechanicalSampleConfigBase {

    public static final double u0 = 0, ux = 0.1, uy = 0.3;
    public static final double v0 = 0, vx = 0.2, vy = 0.4;

    @Override
    @Bean
    public Function<double[], PartialTuple> field() {
        final SingleArray fieldResult = new ArrayPartialTuple.SingleArray(2, 2, 2);
        return xy -> {
            double x = xy[0];
            double y = xy[1];

            fieldResult.fill(0);

            double u = u0 + ux * x + uy * y;
            double v = v0 + vx * x + vy * y;
            fieldResult.set(0, Pds2.U, u);
            fieldResult.set(1, Pds2.U, v);
            fieldResult.set(0, Pds2.U_x, ux);
            fieldResult.set(1, Pds2.U_x, vx);
            fieldResult.set(0, Pds2.U_y, uy);
            fieldResult.set(1, Pds2.U_y, vy);

            return fieldResult;
        };
    }

}

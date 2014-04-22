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
public class MechanicalQuadricSampleConfig extends MechanicalSampleConfigBase {
    public static final double u0 = 0, ux = 0.12, uy = 0.14, uxx = 0.16, uxy = 0.18, uyy = 0.20;
    public static final double v0 = 0, vx = 0.11, vy = 0.13, vxx = 0.15, vxy = 0.10, vyy = 0.21;

    @Override
    @Bean
    public Function<double[], PartialTuple> field() {
        final SingleArray result = new ArrayPartialTuple.SingleArray(2, 2, 2);
        return xy -> {
            double x = xy[0];
            double y = xy[1];
            result.set(0, 0, u0 + x * ux + y * uy + x * x * uxx + x * y * uxy + y * y * uyy);
            result.set(1, 0, v0 + x * vx + y * vy + x * x * vxx + x * y * vxy + y * y * vyy);
            result.set(0, Pds2.U_x, ux + 2 * x * uxx + y * uxy);
            result.set(1, Pds2.U_x, vx + 2 * x * vxx + y * vxy);
            result.set(0, Pds2.U_y, uy + x * uxy + 2 * y * uyy);
            result.set(1, Pds2.U_y, vy + x * vxy + 2 * y * vyy);
            result.set(0, Pds2.U_xx, 2 * uxx);
            result.set(1, Pds2.U_xx, 2 * vxx);
            result.set(0, Pds2.U_xy, uxy);
            result.set(1, Pds2.U_xy, vxy);
            result.set(0, Pds2.U_yy, 2 * uyy);
            result.set(1, Pds2.U_yy, 2 * vyy);
            return result;
        };
    }
}

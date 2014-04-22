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
package net.epsilony.mf.util.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class GridSnapperTest {

    @Test
    public void testFiveSeven() {
        GridSnapper snapper = new GridSnapper(new double[] { 5, 7 });

        double[] samples = { -6, -6.00001, -5.99999, -12, -2.5, 20.5 };
        double[] expRnds = { -5, -7, -5, -10, 0, 20 };
        double[] expSups = { -5, -5, -5, -10, 0, 21 };
        double[] expInfs = { -7, -7, -7, -14, -5, 20 };
        for (int i = 0; i < samples.length; i++) {
            double s = samples[i];
            double eRnd = expRnds[i];
            double eSup = expSups[i];
            double eInf = expInfs[i];
            assertEquals(eRnd, snapper.nearest(s), 0);
            assertEquals(eSup, snapper.sup(s), 0);
            assertEquals(eInf, snapper.inf(s), 0);
        }
    }
}

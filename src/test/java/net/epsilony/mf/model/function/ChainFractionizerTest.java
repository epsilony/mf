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
package net.epsilony.mf.model.function;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.function.ChainFractionizer.ChainFractionResult;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.SimpMFLine;
import net.epsilony.mf.model.geom.util.MFLine2DUtils;
import net.epsilony.mf.model.geom.util.MFLineChainFactory;
import net.epsilony.tb.analysis.Math2D;

import org.junit.Test;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ChainFractionizerTest {

    @Test
    public void testOpenChain() {
        double[][] vertes = new double[][] { { 1, 1 }, { 10, 1 }, { 10, 7 } };

        MFLine openChainHead = genOpenChain(vertes);
        ChainFractionizer sampleChainFractionizer = sampleChainFractionizer(3.1);

        double[][] expVertes = new double[][] { { 1, 1 }, { 4, 1 }, { 7, 1 }, { 10, 1 }, { 10, 4 }, { 10, 7 } };

        ChainFractionResult fractionResult = sampleChainFractionizer.apply(openChainHead);
        int i = 0;
        for (MFLine l : fractionResult.getHead()) {
            assertArrayEquals(expVertes[i], l.getStartCoord(), 1e-12);
            i++;
            MFLine oriLine = fractionResult.getNewToOri().get(l);
            if (oriLine.getSucc() == null) {
                assertArrayEquals(oriLine.getStartCoord(), l.getStartCoord(), 1e-12);
                assertTrue(l.getSucc() == null);
            } else {
                double ps = Math2D.projectionParameter(oriLine.getStartCoord(), oriLine.getEndCoord(),
                        l.getStartCoord());
                double pe = Math2D.projectionParameter(oriLine.getStartCoord(), oriLine.getEndCoord(), l.getEndCoord());
                assertTrue(ps <= 1 && ps >= 0);
                assertTrue(pe <= 1 && ps >= 0);
            }
        }

        try {
            sampleChainFractionizer.apply(openChainHead.getSucc());
            fail("not a real head of open chain but passed");
        } catch (RuntimeException e) {

        }

    }

    public MFLine genOpenChain(double[][] coords) {
        return new MFLineChainFactory(SimpMFLine::new, MFNode::new, false).produce(Arrays.asList(coords));
    }

    @Test
    public void testClosedChain() {
        double[][] vertes = new double[][] { { 1, 1 }, { 10, 1 }, { 10, 7 }, { 1, 7 } };
        ChainFractionizer sampleChainFractionizer = sampleChainFractionizer(3.1);
        double[][] expVertes = new double[][] {
                { 1, 1 },
                { 4, 1 },
                { 7, 1 },
                { 10, 1 },
                { 10, 4 },
                { 10, 7 },
                { 7, 7 },
                { 4, 7 },
                { 1, 7 },
                { 1, 4 },
                { 1, 1 } };
        MFLine chainHead = genClosedChain(vertes);
        ChainFractionResult fractionResult = sampleChainFractionizer.apply(chainHead);
        int i = 0;
        for (MFLine l : fractionResult.getHead()) {
            assertArrayEquals(expVertes[i], l.getStartCoord(), 1e-12);
            i++;
            MFLine oriLine = fractionResult.getNewToOri().get(l);

            double ps = Math2D.projectionParameter(oriLine.getStartCoord(), oriLine.getEndCoord(), l.getStartCoord());
            double pe = Math2D.projectionParameter(oriLine.getStartCoord(), oriLine.getEndCoord(), l.getEndCoord());
            assertTrue(ps <= 1 && ps >= 0);
            assertTrue(pe <= 1 && ps >= 0);
        }

        assertTrue(i > 0);
    }

    public static ChainFractionizer sampleChainFractionizer(double sup) {
        SingleLineFractionizer singleLineFractionizer = new SingleLineFractionizer.ByAverageNeighbourCoordsDistanceSup(
                sup);
        ChainFractionizer chainFractionizer = new ChainFractionizer(singleLineFractionizer, MFNode::new,
                SimpMFLine::new);
        return chainFractionizer;
    }

    public MFLine genClosedChain(double[][] coords) {
        MFLine result = genOpenChain(coords);
        MFLine tail = null;
        for (MFLine l : result) {
            tail = l;
        }
        MFLine2DUtils.link(tail, result);
        return result;
    }

}

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
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.function.ChainFractionizer.ChainFractionResult;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.solid.SegmentIterable;

import org.junit.Test;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ChainFractionizerTest {

    @Test
    public void testOpenChain() {
        double[][] vertes = new double[][] { { 1, 1 }, { 10, 1 }, { 10, 7 } };

        Line openChainHead = genOpenChain(vertes);
        ChainFractionizer sampleChainFractionizer = sampleChainFractionizer(3.1);

        double[][] expVertes = new double[][] { { 1, 1 }, { 4, 1 }, { 7, 1 }, { 10, 1 }, { 10, 4 }, { 10, 7 } };

        ChainFractionResult fractionResult = sampleChainFractionizer.apply(openChainHead);
        int i = 0;
        for (Line l : new SegmentIterable<>(fractionResult.getHead())) {
            assertArrayEquals(expVertes[i], l.getStartCoord(), 1e-12);
            i++;
            Line oriLine = fractionResult.getNewToOri().get(l);
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
            sampleChainFractionizer.apply((Line) openChainHead.getSucc());
            fail("not a real head of open chain but passed");
        } catch (RuntimeException e) {

        }

    }

    public Line genOpenChain(double[][] coords) {
        Line headPred = new Line();
        Line tail = headPred;
        for (double[] crd : coords) {
            Line newLine = new Line(new Node(crd));
            Segment2DUtils.link(tail, newLine);
            tail = newLine;
        }
        Line head = (Line) headPred.getSucc();
        head.setPred(null);
        return head;
    }

    @Test
    public void testClosedChain() {
        double[][] vertes = new double[][] { { 1, 1 }, { 10, 1 }, { 10, 7 }, { 1, 7 } };
        ChainFractionizer sampleChainFractionizer = sampleChainFractionizer(3.1);
        double[][] expVertes = new double[][] { { 1, 1 }, { 4, 1 }, { 7, 1 }, { 10, 1 }, { 10, 4 }, { 10, 7 },
                { 7, 7 }, { 4, 7 }, { 1, 7 }, { 1, 4 }, { 1, 1 } };
        Line chainHead = genClosedChain(vertes);
        ChainFractionResult fractionResult = sampleChainFractionizer.apply(chainHead);
        int i = 0;
        for (Line l : new SegmentIterable<>(fractionResult.getHead())) {
            assertArrayEquals(expVertes[i], l.getStartCoord(), 1e-12);
            i++;
            Line oriLine = fractionResult.getNewToOri().get(l);

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
        ChainFractionizer chainFractionizer = new ChainFractionizer(singleLineFractionizer, MFNode::new);
        return chainFractionizer;
    }

    public Line genClosedChain(double[][] coords) {
        Line result = genOpenChain(coords);
        Line tail = null;
        for (Line l : new SegmentIterable<>(result)) {
            tail = l;
        }
        Segment2DUtils.link(tail, result);
        return result;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.mf.process.integrate;

import java.util.Arrays;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author epsilon
 */
public class LineIntegratePointsFactoryTest {

    public LineIntegratePointsFactoryTest() {
    }

    @Test
    public void testWeightByLength() {
        Chain chain = Chain.byNodesChain(Arrays.asList(new Node(new double[]{-2, 0}), new Node(new double[]{8, 0})), false);
        Line line = (Line) chain.getHead();
        line.fractionize(10, Node.factory());

        LineIntegratePointsFactory lineIntFac = new LineIntegratePointsFactory();
        lineIntFac.setStartLine(line);
        lineIntFac.setStartParameter(0.2);
        lineIntFac.setEndLine((Line) chain.getLast().getPred());
        lineIntFac.setEndParameter(0.6);

        double exp = 9.4;

        double length = 0;
        for (MFIntegratePoint pt : lineIntFac.produce()) {
            length += pt.getWeight();
        }

        assertEquals(exp, length, 1e-12);
    }
}
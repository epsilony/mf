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

package net.epsilony.mf.process.integrate;

import java.util.Arrays;
import java.util.List;
import net.epsilony.mf.model.ChainModelFactory;
import net.epsilony.mf.model.ChainPhM;
import net.epsilony.mf.model.load.AbstractSegmentLoad;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Node;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ChainIntegrateTaskFactoryTest {

    public ChainIntegrateTaskFactoryTest() {
    }

    @Test
    public void testVolume() {
        double start = -1.1;
        double end = 2.3;
        double upper = 0.76;
        int degree = 2;
        final UnivariateFunction volumeFunciton = new UnivariateFunction() {
            @Override
            public double value(double x) {
                return 2 * x + 3;
            }
        };
        double expArea = 14.28;
        ChainIntegrateTaskFactory task = new ChainIntegrateTaskFactory();
        ChainPhM chainPhM = new ChainPhM();

        chainPhM.setChain(Chain.byNodesChain(
                Arrays.asList(new Node(new double[] { start, 0 }), new Node(new double[] { end, 0 })), false));
        chainPhM.setVolumeLoad(new AbstractSegmentLoad() {

            @Override
            public double[] getValue() {
                segment.setDiffOrder(0);
                double[] coord = segment.values(parameter, null);
                return new double[] { volumeFunciton.value(coord[0]) };
            }
        });
        ChainModelFactory chainModelFactory = new ChainModelFactory();
        chainModelFactory.setChainPhM(chainPhM);
        chainModelFactory.setFractionLengthCap(upper);
        task.setQuadratureDegree(degree);
        task.setChainAnalysisModel(chainModelFactory.produce());
        double area = 0;
        List<MFIntegrateUnit> volumeUnits = task.produce().get(MFProcessType.VOLUME);
        for (MFIntegrateUnit unit : volumeUnits) {
            MFIntegratePoint pt = (MFIntegratePoint) unit;
            area += pt.getWeight() * pt.getLoad()[0];
        }
        assertEquals(expArea, area, 1e-14);
    }
}

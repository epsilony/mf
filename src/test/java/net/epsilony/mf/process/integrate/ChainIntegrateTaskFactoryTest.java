/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.Arrays;
import java.util.List;
import net.epsilony.mf.model.ChainModelFactory;
import net.epsilony.mf.model.ChainPhM;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;
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

        chainPhM.setChain(Chain.byNodesChain(Arrays.asList(new Node(new double[]{start, 0}), new Node(new double[]{end, 0})), false));
        chainPhM.setVolumeLoad(new SegmentLoad() {
            Segment segment;
            double parameter;

            @Override
            public boolean isDirichlet() {
                return false;
            }

            @Override
            public void setSegment(Segment seg) {
                segment = seg;
            }

            @Override
            public void setParameter(double parm) {
                parameter = parm;
            }

            @Override
            public double[] getLoad() {
                segment.setDiffOrder(0);
                double[] coord = segment.values(parameter, null);
                return new double[]{volumeFunciton.value(coord[0])};
            }

            @Override
            public boolean[] getLoadValidity() {
                return null;
            }

            @Override
            public boolean isSynchronizedClonable() {
                return false;
            }

            @Override
            public MFLoad synchronizedClone() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

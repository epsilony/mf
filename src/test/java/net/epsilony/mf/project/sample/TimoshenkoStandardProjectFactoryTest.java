/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import net.epsilony.mf.project.MFMechanicalProject;
import net.epsilony.mf.project.quadrature_task.MFQuadraturePoint;
import net.epsilony.mf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.tb.quadrature.GaussLegendre;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TimoshenkoStandardProjectFactoryTest {

    public TimoshenkoStandardProjectFactoryTest() {
    }

    @Test
    public void testAreaLength() {
        double w = 10, h = 6;
        TimoshenkoAnalyticalBeam2D timoBeam = new TimoshenkoAnalyticalBeam2D(w, h, 1000, 0.4, 20);
        double segLen = 1;
        double quadDomainSize = 1;
        double expArea = w * h;
        double expLen = h;
        boolean getHere = false;
        for (int degree = 1; degree <= GaussLegendre.MAXPOINTS * 2 - 1; degree++) {
            TimoshenkoBeamProjectFactory timoFactory = new TimoshenkoBeamProjectFactory();
            timoFactory.setTimoBeam(timoBeam);
            timoFactory.setSegmentLengthUpperBound(segLen);
            timoFactory.setQuadrangleDomainSize(quadDomainSize);
            timoFactory.setQuadrangleDegree(degree);
            MFMechanicalProject mfproject = timoFactory.produce();
            double actArea = 0;
            for (MFQuadraturePoint p : mfproject.getMFQuadratureTask().volumeTasks()) {
                actArea += p.weight;
            }
            assertEquals(expArea, actArea, 1e-10);
            double neumannLen = 0;
            for (MFQuadraturePoint p : timoFactory.rectangleTask.neumannTasks()) {
                neumannLen += p.weight;
            }
            assertEquals(expLen, neumannLen, 1e-10);
            double diriLen = 0;
            for (MFQuadraturePoint p : timoFactory.rectangleTask.dirichletTasks()) {
                diriLen += p.weight;
            }
            assertEquals(expLen, diriLen, 1e-10);
            getHere = true;
        }
        assertTrue(getHere);
    }
}
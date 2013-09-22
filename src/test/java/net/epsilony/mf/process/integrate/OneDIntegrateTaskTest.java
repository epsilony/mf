/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.mf.process.integrate;

import java.util.List;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author epsilon
 */
public class OneDIntegrateTaskTest {

    public OneDIntegrateTaskTest() {
    }

    @Test
    public void testVolume() {
        double start = -1.1;
        double end = 2.3;
        double upper = 0.76;
        int degree = 2;
        UnivariateFunction volumeFunciton = new UnivariateFunction() {
            @Override
            public double value(double x) {
                return 2 * x + 3;
            }
        };
        double expArea = 14.28;
        OneDIntegrateTask task = new OneDIntegrateTask();
        task.setStart(start);
        task.setEnd(end);
        task.setDegree(degree);
        task.setIntegrateDomainUpperBound(upper);
        task.setVolumeFunction(volumeFunciton);
        List<MFIntegratePoint> volumeTasks = task.volumeTasks();
        double area = 0;
        for (MFIntegratePoint pt : volumeTasks) {
            area += pt.getWeight() * pt.getLoad()[0];
        }
        assertEquals(expArea, area, 1e-14);
    }
}
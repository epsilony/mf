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

package net.epsilony.mf.project.sample;

import java.util.List;
import java.util.Map;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.unit.MFIntegratePoint;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.project.MFProject;
import net.epsilony.mf.project.MFProjectKey;
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
            timoFactory.setNodesDistance(quadDomainSize);
            timoFactory.setQuadratureDegree(degree);
            MFProject mfproject = timoFactory.produce();
            double actArea = 0;
            Map<MFProcessType, List<MFIntegrateUnit>> integrateUnitsGroup = (Map<MFProcessType, List<MFIntegrateUnit>>) mfproject.get(MFProjectKey.INTEGRATE_UNITS_GROUP);
            final List<MFIntegrateUnit> volumeTasks = integrateUnitsGroup.get(MFProcessType.VOLUME);
            for (MFIntegrateUnit unit : volumeTasks) {
                MFIntegratePoint pt = (MFIntegratePoint) unit;
                actArea += pt.getWeight();
            }
            assertEquals(expArea, actArea, 1e-10);
            double neumannLen = 0;
            Map<MFProcessType, List<MFIntegrateUnit>> timoUnitsGroup = (Map<MFProcessType, List<MFIntegrateUnit>>) timoFactory.produce().get(MFProjectKey.INTEGRATE_UNITS_GROUP);
            final List<MFIntegrateUnit> neumannTasks = timoUnitsGroup.get(MFProcessType.NEUMANN);
            for (MFIntegrateUnit unit : neumannTasks) {
                MFIntegratePoint p = (MFIntegratePoint) unit;
                neumannLen += p.getWeight();
            }
            assertEquals(expLen, neumannLen, 1e-10);
            double diriLen = 0;
            final List<MFIntegrateUnit> dirichletTasks = timoUnitsGroup.get(MFProcessType.DIRICHLET);
            for (MFIntegrateUnit unit : dirichletTasks) {
                MFIntegratePoint p = (MFIntegratePoint) unit;
                diriLen += p.getWeight();
            }
            assertEquals(expLen, diriLen, 1e-10);
            getHere = true;
        }
        assertTrue(getHere);
    }
}

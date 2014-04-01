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

package net.epsilony.mf.model;

import java.util.List;

import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawAnalysisModel extends RawPhysicalModel implements AnalysisModel {

    protected List<MFNode> spaceNodes;
    protected PhysicalModel origin;
    protected IntegrateUnitsGroup integrateUnitsGroup;

    public PhysicalModel getOrigin() {
        return origin;
    }

    public void setOrigin(PhysicalModel origin) {
        this.origin = origin;
    }

    @Override
    public List<MFNode> getSpaceNodes() {
        return spaceNodes;
    }

    public void setSpaceNodes(List<MFNode> spaceNodes) {
        this.spaceNodes = spaceNodes;
    }

    @Override
    public IntegrateUnitsGroup getIntegrateUnitsGroup() {
        return integrateUnitsGroup;
    }

    public void setIntegrateUnitsGroup(IntegrateUnitsGroup integrateUnitsGroup) {
        this.integrateUnitsGroup = integrateUnitsGroup;
    }
}

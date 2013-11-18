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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawAnalysisModel extends RawPhysicalModel implements AnalysisModel {

    protected List<MFNode> spaceNodes;
    protected PhysicalModel origin;
    protected Map<MFProcessType, List<? extends MFIntegrateUnit>> integrateUnitsGroup = new EnumMap<>(
            MFProcessType.class);

    @Override
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

    public List<? extends MFIntegrateUnit> getIntegrateUnits(MFProcessType key) {
        return integrateUnitsGroup.get(key);
    }

    public void setIntegrateUnits(MFProcessType key, List<? extends MFIntegrateUnit> integrateUnits) {
        integrateUnitsGroup.put(key, integrateUnits);
    }

    @Override
    public Map<MFProcessType, List<? extends MFIntegrateUnit>> getIntegrateUnitsGroup() {
        return integrateUnitsGroup;
    }

    public void setIntegrateUnitsGroup(Map<MFProcessType, List<? extends MFIntegrateUnit>> integrateUnitsGroup) {
        this.integrateUnitsGroup = integrateUnitsGroup;
    }
}

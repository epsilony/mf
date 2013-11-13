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
package net.epsilony.mf.process.indexer;

import java.util.List;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.GeomUnit;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public interface NodesAssembleIndexer {

    public abstract void setSpaceNodes(List<? extends MFNode> spaceNodes);

    public abstract void setGeomRoot(GeomUnit geomRoot);

    public abstract void index();

    public abstract List<MFNode> getSpaceNodes();

    public abstract List<MFNode> getBoundaryNodes();

    public abstract List<MFNode> getAllNodes();

}
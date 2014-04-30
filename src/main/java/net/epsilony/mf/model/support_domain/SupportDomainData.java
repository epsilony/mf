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
package net.epsilony.mf.model.support_domain;

import java.util.List;
import java.util.Map;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.geom.MFLine;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public interface SupportDomainData {
    List<MFNode> getAllNodesContainer();

    List<MFNode> getVisibleNodesContainer();

    List<MFLine> getSegmentsContainer();

    Map<MFNode, MFLine> getInvisibleBlockingMap();

}

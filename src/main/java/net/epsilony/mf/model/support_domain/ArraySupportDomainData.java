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

import java.util.ArrayList;
import java.util.HashMap;

import net.epsilony.mf.model.MFNode;
import net.epsilony.tb.solid.Segment;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ArraySupportDomainData implements SupportDomainData {
    private final ArrayList<MFNode> allNodesContainer = new ArrayList<>();
    private final ArrayList<MFNode> visibleNodesContainer = new ArrayList<>();
    private final ArrayList<Segment> segmentsContainer = new ArrayList<>();
    private final HashMap<MFNode, Segment> invisibleBlockingMap = new HashMap<>();

    @Override
    public ArrayList<MFNode> getAllNodesContainer() {
        return allNodesContainer;
    }

    @Override
    public ArrayList<MFNode> getVisibleNodesContainer() {
        return visibleNodesContainer;
    }

    @Override
    public ArrayList<Segment> getSegmentsContainer() {
        return segmentsContainer;
    }

    @Override
    public HashMap<MFNode, Segment> getInvisibleBlockingMap() {
        return invisibleBlockingMap;
    }

}

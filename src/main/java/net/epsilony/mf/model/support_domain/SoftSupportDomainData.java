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

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.util.ArrayListCache;
import net.epsilony.tb.solid.Segment;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class SoftSupportDomainData implements SupportDomainData {
    ArrayListCache<MFNode> allNodesContainerCache = new ArrayListCache<>();
    ArrayListCache<MFNode> visibleNodesContainerCache = new ArrayListCache<>();
    ArrayListCache<Segment> segmentsContainerCache = new ArrayListCache<>();
    SoftReference<Map<MFNode, Segment>> invisibleBlockingMapReference;

    // public static final boolean DEFAULT_ENABLE_ALL_NODES_CONTAINER = true;
    // public static final boolean DEFAULT_ENABLE_VISIBLE_NODES_CONTAINER =
    // true;
    // public static final boolean DEFAULT_ENABLE_SEGMENTS_CONTAINER = true;
    public static final boolean DEFAULT_ENABLE_INVISIBLE_BLOCKING_MAP = false;

    // boolean allNodesContainerEnable = DEFAULT_ENABLE_ALL_NODES_CONTAINER;
    // boolean visibleNodesContainerEnable =
    // DEFAULT_ENABLE_VISIBLE_NODES_CONTAINER;
    // boolean segmentsContainerEnable = DEFAULT_ENABLE_SEGMENTS_CONTAINER;
    boolean invisibleBlockingMapEnable = DEFAULT_ENABLE_INVISIBLE_BLOCKING_MAP;

    @Override
    public List<MFNode> getAllNodesContainer() {
        return allNodesContainerCache.get();
    }

    @Override
    public List<MFNode> getVisibleNodesContainer() {
        return visibleNodesContainerCache.get();
    }

    @Override
    public List<Segment> getSegmentsContainer() {
        return segmentsContainerCache.get();
    }

    @Override
    public Map<MFNode, Segment> getInvisibleBlockingMap() {
        if (!invisibleBlockingMapEnable) {
            return null;
        }
        Map<MFNode, Segment> map;
        if (null == invisibleBlockingMapReference) {
            map = null;
        } else {
            map = invisibleBlockingMapReference.get();
        }
        if (null == map) {
            HashMap<MFNode, Segment> newMap = new HashMap<>();
            invisibleBlockingMapReference = new SoftReference<Map<MFNode, Segment>>(newMap);
            return newMap;
        }
        return map;
    }

    public boolean isInvisibleBlockingMapEnable() {
        return invisibleBlockingMapEnable;
    }

    public void setInvisibleBlockingMapEnable(boolean invisibleBlockingMapEnable) {
        this.invisibleBlockingMapEnable = invisibleBlockingMapEnable;
    }

}

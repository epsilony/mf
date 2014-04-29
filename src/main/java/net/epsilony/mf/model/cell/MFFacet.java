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
package net.epsilony.mf.model.cell;

import java.util.ArrayList;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFFacet {
    private final ArrayList<MFLine> chainHeads = new ArrayList<>();

    public ArrayList<MFLine> getChainHeads() {
        return chainHeads;
    }

    /**
     * REMARK, this method will not be internally called except
     * {@link #requireWell()}
     */
    public boolean isWell() {
        if (chainHeads.isEmpty()) {
            return true;
        }
        // must be closed, with restrict positive area and well connected
        MFLine firstHead = chainHeads.get(0);
        if (firstHead.getPred() == null || firstHead.getPred() == firstHead || !firstHead.isWellConnected()
                || !firstHead.isAnticlockWise()) {
            return false;
        }

        // must be closed, with restrict negative area and well connected
        for (int i = 1; i < chainHeads.size(); i++) {
            MFLine head = chainHeads.get(i);
            if (head.getPred() == null || head.getPred() == head || !head.isWellConnected()
                    || firstHead.isAnticlockWise()) {
                return false;
            }
        }
        return true;
    }

    public void requireWell() {
        if (!isWell()) {
            throw new IllegalStateException();
        }
    }
}

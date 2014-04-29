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
package net.epsilony.mf.adapt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.model.cell.MFCell;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class FissionForest {

    public static class FissionBranch {

        public FissionBranch(FissionBranch father, List<? extends Object> children) {
            this.father = father;
            this.children = new ArrayList<>(children);
        }

        public void replaceChild(Object from, Object to) {
            if (!((from instanceof FissionBranch) || (from instanceof MFCell))
                    || !((to instanceof FissionBranch) || (to instanceof MFCell))) {
                throw new IllegalArgumentException();
            }
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) == from) {
                    children.set(i, to);
                }
            }
        }

        public boolean isPureFather() {
            for (Object child : children) {
                if (child instanceof FissionBranch) {
                    return false;
                }
            }
            return true;
        }

        public FissionBranch getFather() {
            return father;
        }

        public ArrayList<Object> getChildren() {
            return children;
        }

        private final FissionBranch father;
        private final ArrayList<Object> children;

    }

    void afterFissioned(MFCell origin, List<? extends MFCell> newCells) {
        FissionBranch oriFather = cellToFather.get(origin);

        FissionBranch newFather = new FissionBranch(oriFather, newCells);
        pureFathers.add(newFather);

        if (oriFather != null) {
            if (oriFather.isPureFather()) {
                pureFathers.remove(oriFather);
            }
            oriFather.replaceChild(origin, newFather);
        }

        for (MFCell c : newCells) {
            cellToFather.put(c, newFather);
        }
    }

    void afterMerged(FissionBranch father, MFCell newCell) {
        if (!father.isPureFather()) {
            throw new IllegalArgumentException();
        }
        FissionBranch grand = father.getFather();
        grand.replaceChild(father, newCell);
        pureFathers.remove(father);
        if (grand.isPureFather()) {
            pureFathers.add(grand);
        }

        for (Object obj : father.getChildren()) {
            cellToFather.remove(obj);
        }
        cellToFather.put(newCell, grand);
    }

    public FissionBranch getFather(MFCell key) {
        return cellToFather.get(key);
    }

    public Set<FissionBranch> getPureFathers() {
        return pureFathers;
    }

    private final Map<MFCell, FissionBranch> cellToFather = new HashMap<>();
    private final Set<FissionBranch> pureFathers = new HashSet<>();
}

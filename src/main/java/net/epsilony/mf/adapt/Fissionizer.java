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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.epsilony.mf.model.cell.MFCell;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class Fissionizer {
    private CellFissionizer cellFissionizer;
    private final FissionForest fissionForest = new FissionForest();

    public static class FissionRecord {
        private final ArrayList<Node> newNodes = new ArrayList<>();
        private final ArrayList<MFCell> newCells = new ArrayList<>();
        private final ArrayList<MFCell> fissioned = new ArrayList<>();
        private boolean success;
        private MFCell blockCell;

        public List<Node> getNewNodes() {
            return newNodes;
        }

        public List<MFCell> getNewCells() {
            return newCells;
        }

        public boolean isSuccess() {
            return success;
        }

        public MFCell getBlockCell() {
            return blockCell;
        }

        public ArrayList<MFCell> getFissioned() {
            return fissioned;
        }

        protected void clear() {
            newNodes.clear();
            newCells.clear();
            fissioned.clear();
            blockCell = null;
            success = false;
        }

    }

    private final FissionRecord fissionRecord = new FissionRecord();
    private final ArrayDeque<MFCell> stack = new ArrayDeque<>();
    private final Consumer<FissionResult> cellFissionRecorder = this::recordFission;

    private void recordFission(FissionResult fissionResult) {
        fissionRecord.newNodes.addAll(fissionResult.getNewNodes());
        fissionRecord.newCells.addAll(fissionResult.getNewCells());
        fissionRecord.fissioned.add(fissionResult.getFissioned());
        if (null != fissionForest) {
            fissionForest.afterFissioned(fissionResult.getFissioned(), fissionResult.getNewCells());
        }
    }

    public FissionRecord recursivelyFussion(MFCell cell) {
        fissionRecord.clear();
        MFCell top = cell;
        do {

            cellFissionizer.setCell(top);
            if (!cellFissionizer.isEnabledToFission()) {
                fissionRecord.success = false;
                fissionRecord.blockCell = top;
                return fissionRecord;
            }
            MFCell obstructor = cellFissionizer.nextFissionObstructor();
            if (null == obstructor) {
                cellFissionizer.fission();
                cellFissionizer.record(cellFissionRecorder);
                top = stack.poll();
            } else {
                stack.push(top);
                top = obstructor;
            }
        } while (top != null);
        fissionRecord.success = true;
        return fissionRecord;
    }

    public void setCellFissionizer(CellFissionizer cellFissionizer) {
        this.cellFissionizer = cellFissionizer;
    }

}

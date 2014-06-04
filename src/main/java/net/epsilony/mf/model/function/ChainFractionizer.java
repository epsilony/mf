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
package net.epsilony.mf.model.function;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.epsilony.mf.model.function.ChainFractionizer.ChainFractionResult;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.util.MFLine2DUtils;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ChainFractionizer implements Function<MFLine, ChainFractionResult> {

    Function<MFLine, ? extends List<double[]>> singleLineFractionier;

    Function<double[], ? extends Node>         nodeFactory;
    Supplier<? extends MFLine>                 lineFactory;

    public Function<double[], ? extends Node> getNodeFactory() {
        return nodeFactory;
    }

    public void setNodeFactory(Function<double[], ? extends Node> nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public Supplier<? extends MFLine> getLineFactory() {
        return lineFactory;
    }

    public void setLineFactory(Supplier<? extends MFLine> lineFactory) {
        this.lineFactory = lineFactory;
    }

    public ChainFractionizer(Function<MFLine, ? extends List<double[]>> singleLineFractionier,
            Function<double[], ? extends Node> nodeFactory, Supplier<? extends MFLine> lineFactory) {
        this.singleLineFractionier = singleLineFractionier;
        this.nodeFactory = nodeFactory;
        this.lineFactory = lineFactory;
    }

    public ChainFractionizer() {
    }

    private MFLine newLine(double[] coord) {
        MFLine result = lineFactory.get();
        result.setStart(nodeFactory.apply(coord));
        return result;
    }

    @Override
    public ChainFractionResult apply(MFLine chainHead) {

        Map<MFLine, MFLine> newToOriginMap = new HashMap<>();
        MFLine newHeadPred = newLine(chainHead.getStartCoord());
        MFLine tail = newHeadPred;
        Iterator<MFLine> chainIterator = chainHead.iterator();
        MFLine ori = null;
        while (chainIterator.hasNext()) {
            ori = chainIterator.next();
            MFLine newFirst = newLine(ori.getStartCoord());
            MFLine2DUtils.link(tail, newFirst);
            tail = newFirst;
            newToOriginMap.put(tail, ori);

            if (ori.getSucc() == null) {
                break;
            }

            List<double[]> newCoords = singleLineFractionier.apply(ori);
            for (double[] newCoord : newCoords) {

                MFLine newLine = newLine(newCoord);
                MFLine2DUtils.link(tail, newLine);
                tail = newLine;
                newToOriginMap.put(tail, ori);
            }
        }

        MFLine newChainHead;
        if (ori.getSucc() != null) {

            MFLine2DUtils.link(tail, newHeadPred.getSucc());
            newChainHead = tail.getSucc();

        } else {
            if (chainHead.getPred() != null) {
                throw new IllegalStateException("the inputed chain head \"" + chainHead
                        + "\" is not the real head of and open chain!");
            } else {
                newChainHead = newHeadPred.getSucc();
                newChainHead.setPred(null);
            }
        }
        return new ChainFractionResult(newChainHead, newToOriginMap);
    }

    public Function<MFLine, ? extends List<double[]>> getSingleLineFractionier() {
        return singleLineFractionier;
    }

    public void setSingleLineFractionier(Function<MFLine, ? extends List<double[]>> singleLineFractionier) {
        this.singleLineFractionier = singleLineFractionier;
    }

    public static class ChainFractionResult {
        private final MFLine              head;
        private final Map<MFLine, MFLine> newToOri;

        public MFLine getHead() {
            return head;
        }

        public Map<MFLine, MFLine> getNewToOri() {
            return newToOri;
        }

        public ChainFractionResult(MFLine head, Map<MFLine, MFLine> oriToNew) {
            this.head = head;
            this.newToOri = oriToNew;
        }

    }
}

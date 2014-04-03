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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.epsilony.mf.model.function.ChainFractionizer.ChainFractionResult;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.solid.SegmentIterator;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ChainFractionizer implements Function<Line, ChainFractionResult> {

    Function<Line, ? extends List<double[]>> singleLineFractionier;

    Supplier<? extends Node> nodeFactory;

    @Override
    public ChainFractionResult apply(Line chainHead) {

        Map<Line, Line> newToOriginMap = new HashMap<>();
        Node newStart = nodeFactory.get();
        newStart.setCoord(chainHead.getStartCoord());
        Line newHeadPred = new Line(newStart);
        Line tail = newHeadPred;
        SegmentIterator<Line> chainIterator = new SegmentIterator<Line>(chainHead);
        Line ori = null;
        while (chainIterator.hasNext()) {
            ori = chainIterator.next();
            Line newFirst = new Line();
            Node newNode = nodeFactory.get();
            newNode.setCoord(ori.getStartCoord());
            newFirst.setStart(newNode);
            Segment2DUtils.link(tail, newFirst);
            tail = newFirst;
            newToOriginMap.put(tail, ori);

            if (ori.getSucc() == null) {
                break;
            }

            List<double[]> newCoords = singleLineFractionier.apply(ori);
            for (double[] newCoord : newCoords) {
                newNode = nodeFactory.get();
                newNode.setCoord(newCoord);
                Line newLine = new Line(newNode);
                Segment2DUtils.link(tail, newLine);
                tail = newLine;
                newToOriginMap.put(tail, ori);
            }
        }

        Line newChainHead;
        if (ori.getSucc() != null) {

            Segment2DUtils.link(tail, newHeadPred.getSucc());
            newChainHead = (Line) tail.getSucc();

        } else {
            if (chainHead.getPred() != null) {
                throw new IllegalStateException("the inputed chain head \"" + chainHead
                        + "\" is not the real head of and open chain!");
            } else {
                newChainHead = (Line) newHeadPred.getSucc();
                newChainHead.setPred(null);
            }
        }
        return new ChainFractionResult(newChainHead, newToOriginMap);
    }

    public Function<Line, ? extends List<double[]>> getSingleLineFractionier() {
        return singleLineFractionier;
    }

    public void setSingleLineFractionier(Function<Line, ? extends List<double[]>> singleLineFractionier) {
        this.singleLineFractionier = singleLineFractionier;
    }

    public Supplier<? extends Node> getNodeFactory() {
        return nodeFactory;
    }

    public void setNodeFactory(Supplier<? extends Node> nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public ChainFractionizer(Function<Line, ? extends List<double[]>> singleLineFractionier,
            Supplier<? extends Node> nodeFactory) {
        this.singleLineFractionier = singleLineFractionier;
        this.nodeFactory = nodeFactory;
    }

    public ChainFractionizer() {
    }

    public static class ChainFractionResult {
        private final Line head;
        private final Map<Line, Line> newToOri;

        public Line getHead() {
            return head;
        }

        public Map<Line, Line> getNewToOri() {
            return newToOri;
        }

        public ChainFractionResult(Line head, Map<Line, Line> oriToNew) {
            this.head = head;
            this.newToOri = oriToNew;
        }

    }
}

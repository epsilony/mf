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
package net.epsilony.mf.model.convertor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.util.convertor.Convertor;
import net.epsilony.mf.util.tuple.SimpTwoTuple;
import net.epsilony.mf.util.tuple.TwoTuple;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.solid.SegmentIterator;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ChainFractionizer<N extends Node> implements Convertor<Line, TwoTuple<Line, Map<Line, Line>>> {
    Convertor<Line, ? extends List<double[]>> singleLineFractionier;

    Factory<N> nodeFactory;

    @Override
    public TwoTuple<Line, Map<Line, Line>> convert(Line chainHead) {

        Map<Line, Line> newToOriginMap = new HashMap<>();
        N newStart = nodeFactory.produce();
        newStart.setCoord(chainHead.getStartCoord());
        Line newHeadPred = new Line(newStart);
        Line tail = newHeadPred;
        SegmentIterator<Line> chainIterator = new SegmentIterator<Line>(chainHead);
        Line ori = null;
        while (chainIterator.hasNext()) {
            ori = chainIterator.next();
            Line newFirst = new Line();
            N newNode = nodeFactory.produce();
            newNode.setCoord(ori.getStartCoord());
            newFirst.setStart(newNode);
            Segment2DUtils.link(tail, newFirst);
            tail = newFirst;
            newToOriginMap.put(tail, ori);

            if (ori.getSucc() == null) {
                break;
            }

            List<double[]> newCoords = singleLineFractionier.convert(ori);
            for (double[] newCoord : newCoords) {
                newNode = nodeFactory.produce();
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
        return new SimpTwoTuple<>(newChainHead, newToOriginMap);
    }

    public Convertor<Line, ? extends List<double[]>> getSingleLineFractionier() {
        return singleLineFractionier;
    }

    public void setSingleLineFractionier(Convertor<Line, ? extends List<double[]>> singleLineFractionier) {
        this.singleLineFractionier = singleLineFractionier;
    }

    public Factory<N> getNodeFactory() {
        return nodeFactory;
    }

    public void setNodeFactory(Factory<N> nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public ChainFractionizer(Convertor<Line, ? extends List<double[]>> singleLineFractionier, Factory<N> nodeFactory) {
        this.singleLineFractionier = singleLineFractionier;
        this.nodeFactory = nodeFactory;
    }

    public ChainFractionizer() {
    }
}

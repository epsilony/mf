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

import static org.apache.commons.math3.util.MathArrays.distance;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.epsilony.mf.model.cell.util.MFLineIterator;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public interface MFLine extends Iterable<MFLine> {
    Node getStart();

    MFLine getPred();

    MFLine getSucc();

    void setStart(Node node);

    void setPred(MFLine pred);

    void setSucc(MFLine succ);

    default void connectSucc(MFLine succ) {
        setSucc(succ);
        if (succ != null) {
            succ.setPred(this);
        }
    }

    default void connectPred(MFLine pred) {
        setPred(pred);
        if (pred != null) {
            pred.setSucc(this);
        }
    }

    default double[] getStartCoord() {
        return getStart().getCoord();
    }

    default Node getEnd() {
        return getSucc().getStart();
    }

    default double[] getEndCoord() {
        return getEnd().getCoord();
    }

    default void setStartCoord(double[] coord) {
        getStart().setCoord(coord);
    }

    default void setEndCoord(double[] coord) {
        getEnd().setCoord(coord);
    }

    default double chordLength() {
        return distance(getStartCoord(), getEndCoord());
    }

    @Override
    default Iterator<MFLine> iterator() {
        return new MFLineIterator<>(this, MFLine.class);
    }

    default <T extends MFLine> Iterator<T> iterator(Class<T> type) {
        return new MFLineIterator<T>(this, type);
    }

    default <T extends MFLine> Iterable<T> iterable(Class<T> type) {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return new MFLineIterator<T>(MFLine.this, type);
            }
        };
    }

    default <T extends MFLine> Spliterator<T> spliterator(Class<T> type) {
        return Spliterators.spliteratorUnknownSize(iterator(type), 0);
    }

    default Stream<MFLine> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default <T extends MFLine> Stream<T> stream(Class<T> type) {
        return StreamSupport.stream(spliterator(type), false);
    }

    default boolean isWellConnected() {
        Iterator<MFLine> iterator = iterator();
        while (iterator.hasNext()) {
            MFLine line = iterator.next();
            if (line.getSucc() != null && line.getSucc().getPred() != line) {
                return false;
            }
        }
        return true;
    }

    default void requireWellConnected() {
        if (!isWellConnected()) {
            throw new IllegalStateException();
        }
    }

    default boolean isAnticlockWise() {
        return Math2D.isAnticlockwise(iterator(), MFLine::getStartCoord);
    }
}

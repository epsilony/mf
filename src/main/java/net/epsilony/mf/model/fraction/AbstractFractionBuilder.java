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

package net.epsilony.mf.model.fraction;

import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractFractionBuilder implements FractionBuilder {

    protected Segment segment;
    protected double lengthCap;
    protected double diviationCap;
    protected Factory<? extends Node> nodeFactory;

    @Override
    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    @Override
    public double getLengthCap() {
        return lengthCap;
    }

    @Override
    public void setLengthCap(double lengthCap) {
        this.lengthCap = lengthCap;
    }

    @Override
    public double getDiviationCap() {
        return diviationCap;
    }

    @Override
    public void setDiviationCap(double diviationCap) {
        this.diviationCap = diviationCap;
    }

    @Override
    public void setNodeFactory(Factory<? extends Node> nodeFactory) {
        this.nodeFactory = nodeFactory;
    }
}

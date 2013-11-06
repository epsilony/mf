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

import java.util.HashMap;
import java.util.Map;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MultiTypeFractionBuilder extends AbstractFractionBuilder {

    Map<Class<? extends Segment>, FractionBuilder> fractionBuilderMap = new HashMap<>();

    public MultiTypeFractionBuilder() {
        initDefaultMap();
    }

    private void initDefaultMap() {
        fractionBuilderMap.put(Line.class, new LineFractionBuilder());
    }

    @Override
    public void fractionize() {
        FractionBuilder builder = fractionBuilderMap.get(segment.getClass());
        builder.setDiviationCap(diviationCap);
        builder.setLengthCap(lengthCap);
        builder.setNodeFactory(nodeFactory);
        builder.setSegment(segment);
        builder.fractionize();
    }
}

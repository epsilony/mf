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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.function.Function;
import net.epsilony.mf.util.tuple.SimpTwoTuple;
import net.epsilony.mf.util.tuple.TwoTuple;
import net.epsilony.tb.solid.Line;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ChainsFractionResultsMerger
        implements
        Function<Iterable<? extends TwoTuple<? extends Line, ? extends Map<? extends Line, ? extends Line>>>, TwoTuple<List<Line>, Map<Line, Line>>> {

    @Override
    public TwoTuple<List<Line>, Map<Line, Line>> apply(
            Iterable<? extends TwoTuple<? extends Line, ? extends Map<? extends Line, ? extends Line>>> input) {
        List<Line> heads = new LinkedList<>();
        Map<Line, Line> originToNew = new HashMap<>();
        for (TwoTuple<? extends Line, ? extends Map<? extends Line, ? extends Line>> cfr : input) {
            heads.add(cfr.getFirst());
            originToNew.putAll(cfr.getSecond());
        }
        return new SimpTwoTuple<>(heads, originToNew);
    }

}

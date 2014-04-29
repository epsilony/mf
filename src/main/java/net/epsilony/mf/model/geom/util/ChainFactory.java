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
package net.epsilony.mf.model.geom.util;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class ChainFactory {
    private Supplier<? extends MFLine> lineFactory;
    private Function<double[], ? extends Node> nodeFactory;
    private boolean closed = true;

    public void setLineFactory(Supplier<? extends MFLine> lineFactory) {
        this.lineFactory = lineFactory;
    }

    public void setNodeFactory(Function<double[], ? extends Node> nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public MFLine produce(Iterator<double[]> iterator) {
        return produce(iterator, Function.identity());
    }

    public <T> MFLine produce(Iterator<T> iterator, Function<? super T, double[]> coordGetter) {
        MFLine head = null;
        if (iterator.hasNext()) {
            Node headNode = nodeFactory.apply(coordGetter.apply(iterator.next()));
            head = lineFactory.get();
            head.setStart(headNode);
        }

        MFLine line = head;
        while (iterator.hasNext()) {
            Node node = nodeFactory.apply(coordGetter.apply(iterator.next()));
            MFLine succ = lineFactory.get();
            succ.setStart(node);
            line.connectSucc(succ);
            line = succ;
        }

        if (closed && head != null) {
            line.connectSucc(head);
        }
        return head;
    }

}

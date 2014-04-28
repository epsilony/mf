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
package net.epsilony.mf.model.cell.util;

import java.util.Iterator;
import java.util.Objects;

import net.epsilony.mf.model.cell.MFLine;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFLineIterator<T extends MFLine> implements Iterator<T> {
    private MFLine from;
    private MFLine next;
    private Class<T> type;
    private MFLine to;

    /**
     * 
     * @param from
     *            inclusive
     * @param to
     *            exclusive, for loop and chain null is both OK. Normally NOT
     *            from;
     * @param type
     */
    public MFLineIterator(MFLine from, MFLine to, Class<T> type) {
        this(type);
        setup(from, to);
    }

    public void setup(MFLine from) {
        setup(from, null);
    }

    public void setup(MFLine from, MFLine to) {
        Objects.requireNonNull(from);
        this.from = from;
        this.next = from;
        this.to = to;
    }

    public MFLineIterator(Class<T> type) {
        Objects.requireNonNull(type);
        this.type = type;
    }

    public MFLineIterator(MFLine from, Class<T> type) {
        this(from, null, type);
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new IllegalStateException();
        }
        MFLine result = next;
        next = next.getSucc();
        if (next == from || next == to) {
            next = null;
        }
        return type.cast(result);
    }

}

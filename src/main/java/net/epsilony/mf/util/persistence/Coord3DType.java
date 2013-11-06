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

package net.epsilony.mf.util.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.DoubleType;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Coord3DType extends MutableUserType {

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.DOUBLE, Types.DOUBLE, Types.DOUBLE};
    }

    @Override
    public Class returnedClass() {
        return double[].class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {

        double[] result = new double[3];
        int i = 0;
        for (String name : names) {
            Object nullSafeGet = org.hibernate.type.DoubleType.INSTANCE.nullSafeGet(rs, name, session, owner);
            if (i == 0 && null == nullSafeGet) {
                return null;
            } else if (null != nullSafeGet) {
                result[i++] = (double) nullSafeGet;
            } else {
                break;
            }
        }
        if (i < 3) {
            return Arrays.copyOf(result, i);
        } else {
            return result;
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        double[] xs = (double[]) value;
        for (int i = 0; i < 3; i++) {
            if (xs == null || i >= xs.length) {
                DoubleType.INSTANCE.nullSafeSet(st, null, index + i, session);
            } else {
                DoubleType.INSTANCE.nullSafeSet(st, xs[i], index + i, session);
            }
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if (null == value) {
            return null;
        }
        double[] xs = (double[]) value;
        return Arrays.copyOf(xs, xs.length);
    }
}

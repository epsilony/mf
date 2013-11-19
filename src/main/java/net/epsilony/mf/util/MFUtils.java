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

package net.epsilony.mf.util;

import java.util.List;

import net.epsilony.mf.process.integrate.MFIntegrator;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFUtils {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void RudeAddTo(List src, List dst) {
        dst.addAll(src);
    }

    public static String singletonName(Class<?> singletonClass) {
        String simpleName = singletonClass.getSimpleName();
        StringBuilder builder = new StringBuilder();
        boolean atFirst = true;
        for (int i = 0; i < simpleName.length(); i++) {
            char c = simpleName.charAt(i);
            if (Character.isUpperCase(c) && atFirst) {
                builder.append(Character.toLowerCase(c));
            } else {
                atFirst = false;
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println("singletonName of MFIntegrator = " + singletonName(MFIntegrator.class));
    }
}

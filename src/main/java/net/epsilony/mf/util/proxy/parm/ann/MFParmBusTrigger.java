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
package net.epsilony.mf.util.proxy.parm.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Target method specification:
 * <ul>
 * <li>can be any public method</li>
 * <li>when {@link #value()} is not empty, it can only contains readable
 * property names of declaring class. (alias name is not permitted)</li>
 * <li>if a setter and {@link #value()} is empty, there must be a corresponding
 * getter as an implicit bus data source.</li>
 * <li>if not a setter, {@link #value()} cannot be empty</li>
 * </ul>
 * 
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MFParmBusTrigger {
    String[] value() default {};

    String group() default "";
}

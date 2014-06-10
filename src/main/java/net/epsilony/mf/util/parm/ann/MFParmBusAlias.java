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
package net.epsilony.mf.util.parm.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Target method specification:
 * <ul>
 * <li>can only be a bean property getter or setter</li>
 * <li>if setter, {@link #value() value}:
 * <ul>
 * <li>is only used by upper {@link MFParmBusPoolRegsiter} target</li>
 * <li>will override target method property name as registry key</li>
 * </ul>
 * </li>
 * <li>if getter:
 * <ul>
 * <li>must have at least one corresponding {@link MFParmBusPoolTrigger}</li>
 * <li>must have a {@link MFParmBusPool} for the declaring class.</li>
 * <li>will add (not override) {@link #value()} as an alias to property name.
 * The property name and alias name will both be the key String of a same weak
 * bus.</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * {@code
 *  import new;
 * }
 * 
 * @author Man YUAN <epsilonyuan@gmail.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface MFParmBusAlias {
    String value();
}

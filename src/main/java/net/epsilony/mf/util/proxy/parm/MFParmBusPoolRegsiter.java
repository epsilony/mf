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
package net.epsilony.mf.util.proxy.parm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Target method declaring class must have proper method annotated by
 * {@link MFParmBusPool}</br>
 * 
 * Target method specification:
 * <ul>
 * <li>cannot be setter of getter</li>
 * <li>must be void</li>
 * <li>only one Object parameter</li>
 * </ul>
 * 
 * Target method will register input object property setters that:
 * <ul>
 * <li>not annotated by {@link MFParmIgnore}</li>
 * </ul>
 * 
 * Target method will use {@link MFParmBusAlias#value()} instead of property
 * name as weak bus search key if annotated by.
 * 
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MFParmBusPoolRegsiter {

}

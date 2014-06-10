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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.epsilony.mf.util.bus.WeakBus;

/**
 * Target method specification:<br>
 * <ul>
 * <li>cannot be setters or getters</li>
 * <li>if there isn't any {@link MFParmBusTrigger} on declaring class, the
 * {@link #superBuses()} should not be empty!</li>
 * <li>has only one {@link String} parameter</li>
 * <li>return {@link WeakBus}</li>
 * </ul>
 * 
 * 
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MFParmWithBusPool {
    /**
     * used to help register into upper {@link MFParmBusPoolRegsiter}
     */
    String[] superBuses() default {};
}

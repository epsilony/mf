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
package net.epsilony.mf.util.parm;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public interface MFParmContainer {

    default TriggerParmToBusSwitcher parmToBusSwitcher() {
        return null;
    }

    default MFParmIndex parmIndex() {
        return null;
    }

    default void setParmValue(String parm, Object value) {
        parmIndex().getParmDescriptor(parm).setObjectValue(this, value);
        parmToBusSwitcher().triggerParmAims(parm);
    }

    default void register(Object been, String... parms) {
        for (String parm : parms) {
            boolean succ = parmToBusSwitcher().register(parm, been);
            if (!succ) {
                throw new IllegalStateException();
            }
        }
    }

    default void registerAsSubBus(Object bean, String... parms) {
        for (String parm : parms) {
            boolean succ = parmToBusSwitcher().registerAsSubBus(parm, bean);
            if (!succ) {
                throw new IllegalStateException();
            }
        }
    }

    default void autoRegister(Object bean) {
        parmToBusSwitcher().autoRegister(bean);
    }

    default void autoRegister(Object bean, boolean globalOnly) {
        parmToBusSwitcher().autoRegister(bean, globalOnly);
    }
}

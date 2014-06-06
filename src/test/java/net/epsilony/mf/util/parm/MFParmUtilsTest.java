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

import static org.junit.Assert.assertEquals;
import net.epsilony.mf.util.bus.WeakBus;

import org.junit.Test;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmUtilsTest {

    @Test
    public void testRegisterToBusPool() {
        SampleClass registry = new SampleClass();
        SamplePoolClass pool = new SamplePoolClass();
        MFParmUtils.registryToBusPool(pool, "exp", registry);
        pool.weakBus.post("expValue");

        assertEquals("expValue", registry.value);
    }

    public class SampleClass {
        public String value;

        public void setExp(String value) {
            this.value = value;
        }
    }

    public class SamplePoolClass {

        WeakBus<Object> weakBus = new WeakBus<>("test bus");

        @MFParmBusPool
        public WeakBus<Object> busPool(String name) {
            if (name.equals("exp")) {
                return weakBus;
            }
            return null;
        }
    }

}

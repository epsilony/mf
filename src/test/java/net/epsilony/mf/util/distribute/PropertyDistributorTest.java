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
package net.epsilony.mf.util.distribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.epsilony.mf.util.distribute.PropertyDistributor;

import org.junit.Test;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class PropertyDistributorTest {

    @Test
    public void testDistribute() {
        List<Setter> setters = Arrays.asList(new Setter(), new Setter());
        PropertyDistributor pd = new PropertyDistributor();
        Getter getter = new Getter();
        pd.setGetter(getter);
        pd.setSetters(setters);
        pd.setName("mock");
        pd.distribute();
        boolean tested = false;
        for (Setter setter : setters) {
            assertEquals(getter.getMock(), setter.getMock());
            tested = true;
        }
        assertTrue(tested);
    }

    // must be public class or the getter is not proper getter
    public static class Getter {
        String mock = "this is mock";

        public String getMock() {
            return mock;
        }

        public void setMock(String mock) {
            this.mock = mock;
        }

    }

    public static class Setter {
        String mock;

        public String getMock() {
            return mock;
        }

        public void setMock(String mock) {
            this.mock = mock;
        }
    }

}

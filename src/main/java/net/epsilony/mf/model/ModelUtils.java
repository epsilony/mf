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
package net.epsilony.mf.model;

import net.epsilony.mf.model.load.MFLoad;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ModelUtils {
    public static MFLoad getVolumeLoad(PhysicalModel model) {
        return model.getLoadMap().get(model.getGeomRoot());
    }

    public static void setVolumeLoad(PhysicalModel model, MFLoad load) {
        model.getLoadMap().put(model.getGeomRoot(), load);
    }
}

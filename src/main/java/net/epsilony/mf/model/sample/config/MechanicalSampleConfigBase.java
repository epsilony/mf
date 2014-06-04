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
package net.epsilony.mf.model.sample.config;

import net.epsilony.mf.cons_law.PlaneStress;
import net.epsilony.mf.model.sample.MechanicalPatchModelFactory2D;

import org.springframework.context.annotation.Bean;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public abstract class MechanicalSampleConfigBase extends SampleConfigBase {

    public static final double youngModulity = 100;
    public static final double poissonRatio  = 0.3;

    @Bean
    @Override
    public MechanicalPatchModelFactory2D patchModelFactory2D() {
        MechanicalPatchModelFactory2D result = new MechanicalPatchModelFactory2D();
        result.setField(field());

        result.setRectangle(rectangle());
        result.setFacetFractionizer(facetFractionizer());
        result.setSpaceNodesCoordsGenerator(spaceNodesCoordsGenerator());
        result.setVolumeUnitsGenerator(volumeUnitsGenerator());

        result.setConstitutiveLaw(constitutiveLaw());
        return result;
    }

    @Bean
    public PlaneStress constitutiveLaw() {
        return new PlaneStress(youngModulity, poissonRatio);
    }

}

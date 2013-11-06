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

package net.epsilony.mf.process;

import net.epsilony.mf.model.support_domain.SupportDomainSearcherFactory;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.tb.Factory;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFMixerFactory implements Factory<MFMixer> {

    private MFShapeFunction shapeFunction;
    private SupportDomainSearcherFactory supportDomainSearcherFactory;
    private double maxNodesInfluenceRadius;

    public void setShapeFunction(MFShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
    }

    public void setSupportDomainSearcherFactory(SupportDomainSearcherFactory supportDomainSearcherFactory) {
        this.supportDomainSearcherFactory = supportDomainSearcherFactory;
    }

    public void setMaxNodesInfluenceRadius(double maxNodesInfluenceRadius) {
        this.maxNodesInfluenceRadius = maxNodesInfluenceRadius;
    }

    @Override
    public MFMixer produce() {
        Mixer mixer = new Mixer();
        mixer.setShapeFunction(SerializationUtils.clone(shapeFunction));
        mixer.setSupportDomainSearcher(supportDomainSearcherFactory.produce());
        mixer.setMaxInfluenceRad(maxNodesInfluenceRadius);
        return mixer;
    }
}

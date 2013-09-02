/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.geomodel.support_domain.SupportDomainSearcherFactory;
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

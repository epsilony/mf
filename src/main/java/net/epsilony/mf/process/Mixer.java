/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.ArrayList;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.geomodel.support_domain.SupportDomainData;
import net.epsilony.mf.geomodel.support_domain.SupportDomainSearcher;
import net.epsilony.mf.util.Constants;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Mixer implements MFMixer {

    public static final int DEFAULT_CACHE_CAPACITY = 60;
    ArrayList<double[]> coords = new ArrayList<>(DEFAULT_CACHE_CAPACITY);
    TDoubleArrayList infRads = new TDoubleArrayList(DEFAULT_CACHE_CAPACITY);
    SupportDomainSearcher supportDomainSearcher;
    MFShapeFunction shapeFunction;
    double maxInfluenceRad;
    CacheableMixResult cacheableMixResult = new CacheableMixResult();

    @Override
    public MixResult mix(double[] center, Segment bnd) {
        SupportDomainData searchResult = supportDomainSearcher.searchSupportDomain(center, bnd, maxInfluenceRad);
        if (Constants.SUPPORT_COMPLEX_CRITERION) {
            throw new UnsupportedOperationException();
        }

        cacheableMixResult.setNodes(searchResult.visibleNodes);

        shapeFunction.setNodes(searchResult.visibleNodes);
        shapeFunction.setPosition(center);
        shapeFunction.values(cacheableMixResult.getShapeFunctionValues());

        return cacheableMixResult;
    }

    @Override
    public int getDiffOrder() {
        return shapeFunction.getDiffOrder();
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        shapeFunction.setDiffOrder(diffOrder);
        cacheableMixResult.setDiffOrder(diffOrder);
    }

    public SupportDomainSearcher getSupportDomainSearcher() {
        return supportDomainSearcher;
    }

    public void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher) {
        this.supportDomainSearcher = supportDomainSearcher;
    }

    public MFShapeFunction getShapeFunction() {
        return shapeFunction;
    }

    public void setShapeFunction(MFShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
        shapeFunction.setDiffOrder(0);
    }

    public double getMaxInfluenceRad() {
        return maxInfluenceRad;
    }

    public void setMaxInfluenceRad(double maxInfluenceRad) {
        this.maxInfluenceRad = maxInfluenceRad;
    }

    @Override
    public String toString() {
        return String.format("%s{influ rad: %f, shape function: %s, support domain searcher: %s}",
                MiscellaneousUtils.simpleToString(this),
                getMaxInfluenceRad(),
                getShapeFunction(),
                getSupportDomainSearcher());
    }
}

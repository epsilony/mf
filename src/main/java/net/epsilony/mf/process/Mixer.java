/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.project.SimpMfProject;
import gnu.trove.list.array.TDoubleArrayList;
import java.util.ArrayList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.tb.solid.Segment;
import net.epsilony.mf.geomodel.support_domain.SupportDomainData;
import net.epsilony.mf.geomodel.support_domain.SupportDomainSearcher;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.analysis.WithDiffOrder;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Mixer implements WithDiffOrder {

    public static final int DEFAULT_CACHE_CAPACITY = 60;
    ArrayList<double[]> coords = new ArrayList<>(DEFAULT_CACHE_CAPACITY);
    TDoubleArrayList infRads = new TDoubleArrayList(DEFAULT_CACHE_CAPACITY);
    SupportDomainSearcher supportDomainSearcher;
    MFShapeFunction shapeFunction;
    double maxInfluenceRad;

    public MixResult mix(double[] center, Segment bnd) {
        SupportDomainData searchResult = supportDomainSearcher.searchSupportDomain(center, bnd, maxInfluenceRad);
        if (SimpMfProject.SUPPORT_COMPLEX_CRITERION) {
            throw new UnsupportedOperationException();
        }
        shapeFunction.setNodes(searchResult.visibleNodes);
        shapeFunction.setPosition(center);
        double[][] vals = shapeFunction.values(null);
        TDoubleArrayList[] shapeFunctionValueLists = new TDoubleArrayList[vals.length];
        for (int i = 0; i < vals.length; i++) {
            shapeFunctionValueLists[i] = TDoubleArrayList.wrap(vals[i]);
        }
        return new MixResult(shapeFunctionValueLists, searchResult.visibleNodes);
    }

    @Override
    public int getDiffOrder() {
        return shapeFunction.getDiffOrder();
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        shapeFunction.setDiffOrder(diffOrder);
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

    public static class MixResult {

        public TDoubleArrayList[] shapeFunctionValueLists;
        public List<MFNode> nodes;

        public MixResult(TDoubleArrayList[] shapeFunctionValueLists, List<MFNode> nodes) {
            this.shapeFunctionValueLists = shapeFunctionValueLists;
            this.nodes = nodes;
        }
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

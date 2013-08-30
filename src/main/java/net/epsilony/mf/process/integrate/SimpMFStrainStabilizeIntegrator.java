/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.procedure.TIntDoubleProcedure;
import java.util.Arrays;
import net.epsilony.mf.process.MixResult;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.synchron.SynchronizedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpMFStrainStabilizeIntegrator extends AbstractMFIntegrator implements MFIntegrator {

    public static Logger logger = LoggerFactory.getLogger(SimpMFStrainStabilizeIntegrator.class);
    SynchronizedIterator<MFStrainStabilizeIntegrateDomain> volumeDomainSynchronizedIterator;
    private TIntDoubleHashMap[] idShapeFuncMap;
    private final static int DIMENSION = 2;
    private final static int DEFAULT_CAPACITY = 50;
    private double[][] shapeFuncValue;
    private TIntArrayList shapeFuncIndes = new TIntArrayList();
    private FillShapeFunc fillShapeFunc = new FillShapeFunc();
    private double area;
    private double[] areaCenter = new double[DIMENSION];

    public SimpMFStrainStabilizeIntegrator() {
        idShapeFuncMap = new TIntDoubleHashMap[DIMENSION];
        for (int i = 0; i < idShapeFuncMap.length; i++) {
            idShapeFuncMap[i] = new TIntDoubleHashMap(DEFAULT_CAPACITY, 0.5F, -1, 0);
        }
    }

    public void setVolumeDomainSynchronizedIterator(SynchronizedIterator<MFStrainStabilizeIntegrateDomain> volumeDomainSynchronizedIterator) {
        this.volumeDomainSynchronizedIterator = volumeDomainSynchronizedIterator;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void processVolume() {
        mixer.setDiffOrder(assembler.getVolumeDiffOrder());
        mixer.setDiffOrder(0);
        for (MFStrainStabilizeIntegrateDomain ssDomain = volumeDomainSynchronizedIterator.nextItem();
                ssDomain != null;
                ssDomain = volumeDomainSynchronizedIterator.nextItem()) {
            genStabilizedShapeFuncVals(ssDomain);
            MixResult mixResult = mixer.mix(areaCenter, null);
            assembler.setWeight(area);
            assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
            assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
            assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
            assembler.setLoad(ssDomain.load(areaCenter), null);
            assembler.assembleVolume();
            if (null != observer) {
                observer.volumeProcessed(this);
            }
        }
    }

    private void genStabilizedShapeFuncVals(Iterable<MFBoundaryIntegratePoint> ssDomain) {
        for (int dim = 0; dim < idShapeFuncMap.length; dim++) {
            idShapeFuncMap[dim].clear();
        }
        area = 0;
        Arrays.fill(areaCenter, 0);
        for (MFBoundaryIntegratePoint pt : ssDomain) {
            double[] coord = pt.getCoord();
            MixResult mixResult = mixer.mix(coord, pt.getBoundary());
            double weight = pt.getWeight();
            Segment boundary = pt.getBoundary();
            double[] n = Segment2DUtils.chordUnitOutNormal(boundary, null);
            TIntArrayList nodesAssemblyIndes = mixResult.getNodesAssemblyIndes();
            double[] shapeFunctionValue = mixResult.getShapeFunctionValues()[0];
            for (int nodeI = 0; nodeI < nodesAssemblyIndes.size(); nodeI++) {
                int index = nodesAssemblyIndes.getQuick(nodeI);
                double val = shapeFunctionValue[nodeI];
                for (int dim = 0; dim < DIMENSION; dim++) {
                    double stabVal = weight * n[dim] * val;
                    idShapeFuncMap[dim].adjustOrPutValue(index, stabVal, stabVal);

                    double c = coord[dim];
                    area += 0.5 * weight * n[dim] * c;
                    areaCenter[dim] += 0.5 * weight * n[dim] * c * c;
                }
            }
        }

        for (int dim = 0; dim < DIMENSION; dim++) {
            areaCenter[dim] /= area;
        }

        mapToShapeFuncs(idShapeFuncMap);
    }

    private void mapToShapeFuncs(TIntDoubleHashMap[] idShapeFuncMap) {
        shapeFuncIndes.resetQuick();
        int size = idShapeFuncMap[0].size();
        shapeFuncIndes.ensureCapacity(size);
        shapeFuncValue = new double[DIMENSION + 1][size];
        for (int dim = 0; dim < DIMENSION; dim++) {
            fillShapeFunc.setDiffIndex(dim + 1);
            fillShapeFunc.resetCount();
            idShapeFuncMap[dim].forEachEntry(fillShapeFunc);
        }
    }

    private class FillShapeFunc implements TIntDoubleProcedure {

        int diffIndex;
        int count = 0;

        public void setDiffIndex(int diffIndex) {
            this.diffIndex = diffIndex;
        }

        public void resetCount() {
            count = 0;
        }

        @Override
        public boolean execute(int a, double b) {
            shapeFuncValue[diffIndex][count++] = b / area;
            shapeFuncIndes.add(a);
            return true;
        }
    }
}

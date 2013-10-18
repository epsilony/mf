/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.process.integrate.point.MFStrainStabilizeIntegrateDomain;
import net.epsilony.mf.process.integrate.point.MFStrainStabilizeIntegratePoint;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.procedure.TIntDoubleProcedure;
import java.io.Serializable;
import java.util.Arrays;
import net.epsilony.mf.process.MixResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class StrainStabilizeIntegrateCore extends AbstractMFIntegrateCore<MFStrainStabilizeIntegrateDomain> {

    public static Logger logger = LoggerFactory.getLogger(StrainStabilizeIntegrateCore.class);
    private final TIntDoubleHashMap[] idShapeFuncMap;
    private final static int DIMENSION = 2;
    private final static int DEFAULT_CAPACITY = 50;
    private double[][] shapeFuncValue;
    private final TIntArrayList shapeFuncIndes = new TIntArrayList();
    private final FillShapeFunc fillShapeFunc = new FillShapeFunc();
    private double area;
    private final double[] areaCenter = new double[DIMENSION];

    public StrainStabilizeIntegrateCore() {
        idShapeFuncMap = new TIntDoubleHashMap[DIMENSION];
        for (int i = 0; i < idShapeFuncMap.length; i++) {
            idShapeFuncMap[i] = new TIntDoubleHashMap(DEFAULT_CAPACITY, 0.5F, -1, 0);
        }
    }

    @Override
    public void integrateVolume(MFStrainStabilizeIntegrateDomain ssDomain) {
        mixer.setDiffOrder(0);

        genStabilizedShapeFuncVals(ssDomain);
        mixer.setCenter(areaCenter);
        mixer.setBoundary(null);
        MixResult mixResult = mixer.mix();
        assembler.setWeight(area);
        assembler.setNodesAssemblyIndes(mixResult.getNodesAssemblyIndes());
        assembler.setTrialShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setTestShapeFunctionValues(mixResult.getShapeFunctionValues());
        assembler.setLoad(ssDomain.load(areaCenter), null);
        assembler.assembleVolume();
    }

    private void genStabilizedShapeFuncVals(MFStrainStabilizeIntegrateDomain ssDomain) {
        for (int dim = 0; dim < idShapeFuncMap.length; dim++) {
            idShapeFuncMap[dim].clear();
        }
        area = 0;
        Arrays.fill(areaCenter, 0);
        for (MFStrainStabilizeIntegratePoint pt : ssDomain) {
            double[] coord = pt.getCoord();
            mixer.setCenter(coord);
            mixer.setBoundary(pt.getSolidBoundary());
            MixResult mixResult = mixer.mix();
            double weight = pt.getWeight();
            double[] n = pt.getUnitOutNormal();
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

    private class FillShapeFunc implements TIntDoubleProcedure, Serializable {

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

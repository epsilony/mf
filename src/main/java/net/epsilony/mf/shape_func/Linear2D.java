/* (c) Copyright by Man YUAN */
package net.epsilony.mf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.tb.analysis.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Linear2D implements MFShapeFunction {

    TDoubleArrayList[] shapeFunctionValueLists = null;

    @Override
    public TDoubleArrayList[] values(
            double[] xy,
            List<MFNode> nodes,
            TDoubleArrayList[] dists) {
        if (null != shapeFunctionValueLists) {
            shapeFunctionValueLists[0].resetQuick();
        } else {
            shapeFunctionValueLists = new TDoubleArrayList[]{new TDoubleArrayList(2)};
        }
        double v2 = calcV2(nodes.get(0).getCoord(), nodes.get(1).getCoord(), xy);
        double v1 = 1 - v2;
        shapeFunctionValueLists[0].add(v1);
        shapeFunctionValueLists[1].add(v2);
        return shapeFunctionValueLists;
    }

    public static double[] values(double[] xy, double[] hCoord, double[] rCoord, double[] output) {
        if (null == output) {
            output = new double[2];
        }
        double v2 = calcV2(hCoord, rCoord, xy);
        double v1 = 1 - v2;
        output[0] = v1;
        output[1] = v2;
        return output;
    }

    @Override
    public int getDiffOrder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static double calcV2(double[] hCoord, double[] rCoord, double[] xy) {
        double len = Math2D.distance(hCoord, rCoord);
        double v2 = Math2D.distance(xy, hCoord) / len;
        return v2;
    }

    @Override
    public MFShapeFunction synchronizeClone() {
        MFShapeFunction result = new Linear2D();
        result.setDiffOrder(getDiffOrder());
        return result;
    }
}

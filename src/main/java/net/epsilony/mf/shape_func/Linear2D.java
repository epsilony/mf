/* (c) Copyright by Man YUAN */
package net.epsilony.mf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.Arrays;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.tb.analysis.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Linear2D implements MFShapeFunction {

    int id;
    List<MFNode> nodes;
    double[] position;

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public void setDimension(int dimension) {
        if (dimension != 2) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setNodes(List<MFNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public void setDistancesToPosition(TDoubleArrayList[] distances) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPosition(double[] position) {
        this.position = position;
    }

    @Override
    public double[][] values(double[][] output) {
        if (null == output) {
            output = new double[1][2];
        } else {
            Arrays.fill(output[0], 0);
        }
        double v2 = calcV2(nodes.get(0).getCoord(), nodes.get(1).getCoord(), position);
        double v1 = 1 - v2;
        output[0][0] += v1;
        output[0][1] += v2;
        return output;
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
}

/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.observer;

import gnu.trove.list.array.TIntArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.epsilony.mf.process.MixResult;
import net.epsilony.mf.process.RawMixResult;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class CoreRecorderObserver implements MFIntegratorObserver {

    List<Record> records = Collections.synchronizedList(new LinkedList<Record>());

    public static class Record {

        double[] coord;
        double[] outNormal;
        double[] load;
        boolean[] loadValidity;
        GeomUnit boundary;
        MixResult mixResult;
        double weight;
        double[] lagrangleShapeFunction;
        TIntArrayList lagrangleIndes;

        public double[] getCoord() {
            return coord;
        }

        public double[] getOutNormal() {
            return outNormal;
        }

        public double[] getLoad() {
            return load;
        }

        public boolean[] getLoadValidity() {
            return loadValidity;
        }

        public GeomUnit getBoundary() {
            return boundary;
        }

        public MixResult getMixResult() {
            return mixResult;
        }

        public double getWeight() {
            return weight;
        }

        public double[] getLagrangleShapeFunction() {
            return lagrangleShapeFunction;
        }

        public TIntArrayList getLagrangleIndes() {
            return lagrangleIndes;
        }
    }

    @Override
    public void update(Map<MFIntegratorObserverKey, Object> data) {
        MFIntegratorStatus status = (MFIntegratorStatus) data.get(MFIntegratorObserverKey.STATUS);
        if (status != MFIntegratorStatus.CORE_UNIT_INTEGRATED) {
            return;
        }
        addToRecords(data);
    }

    private void addToRecords(Map<MFIntegratorObserverKey, Object> data) {
        Record record = copyToRecord(data);
        records.add(record);
    }

    private Record copyToRecord(Map<MFIntegratorObserverKey, Object> data) {
        Record record = new Record();
        double[] coord = (double[]) data.get(MFIntegratorObserverKey.COORD);
        double[] load = (double[]) data.get(MFIntegratorObserverKey.LOAD);
        boolean[] loadValidity = (boolean[]) data.get(MFIntegratorObserverKey.LOAD_VALIDITY);
        GeomUnit boundary = (GeomUnit) data.get(MFIntegratorObserverKey.BOUNDARY);
        double[] outNormal = (double[]) data.get(MFIntegratorObserverKey.OUT_NORMAL);
        MixResult mixResult = (MixResult) data.get(MFIntegratorObserverKey.MIX_RESULT);
        double weight = (double) data.get(MFIntegratorObserverKey.WEIGHT);
        TIntArrayList lagrangleIndes = (TIntArrayList) data.get(MFIntegratorObserverKey.LAGRANGLE_INDES);
        double[] lagrangleShapeFunction = (double[]) data.get(MFIntegratorObserverKey.LAGRANGLE_SHAPE_FUNCTION);

        record.coord = copyOf(coord);
        record.load = copyOf(load);
        record.loadValidity = copyOf(loadValidity);
        record.boundary = boundary;
        record.outNormal = copyOf(outNormal);
        record.mixResult = copyOf(mixResult);
        record.weight = weight;
        record.lagrangleIndes = copyOf(lagrangleIndes);
        record.lagrangleShapeFunction = copyOf(lagrangleShapeFunction);
        return record;
    }

    double[] copyOf(double[] value) {
        if (null == value) {
            return null;
        }
        return Arrays.copyOf(value, value.length);
    }

    TIntArrayList copyOf(TIntArrayList list) {
        if (null == list) {
            return null;
        }
        return new TIntArrayList(list);
    }

    boolean[] copyOf(boolean[] value) {
        if (null == value) {
            return null;
        }
        return Arrays.copyOf(value, value.length);
    }

    private MixResult copyOf(MixResult mixResult) {
        TIntArrayList nodesAssemblyIndes = mixResult.getNodesAssemblyIndes();
        TIntArrayList indesCopy = new TIntArrayList(nodesAssemblyIndes);
        double[][] shapeFunctionValues = mixResult.getShapeFunctionValues();
        double[][] valueCopy = new double[shapeFunctionValues.length][];
        for (int i = 0; i < valueCopy.length; i++) {
            valueCopy[i] = Arrays.copyOf(shapeFunctionValues[i], shapeFunctionValues[i].length);
        }
        RawMixResult copy = new RawMixResult();
//        copy.setNodes(null);
        copy.setNodesAssemblyIndes(indesCopy);
        copy.setShapeFunctionValues(valueCopy);
        return copy;
    }

    public List<Record> getRecords() {
        return records;
    }

}

/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tb.MiscellaneousUtils;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalLagrangeAssembler
        extends AbstractMechanicalAssembler<MechanicalLagrangeAssembler>
        implements LagrangeAssembler<MechanicalLagrangeAssembler> {

    int dirichletDimensionSize;
    TIntArrayList lagrangeAssemblyIndes;
    TDoubleArrayList lagrangeShapeFunctionValue;

    @Override
    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            TDoubleArrayList lagrangeShapeFunctionValue) {
        this.lagrangeAssemblyIndes = lagrangeAssemblyIndes;
        this.lagrangeShapeFunctionValue = lagrangeShapeFunctionValue;
    }

    @Override
    public void assembleDirichlet() {
        Matrix mat = mainMatrix;
        DenseVector vec = mainVector;
        double[] shapeFunc = trialShapeFunctionValues[0];
        final int nodeValueDimension = getNodeValueDimension();

        for (int j = 0; j < lagrangeAssemblyIndes.size(); j++) {
            int odd = j % nodeValueDimension;
            if (!loadValidity[odd]) {
                continue;
            }

            double weightedLagShapeFunc_j = lagrangeShapeFunctionValue.getQuick(j / nodeValueDimension) * weight;
            int col = lagrangeAssemblyIndes.getQuick(j);
            vec.add(col, -weightedLagShapeFunc_j * load[odd]);
            for (int i = 0; i < testAssemblyIndes.size(); i++) {
                double shapeFunc_i = shapeFunc[i];
                int row = testAssemblyIndes.getQuick(i) * nodeValueDimension + odd;
                double d = -shapeFunc_i * weightedLagShapeFunc_j;
                mat.add(row, col, d);
                if (!isUpperSymmetric()) {
                    mat.add(col, row, d);
                }
            }
        }
    }

    @Override
    public int getDirichletDimensionSize() {
        return dirichletDimensionSize;
    }

    @Override
    public void setDirichletDimensionSize(int dirichletDimensionSize) {
        this.dirichletDimensionSize = dirichletDimensionSize;
    }

    @Override
    protected int getMainMatrixSize() {
        return getNodeValueDimension() * nodesNum + dirichletDimensionSize;
    }

    @Override
    public MechanicalLagrangeAssembler synchronizeClone() {
        MechanicalLagrangeAssembler result = new MechanicalLagrangeAssembler();
        result.setConstitutiveLaw(constitutiveLaw);
        result.setDirichletDimensionSize(dirichletDimensionSize);
        result.setMatrixDense(isMatrixDense());
        result.setNodesNum(nodesNum);
        result.prepare();
        return result;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D:%d/%d/%d,"
                + " mat dense/sym: %b/%b,"
                + " dirichlet dimension size: %d}",
                getNodesNum(),
                getNodeValueDimension(),
                getVolumeDiffOrder(),
                getNeumannDiffOrder(),
                getDirichletDiffOrder(),
                isMatrixDense(),
                isUpperSymmetric(),
                getDirichletDimensionSize());
    }

    @Override
    public int getNodeValueDimension() {
        return 2;
    }
}

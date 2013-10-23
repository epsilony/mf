/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.mf.process.integrate;

import net.epsilony.mf.util.matrix.MFMatrix;

/**
 *
 * @author epsilon
 */
public class RawMFIntegrateResult implements MFIntegrateResult {

    boolean lagrangle;
    int lagrangleDimension;
    MFMatrix mainMatrix, mainVector;

    @Override
    public boolean isLagrangle() {
        return lagrangle;
    }

    public void setLagrangle(boolean lagrangle) {
        this.lagrangle = lagrangle;
    }

    @Override
    public int getLagrangleDimension() {
        return lagrangleDimension;
    }

    public void setLagrangleDimension(int lagrangleDimension) {
        this.lagrangleDimension = lagrangleDimension;
    }

    @Override
    public MFMatrix getMainMatrix() {
        return mainMatrix;
    }

    public void setMainMatrix(MFMatrix mainMatrix) {
        this.mainMatrix = mainMatrix;
    }

    @Override
    public MFMatrix getMainVector() {
        return mainVector;
    }

    public void setMainVector(MFMatrix mainVector) {
        this.mainVector = mainVector;
    }

    public void set(MFIntegrateResult result) {
        this.mainMatrix = result.getMainMatrix();
        this.mainVector = result.getMainVector();
        this.lagrangle = result.isLagrangle();
        this.lagrangleDimension = result.getLagrangleDimension();
    }
}

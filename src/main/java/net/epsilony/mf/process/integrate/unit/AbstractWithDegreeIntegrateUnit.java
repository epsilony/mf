package net.epsilony.mf.process.integrate.unit;

public abstract class AbstractWithDegreeIntegrateUnit implements WithDegreeIntegrateUnit {

    int degree = MFIntegrateUnits.NULL_DEGREE;

    public AbstractWithDegreeIntegrateUnit() {
        super();
    }

    @Override
    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

}
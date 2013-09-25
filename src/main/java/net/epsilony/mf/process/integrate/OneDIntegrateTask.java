/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

import java.util.ArrayList;
import java.util.List;
import net.epsilony.mf.process.integrate.point.MFBoundaryIntegratePoint;
import net.epsilony.mf.process.integrate.point.MFIntegratePoint;
import net.epsilony.mf.process.integrate.point.SimpMFIntegratePoint;
import net.epsilony.tb.quadrature.GaussLegendre;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class OneDIntegrateTask implements MFIntegrateTask<MFIntegratePoint, MFBoundaryIntegratePoint, MFBoundaryIntegratePoint> {

    int id;
    double start;
    double end;
    double integrateDomainUpperBound = -1;
    List<MFBoundaryIntegratePoint> neumannTasks;
    List<MFBoundaryIntegratePoint> dirichletTasks;
    int degree;
    double[] quadratureParameters;
    double[] quadratureWeights;
    UnivariateFunction volumeFunction;

    public void setVolumeFunction(UnivariateFunction volumeFunction) {
        this.volumeFunction = volumeFunction;
    }

    public void setDegree(int degree) {
        this.degree = degree;
        double[][] pointsWeightsByDegree = GaussLegendre.pointsWeightsByDegree(degree);
        quadratureParameters = pointsWeightsByDegree[0];
        quadratureWeights = pointsWeightsByDegree[1];

    }

    public void setStart(double start) {
        this.start = start;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public void setIntegrateDomainUpperBound(double integrateDomainUpperBound) {
        if (integrateDomainUpperBound <= 0) {
            throw new IllegalArgumentException();
        }
        this.integrateDomainUpperBound = integrateDomainUpperBound;
    }

    public void setNeumannTasks(List<MFBoundaryIntegratePoint> neumannTasks) {
        this.neumannTasks = neumannTasks;
    }

    public void setDirichletTasks(List<MFBoundaryIntegratePoint> dirichletTasks) {
        this.dirichletTasks = dirichletTasks;
    }

    @Override
    public List<MFIntegratePoint> volumeTasks() {
        int domainNum = getIntegrateDomainNum();
        double domainLength = getIntegrateDomainLength();
        ArrayList<MFIntegratePoint> result = new ArrayList<>(domainNum * quadratureWeights.length);
        for (int i = 0; i < domainNum; i++) {
            double domainStart = start + i * domainLength;
            for (int j = 0; j < quadratureWeights.length; j++) {
                double weight = quadratureWeights[j] * 0.5 * domainLength;
                double t = quadratureParameters[j];
                double pos = domainStart + domainLength * (t + 1) / 2;
                double load = volumeFunction.value(pos);
                SimpMFIntegratePoint integratePoint = new SimpMFIntegratePoint();
                integratePoint.setCoord(new double[]{pos});
                integratePoint.setDimension(1);
                integratePoint.setWeight(weight);
                integratePoint.setLoad(new double[]{load});
                result.add(integratePoint);
            }
        }
        return result;
    }

    public int getIntegrateDomainNum() {
        if (end <= start) {
            throw new IllegalStateException();
        }
        return (int) Math.ceil((end - start) / integrateDomainUpperBound);
    }

    public double getIntegrateDomainLength() {
        return (end - start) / getIntegrateDomainNum();
    }

    @Override
    public List<MFBoundaryIntegratePoint> neumannTasks() {
        return neumannTasks;
    }

    @Override
    public List<MFBoundaryIntegratePoint> dirichletTasks() {
        return dirichletTasks;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}

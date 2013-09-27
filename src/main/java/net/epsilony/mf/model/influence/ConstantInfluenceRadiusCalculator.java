/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.influence;

import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tb.solid.GeomUnit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ConstantInfluenceRadiusCalculator implements InfluenceRadiusCalculator {

    double rad;
    private int id;

    @Override
    public double calcInflucenceRadius(double[] coord, GeomUnit bnd) {
        return rad;
    }

    public ConstantInfluenceRadiusCalculator() {
    }

    public ConstantInfluenceRadiusCalculator(double rad) {
        this.rad = rad;
    }

    public double getRad() {
        return rad;
    }

    public void setRad(double rad) {
        this.rad = rad;
    }

    @Override
    public void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher) {
    }

    @Override
    public String toString() {
        return "ConstantInfluenceRadiusCalculator{" + "rad=" + rad + '}';
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

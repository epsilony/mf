/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.epsilony.mf.opt.sample;

import static org.apache.commons.math3.util.FastMath.PI;
import static org.apache.commons.math3.util.FastMath.pow;
import static org.apache.commons.math3.util.MathArrays.distance;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleFunction;

import net.epsilony.mf.integrate.unit.GeomPoint;
import net.epsilony.mf.opt.LevelOptModel;
import net.epsilony.mf.opt.NloptIntegralProcessor;
import net.epsilony.mf.opt.PowerRangePenaltyFunction;
import net.epsilony.mf.opt.integrate.CoreShiftRangeFunctionalIntegrator;
import net.epsilony.mf.opt.integrate.LevelFunctionalIntegrator;
import net.epsilony.mf.opt.integrate.LevelPenaltyIntegrator;
import net.epsilony.mf.util.MFBeanUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class CenterCircleAim {

    public static final Logger          logger                = LoggerFactory.getLogger(CenterCircleAim.class);

    private double                      left                  = 1;
    private double                      up                    = 15;
    private int                         width                 = 40;
    private int                         height                = 40;
    private int                         margin                = 4;
    private double                      triangleScale         = 0.5;
    private double[]                    distanceCenter        = { 11, 9 };
    private ToDoubleFunction<GeomPoint> objectCoreFunction    = gp -> {
                                                                  double t = distance(gp.getCoord(), distanceCenter);
                                                                  return pow(t, 5);
                                                              };
    private double[]                    inequalTolerents      = new double[] { 1, 0 };
    private ToDoubleFunction<GeomPoint> inequalIntegratorCore = gp -> -10;
    private double                      inequalShift          = 4 * 4 * PI * 10;

    private LevelOptModel               levelOptModel;

    private double                      influenceRadiusRatio  = 3;
    private double                      penaltyScale          = 1000;

    public LevelOptModel getLevelOptModel() {
        RangeMarginLevelOptModelFactory factory = new RangeMarginLevelOptModelFactory(left, up, width, height, margin,
                margin, triangleScale, triangleScale, (line) -> false);
        levelOptModel = factory.produce();
        return levelOptModel;
    }

    public double getInfluenceRadius() {
        return influenceRadiusRatio * triangleScale;
    }

    public LevelFunctionalIntegrator getObjectIntegrator() {
        CoreShiftRangeFunctionalIntegrator objectIntegrator = new CoreShiftRangeFunctionalIntegrator();
        objectIntegrator.setCoreFunction(objectCoreFunction);
        return objectIntegrator;
    }

    public List<LevelFunctionalIntegrator> getInequalRangeIntegrators() {
        LevelPenaltyIntegrator result = new LevelPenaltyIntegrator();
        result.setPenalty(new PowerRangePenaltyFunction(penaltyScale, 3));

        return Arrays.asList(result);
    }

    public List<LevelFunctionalIntegrator> getInequalDomainIntegrators() {
        CoreShiftRangeFunctionalIntegrator inequalIntegrator = new CoreShiftRangeFunctionalIntegrator();
        inequalIntegrator.setCoreFunction(inequalIntegratorCore);
        inequalIntegrator.setShift(inequalShift);
        return Arrays.asList(inequalIntegrator);
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getUp() {
        return up;
    }

    public void setUp(double up) {
        this.up = up;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public double getTriangleScale() {
        return triangleScale;
    }

    public void setTriangleScale(double triangleScale) {
        this.triangleScale = triangleScale;
    }

    public double[] getDistanceCenter() {
        return distanceCenter;
    }

    public void setDistanceCenter(double[] distanceCenter) {
        this.distanceCenter = distanceCenter;
    }

    public double getInfluenceRadiusRatio() {
        return influenceRadiusRatio;
    }

    public void setInfluenceRadiusRatio(double influenceRadiusRatio) {
        this.influenceRadiusRatio = influenceRadiusRatio;
    }

    public double getPenaltyScale() {
        return penaltyScale;
    }

    public void setPenaltyScale(double penaltyScale) {
        this.penaltyScale = penaltyScale;
    }

    public String getName() {
        return toString();
    }

    public double[] getInequalTolerents() {
        return inequalTolerents;
    }

    public void setInequalTolerents(double[] inequalTolerents) {
        this.inequalTolerents = inequalTolerents;
    }

    @Override
    public String toString() {
        return "CenterCircleAim [left=" + left + ", up=" + up + ", width=" + width + ", height=" + height + ", margin="
                + margin + ", triangleScale=" + triangleScale + ", distanceCenter=" + Arrays.toString(distanceCenter)
                + ", inequalTolerents=" + Arrays.toString(inequalTolerents) + ", inequalShift=" + inequalShift
                + ", influenceRadiusRatio=" + influenceRadiusRatio + ", penaltyScale=" + penaltyScale + "]";
    }

    public static void main(String[] args) {
        CenterCircleAim centerCircleAim = new CenterCircleAim();

        NloptIntegralProcessor processor = new NloptIntegralProcessor();

        MFBeanUtils.transmitProperties(centerCircleAim, processor, logger);

        processor.initialProcess();

        processor.prepareOpt();

        processor.optimize();
    }

}

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
package net.epsilony.mf.opt.config;

import java.util.Collection;
import java.util.List;

import net.epsilony.mf.integrate.integrator.config.IntegralBaseConfig;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.geom.MFCell;
import net.epsilony.mf.opt.InequalConstraintsCalculator;
import net.epsilony.mf.opt.ObjectCalculator;
import net.epsilony.mf.opt.integrate.CoreShiftRangeFunctionalIntegrator;
import net.epsilony.mf.opt.integrate.LevelFunctionalIntegralUnitsGroup;
import net.epsilony.mf.opt.integrate.LevelFunctionalIntegrator;
import net.epsilony.mf.opt.integrate.LevelPenaltyIntegrator;
import net.epsilony.mf.opt.integrate.TriangleMarchingIntegralUnitsFactory;
import net.epsilony.mf.opt.nlopt.NloptMMADriver;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class OptConfigHub extends ApplicationContextAwareImpl {

    private double[] start;
    private List<? extends MFNode> nodes;
    private LevelFunctionalIntegrator objectIntegrator;
    private List<? extends LevelFunctionalIntegrator> inequalRangeIntegrators;
    private List<? extends LevelFunctionalIntegrator> inequalDomainIntegrators;
    private LevelFunctionalIntegralUnitsGroup rangeIntegralUnitsGroup;
    private List<? extends MFCell> cells;
    private double[] inequalTolerents;
    private double objectAbsoluteTolerence = 1e-6;
    private double objectRelativeTolerent = 1e-4;
    private int quadratureDegree = 2;

    public OptConfigHub() {
    }

    public OptConfigHub(double[] start, List<? extends MFNode> nodes, LevelFunctionalIntegrator objectIntegrator,
            List<? extends LevelFunctionalIntegrator> inequalRangeIntegrators,
            List<? extends LevelFunctionalIntegrator> inequalDomainIntegrators,
            LevelFunctionalIntegralUnitsGroup rangeIntegralUnitsGroup, double[] inequalTolerents,
            List<? extends MFCell> cells) {
        this.start = start;
        this.nodes = nodes;
        this.objectIntegrator = objectIntegrator;
        this.inequalRangeIntegrators = inequalRangeIntegrators;
        this.inequalDomainIntegrators = inequalDomainIntegrators;
        this.rangeIntegralUnitsGroup = rangeIntegralUnitsGroup;
        this.inequalTolerents = inequalTolerents;
        this.cells = cells;
    }

    public void setup() {

        @SuppressWarnings("unchecked")
        WeakBus<Integer> spatialDimensionBus = (WeakBus<Integer>) applicationContext
                .getBean(ModelBusConfig.SPATIAL_DIMENSION_BUS);
        spatialDimensionBus.post(2);

        @SuppressWarnings("unchecked")
        WeakBus<Integer> quadratureDegreeBus = (WeakBus<Integer>) applicationContext
                .getBean(IntegralBaseConfig.QUADRATURE_DEGREE_BUS);
        quadratureDegreeBus.post(quadratureDegree);

        @SuppressWarnings("unchecked")
        WeakBus<Collection<? extends MFNode>> nodesBus = (WeakBus<Collection<? extends MFNode>>) applicationContext
                .getBean(ModelBusConfig.NODES_BUS);
        nodesBus.post(nodes);

        @SuppressWarnings("unchecked")
        WeakBus<Boolean> modelInputedBus = (WeakBus<Boolean>) applicationContext
                .getBean(ModelBusConfig.MODEL_INPUTED_BUS);
        modelInputedBus.post(true);

        getTriangleMarchingIntegralUnitsFactory().setCells(cells);

        ObjectCalculator objectCalculator = getObjectCalculator();
        objectCalculator.setIntegrator(objectIntegrator);

        InequalConstraintsCalculator inequalConstraintsCalculator = getInequalConstraintsCalculator();
        inequalConstraintsCalculator.clearRangeCores();
        inequalConstraintsCalculator.addRangeCores(inequalRangeIntegrators);
        inequalConstraintsCalculator.clearDomainCores();
        inequalConstraintsCalculator.addDomainCores(inequalDomainIntegrators);
        inequalConstraintsCalculator.setRangeIntegralUnitsGroup(rangeIntegralUnitsGroup);

        List<LevelFunctionalIntegrator> tlist = Lists.newArrayList(objectIntegrator);
        tlist.addAll(inequalDomainIntegrators);
        tlist.addAll(inequalRangeIntegrators);
        tlist.forEach(inter -> {
            inter.setGradientSize(start.length);
        });

        NloptMMADriver nloptMMADriver = getNloptMMADriver();
        nloptMMADriver.setInequalConstraintsSize(inequalConstraintsCalculator.size());
        nloptMMADriver.setInequalTolerents(inequalTolerents);
        nloptMMADriver.setObjectAbsoluteTolerence(objectAbsoluteTolerence);
        nloptMMADriver.setObjectRelativeTolerent(objectRelativeTolerent);

        nloptMMADriver.setStart(start);
    }

    public TriangleMarchingIntegralUnitsFactory getTriangleMarchingIntegralUnitsFactory() {
        return applicationContext.getBean(TriangleMarchingIntegralUnitsFactory.class);
    }

    public ObjectCalculator getObjectCalculator() {
        ObjectCalculator objectCalculator = applicationContext.getBean(OptBaseConfig.OBJECT_CALCULATOR,
                ObjectCalculator.class);
        return objectCalculator;
    }

    public InequalConstraintsCalculator getInequalConstraintsCalculator() {
        InequalConstraintsCalculator inequalConstraintsCalculator = applicationContext.getBean(
                OptBaseConfig.INEQUAL_CONSTRAINTS_CALCULATOR, InequalConstraintsCalculator.class);
        return inequalConstraintsCalculator;
    }

    public NloptMMADriver getNloptMMADriver() {
        NloptMMADriver nloptMMADriver = applicationContext
                .getBean(OptBaseConfig.NLOPT_MMA_DRIVER, NloptMMADriver.class);
        return nloptMMADriver;
    }

    public void setCells(List<? extends MFCell> cells) {
        this.cells = cells;
    }

    public double[] getInequalTolerents() {
        return inequalTolerents;
    }

    public void setInequalTolerents(double[] inequalTolerents) {
        this.inequalTolerents = inequalTolerents;
    }

    public double getObjectAbsoluteTolerence() {
        return objectAbsoluteTolerence;
    }

    public void setObjectAbsoluteTolerence(double objectAbsoluteTolerence) {
        this.objectAbsoluteTolerence = objectAbsoluteTolerence;
    }

    public double getObjectRelativeTolerent() {
        return objectRelativeTolerent;
    }

    public void setObjectRelativeTolerent(double objectRelativeTolerent) {
        this.objectRelativeTolerent = objectRelativeTolerent;
    }

    public double[] getStart() {
        return start;
    }

    public void setStart(double[] start) {
        this.start = start;
    }

    public List<? extends MFNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<? extends MFNode> nodes) {
        this.nodes = nodes;
    }

    public void setInequalConstraintCores(List<CoreShiftRangeFunctionalIntegrator> inequalCores) {
        this.inequalDomainIntegrators = inequalCores;
    }

    public void setObjectIntegrator(LevelFunctionalIntegrator objectIntegrator) {
        this.objectIntegrator = objectIntegrator;
    }

    public void setInequalRangeIntegrators(List<? extends LevelFunctionalIntegrator> inequalRangeIntegrators) {
        this.inequalRangeIntegrators = inequalRangeIntegrators;
    }

    public void setInequalDomainIntegrators(List<? extends LevelFunctionalIntegrator> inequalDomainIntegrators) {
        this.inequalDomainIntegrators = inequalDomainIntegrators;
    }

    public void setRangeIntegralUnitsGroup(LevelFunctionalIntegralUnitsGroup rangeIntegralUnitsGroup) {
        this.rangeIntegralUnitsGroup = rangeIntegralUnitsGroup;
    }

    public LevelPenaltyIntegrator penaltyRangeIntegrator() {
        return applicationContext.getBean(LevelPenaltyIntegrator.class);
    }

}

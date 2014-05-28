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
package net.epsilony.mf.opt.nlopt;

import static net.epsilony.tb.nlopt.NloptLibrary.nloptSetMinObjective;
import static net.epsilony.tb.nlopt.NloptLibrary.nloptSetXtolRel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.epsilony.mf.util.persist.Record;
import net.epsilony.tb.nlopt.NloptLibrary;
import net.epsilony.tb.nlopt.NloptLibrary.NloptAlgorithm;
import net.epsilony.tb.nlopt.NloptLibrary.NloptOpt;
import net.epsilony.tb.nlopt.NloptLibrary.NloptResult;

import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NloptMMADriver {
    public static final Logger logger = LoggerFactory.getLogger(NloptMMADriver.class);
    private NloptOpt nlopt;
    private NloptFuncWrapper object;
    private NloptMFuncWrapper inequalConstraints;

    @Record
    private String name;
    @Record
    private double[] inequalTolerents;
    @Record
    private double objectRelativeTolerent = 1e-4;
    @Record
    private double objectAbsoluteTolerence = 1e-6;
    @Record
    private double[] start;
    @Record
    private double[] resultParameters;
    @Record
    private double resultValue;
    @Record
    private long resultStatus;

    private Consumer<Map<String, Object>> initOptimizationTrigger;

    public NloptMMADriver(NloptFuncWrapper object, NloptMFuncWrapper inequalConstraints, int inequalConstraintSize,
            double[] inequalTolerents, double[] start) {
        this.object = object;
        this.inequalConstraints = inequalConstraints;
        this.inequalTolerents = inequalTolerents;
        this.start = start;
        if (inequalTolerents.length != inequalConstraintSize) {
            throw new IllegalArgumentException();
        }
    }

    public NloptMMADriver() {
    }

    public void doOptimize() {
        nlopt = NloptLibrary.nloptCreate(NloptAlgorithm.NLOPT_LD_MMA, start.length);

        IntValuedEnum<NloptResult> nloptResult = nloptSetMinObjective(nlopt, Pointer.pointerTo(object), Pointer.NULL);
        if (!nloptResult.equals(NloptResult.NLOPT_SUCCESS)) {
            throw new IllegalStateException(nloptResult.toString());
        }

        nloptResult = NloptLibrary.nloptAddInequalityMconstraint(nlopt, inequalConstraints.getConstraintsSize(),
                Pointer.pointerTo(inequalConstraints), Pointer.NULL, Pointer.pointerToDoubles(inequalTolerents));
        if (!nloptResult.equals(NloptResult.NLOPT_SUCCESS)) {
            throw new IllegalStateException(nloptResult.toString());
        }

        nloptSetXtolRel(nlopt, objectRelativeTolerent);
        NloptLibrary.nloptSetXtolAbs1(nlopt, objectAbsoluteTolerence);

        Pointer<Double> functionResultPoint = Pointer.pointerToDouble(0);
        Pointer<Double> parameterPoint = Pointer.pointerToDoubles(start);

        if (null != initOptimizationTrigger) {
            Map<String, Object> initData = new HashMap<String, Object>();
            initData.put("start", start);
            initOptimizationTrigger.accept(initData);
        } else {
            logger.warn("initOptimizationBus is null");
        }
        resultStatus = NloptLibrary.nloptOptimize(nlopt, parameterPoint, functionResultPoint).value();

        resultParameters = parameterPoint.getDoubles();
        resultValue = functionResultPoint.getDouble();

        NloptLibrary.nloptDestroy(nlopt);
    }

    public Consumer<Map<String, Object>> getInitOptimizationTrigger() {
        return initOptimizationTrigger;
    }

    public void setInitOptimizationTrigger(Consumer<Map<String, Object>> initOptimizationTrigger) {
        this.initOptimizationTrigger = initOptimizationTrigger;
    }

    public double[] getInequalTolerents() {
        return inequalTolerents;
    }

    public void setInequalTolerents(double[] inequalTolerents) {
        this.inequalTolerents = inequalTolerents;
    }

    public double getObjectRelativeTolerent() {
        return objectRelativeTolerent;
    }

    public void setObjectRelativeTolerent(double objectRelativeTolerent) {
        this.objectRelativeTolerent = objectRelativeTolerent;
    }

    public double getObjectAbsoluteTolerence() {
        return objectAbsoluteTolerence;
    }

    public void setObjectAbsoluteTolerence(double objectAbsoluteTolerence) {
        this.objectAbsoluteTolerence = objectAbsoluteTolerence;
    }

    public double[] getResultParameters() {
        return resultParameters;
    }

    public double getResultValue() {
        return resultValue;
    }

    public NloptFuncWrapper getObject() {
        return object;
    }

    public NloptMFuncWrapper getInequalConstraints() {
        return inequalConstraints;
    }

    public void setObject(NloptFuncWrapper object) {
        this.object = object;
    }

    public void setInequalConstraints(NloptMFuncWrapper inequalConstraints) {
        this.inequalConstraints = inequalConstraints;
    }

    public double[] getStart() {
        return start;
    }

    public void setStart(double[] start) {
        this.start = start;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getResultStatus() {
        return resultStatus;
    }

}

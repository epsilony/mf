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
import net.epsilony.tb.nlopt.NloptLibrary;
import net.epsilony.tb.nlopt.NloptLibrary.NloptAlgorithm;
import net.epsilony.tb.nlopt.NloptLibrary.NloptFunc;
import net.epsilony.tb.nlopt.NloptLibrary.NloptMfunc;
import net.epsilony.tb.nlopt.NloptLibrary.NloptOpt;
import net.epsilony.tb.nlopt.NloptLibrary.NloptResult;

import org.bridj.IntValuedEnum;
import org.bridj.Pointer;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NloptMMADriver {
    private NloptOpt nlopt;
    private NloptFunc object;
    private NloptMfunc inequalConstraints;
    private int parameterSize;
    private int inequalConstraintsSize;
    private double[] inequalTolerents;
    private double objectRelativeTolerent = 1e-4;
    private double objectAbsoluteTolerence = 1e-6;
    private double[] start;
    private double[] resultParameters;
    private double resultValue;

    public NloptMMADriver(NloptFunc object, NloptMfunc inequalConstraints, int inequalConstraintSize,
            double[] inequalTolerents, double[] start) {
        this.parameterSize = start.length;
        this.object = object;
        this.inequalConstraints = inequalConstraints;
        this.inequalConstraintsSize = inequalConstraintSize;
        this.inequalTolerents = inequalTolerents;
        this.start = start;
        if (inequalTolerents.length != inequalConstraintSize) {
            throw new IllegalArgumentException();
        }
    }

    public NloptMMADriver() {
    }

    public void doOptimize() {
        nlopt = NloptLibrary.nloptCreate(NloptAlgorithm.NLOPT_LD_MMA, parameterSize);

        IntValuedEnum<NloptResult> nloptResult = nloptSetMinObjective(nlopt, Pointer.pointerTo(object), Pointer.NULL);
        if (!nloptResult.equals(NloptResult.NLOPT_SUCCESS)) {
            throw new IllegalStateException(nloptResult.toString());
        }

        nloptResult = NloptLibrary.nloptAddInequalityMconstraint(nlopt, inequalConstraintsSize,
                Pointer.pointerTo(inequalConstraints), Pointer.NULL, Pointer.pointerToDoubles(inequalTolerents));
        if (!nloptResult.equals(NloptResult.NLOPT_SUCCESS)) {
            throw new IllegalStateException(nloptResult.toString());
        }

        nloptSetXtolRel(nlopt, objectRelativeTolerent);
        NloptLibrary.nloptSetXtolAbs1(nlopt, objectAbsoluteTolerence);

        Pointer<Double> functionResultPoint = Pointer.pointerToDouble(0);
        Pointer<Double> parameterPoint = Pointer.pointerToDoubles(start);
        NloptLibrary.nloptOptimize(nlopt, parameterPoint, functionResultPoint);

        resultParameters = parameterPoint.getDoubles();
        resultValue = functionResultPoint.getDouble();

        NloptLibrary.nloptDestroy(nlopt);
    }

    public int getInequalConstraintsSize() {
        return inequalConstraintsSize;
    }

    public void setInequalConstraintsSize(int inequalConstraintsSize) {
        this.inequalConstraintsSize = inequalConstraintsSize;
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

    public void setObject(NloptFunc object) {
        this.object = object;
    }

    public void setInequalConstraints(NloptMfunc inequalConstraints) {
        this.inequalConstraints = inequalConstraints;
    }

    public double[] getStart() {
        return start;
    }

    public void setStart(double[] start) {
        this.start = start;
        parameterSize = start.length;
    }

}

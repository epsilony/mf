/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.Model2D;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.shape_func.MFShapeFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpleWeakformProject implements WeakformProject {

    public WeakformQuadratureTask weakformQuadratureTask;
    public Model2D model;
    public InfluenceRadiusCalculator influenceRadiusCalculator;
    public Assembler assembler;
    public MFShapeFunction shapeFunction;
    public ConstitutiveLaw constitutiveLaw;

    public SimpleWeakformProject(
            WeakformQuadratureTask project,
            Model2D model,
            InfluenceRadiusCalculator influenceRadCalc,
            Assembler assembler,
            MFShapeFunction shapeFunc,
            ConstitutiveLaw constitutiveLaw) {
        this.weakformQuadratureTask = project;
        this.model = model;
        this.influenceRadiusCalculator = influenceRadCalc;
        model.updateInfluenceAndSupportDomains(influenceRadiusCalculator);
        this.assembler = assembler;
        this.shapeFunction = shapeFunc;
        this.constitutiveLaw = constitutiveLaw;
    }

    @Override
    public WeakformQuadratureTask getWeakformQuadratureTask() {
        return weakformQuadratureTask;
    }

    @Override
    public Model2D getModel() {
        return model;
    }

    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return influenceRadiusCalculator;
    }

    @Override
    public Assembler getAssembler() {
        return assembler;
    }

    @Override
    public MFShapeFunction getShapeFunction() {
        return shapeFunction;
    }

    @Override
    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }
}

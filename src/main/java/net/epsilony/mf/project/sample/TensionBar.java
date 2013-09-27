/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import java.util.Arrays;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.cons_law.PlaneStress;
import static net.epsilony.mf.model.MFRectangleEdge.*;
import net.epsilony.mf.model.load.ConstantSegmentLoad;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.project.SimpMFMechanicalProject;
import net.epsilony.mf.util.MFConstants;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TensionBar extends AbstractRectangleProject {

    double tension = 2000;
    double E = 1000;
    double mu = 0.3;

    @Override
    protected ConstitutiveLaw genConstitutiveLaw() {
        return new PlaneStress(E, mu);
    }

    @Override
    protected void applyLoadsOnRectangle() {
        ConstantSegmentLoad leftLoad = new ConstantSegmentLoad();
        leftLoad.setLoad(new double[]{0, 0});
        leftLoad.setLoadValidity(new boolean[]{true, false});
        rect.setEdgeLoad(LEFT, leftLoad);

        ConstantSegmentLoad rightLoad = new ConstantSegmentLoad();
        rightLoad.setLoad(new double[]{tension, 0});
        rect.setEdgeLoad(RIGHT, rightLoad);

        ConstantSegmentLoad downLoad = new ConstantSegmentLoad();
        downLoad.setLoad(new double[]{0, 0});
        downLoad.setLoadValidity(new boolean[]{false, true});
        rect.setEdgeLoad(DOWN, downLoad);
    }

    public static void main(String[] args) {
        TensionBar tensionBar = new TensionBar();
        SimpMFMechanicalProject project = tensionBar.produce();
        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
        processor.setProject(project);
        processor.getSettings().put(MFConstants.KEY_ENABLE_MULTI_THREAD, false);
        processor.preprocess();
        processor.solve();
        PostProcessor pp = processor.genPostProcessor();
        MechanicalPostProcessor mpp = processor.genMechanicalPostProcessor();
        int stepNum = 20;
        for (int i = 0; i < stepNum; i++) {
            double[] pt = new double[]{2.99, 0.01 + 2.98 * (i * 1.0 / (stepNum - 1))};
            double[] strain = mpp.engineeringStrain(pt, null);
            double[] value = mpp.value(pt, null);
            System.out.println("pt = " + Arrays.toString(pt) + ", value = " + Arrays.toString(value) + ", eng strain = " + Arrays.toString(strain));
        }
    }
}

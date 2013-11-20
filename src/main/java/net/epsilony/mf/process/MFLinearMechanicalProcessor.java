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

package net.epsilony.mf.process;

import java.util.Map;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.mf.process.assembler.MechanicalVolumeAssembler;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFLinearMechanicalProcessor extends MFLinearProcessor {

    private ConstitutiveLaw constitutiveLaw;

    @Override
    protected void prepareAssemblersGroup(Map<MFProcessType, Assembler> assemblersGroup) {
        super.prepareAssemblersGroup(assemblersGroup);
        MechanicalVolumeAssembler meVolAssembler = (MechanicalVolumeAssembler) assemblersGroup
                .get(MFProcessType.VOLUME);
        meVolAssembler.setConstitutiveLaw(constitutiveLaw);
    }

    public MechanicalPostProcessor genMechanicalPostProcessor() {
        MechanicalPostProcessor result = new MechanicalPostProcessor();

        result.setConstitutiveLaw(constitutiveLaw);
        result.setMaxInfluenceRad(nodesInfluenceRadiusProcessor.getMaxNodesInfluenceRadius());
        result.setNodeValueDimension(analysisModel.getValueDimension());
        result.setShapeFunction(shapeFunction);
        result.setSupportDomainSearcher(nodesInfluenceRadiusProcessor.getSupportDomainSearcherFactory().produce());
        return result;
    }

    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    public static void main(String[] args) {
        // TimoshenkoBeamProjectFactory timo = genTimoshenkoProjectFactory();
        // SimpMFMechanicalProject project = (SimpMFMechanicalProject)
        // timo.produce();
        // MFLinearMechanicalProcessor processor = new
        // MFLinearMechanicalProcessor();
        // processor.setProject(project);
        // processor.getSettings().put(MFConstants.KEY_ENABLE_MULTI_THREAD,
        // false);
        // processor.preprocess();
        // processor.solve();
        //
        // PostProcessor pp = processor.genPostProcessor();
        // MechanicalPostProcessor mpp = processor.genMechanicalPostProcessor();
        // double[] engineeringStrain = mpp.engineeringStrain(new double[]{1,
        // 0}, null);
        // System.out.println("engineeringStrain = " +
        // Arrays.toString(engineeringStrain));
        // double[] expStrain = timo.getTimoBeam().strain(1, 0, null);
        // System.out.println("expStraint = " + Arrays.toString(expStrain));
        // double[] value = pp.value(new double[]{1, 0}, null);
        // System.out.println("value = " + Arrays.toString(value));
    }
}

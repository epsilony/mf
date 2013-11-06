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

package net.epsilony.mf.process.assembler;

import net.epsilony.mf.cons_law.RawConstitutiveLaw;
import static net.epsilony.mf.process.assembler.AssemblerTestUtils.*;
import net.epsilony.mf.util.matrix.MFMatries;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalVolumeAssemblerTest extends AssemblerTestTemplate<MechanicalVolumeAssembler, MechanicalVolumeAssemblerTest.MechanicalVolumeTestData> {

    private final String python_script = "mechanical_volume_assembler.py";

    @Override
    protected String getPythonScriptName() {
        return python_script;
    }

    public static class MechanicalVolumeTestData extends AssemblerTestData {

        public double[][] constitutiveLaw;

    }

    public MechanicalVolumeAssemblerTest() {
        super(MechanicalVolumeAssembler.class, MechanicalVolumeTestData.class);
    }

    @Override
    protected void initAssembler(MechanicalVolumeTestData data) {
        super.initAssembler(data);
        assembler.setConstitutiveLaw(new RawConstitutiveLaw(MFMatries.wrap(data.constitutiveLaw)));
    }

}

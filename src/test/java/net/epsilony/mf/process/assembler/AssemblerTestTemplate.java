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

import static net.epsilony.mf.process.assembler.AssemblerTestUtils.assertMatrixByDifference;
import static net.epsilony.mf.process.assembler.AssemblerTestUtils.copyTestMatrix;
import static net.epsilony.mf.process.assembler.AssemblerTestUtils.genRandomMatrix;
import static net.epsilony.mf.process.assembler.AssemblerTestUtils.getDataFromPythonScript;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.Random;

import net.epsilony.mf.process.assembler.AssemblerTestUtils.AssemblerTestData;
import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrix;
import net.epsilony.tb.RudeFactory;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <T>
 * @param <D>
 */
@Ignore
public abstract class AssemblerTestTemplate<D extends AssemblerTestData> {

    public Logger logger = LoggerFactory.getLogger(this.getClass());
    MFMatrix mainMatrix;
    MFMatrix mainMatrixBackup;
    MFMatrix mainVector;
    MFMatrix mainVectorBackup;
    Class<? extends Assembler> assemblerClass;
    Assembler assembler;
    Class<D> dataClass;
    Random rand = new Random(147);
    long randomSeed = 147;

    public AssemblerTestTemplate(Class<? extends Assembler> assemblerClass, Class<D> dataClass) {
        this.assemblerClass = assemblerClass;
        this.dataClass = dataClass;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAssemble() {
        D[] testDatas;
        testDatas = (D[]) getDataFromPythonScript(getPythonScriptName(), Array.newInstance(dataClass, 0).getClass());
        boolean hasData = false;
        for (D data : testDatas) {
            testByData(data);
            hasData = true;
        }
        assertTrue(hasData);
    }

    protected abstract String getPythonScriptName();

    protected void testByData(D data) {
        logger.info("test value dimension {}", data.valueDimension);
        initMainMatrixVector(data);
        initAssembler(data);
        setMainMatrixVectorToAssembler();
        backupMainMatrixVector();
        logger.info("inited {}", assembler);
        setAssembler(data);
        assembler.assemble();
        assertDifference(data);
        logger.info("tested");
    }

    protected void initMainMatrixVector(D data) {
        int mainMatrixSize = getMainMatrixSize(data);
        mainMatrix = genRandomMatrix(mainMatrixSize, mainMatrixSize, getRandom());
        mainVector = genRandomMatrix(mainMatrixSize, 1, getRandom());
    }

    protected Random getRandom() {
        return rand;
    }

    protected int getMainMatrixSize(D data) {
        int mainMatrixSize = data.allNodesSize * data.valueDimension;
        return mainMatrixSize;
    }

    protected void backupMainMatrixVector() {
        mainMatrixBackup = copyTestMatrix(mainMatrix);
        mainVectorBackup = copyTestMatrix(mainVector);
    }

    protected void initAssembler(D data) {
        assembler = new RudeFactory<>(assemblerClass).produce();
        assembler.setAllNodesNum(data.allNodesSize);
        assembler.setSpatialDimension(data.spatialDimension);
        assembler.setValueDimension(data.valueDimension);

    }

    protected void setMainMatrixVectorToAssembler() {
        assembler.setMainMatrix(mainMatrix);
        assembler.setMainVector(mainVector);
    }

    protected void setAssembler(D data) {
        AssemblerTestUtils.setupAssembler(assembler, data);
    }

    protected void assertDifference(D data) {
        MFMatrix mainMatrixDifference = data.mainMatrixDifference == null ? null : MFMatries
                .wrap(data.mainMatrixDifference);
        MFMatrix mainVectorDifference = data.mainVectorDifference == null ? null : MFMatries
                .wrap(data.mainVectorDifference);
        assertMatrixByDifference(mainMatrixBackup, mainMatrix, mainMatrixDifference);
        assertMatrixByDifference(mainVectorBackup, mainVector, mainVectorDifference);
    }
}

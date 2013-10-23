/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assembler;

import java.lang.reflect.Array;
import java.util.Random;
import net.epsilony.mf.util.matrix.MFMatries;
import net.epsilony.mf.util.matrix.MFMatrix;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static net.epsilony.mf.process.assembler.AssemblerTestUtils.*;
import net.epsilony.tb.RudeFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 * @param <T>
 * @param <D>
 */
@Ignore
public abstract class AssemblerTestTemplate<T extends Assembler, D extends AssemblerTestData> {

    public Logger logger = LoggerFactory.getLogger(this.getClass());
    MFMatrix mainMatrix;
    MFMatrix mainMatrixBackup;
    MFMatrix mainVector;
    MFMatrix mainVectorBackup;
    Class<T> assemblerClass;
    T assembler;
    Class<D> dataClass;
    Random rand = new Random(147);
    long randomSeed = 147;

    public AssemblerTestTemplate(Class<T> assemblerClass, Class<D> dataClass) {
        this.assemblerClass = assemblerClass;
        this.dataClass = dataClass;
    }

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
        assembler.setNodesNum(data.allNodesSize);
        assembler.setSpatialDimension(data.spatialDimension);
        assembler.setValueDimension(data.valueDimension);

    }

    protected void setMainMatrixVectorToAssembler() {
        assembler.setMainMatrix(mainMatrix);
        assembler.setMainVector(mainVector);
    }

    protected void setAssembler(D data) {
        setupAssembler(assembler, data);
    }

    protected void assertDifference(D data) {
        MFMatrix mainMatrixDifference = data.mainMatrixDifference == null ? null : MFMatries.wrap(data.mainMatrixDifference);
        MFMatrix mainVectorDifference = data.mainVectorDifference == null ? null : MFMatries.wrap(data.mainVectorDifference);
        assertMatrixByDifference(mainMatrixBackup, mainMatrix, mainMatrixDifference);
        assertMatrixByDifference(mainVectorBackup, mainVector, mainVectorDifference);
    }
}

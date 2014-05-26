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
package net.epsilony.mf.opt.nlopt.config;

import static org.apache.commons.math3.util.FastMath.pow;
import static org.apache.commons.math3.util.FastMath.sqrt;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import net.epsilony.mf.opt.config.OptPersistBaseConfig;
import net.epsilony.mf.opt.nlopt.NloptMMADriver;
import net.epsilony.mf.opt.persist.OptRootRecorder;
import net.epsilony.mf.util.MFBeanUtils;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class NloptConfigTest {

    @Test
    public void test() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(NloptConfig.class);
        NloptMMADriver driver = ac.getBean(NloptMMADriver.class);
        NloptHub nloptHub = ac.getBean(NloptHub.class);
        SampleOptFunctions function = new SampleOptFunctions();
        MFBeanUtils.transmitProperties(function, nloptHub);
        driver.setStart(new double[] { 1.234, 5.678 });
        driver.doOptimize();
        double[] resultParameters = driver.getResultParameters();
        double result = driver.getResultValue();

        double[] expParamters = new double[] { 1 / 3.0, 8 / 27.0 };
        double expValue = sqrt(8 / 27.0);

        assertEquals(expValue, result, 1e-6);
        assertArrayEquals(expParamters, resultParameters, 1e-6);

        ac.close();
    }

    @Test
    public void testWithPersist() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(NloptConfig.class,
                OptPersistBaseConfig.class, NloptPersistConfig.class);
        NloptMMADriver driver = ac.getBean(NloptMMADriver.class);
        NloptHub nloptHub = ac.getBean(NloptHub.class);
        SampleOptFunctions function = new SampleOptFunctions();
        MFBeanUtils.transmitProperties(function, nloptHub);

        driver.setStart(new double[] { 1.234, 5.678 });
        driver.doOptimize();

        OptRootRecorder rootRecorder = ac.getBean(OptRootRecorder.class);
        ObjectId currentId = rootRecorder.getCurrentId();
        DBCollection objCollection = ac.getBean(NloptPersistConfig.OBJECT_DB_COLLECTION, DBCollection.class);
        DBCollection ineqCollection = ac.getBean(NloptPersistConfig.INEQUAL_CONSTRAINTS_DB_COLLECTION,
                DBCollection.class);

        DBCursor objCurser = objCollection.find(new BasicDBObject("upId", currentId)).sort(
                new BasicDBObject("refIndex", 1));
        DBCursor ineqCurser = ineqCollection.find(new BasicDBObject("upId", currentId)).sort(
                new BasicDBObject("refIndex", 1));
        int index = 0;
        while (objCurser.hasNext()) {
            DBObject objDbo = objCurser.next();
            DBObject ineqDbo = ineqCurser.next();
            assertArrayObjectEquals(function.objectParameterRecord.get(index), objDbo.get("parameter"));
            assertEquals(function.objectRecord.get(index), (double) objDbo.get("result"), 0);
            assertArrayObjectEquals(function.objectGradRecord.get(index), objDbo.get("gradient"));

            assertArrayObjectEquals(function.inequalParameterRecord.get(index), ineqDbo.get("parameter"));
            assertArrayObjectEquals(function.inequalRecord.get(index), ineqDbo.get("result"));
            @SuppressWarnings("unchecked")
            List<List<Double>> actGradients = (List<List<Double>>) ineqDbo.get("gradient");
            double[][] expGradients = function.inequalGradRecord.get(index);
            assertEquals(expGradients.length, actGradients.size());
            for (int i = 0; i < expGradients.length; i++) {
                assertArrayObjectEquals(expGradients[i], actGradients.get(i));
            }
            index++;
        }
        ac.close();
    }

    private void assertArrayObjectEquals(double[] exp, Object act) {
        @SuppressWarnings("unchecked")
        List<Double> actList = (List<Double>) act;
        int i = 0;
        for (Double d : actList) {
            assertEquals(exp[i++], d, 0);
        }
        assertEquals(exp.length, actList.size());
    }

    public static class SampleOptFunctions {

        private double object;
        private double[] objectGradient;
        private boolean objectTriggered = false;

        private double[] inequalValues;
        private double[][] inequalGradients;
        private boolean inequalTriggered = false;

        public final ArrayList<double[]> objectParameterRecord = new ArrayList<>();
        public final ArrayList<Double> objectRecord = new ArrayList<>();
        public final ArrayList<double[]> objectGradRecord = new ArrayList<>();
        public final ArrayList<double[]> inequalParameterRecord = new ArrayList<>();
        public final ArrayList<double[]> inequalRecord = new ArrayList<>();
        public final ArrayList<double[][]> inequalGradRecord = new ArrayList<>();

        public void applyObjectParameter(double[] parameter) {
            double x2 = parameter[1];
            object = sqrt(x2);
            objectGradient = new double[] { 0.0, 0.5 / object };
            objectTriggered = false;

            objectParameterRecord.add(parameter.clone());
            objectRecord.add(object);
            objectGradRecord.add(objectGradient);
        }

        public double objectValue() {
            assertTrue(objectTriggered);
            return object;
        }

        public double[] objectGradient() {
            return objectGradient;
        }

        public void applyInequalConstraintsParameter(double[] parameter) {
            double x1 = parameter[0];
            double x2 = parameter[1];
            double a1 = 2, b1 = 0, a2 = -1, b2 = 1;
            inequalValues = new double[] { -x2, pow(a1 * x1 + b1, 3) - x2, pow(a2 * x1 + b2, 3) - x2 };
            inequalGradients = new double[][] { { 0, -1 }, { pow(a1 * x1 + b1, 2) * 3 * a1, -1 },
                    { pow(a2 * x1 + b2, 2) * 3 * a2, -1 } };

            inequalParameterRecord.add(parameter.clone());
            inequalRecord.add(inequalValues);
            inequalGradRecord.add(inequalGradients);
        }

        public double[] inequalConstraintsValues() {
            assertTrue(inequalTriggered);
            return inequalValues;
        }

        public double[][] inequalConstraintsGradients() {
            return inequalGradients;
        }

        public int inequalConstraintsSize() {
            return 3;
        }

        public void optPrepare(Object data) {
        }

        public Consumer<double[]> getObjectParameterConsumer() {
            return this::applyObjectParameter;
        }

        public Consumer<Object> getObjectCalculateTrigger() {
            return obj -> {
                objectTriggered = true;
            };
        }

        public Consumer<double[]> getInequalConstraintsParameterConsumer() {
            return this::applyInequalConstraintsParameter;
        }

        public Consumer<Object> getInequalConstraintsCalculateTrigger() {
            return obj -> {
                inequalTriggered = true;
            };
        }

        public DoubleSupplier getObjectValueSupplier() {
            return this::objectValue;
        }

        public Supplier<double[]> getObjectGradientSupplier() {
            return this::objectGradient;
        }

        public List<DoubleSupplier> getInequalConstraintsValueSuppliers() {
            List<DoubleSupplier> result = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                int index = i;
                result.add(() -> inequalConstraintsValues()[index]);
            }
            return result;
        }

        public List<Supplier<double[]>> getInequalConstraintsGradientSuppliers() {
            List<Supplier<double[]>> result = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                int index = i;
                result.add(() -> inequalConstraintsGradients()[index]);
            }

            return result;
        }

    }

}

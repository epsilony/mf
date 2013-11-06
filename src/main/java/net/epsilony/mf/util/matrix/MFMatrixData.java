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

package net.epsilony.mf.util.matrix;

import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import no.uib.cipr.matrix.MatrixEntry;
import org.ejml.data.DenseMatrix64F;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFMatrixData implements Serializable {

    int numRows;
    int numCols;
    Class matrixClass;
    List<MatrixEntry> matrixEntries;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "num_rows", nullable = false)
    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Class getMatrixClass() {
        return matrixClass;
    }

    public void setMatrixClass(Class matrixClass) {
        this.matrixClass = matrixClass;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public List<MatrixEntry> getMatrixEntries() {
        return matrixEntries;
    }

    public void setMatrixEntries(List<MatrixEntry> matrixEntries) {
        this.matrixEntries = matrixEntries;
    }

    @Override
    public String toString() {
        return "MFMatrixData{" + "numRows=" + numRows + ", numCols=" + numCols + ", matrixClass=" + matrixClass + ", id=" + id + '}';
    }

    public static void main(String[] args) {

        Configuration conf = new Configuration();

        conf.configure();
        //!must add prefix hibernate before any property names
        conf.setProperty("hibernate.connection.url", "jdbc:sqlite:target/MFMatrixData_demo.sqlite");
        conf.setProperty("hibernate.hbm2ddl.auto", "create");

        ServiceRegistry buildServiceRegistry = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        SessionFactory factory = conf.buildSessionFactory(buildServiceRegistry);
        DenseMatrix64F denseMatrix64F = new DenseMatrix64F(3, 3);
        denseMatrix64F.data = new double[]{11, 12, 13, 21, 22, 23, 31, 32, 33};
        WrapperMFMatrix<DenseMatrix64F> wrap = MFMatries.wrap(denseMatrix64F);
        MFMatrixData data = wrap.genMatrixData();

        Session session = factory.openSession();
        session.beginTransaction();
        session.save(data);
        session.getTransaction().commit();

        session.close();

        session = factory.openSession();
        MFMatrixData mat = (MFMatrixData) session.get(MFMatrixData.class, data.id);
        session.close();
        System.out.println("mat = " + mat);
    }
}

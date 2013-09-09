/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import net.epsilony.mf.util.matrix.wrapper.WrapperMFMatrix;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Matrix64F;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Entity
public class MFMatrixData implements Serializable {

    int numRows;
    int numCols;
    Class matrixClass;
    List<RawMatrixEntry> matrixEntries;
    int id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MFMatrixData_id")
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

    @Column(name = "num_cols", nullable = false)
    public int getNumCols() {
        return numCols;
    }

    @Column(name = "matrix_class", nullable = false)
    public Class getMatrixClass() {
        return matrixClass;
    }

    public void setMatrixClass(Class matrixClass) {
        this.matrixClass = matrixClass;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn()
    public List<RawMatrixEntry> getMatrixEntries() {
        return matrixEntries;
    }

    public void setMatrixEntries(List<RawMatrixEntry> matrixEntries) {
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
        WrapperMFMatrix<Matrix64F> wrap = MFMatries.wrap(denseMatrix64F);
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

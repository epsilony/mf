/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.point;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Entity
public class SimpMFRawIntegratePoint implements MFRawIntegratePoint, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    double[] coord;
    double weight;
    int dimension;

    @Type(type = "net.epsilony.mf.util.persistence.Coord3DType")
    @Columns(columns = {
        @Column(name = "x"),
        @Column(name = "y"),
        @Column(name = "z")})
    @Override
    public double[] getCoord() {
        return coord;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public void setCoord(double[] coord) {
        this.coord = coord;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
}

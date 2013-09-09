/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.junit.Ignore;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Ignore
@Entity
public class Coord3DTestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    @Basic
    String name;
    @Columns(columns = {
        @Column(name = "x"),
        @Column(name = "y"),
        @Column(name = "z")})
    @Type(type = "net.epsilony.mf.util.persistence.Coord3DType")
    double[] coord;

    public Coord3DTestEntity(String name, double[] coord) {
        this.name = name;
        this.coord = coord;
    }

    public Coord3DTestEntity() {
    }
}

/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Entity
public class HibernateDemoItem implements Serializable {

    static long maxId = 1;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    @Basic
    String name;
    @Column(name = "item_value")
    double[] value;

    public HibernateDemoItem(String name, double[] value) {
        this.name = name;
        this.value = value;
    }

    public HibernateDemoItem() {
    }
}

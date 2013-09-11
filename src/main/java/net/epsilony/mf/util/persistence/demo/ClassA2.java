/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence.demo;

import net.epsilony.tb.IntIdentity;
import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ClassA2 implements InterfaceA, IntIdentity {

    public ClassA2() {
    }

    public ClassA2(double a) {
        this.a = a;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public double getA() {
        return a;
    }

    @Override
    public void setA(double a) {
        this.a = a;
    }
    int id;
    double a;

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this) + "{" + "id=" + id + ", a=" + a + '}';
    }
}

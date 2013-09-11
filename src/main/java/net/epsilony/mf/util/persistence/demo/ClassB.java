/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence.demo;

import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ClassB extends ClassA {

    double b;

    public ClassB(double b, double a) {
        super(a);
        this.b = b;
    }

    public ClassB() {
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this) + "{" + "b=" + b + '}';
    }
}

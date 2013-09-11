/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.persistence.demo;

import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ClassB2 extends ClassA2 {

    double b;

    public ClassB2(double b, double a) {
        super(a);
        this.b = b;
    }

    public ClassB2() {
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this) + "{" + "b=" + b + '}';
    }
}

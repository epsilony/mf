/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.fraction;

import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractFractionBuilder implements FractionBuilder {

    protected Segment segment;
    protected double lengthCap;
    protected double diviationCap;
    protected Factory<? extends Node> nodeFactory;

    @Override
    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    @Override
    public double getLengthCap() {
        return lengthCap;
    }

    @Override
    public void setLengthCap(double lengthCap) {
        this.lengthCap = lengthCap;
    }

    @Override
    public double getDiviationCap() {
        return diviationCap;
    }

    @Override
    public void setDiviationCap(double diviationCap) {
        this.diviationCap = diviationCap;
    }

    @Override
    public void setNodeFactory(Factory<? extends Node> nodeFactory) {
        this.nodeFactory = nodeFactory;
    }
}

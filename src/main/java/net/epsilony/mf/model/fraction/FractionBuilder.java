/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.fraction;

import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface FractionBuilder {

    void setSegment(Segment segment);

    void setLengthCap(double lengthCap);

    void setDiviationCap(double diviationCap);

    void setNodeFactory(Factory<? extends Node> nodeFactory);

    void fractionize();

    double getDiviationCap();

    double getLengthCap();
}

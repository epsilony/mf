/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.fraction;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Segment;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MultiTypeFractionBuilder extends AbstractFractionBuilder {

    Map<Class<? extends Segment>, FractionBuilder> fractionBuilderMap = new HashMap<>();

    public MultiTypeFractionBuilder() {
        initDefaultMap();
    }

    private void initDefaultMap() {
        fractionBuilderMap.put(Line.class, new LineFractionBuilder());
    }

    @Override
    public void fractionize() {
        FractionBuilder builder = fractionBuilderMap.get(segment.getClass());
        builder.setDiviationCap(diviationCap);
        builder.setLengthCap(lengthCap);
        builder.setNodeFactory(nodeFactory);
        builder.setSegment(segment);
        builder.fractionize();
    }
}

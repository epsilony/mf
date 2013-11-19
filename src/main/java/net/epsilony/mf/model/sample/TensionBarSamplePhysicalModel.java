/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.epsilony.mf.model.sample;

import static net.epsilony.mf.model.MFRectangleEdge.DOWN;
import static net.epsilony.mf.model.MFRectangleEdge.LEFT;
import static net.epsilony.mf.model.MFRectangleEdge.RIGHT;
import static net.epsilony.mf.model.MFRectangleEdge.UP;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.cons_law.PlaneStress;
import net.epsilony.mf.model.RectanglePhysicalModel;
import net.epsilony.mf.model.load.ConstantSegmentLoad;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class TensionBarSamplePhysicalModel extends RectanglePhysicalModel {
    public static final int VALUE_DIMENSION = 2;

    double tension = 2000;
    double E = 1000;
    double mu = 0.3;
    public static final double DEFAULT_LEFT = 0;
    public static final double DEFAULT_RIGHT = 10;
    public static final double DEFAULT_DOWN = 0;
    public static final double DEFAULT_UP = 6;

    public TensionBarSamplePhysicalModel() {
        super();
        setValueDimension(VALUE_DIMENSION);
        initEdgePositions();
        applyLoadsOnRectangle();
    }

    private void initEdgePositions() {
        setEdgePosition(DOWN, DEFAULT_DOWN);
        setEdgePosition(LEFT, DEFAULT_LEFT);
        setEdgePosition(RIGHT, DEFAULT_RIGHT);
        setEdgePosition(UP, DEFAULT_UP);
    }

    protected void applyLoadsOnRectangle() {
        ConstantSegmentLoad leftLoad = new ConstantSegmentLoad();
        leftLoad.setValue(new double[] { 0, 0 });
        leftLoad.setValidity(new boolean[] { true, false });
        setEdgeLoad(LEFT, leftLoad);

        ConstantSegmentLoad rightLoad = new ConstantSegmentLoad();
        rightLoad.setValue(new double[] { tension, 0 });
        setEdgeLoad(RIGHT, rightLoad);

        ConstantSegmentLoad downLoad = new ConstantSegmentLoad();
        downLoad.setValue(new double[] { 0, 0 });
        downLoad.setValidity(new boolean[] { false, true });
        setEdgeLoad(DOWN, downLoad);
    }

    public ConstitutiveLaw getConstitutiveLaw() {
        return new PlaneStress(E, mu);
    }
}

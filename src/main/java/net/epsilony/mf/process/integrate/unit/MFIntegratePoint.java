/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate.unit;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegratePoint extends MFIntegrateUnit {

    double getWeight();

    double[] getCoord();

    double[] getLoad();

    boolean[] getLoadValidity();
}

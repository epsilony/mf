/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project;

import net.epsilony.mf.process.integrate.OneDIntegrateTask;
import net.epsilony.tb.Factory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class OneDPoissonProjectFactory implements Factory<MFProject> {

    OneDIntegrateTask integrateTask = new OneDIntegrateTask();
    int quadratureDegree;

    @Override
    public MFProject produce() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

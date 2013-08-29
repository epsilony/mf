/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.integrate;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface MFIntegrator extends Runnable {

    void processVolume();

    void processNeumann();

    void processDirichlet();
}

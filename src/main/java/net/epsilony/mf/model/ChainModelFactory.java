/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.Iterator;
import java.util.LinkedList;
import net.epsilony.mf.model.fraction.MultiTypeFractionBuilder;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.tb.Factory;
import net.epsilony.tb.RudeFactory;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.SegmentIterable;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ChainModelFactory implements Factory<AnalysisModel> {

    double fractionLengthCap;
    boolean genVolumeSubdomains = true;
    ChainPhM chainPhM;
    RawAnalysisModel analysisModel;

    @Override
    public AnalysisModel produce() {
        initAnalysisModel();
        genFractionizePhM();
        genSpaceNodes();
        return analysisModel;
    }

    private void initAnalysisModel() {
        analysisModel = new RawAnalysisModel();
        analysisModel.setPhysicalModel(chainPhM);
    }

    private void genFractionizePhM() {

        MultiTypeFractionBuilder fractionBuilder = new MultiTypeFractionBuilder();
        fractionBuilder.setLengthCap(fractionLengthCap);
        fractionBuilder.setDiviationCap(Double.POSITIVE_INFINITY);
        fractionBuilder.setNodeFactory(new RudeFactory<>(MFNode.class));


        Chain chain = chainPhM.getChain();
        Chain newChain = SerializationUtils.clone(chain);


        RawPhysicalModel fractionizedModel = new RawPhysicalModel();
        fractionizedModel.setGeomRoot(newChain);
        fractionizedModel.setVolumeLoad(chainPhM.getVolumeLoad());
        fractionizedModel.setDimension(chainPhM.getDimension());

        Iterator<Segment> iterator = chain.iterator();
        Iterator<Segment> newIter = newChain.iterator();
        while (iterator.hasNext()) {
            Segment seg = iterator.next();
            MFLoad segLoad = chainPhM.getLoadMap().get(seg);
            MFLoad startLoad = chainPhM.getLoadMap().get(seg.getStart());
            Segment newSeg = newIter.next();
            Segment formerSucc = newSeg.getSucc();
            newSeg.setStart(new MFNode(newSeg.getStart().getCoord()));
            fractionBuilder.setSegment(newSeg);
            fractionBuilder.fractionize();
            if (null == segLoad && startLoad == null) {
                continue;
            }
            for (Segment fracSeg : new SegmentIterable<>(newSeg)) {
                if (fracSeg == formerSucc) {
                    break;
                }
                if (null != segLoad) {
                    fractionizedModel.getLoadMap().put(fracSeg, segLoad);
                }
                if (null != startLoad) {
                    fractionizedModel.getLoadMap().put(fracSeg.getStart(), startLoad);
                }
            }
        }

        analysisModel.setFractionizedModel(fractionizedModel);
    }

    private void genSpaceNodes() {
        Chain chain = (Chain) analysisModel.getFractionizedModel().getGeomRoot();
        Iterator<Segment> iterator = chain.iterator();
        iterator.next();
        LinkedList<MFNode> spaceNodes = new LinkedList<>();
        while (iterator.hasNext()) {
            spaceNodes.add((MFNode) iterator.next().getStart());
        }
        analysisModel.setSpaceNodes(spaceNodes);
    }

    public ChainPhM getOneLinePhM() {
        return chainPhM;
    }

    public void setOneLinePhM(ChainPhM oneLinePhM) {
        this.chainPhM = oneLinePhM;
    }

    public double getFractionLengthCap() {
        return fractionLengthCap;
    }

    public void setFractionLengthCap(double fractionLengthCap) {
        this.fractionLengthCap = fractionLengthCap;
    }

    public boolean isGenSubdomains() {
        return genVolumeSubdomains;
    }

    public void setGenVolumeSubdomains(boolean genVolumeSubdomains) {
        this.genVolumeSubdomains = genVolumeSubdomains;
    }
}

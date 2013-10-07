/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.epsilony.mf.model.fraction.MultiTypeFractionBuilder;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.tb.Factory;
import net.epsilony.tb.RudeFactory;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.GeomUnit;
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
        genSubdomains();
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
        fractionizedModel.setLoadMap(new HashMap());
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
            if (null != newSeg.getSucc()) {
                fractionBuilder.setSegment(newSeg);
                fractionBuilder.fractionize();
            }
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

    private void genSubdomains() {
        if (!genVolumeSubdomains) {
            return;
        }
        Chain chain = (Chain) analysisModel.getFractionizedModel().getGeomRoot();
        List<MFSubdomain> segSubdomains = new LinkedList<>();
        List<MFSubdomain> nodeSubdomains = new LinkedList<>();
        Map<GeomUnit, MFLoad> loadMap = analysisModel.getFractionizedModel().getLoadMap();
        for (Segment seg : chain) {
            if (null != seg.getSucc()) {
                SegmentSubdomain segmentSubdomain = new SegmentSubdomain();
                segmentSubdomain.setSegment(seg);
                segSubdomains.add(segmentSubdomain);
            }
            MFNode start = (MFNode) seg.getStart();
            MFLoad load = loadMap.get(seg);
            if (null != load) {
                nodeSubdomains.add(new MFNodeSubdomain(start));
            }
        }
        analysisModel.setSubdomains(1, segSubdomains);
        analysisModel.setSubdomains(0, nodeSubdomains);
    }

    public ChainPhM getChainPhM() {
        return chainPhM;
    }

    public void setChainPhM(ChainPhM chainPhM) {
        this.chainPhM = chainPhM;
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

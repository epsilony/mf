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

package net.epsilony.mf.model.factory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.ChainPhysicalModel;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.RawAnalysisModel;
import net.epsilony.mf.model.fraction.MultiTypeFractionBuilder;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.process.MFProcessType;
import net.epsilony.mf.process.integrate.unit.GeomUnitSubdomain;
import net.epsilony.mf.process.integrate.unit.MFIntegrateUnit;
import net.epsilony.mf.process.integrate.unit.NodeIntegrateUnit;
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
public class ChainAnalysisModelFactory implements Factory<AnalysisModel> {

    double fractionLengthCap;
    boolean genVolumeSubdomains = true;
    ChainPhysicalModel chainPhM;
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
        analysisModel.setOrigin(chainPhM);
    }

    private void genFractionizePhM() {

        MultiTypeFractionBuilder fractionBuilder = new MultiTypeFractionBuilder();
        fractionBuilder.setLengthCap(fractionLengthCap);
        fractionBuilder.setDiviationCap(Double.POSITIVE_INFINITY);
        fractionBuilder.setNodeFactory(new RudeFactory<>(MFNode.class));

        Chain chain = chainPhM.getChain();
        Chain newChain = SerializationUtils.clone(chain);

        analysisModel.setGeomRoot(newChain);
        analysisModel.setLoadMap(new HashMap<GeomUnit, MFLoad>());
        analysisModel.setVolumeLoad(chainPhM.getVolumeLoad());
        analysisModel.setSpatialDimension(chainPhM.getSpatialDimension());

        Iterator<Segment> iterator = chain.iterator();
        Iterator<Segment> newIter = newChain.iterator();
        while (iterator.hasNext()) {
            Segment seg = iterator.next();
            MFLoad segLoad = chainPhM.getLoadMap().get(seg);

            Segment newSeg = newIter.next();
            Segment formerSucc = newSeg.getSucc();

            newSeg.setStart(new MFNode(newSeg.getStart().getCoord()));
            MFLoad startLoad = chainPhM.getLoadMap().get(seg.getStart());
            if (null != startLoad) {
                analysisModel.getLoadMap().put(newSeg.getStart(), startLoad);
            }

            if (null == newSeg.getSucc()) {
                continue;
            }
            fractionBuilder.setSegment(newSeg);
            fractionBuilder.fractionize();
            if (null == segLoad) {
                continue;
            }
            for (Segment fracSeg : new SegmentIterable<>(newSeg)) {
                if (fracSeg == formerSucc) {
                    break;
                }
                analysisModel.getLoadMap().put(fracSeg, segLoad);
            }
        }
    }

    private void genSpaceNodes() {
        Chain chain = (Chain) analysisModel.getGeomRoot();
        Iterator<Segment> iterator = chain.iterator();
        iterator.next();
        LinkedList<MFNode> spaceNodes = new LinkedList<>();
        while (iterator.hasNext()) {
            spaceNodes.add((MFNode) iterator.next().getStart());
        }
        spaceNodes.removeLast();
        analysisModel.setSpaceNodes(spaceNodes);
    }

    private void genSubdomains() {
        if (!genVolumeSubdomains) {
            return;
        }
        Chain chain = (Chain) analysisModel.getGeomRoot();
        List<MFIntegrateUnit> segSubdomains = new LinkedList<>();
        List<MFIntegrateUnit> dirichlet = new LinkedList<>();
        List<MFIntegrateUnit> neumann = new LinkedList<>();
        Map<GeomUnit, MFLoad> loadMap = analysisModel.getLoadMap();
        for (Segment seg : chain) {
            if (null != seg.getSucc()) {
                GeomUnitSubdomain segmentSubdomain = new GeomUnitSubdomain();
                segmentSubdomain.setGeomUnit(seg);
                segSubdomains.add(segmentSubdomain);
            }
            MFNode start = (MFNode) seg.getStart();
            MFLoad load = loadMap.get(start);
            if (null != load) {
                NodeLoad nl = (NodeLoad) load;
                if (nl.isDirichlet()) {
                    dirichlet.add(new NodeIntegrateUnit(start));
                } else {
                    neumann.add(new NodeIntegrateUnit(start));
                }
            }
        }
        analysisModel.setIntegrateUnits(MFProcessType.VOLUME, segSubdomains);
        analysisModel.setIntegrateUnits(MFProcessType.DIRICHLET, dirichlet);
        analysisModel.setIntegrateUnits(MFProcessType.NEUMANN, neumann);
    }

    public ChainPhysicalModel getChainPhM() {
        return chainPhM;
    }

    public void setChainPhM(ChainPhysicalModel chainPhM) {
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

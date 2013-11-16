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

import java.util.Arrays;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.ChainPhysicalModel;
import net.epsilony.mf.model.load.NodeLoad;
import net.epsilony.mf.model.load.SegmentLoad;
import net.epsilony.tb.Factory;
import net.epsilony.tb.solid.Chain;
import net.epsilony.tb.solid.Node;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class OneDLineChainAnalysisFactory implements Factory<AnalysisModel> {

    double start = 0;
    double end = 1;
    NodeLoad startLoad, endLoad;
    SegmentLoad volumeLoad;
    int nodesNum;

    @Override
    public AnalysisModel produce() {
        Chain chain = Chain.byNodesChain(
                Arrays.asList(new Node(new double[] { start, 0 }), new Node(new double[] { end, 0 })), false);
        ChainPhysicalModel chainPhM = new ChainPhysicalModel();
        chainPhM.setChain(chain);
        chainPhM.setVolumeLoad(volumeLoad);
        chainPhM.getLoadMap().put(chain.getHead().getStart(), startLoad);
        chainPhM.getLoadMap().put(chain.getLast().getStart(), endLoad);

        ChainAnalysisModelFactory chainModelFactory = new ChainAnalysisModelFactory();
        chainModelFactory.setChainPhM(chainPhM);
        chainModelFactory.setFractionLengthCap((end - start) / (nodesNum - 1.1));

        return chainModelFactory.produce();
    }

    public void setStart(double start) {
        this.start = start;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public void setStartLoad(NodeLoad startLoad) {
        this.startLoad = startLoad;
    }

    public void setEndLoad(NodeLoad endLoad) {
        this.endLoad = endLoad;
    }

    public void setVolumeLoad(SegmentLoad volumeLoad) {
        this.volumeLoad = volumeLoad;
    }

    public void setNodesNum(int nodesNum) {
        this.nodesNum = nodesNum;
    }
}

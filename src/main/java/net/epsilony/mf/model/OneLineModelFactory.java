/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.mf.model.load.MFLoad;
import net.epsilony.tb.Factory;
import net.epsilony.tb.RudeFactory;
import net.epsilony.tb.solid.GeomUnit;
import net.epsilony.tb.solid.Line;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class OneLineModelFactory implements Factory<AnalysisModel> {

    int fractionNum;
    boolean genSpaceNodes = true;
    boolean genSubdomains = true;
    OneLinePhM oneLinePhM;

    @Override
    public AnalysisModel produce() {
        RawAnalysisModel result = new RawAnalysisModel();
        result.setDimension(oneLinePhM.getDimension());
        
        Line line = new Line(oneLinePhM.getVertex(true));
        line.setSucc(new Line(oneLinePhM.getVertex(false)));

        line.fractionize(fractionNum, new RudeFactory<>(MFNode.class));
        result.setGeomRoot(line);
        Map<GeomUnit, MFLoad> formerMap = oneLinePhM.getLoadMap();
        Map<GeomUnit, MFLoad> loadMap = new HashMap<>();
        loadMap.put(oneLinePhM.getVertex(true), formerMap.get(oneLinePhM.getVertex(true)));
        loadMap.put(oneLinePhM.getVertex(false), formerMap.get(oneLinePhM.getVertex(false)));
        result.setLoadMap(loadMap);
        result.setVolumeLoad(oneLinePhM.getVolumeLoad());
        return result;
    }

    public OneLinePhM getOneLinePhM() {
        return oneLinePhM;
    }

    public void setOneLinePhM(OneLinePhM oneLinePhM) {
        this.oneLinePhM = oneLinePhM;
    }

    public int getFractionNum() {
        return fractionNum;
    }

    public void setFractionNum(int fractionNum) {
        this.fractionNum = fractionNum;
    }

    public boolean isGenSpaceNodes() {
        return genSpaceNodes;
    }

    public void setGenSpaceNodes(boolean genSpaceNodes) {
        this.genSpaceNodes = genSpaceNodes;
    }

    public boolean isGenSubdomains() {
        return genSubdomains;
    }

    public void setGenSubdomains(boolean genSubdomains) {
        this.genSubdomains = genSubdomains;
    }
}

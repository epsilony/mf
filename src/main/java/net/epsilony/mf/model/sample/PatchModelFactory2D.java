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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.epsilony.mf.integrate.unit.IntegrateUnitsGroup;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.MFRectangle;
import net.epsilony.mf.model.RawAnalysisModel;
import net.epsilony.mf.model.geom.MFFacet;
import net.epsilony.mf.model.geom.MFLine;
import net.epsilony.mf.model.geom.SimpMFLine;
import net.epsilony.mf.model.geom.util.MFFacetFactory;
import net.epsilony.mf.model.load.GeomPointLoad;
import net.epsilony.mf.util.math.PartialTuple;
import net.epsilony.tb.solid.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public abstract class PatchModelFactory2D implements Supplier<AnalysisModel> {

    private MFRectangle rectangle;

    protected abstract GeomPointLoad genDirichletLoad();

    protected abstract GeomPointLoad genVolumeLoad();

    protected abstract GeomPointLoad genNeumannLoad();

    protected abstract int getValueDimension();

    protected Function<double[], PartialTuple>                          field;
    private Function<MFFacet, MFFacet>                                  facetFractionizer;
    private Function<MFRectangle, List<double[]>>                       spaceNodesCoordsGenerator;
    private Function<MFRectangle, List<? extends PolygonIntegrateUnit>> volumeUnitsGenerator;
    public static final Logger                                          logger             = LoggerFactory
                                                                                                   .getLogger(PoissonPatchModelFactory2D.class);
    private Predicate<double[]>                                         dirichletPredicate = (xy) -> xy[1] == rectangle
                                                                                                   .getUp();
    private final Function<double[], MFNode>                            nodeFactory        = MFNode::new;
    private final Supplier<MFLine>                                      lineFactory        = SimpMFLine::new;

    @Override
    public AnalysisModel get() {
        RawAnalysisModel result = new RawAnalysisModel();
        MFFacet facet = genFacet();
        result.setGeomRoot(facet);
        result.setSpaceNodes(genSpaceNodes());
        result.setSpatialDimension(2);
        result.setValueDimension(getValueDimension());
        result.setLoadMap(genLoadMap(facet));
        result.setIntegrateUnitsGroup(genIntegrateUnitsGroup(facet));
        return result;
    }

    public void setRectangle(MFRectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Function<double[], PartialTuple> getField() {
        return field;
    }

    public void setField(Function<double[], PartialTuple> field) {
        this.field = field;
    }

    public void setFacetFractionizer(Function<MFFacet, MFFacet> facetFractionizer) {
        this.facetFractionizer = facetFractionizer;
    }

    public void setSpaceNodesCoordsGenerator(Function<MFRectangle, List<double[]>> spaceNodesCoordsGenerator) {
        this.spaceNodesCoordsGenerator = spaceNodesCoordsGenerator;
    }

    public void setVolumeUnitsGenerator(Function<MFRectangle, List<? extends PolygonIntegrateUnit>> volumeUnitsGenerator) {
        this.volumeUnitsGenerator = volumeUnitsGenerator;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private IntegrateUnitsGroup genIntegrateUnitsGroup(MFFacet facet) {
        List<? extends PolygonIntegrateUnit> volumes = volumeUnitsGenerator.apply(rectangle);
        volumes.forEach((p) -> p.setLoadKey(facet));
        IntegrateUnitsGroup result = new IntegrateUnitsGroup();
        result.setVolume((List) volumes);

        ArrayList<Object> neumanns = new ArrayList<>();
        ArrayList<Object> dirichlets = new ArrayList<>();
        for (MFLine seg : facet) {
            if (isDirichlet(seg)) {
                dirichlets.add(seg);
            } else {
                neumanns.add(seg);
            }
        }
        result.setDirichlet(dirichlets);
        result.setNeumann(neumanns);
        return result;
    }

    private MFFacet genFacet() {
        MFFacet ori = new MFFacetFactory(lineFactory, nodeFactory).produceBySingleChain(rectangle.vertesCoords());
        return facetFractionizer.apply(ori);
    }

    private List<MFNode> genSpaceNodes() {
        List<double[]> coords = spaceNodesCoordsGenerator.apply(rectangle);
        ArrayList<MFNode> result = new ArrayList<>(coords.size());
        coords.stream().forEach((crd) -> {
            result.add(new MFNode(crd));
        });
        return result;
    }

    private Map<Object, GeomPointLoad> genLoadMap(MFFacet facet) {
        Map<Object, GeomPointLoad> loadMap = new HashMap<>();
        loadMap.put(facet, genVolumeLoad());
        GeomPointLoad diriLoad = genDirichletLoad();
        GeomPointLoad neuLoad = genNeumannLoad();
        for (MFLine seg : facet) {
            if (isDirichlet(seg)) {
                loadMap.put(seg, diriLoad);
            } else {
                loadMap.put(seg, neuLoad);
            }
        }
        return loadMap;
    }

    public boolean isDirichlet(MFLine seg) {
        return isDirichlet(seg.getStart()) && isDirichlet(seg.getEnd());
    }

    public boolean isDirichlet(Node node) {
        return isDirichlet(node.getCoord());
    }

    public boolean isDirichlet(double[] coord) {
        return dirichletPredicate.test(coord);
    }

    public Predicate<double[]> getDirichletPredicate() {
        return dirichletPredicate;
    }

    public void setDirichletPredicate(Predicate<double[]> dirichletPredicate) {
        this.dirichletPredicate = dirichletPredicate;
    }

    public MFRectangle getRectangle() {
        return rectangle;
    }

}
/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.support_domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.geomodel.Polygon2DModel;
import net.epsilony.mf.geomodel.GeomModel2DUtils;
import net.epsilony.mf.geomodel.MFLineBnd;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.IntIdentityComparator;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.pair.WithPair;
import net.epsilony.tb.pair.WithPairComparator;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Polygon2D;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SupportDomainSearcherFactoryTest {

    public SupportDomainSearcherFactoryTest() {
    }

    @Test
    public void testSearchOnAHorizontalBnd() {
        double[][][] vertesCoords = new double[][][]{
            {{0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}, {8, 0}, {9, 0}, {9, -1}, {5, -1}, {4, -1},
            {4, -2}, {10, -2}, {10, 3}, {0, 3}},
            {{4, 0.5}, {4, 1}, {4.5, 1}, {5, 1}, {5, 0.5}, {4.5, 0.5}}};

        double[] center = new double[]{4.5, 0};
        int bndId = 4;
        double radius = 100;
        double[][] spaceNodeCoords = new double[][]{
            {1, 2}, {2, 2}, {3, 2}, {4, 2}, {5, 2}, {6, 2}, {7, 2}, {8, 2}};

        Polygon2D rawPg = Polygon2D.byCoordChains(vertesCoords);
        Polygon2D pg = GeomModel2DUtils.clonePolygonWithMFNode(rawPg);
        LinkedList<Segment> pgSegs = new LinkedList<>();
        for (Object seg : pg) {
            pgSegs.add((Segment) seg);
        }
        Line bndLine = (Line) pgSegs.get(bndId);
        LinkedList<MFNode> spaceNodes = new LinkedList<>();
        for (double[] crd : spaceNodeCoords) {
            spaceNodes.add(new MFNode(crd));
        }
        boolean[] withPerturb = new boolean[]{false, true};
        int[] expSpaceNdIdx = new int[]{0, 1, 6, 7};
        int[] expPolygonNdIdxNoPerb = new int[]{11, 12, 13, 14, 23, 24, 25, 29, 30};//{3, 4, 5, 6, 15, 16, 17, 21, 22};
        int[] expPolygonNdIdxWithPerb = new int[]{8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 23, 24, 25, 29, 30};//{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 15, 16, 17, 21, 22};
        for (boolean wp : withPerturb) {
            Polygon2DModel sampleModel2D = new Polygon2DModel();
            sampleModel2D.setPolygon(pg);
            sampleModel2D.setSpaceNodes(spaceNodes);
            int asmId = 0;
            List<MFNode> allNodes = GeomModel2DUtils.getAllGeomNodes(sampleModel2D);
            for (MFNode nd : allNodes) {
                nd.setAssemblyIndex(asmId++);
            }
            SupportDomainSearcherFactory factory = new SupportDomainSearcherFactory();
            factory.setAllMFNodes(allNodes);
            factory.setBoundarySegmentsChainsHeads(pg.getChainsHeads());
            factory.setIgnoreInvisibleNodesInformation(false);
            factory.setUseCenterPerturb(wp);
            SupportDomainSearcher searcher = factory.produce();
            SupportDomainData searchResult = searcher.searchSupportDomain(center, new MFLineBnd(bndLine), radius);
            Collections.sort(searchResult.visibleNodes, new Comparator<MFNode>() {
                @Override
                public int compare(MFNode o1, MFNode o2) {
                    return o1.getAssemblyIndex() - o2.getAssemblyIndex();
                }
            });
            int[] expPolygonNdIdx = wp ? expPolygonNdIdxWithPerb : expPolygonNdIdxNoPerb;

            int idx = 0;
            boolean getHere = false;
            for (MFNode nd : searchResult.visibleNodes) {
                if (idx < expSpaceNdIdx.length) {
                    assertEquals(expSpaceNdIdx[idx], nd.getAssemblyIndex());
                } else {
                    assertEquals(expPolygonNdIdx[idx - expSpaceNdIdx.length], nd.getAssemblyIndex());
                }
                idx++;
                getHere = true;
            }
            assertTrue(getHere);
        }
    }

    @Test
    public void testSearchSimp() {
        double[][][] vertesCoords = new double[][][]{
            {{0, 0}, {1, 0}, {2, 0}, {2, 1}, {2, 2}, {1, 2}, {0, 2}, {0, 1}},
            {{0.5, 0.5}, {0.5, 0.75}, {1.5, 0.75}, {1.5, 0.5}},};

        double[] center = new double[]{1, 0.45};
        double radius = 1.5;
        double[][] spaceNodeCoords = new double[][]{
            {1, 1},};

        Polygon2D rawPg = Polygon2D.byCoordChains(vertesCoords);
        Polygon2D pg = GeomModel2DUtils.clonePolygonWithMFNode(rawPg);

        LinkedList<MFNode> spaceNodes = new LinkedList<>();
        for (double[] crd : spaceNodeCoords) {
            spaceNodes.add(new MFNode(crd));
        }
        Polygon2DModel sampleModel2D = new Polygon2DModel();
        sampleModel2D.setPolygon(pg);
        sampleModel2D.setSpaceNodes(spaceNodes);
        int asmId = 0;
        List<MFNode> allNodes = GeomModel2DUtils.getAllGeomNodes(sampleModel2D);
        for (MFNode nd : allNodes) {
            nd.setAssemblyIndex(asmId++);
        }
        int segId = 0;
        for (Segment seg : sampleModel2D.getPolygon()) {
            seg.setId(segId++);
        }
        SupportDomainSearcherFactory factory = new SupportDomainSearcherFactory();
        factory.setAllMFNodes(allNodes);
        factory.setBoundarySegmentsChainsHeads(pg.getChainsHeads());
        factory.setIgnoreInvisibleNodesInformation(false);
        SupportDomainSearcher searcher = factory.produce();
        SupportDomainData searchResult = searcher.searchSupportDomain(center, null, radius);
        Collections.sort(searchResult.visibleNodes, new Comparator<MFNode>() {
            @Override
            public int compare(MFNode o1, MFNode o2) {
                return o1.getAssemblyIndex() - o2.getAssemblyIndex();
            }
        });
        List<WithPair<MFNode, Segment>> blockPair = searchResult.invisibleNodesAndBlockingSegments;
        Collections.sort(blockPair, new WithPairComparator<MFNode, Segment>(new Comparator<MFNode>() {
            @Override
            public int compare(MFNode o1, MFNode o2) {
                return o1.getAssemblyIndex() - o2.getAssemblyIndex();
            }
        }));
        Collections.sort(searchResult.segments, new IntIdentityComparator<>());

        int[] ndsIdsExp = new int[]{1, 2, 3, 9, 12};
        int[] segsIdsExp = new int[]{0, 1, 2, 3, 6, 7, 8, 9, 10, 11};
        int idx = 0;
        for (MFNode nd : searchResult.visibleNodes) {
            assertEquals(ndsIdsExp[idx], nd.getAssemblyIndex());
            idx++;
        }
        idx = 0;
        for (Segment seg : searchResult.segments) {
            assertEquals(segsIdsExp[idx], seg.getId());
            idx++;
        }
        int[] blockedNdsIds = new int[]{0, 4, 8, 10, 11};
        idx = 0;
        boolean getHere = false;
        for (WithPair<MFNode, Segment> p : blockPair) {

            assertEquals(blockedNdsIds[idx], p.getKey().getAssemblyIndex());
            Node exp_nd = p.getKey();
            Segment seg = p.getValue();
            assertTrue(
                    Math2D.isSegmentsIntersecting(seg.getStart().getCoord(), seg.getEnd().getCoord(), center, exp_nd.getCoord()));
            idx++;
            getHere = true;
        }
        assertTrue(getHere);
    }
}

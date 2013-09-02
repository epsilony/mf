/* (c) Copyright by Man YUAN */
package net.epsilony.mf.geomodel.support_domain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.geomodel.GeomModel2D;
import net.epsilony.mf.geomodel.GeomModel2DUtils;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.Line2D;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.IntIdentityComparator;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.pair.WithPair;
import net.epsilony.tb.pair.WithPairComparator;
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

        Polygon2D<Node> rawPg = Polygon2D.byCoordChains(vertesCoords);
        Polygon2D<MFNode> pg = GeomModel2DUtils.clonePolygonWithMFNode(rawPg);
        LinkedList<Line2D> pgSegs = new LinkedList<>();
        for (Line2D seg : pg) {
            pgSegs.add(seg);
        }
        Line2D bnd = pgSegs.get(bndId);
        LinkedList<MFNode> spaceNodes = new LinkedList<>();
        for (double[] crd : spaceNodeCoords) {
            spaceNodes.add(new MFNode(crd));
        }
        boolean[] withPerturb = new boolean[]{false, true};
        int[] expSpaceNdIdx = new int[]{23, 24, 29, 30};
        int[] expPolygonNdIdxNoPerb = new int[]{3, 4, 5, 6, 15, 16, 17, 21, 22};
        int[] expPolygonNdIdxWithPerb = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 15, 16, 17, 21, 22};
        for (boolean wp : withPerturb) {
            GeomModel2D sampleModel2D = new GeomModel2D(pg, spaceNodes);
            SupportDomainSearcherFactory factory = new SupportDomainSearcherFactory();
            factory.setAllMFNodes(sampleModel2D.getAllNodes());
            factory.setBoundaryByChainsHeads(pg.getChainsHeads());
            factory.setIgnoreInvisibleNodesInformation(false);
            factory.setUseCenterPerturb(wp);
            SupportDomainSearcher searcher = factory.produce();
            SupportDomainData searchResult = searcher.searchSupportDomain(center, bnd, radius);
            Collections.sort(searchResult.visibleNodes, new IntIdentityComparator<>());
            int[] expPolygonNdIdx = wp ? expPolygonNdIdxWithPerb : expPolygonNdIdxNoPerb;

            int idx = 0;
            boolean getHere = false;
            for (Node nd : searchResult.visibleNodes) {
                if (idx < expPolygonNdIdx.length) {
                    assertEquals(expPolygonNdIdx[idx], nd.getId());
                } else {
                    assertEquals(expSpaceNdIdx[idx - expPolygonNdIdx.length], nd.getId());
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

        Polygon2D<Node> rawPg = Polygon2D.byCoordChains(vertesCoords);
        Polygon2D<MFNode> pg = GeomModel2DUtils.clonePolygonWithMFNode(rawPg);

        LinkedList<MFNode> spaceNodes = new LinkedList<>();
        for (double[] crd : spaceNodeCoords) {
            spaceNodes.add(new MFNode(crd));
        }
        GeomModel2D sampleModel2D = new GeomModel2D(pg, spaceNodes);

        SupportDomainSearcherFactory factory = new SupportDomainSearcherFactory();
        factory.setAllMFNodes(sampleModel2D.getAllNodes());
        factory.setBoundaryByChainsHeads(pg.getChainsHeads());
        factory.setIgnoreInvisibleNodesInformation(false);
        SupportDomainSearcher searcher = factory.produce();
        SupportDomainData searchResult = searcher.searchSupportDomain(center, null, radius);
        Collections.sort(searchResult.visibleNodes, new IntIdentityComparator<>());
        List<WithPair<MFNode, Segment>> blockPair = searchResult.invisibleNodesAndBlockingSegments;
        Collections.sort(blockPair, new WithPairComparator<MFNode, Segment>(new IntIdentityComparator<MFNode>()));
        Collections.sort(searchResult.segments, new IntIdentityComparator<>());

        int[] ndsIdsExp = new int[]{0, 1, 2, 8, 11};
        int[] segsIdsExp = new int[]{0, 1, 2, 3, 6, 7, 8, 9, 10, 11};
        int idx = 0;
        for (Node nd : searchResult.visibleNodes) {
            assertEquals(ndsIdsExp[idx], nd.getId());
            idx++;
        }
        idx = 0;
        for (Segment seg : searchResult.segments) {
            assertEquals(segsIdsExp[idx], seg.getId());
            idx++;
        }
        int[] blockedNdsIds = new int[]{3, 7, 9, 10, 12};
        idx = 0;
        boolean getHere = false;
        for (WithPair<MFNode, Segment> p : blockPair) {

            assertEquals(blockedNdsIds[idx], p.getKey().getId());
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

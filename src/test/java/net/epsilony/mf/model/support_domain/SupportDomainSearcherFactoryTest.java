/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.GeomModel2DUtils;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.IntIdentityComparator;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.pair.WithPair;
import net.epsilony.tb.pair.WithPairComparator;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.Segment2DUtils;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SupportDomainSearcherFactoryTest {

    public SupportDomainSearcherFactoryTest() {
    }

    public static class TestSample {

        double[] center;
        int bndId = -1;
        double radius;
        List<MFNode> allNodes;
        List<MFNode> spaceNodes;
        int[] expSpaceNdIdx;
        int[] expPolygonNdIdxNoPerb;
        int[] expPolygonNdIdxWithPerb;
        Facet facet;

        public void setFacetByCoords(double[][][] vertesCoords) {
            facet = Facet.byCoordChains(vertesCoords);
            facet = GeomModel2DUtils.clonePolygonWithMFNode(facet);
            int segId = 0;
            for (Segment seg : facet) {
                seg.setId(segId++);
            }
        }

        public void setSpaceNodesByCoords(double[][] spaceNodeCoords) {
            spaceNodes = new LinkedList<>();
            for (double[] crd : spaceNodeCoords) {
                spaceNodes.add(new MFNode(crd));
            }
        }

        public Line getBnd() {
            if (bndId < 0) {
                return null;
            }
            int i = 0;
            for (Segment seg : facet) {
                if (i == bndId) {
                    return (Line) seg;
                }
                i++;
            }
            return null;
        }

        public void genIndexedAllNodes() {
            allNodes = new LinkedList<>();
            allNodes.addAll(spaceNodes);
            for (Segment seg : facet) {
                allNodes.add((MFNode) seg.getStart());
            }
            int asmId = 0;
            for (MFNode nd : allNodes) {
                nd.setAssemblyIndex(asmId++);
            }
        }

        public void assertResults(SupportDomainData searchResult, boolean withPerturb) {
            Collections.sort(searchResult.visibleNodes, new Comparator<MFNode>() {
                @Override
                public int compare(MFNode o1, MFNode o2) {
                    return o1.getAssemblyIndex() - o2.getAssemblyIndex();
                }
            });
            int[] expPolygonNdIdx = withPerturb ? expPolygonNdIdxWithPerb : expPolygonNdIdxNoPerb;

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

    public TestSample getTestSampleOfSearchOnAHorizontalBnd() {
        TestSample sample = new TestSample();

        sample.center = new double[]{4.5, 0};
        sample.bndId = 4;
        sample.radius = 100;
        double[][][] vertesCoords = new double[][][]{
            {{0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}, {8, 0}, {9, 0}, {9, -1}, {5, -1}, {4, -1},
                {4, -2}, {10, -2}, {10, 3}, {0, 3}},
            {{4, 0.5}, {4, 1}, {4.5, 1}, {5, 1}, {5, 0.5}, {4.5, 0.5}}};
        sample.setFacetByCoords(vertesCoords);
        double[][] spaceNodeCoords = new double[][]{
            {1, 2}, {2, 2}, {3, 2}, {4, 2}, {5, 2}, {6, 2}, {7, 2}, {8, 2}};
        sample.setSpaceNodesByCoords(spaceNodeCoords);
        sample.genIndexedAllNodes();
        sample.expSpaceNdIdx = new int[]{0, 1, 6, 7};
        sample.expPolygonNdIdxNoPerb = new int[]{11, 12, 13, 14, 23, 24, 25, 29, 30};//{3, 4, 5, 6, 15, 16, 17, 21, 22};
        sample.expPolygonNdIdxWithPerb = new int[]{8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 23, 24, 25, 29, 30};//{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 15, 16, 17, 21, 22};
        return sample;
    }

    @Test
    public void testSearchOnAHorizontalBnd() {
        TestSample sample = getTestSampleOfSearchOnAHorizontalBnd();

        Line bndLine = sample.getBnd();

        boolean[] withPerturb = new boolean[]{false, true};

        for (boolean wp : withPerturb) {
            SupportDomainSearcherFactory factory = new SupportDomainSearcherFactory();
            factory.setAllMFNodes(sample.allNodes);
            factory.setBoundarySegmentsChainsHeads(sample.facet.getRingsHeads());
            factory.setIgnoreInvisibleNodesInformation(false);
            factory.setUseCenterPerturb(wp);
            SupportDomainSearcher searcher = factory.produce();
            for (boolean useUnitOutNormal : new boolean[]{false, true}) {
                searcher.setCenter(sample.center);
                if (useUnitOutNormal) {
                    searcher.setBoundary(null);
                    searcher.setUnitOutNormal(Segment2DUtils.chordUnitOutNormal(bndLine, null));
                } else {
                    searcher.setBoundary(bndLine);
                }
                searcher.setRadius(sample.radius);
                SupportDomainData searchResult = searcher.searchSupportDomain();
                sample.assertResults(searchResult, wp);
            }
        }
    }

    TestSample getTestSearchSimpSample() {
        TestSample sample = new TestSample();
        double[][][] vertesCoords = new double[][][]{
            {{0, 0}, {1, 0}, {2, 0}, {2, 1}, {2, 2}, {1, 2}, {0, 2}, {0, 1}},
            {{0.5, 0.5}, {0.5, 0.75}, {1.5, 0.75}, {1.5, 0.5}},};
        sample.setFacetByCoords(vertesCoords);
        sample.center = new double[]{1, 0.45};
        sample.radius = 1.5;
        double[][] spaceNodeCoords = new double[][]{
            {1, 1},};
        sample.setSpaceNodesByCoords(spaceNodeCoords);
        sample.genIndexedAllNodes();
        return sample;
    }

    @Test
    public void testSearchSimp() {

        TestSample sample = getTestSearchSimpSample();

        SupportDomainSearcherFactory factory = new SupportDomainSearcherFactory();
        factory.setAllMFNodes(sample.allNodes);
        factory.setBoundarySegmentsChainsHeads(sample.facet.getRingsHeads());
        factory.setIgnoreInvisibleNodesInformation(false);
        SupportDomainSearcher searcher = factory.produce();
        searcher.setCenter(sample.center);
        searcher.setBoundary(null);
        searcher.setUnitOutNormal(null);
        searcher.setRadius(sample.radius);
        SupportDomainData searchResult = searcher.searchSupportDomain();
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
                    Math2D.isSegmentsIntersecting(seg.getStart().getCoord(), seg.getEnd().getCoord(), sample.center, exp_nd.getCoord()));
            idx++;
            getHere = true;
        }
        assertTrue(getHere);
    }
}

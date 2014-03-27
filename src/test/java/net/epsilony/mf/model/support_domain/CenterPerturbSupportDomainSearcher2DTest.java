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
package net.epsilony.mf.model.support_domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.model.GeomModel2DUtils;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.search.config.TwoDLRTreeSearcherConfig;
import net.epsilony.mf.model.support_domain.config.CenterPerturbSupportDomainSearcherConfig;
import net.epsilony.mf.model.support_domain.config.SupportDomainBaseConfig;
import net.epsilony.mf.util.bus.FreshPoster;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;
import net.epsilony.tb.solid.Segment2DUtils;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.collect.Lists;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class CenterPerturbSupportDomainSearcher2DTest {

    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ModelBusConfig.class,
            CenterPerturbSupportDomainSearcherConfig.class, TwoDLRTreeSearcherConfig.class);

    public TestSample getTestSampleOfSearchOnAHorizontalBnd() {
        TestSample sample = new TestSample();

        sample.center = new double[] { 4.5, 0 };
        sample.bndId = 4;
        sample.radius = 100;
        double[][][] vertesCoords = new double[][][] {
                { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 }, { 4, 0 }, { 5, 0 }, { 6, 0 }, { 7, 0 }, { 8, 0 }, { 9, 0 },
                        { 9, -1 }, { 5, -1 }, { 4, -1 }, { 4, -2 }, { 10, -2 }, { 10, 3 }, { 0, 3 } },
                { { 4, 0.5 }, { 4, 1 }, { 4.5, 1 }, { 5, 1 }, { 5, 0.5 }, { 4.5, 0.5 } } };
        sample.setFacetByCoords(vertesCoords);
        double[][] spaceNodeCoords = new double[][] { { 1, 2 }, { 2, 2 }, { 3, 2 }, { 4, 2 }, { 5, 2 }, { 6, 2 },
                { 7, 2 }, { 8, 2 } };
        sample.setSpaceNodesByCoords(spaceNodeCoords);
        sample.genIndexedAllNodes();
        sample.expSpaceNdIdx = new int[] { 0, 1, 6, 7 };
        sample.expPolygonNdIdxWithPerb = new int[] { 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 23, 24, 25, 29, 30 };
        return sample;
    }

    @Test
    public void testSearchOnAHorizontalBnd() {
        TestSample sample = getTestSampleOfSearchOnAHorizontalBnd();

        SupportDomainSearcher searcher = genSearcher(sample);

        SoftSupportDomainData searchResult = new SoftSupportDomainData();
        searchResult.setInvisibleBlockingMapEnable(true);
        Line bndLine = sample.getBnd();
        for (boolean useUnitOutNormal : new boolean[] { false, true }) {
            searcher.setCenter(sample.center);
            if (useUnitOutNormal) {
                searcher.setBoundary(null);
                searcher.setUnitOutNormal(Segment2DUtils.chordUnitOutNormal(bndLine, null));
            } else {
                searcher.setBoundary(bndLine);
            }
            searcher.setRadius(sample.radius);
            searcher.search(searchResult);
            sample.assertResults(searchResult);
        }
    }

    TestSample getTestSearchSimpSample() {
        TestSample sample = new TestSample();
        double[][][] vertesCoords = new double[][][] {
                { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 2, 1 }, { 2, 2 }, { 1, 2 }, { 0, 2 }, { 0, 1 } },
                { { 0.5, 0.5 }, { 0.5, 0.75 }, { 1.5, 0.75 }, { 1.5, 0.5 } }, };
        sample.setFacetByCoords(vertesCoords);
        sample.center = new double[] { 1, 0.45 };
        sample.radius = 1.5;
        double[][] spaceNodeCoords = new double[][] { { 1, 1 }, };
        sample.setSpaceNodesByCoords(spaceNodeCoords);
        sample.genIndexedAllNodes();
        return sample;
    }

    @SuppressWarnings("unchecked")
    private SupportDomainSearcher genSearcher(TestSample sample) {
        SupportDomainSearcher searcher = applicationContext.getBean(
                SupportDomainBaseConfig.SUPPORT_DOMAIN_SEARCHER_PROTO, SupportDomainSearcher.class);
        applicationContext.getBean(ModelBusConfig.NODES_BUS, FreshPoster.class).postToFresh(sample.allNodes);
        applicationContext.getBean(ModelBusConfig.BOUNDARIES_BUS, FreshPoster.class).postToFresh(
                Lists.newArrayList(sample.facet));
        applicationContext.getBean(ModelBusConfig.SPATIAL_DIMENSION_BUS, FreshPoster.class).postToFresh(2);
        applicationContext.getBean(ModelBusConfig.MODEL_INPUTED_BUS, FreshPoster.class).postToFresh("good");
        return searcher;
    }

    @Test
    public void testSearchSimp() {

        TestSample sample = getTestSearchSimpSample();
        SupportDomainSearcher searcher = genSearcher(sample);
        searcher.setCenter(sample.center);
        searcher.setBoundary(null);
        searcher.setUnitOutNormal(null);
        searcher.setRadius(sample.radius);
        SoftSupportDomainData searchResult = new SoftSupportDomainData();
        searchResult.setInvisibleBlockingMapEnable(true);

        searcher.search(searchResult);

        int[] ndsIdsExp = new int[] { 1, 2, 3, 9, 12 };

        Set<Integer> ndsIdsExpSet = fromIntArray(ndsIdsExp);
        assertEquals(ndsIdsExpSet.size(), searchResult.getVisibleNodesContainer().size());
        for (MFNode nd : searchResult.getVisibleNodesContainer()) {
            assertTrue(ndsIdsExpSet.contains(nd.getAssemblyIndex()));

        }

        int[] segsIdsExp = new int[] { 0, 1, 2, 3, 6, 7, 8, 9, 10, 11 };
        Set<Integer> segsIdsExpSet = fromIntArray(segsIdsExp);
        assertEquals(segsIdsExpSet.size(), searchResult.getSegmentsContainer().size());
        for (Segment seg : searchResult.getSegmentsContainer()) {
            assertTrue(segsIdsExpSet.contains(seg.getId()));
        }

        int[] blockedNdsIds = new int[] { 0, 4, 8, 10, 11 };
        Set<Integer> blockedNdsIdsSet = fromIntArray(blockedNdsIds);
        Map<MFNode, Segment> blockPair = searchResult.getInvisibleBlockingMap();
        assertEquals(blockedNdsIdsSet.size(), blockPair.size());
        boolean getHere = false;
        for (Map.Entry<MFNode, Segment> p : blockPair.entrySet()) {

            assertTrue(blockedNdsIdsSet.contains(p.getKey().getAssemblyIndex()));
            Node exp_nd = p.getKey();
            Segment seg = p.getValue();
            assertTrue(Math2D.isSegmentsIntersecting(seg.getStart().getCoord(), seg.getEnd().getCoord(), sample.center,
                    exp_nd.getCoord()));
            getHere = true;
        }
        assertTrue(getHere);
    }

    public static Set<Integer> extractNodesAssemblyIndes(Iterable<? extends MFNode> nodes) {
        Set<Integer> result = new HashSet<>();
        for (MFNode node : nodes) {
            result.add(node.getAssemblyIndex());
        }
        return result;
    }

    public static Set<Integer> fromIntArray(int[] input) {
        Set<Integer> result = new HashSet<>();
        for (int i : input) {
            result.add(i);
        }
        return result;
    }

    public static class TestSample {

        double[] center;
        int bndId = -1;
        double radius;
        List<MFNode> allNodes;
        List<MFNode> spaceNodes;
        int[] expSpaceNdIdx;
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

        public void assertResults(SupportDomainData searchResult) {
            Collections.sort(searchResult.getVisibleNodesContainer(), new Comparator<MFNode>() {
                @Override
                public int compare(MFNode o1, MFNode o2) {
                    return o1.getAssemblyIndex() - o2.getAssemblyIndex();
                }
            });
            int[] expPolygonNdIdx = expPolygonNdIdxWithPerb;

            int idx = 0;
            boolean getHere = false;
            for (MFNode nd : searchResult.getVisibleNodesContainer()) {
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
}

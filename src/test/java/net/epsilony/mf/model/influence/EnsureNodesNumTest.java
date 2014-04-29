package net.epsilony.mf.model.influence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.epsilony.mf.model.AnalysisModel;
import net.epsilony.mf.model.GeomModel2DUtils;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.model.RawAnalysisModel;
import net.epsilony.mf.model.config.ModelBusConfig;
import net.epsilony.mf.model.influence.config.EnsureNodesNumConfig;
import net.epsilony.mf.model.influence.config.InfluenceBaseConfig;
import net.epsilony.mf.model.search.config.TwoDLRTreeSearcherConfig;
import net.epsilony.mf.model.support_domain.config.CenterPerturbSupportDomainSearcherConfig;
import net.epsilony.mf.util.bus.FreshPoster;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.tb.TestTool;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.collect.Lists;

public class EnsureNodesNumTest {

    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ModelBusConfig.class,
            CenterPerturbSupportDomainSearcherConfig.class, TwoDLRTreeSearcherConfig.class, EnsureNodesNumConfig.class);

    /**
     * Test of calcInflucenceRadius method, of class EnsureNodesNum.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testInflucenceRadius() {
        AnalysisModel sampleModel = sampleModel();
        Facet facet = (Facet) sampleModel.getGeomRoot();
        Line sampleLine = (Line) facet.getRingsHeads().get(0);
        int[] numLowerBounds = new int[] { 2, 4, 8, 20 };

        EnsureNodesNum calc = applicationContext.getBean(InfluenceBaseConfig.INFLUENCE_RADIUS_CALCULATOR_PROTO,
                EnsureNodesNum.class);
        applicationContext.getBean(EnsureNodesNumConfig.ENSURE_NODES_NUM_INIT_RADIUS_BUS, WeakBus.class).postToFresh(
                5.0);
        applicationContext.getBean(EnsureNodesNumConfig.ENSURE_NODES_NUM_LOWER_BOUND_BUS, WeakBus.class)
                .postToFresh(10);

        List<MFNode> allNodes = new ArrayList<>();
        for (Segment segment : facet) {
            allNodes.add((MFNode) segment.getStart());
        }
        allNodes.addAll(sampleModel.getSpaceNodes());
        List<Segment> allSegments = Lists.newArrayList(facet);

        applicationContext.getBean(ModelBusConfig.NODES_BUS, FreshPoster.class).postToFresh(allNodes);
        applicationContext.getBean(ModelBusConfig.BOUNDARIES_BUS, FreshPoster.class).postToFresh(allSegments);
        applicationContext.getBean(ModelBusConfig.SPATIAL_DIMENSION_BUS, FreshPoster.class).postToFresh(2);
        applicationContext.getBean(ModelBusConfig.MODEL_INPUTED_BUS, FreshPoster.class).postToFresh("GOOD");

        for (boolean onlySpaceNodes : new boolean[] { false, true }) {
            calc.setOnlyCountSpaceNodes(onlySpaceNodes);
            doTest(calc, sampleModel, sampleLine, numLowerBounds);
        }
    }

    public void doTest(EnsureNodesNum calc, AnalysisModel sampleModel, Line sampleSeg, int[] numLowerBounds) {

        LinkedList<Double> enlargedDistances = new LinkedList<>();
        List<MFNode> nodes = calc.isOnlyCountSpaceNodes() ? sampleModel.getSpaceNodes() : GeomModel2DUtils
                .getAllGeomNodes(sampleModel);

        for (Node nd : nodes) {
            double distance = Math2D.distance(nd.getCoord(), sampleTranslateVector);
            double enlarged = calc.getResultEnlargeRatio() * distance;
            enlargedDistances.add(enlarged);
        }
        Collections.sort(enlargedDistances);
        System.out.println("enlargedDistances = " + enlargedDistances);
        boolean getHere = false;
        for (int i = 0; i < numLowerBounds.length; i++) {
            calc.setNodesNumLowerBound(numLowerBounds[i]);
            double act = calc.calcInflucenceRadius(sampleSeg.getStart().getCoord(), sampleSeg);
            double exp = enlargedDistances.get(numLowerBounds[i] - 1);
            assertEquals(exp, act, 1e-10);
            getHere = true;
        }
        assertTrue(getHere);

    }

    private AnalysisModel sampleModel() {
        Facet triPolygon = sampleTrianglePolygon();
        triPolygon = GeomModel2DUtils.clonePolygonWithMFNode(triPolygon);
        List<MFNode> spaceNodes = sampleSpaceNodesInTriangle();

        RawAnalysisModel result = new RawAnalysisModel();
        result.setGeomRoot(triPolygon);
        result.setSpaceNodes(spaceNodes);
        return result;
    }

    private Facet sampleTrianglePolygon() {
        double[][] vertes = threeSampleTriangleVertes();
        int[] numByEdge = new int[] { 12, 20, 10 };
        LinkedList<Node> triangleVertes = genAllTriangleVertesNodes(vertes, numByEdge);
        Facet triangle = Facet.byNodesChains(Arrays.asList(triangleVertes));
        return triangle;
    }

    private final double[] sampleTranslateVector = new double[] { -1, 2 };

    private double[] translate(double[] vec) {
        return new double[] { vec[0] + sampleTranslateVector[0], vec[1] + sampleTranslateVector[1] };
    }

    private LinkedList<MFNode> sampleSpaceNodesInTriangle() {
        double[][] starts = new double[][] { { 3, 2 }, { 2, 6 } };
        double[][] ends = new double[][] { { 40, 30 }, { 10, 45 } };
        int[] numPts = new int[] { 10, 10 };
        LinkedList<MFNode> result = new LinkedList<>();
        for (int i = 0; i < numPts.length; i++) {
            double[] start = translate(starts[i]);
            double[] end = translate(ends[i]);
            int numPt = numPts[i];
            result.addAll(linSpaceNodes(start, end, numPt));
        }
        return result;
    }

    private LinkedList<MFNode> linSpaceNodes(double[] start, double[] end, int numNds) {
        LinkedList<MFNode> result = new LinkedList<>();
        LinkedList<double[]> coords = TestTool.linSpace2D(start, end, numNds);
        for (double[] coord : coords) {
            result.add(new MFNode(coord));
        }
        return result;
    }

    private LinkedList<Node> genAllTriangleVertesNodes(double[][] threeVertes, int[] numByEdge) {
        LinkedList<Node> result = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            double[] start = threeVertes[i];
            double[] end = threeVertes[(i + 1) % 3];
            int numPt = numByEdge[i];
            LinkedList<double[]> coords = TestTool.linSpace2D(start, end, numPt);
            for (double[] coord : coords.subList(0, coords.size() - 1)) {
                result.add(new Node(coord));
            }
        }
        return result;
    }

    private double[][] threeSampleTriangleVertes() {
        double[] a = new double[] { 0, 0 };
        double[] b = new double[] { 60, 26 };
        double[] c = new double[] { -13, 60 };

        double[][] result = new double[3][];
        int i = 0;
        for (double[] pt : new double[][] { a, b, c }) {
            result[i] = translate(pt);
            i++;
        }
        return result;
    }
}

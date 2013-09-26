/* (c) Copyright by Man YUAN */
package net.epsilony.mf.project.sample;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.cons_law.PlaneStress;
import net.epsilony.mf.geomodel.GeomModel;
import net.epsilony.mf.geomodel.FacetModel;
import net.epsilony.mf.geomodel.MFNode;
import net.epsilony.mf.geomodel.influence.EnsureNodesNum;
import net.epsilony.mf.geomodel.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.MFLinearMechanicalProcessor;
import net.epsilony.mf.process.MechanicalPostProcessor;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.assembler.MechanicalLagrangeAssembler;
import net.epsilony.mf.process.integrate.MFIntegrateTask;
import net.epsilony.mf.project.SimpMFMechanicalProject;
import net.epsilony.mf.process.integrate.Common2DTask;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.MLS;
import net.epsilony.mf.util.MFConstants;
import net.epsilony.tb.Factory;
import net.epsilony.tb.RudeFactory;
import net.epsilony.tb.analysis.GenericFunction;
import net.epsilony.tb.analysis.Math2D;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.quadrature.SymmetricTriangleQuadrature;
import net.epsilony.tb.solid.Facet;
import net.epsilony.tb.solid.winged.FacetTriangulatorFactory;
import net.epsilony.tb.solid.winged.RawWingedEdge;
import net.epsilony.tb.solid.winged.TriangleCell;
import net.epsilony.tb.solid.winged.TriangleArrayContainers;
import net.epsilony.tb.solid.winged.WingedEdge;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TimoshenkoHoleyPlate implements Factory<SimpMFMechanicalProject> {

    double holeRadius = 0.5;
    double sideLen = 4;
    double distributeTension = 1;
    double E = 1000;
    double mu = 0.3;
    double maxSegmentLen = 0.2;
    double curveFlatness = holeRadius * (1 - cos(PI / 2 / 4 / 2));//max distance from segment to original curve
    double triangleArea = maxSegmentLen * maxSegmentLen * sqrt(3) / 4 * 0.8;
//    public static final int DOWN_LINE_INDEX = 0, RIGHT_LINE_INDEX = 1, UP_LINE_INDEX = 2, LEFT_LINE_INDEX = 3, CIRCLE_INDEX = 4;
    private int quadratureDegree = 3;
    InfluenceRadiusCalculator influenceRadiusCalculator = new EnsureNodesNum(maxSegmentLen * 1.1, 15);
    MFShapeFunction shapeFunc = new MLS();
    GeomModel model;
    MFIntegrateTask integrateTask;

    public double getE() {
        return E;
    }

    public double getMu() {
        return mu;
    }

    public double[] getStress(double[] coord, double[] result) {
        double a = holeRadius;
        double s = distributeTension;
        double x = coord[0];
        double y = coord[1];
        double theta = atan2(y, x);
        double r = sqrt(x * x + y * y);
        double a_r = a / r;
        double a_r_s = a_r * a_r;
        double a_r_q = a_r_s * a_r_s;
        double sigma_x = s * (1 - (1.5 * cos(2 * theta) + cos(4 * theta)) * a_r_s + 1.5 * cos(4 * theta) * a_r_q);
        double sigma_y = s * (-(0.5 * cos(theta * 2) - cos(4 * theta)) * a_r_s - 1.5 * cos(4 * theta) * a_r_q);
        double tau_xy = s * (-(0.5 * sin(2 * theta) + sin(4 * theta)) * a_r_s + 1.5 * sin(4 * theta) * a_r_q);
        if (result == null) {
            result = new double[]{sigma_x, sigma_y, tau_xy};
        } else {
            result[0] = sigma_x;
            result[1] = sigma_y;
            result[2] = tau_xy;
        }

        return result;
    }

    @Override
    public SimpMFMechanicalProject produce() {
        Facet polygon = genPolygon();
        genModelAndTask(polygon);

        SimpMFMechanicalProject project = new SimpMFMechanicalProject();
        project.setModel(model);
        project.setMFIntegrateTask(integrateTask);
        project.setAssembler(new MechanicalLagrangeAssembler());
//        project.setAssembler(new MechanicalPenaltyAssembler(1e8));
        project.setConstitutiveLaw(genConstitutiveLaw());
        project.setInfluenceRadiusCalculator(influenceRadiusCalculator);
        return project;
    }

    private Facet genPolygon() {
        Shape shape = genBoundaryShape();

        PathIterator pathIter = shape.getPathIterator(null, curveFlatness);
        double[] coords = new double[6];
        LinkedList<double[]> chainVertes = new LinkedList<>();

        int moveToCount = 0;
        while (!pathIter.isDone()) {
            switch (pathIter.currentSegment(coords)) {
                case PathIterator.SEG_CLOSE:
                    break;
                case PathIterator.SEG_MOVETO:
                    moveToCount++;
                    if (moveToCount > 1) {
                        throw new IllegalStateException();
                    }
                default:
                    chainVertes.add(Arrays.copyOf(coords, 2));
            }
            pathIter.next();
        }
        if (!Math2D.isAnticlockwise(chainVertes)) {
            Collections.reverse(chainVertes);
        }
        List<List<MFNode>> nodesLists = new ArrayList<>(1);
        ArrayList<MFNode> nodes = new ArrayList<>(chainVertes.size());
        nodesLists.add(nodes);
        for (double[] coord : chainVertes) {
            MFNode newNode = new MFNode(coord);
            nodes.add(newNode);
        }
        Facet polygon = Facet.byNodesChains(nodesLists);
        return polygon.fractionize(maxSegmentLen);
    }

    private Shape genBoundaryShape() {
        Area area = new Area(new Rectangle2D.Double(0, 0, sideLen, sideLen));
        area.subtract(new Area(new Ellipse2D.Double(-holeRadius, -holeRadius, 2 * holeRadius, 2 * holeRadius)));
        return area;
    }

    private void genModelAndTask(Facet polygon) {
        Common2DTask modelTask = new Common2DTask();
        integrateTask = modelTask;
        TriangleArrayContainers triangulated = triangulate(polygon);
        List<MFNode> spaceNodes = genSpaceNodes(triangulated);
        FacetModel polygonModel = new FacetModel();
        model = polygonModel;
        polygonModel.setFacet(polygon);
        polygonModel.setSpaceNodes(spaceNodes);
        modelTask.setBoundaries(polygonModel.getBoundaries());

        List<QuadraturePoint> volumeQuadPts = genVolumeQuadraturePoints(triangulated);
        modelTask.setQuadratureDegree(quadratureDegree);
        modelTask.setVolumeSpecification(null, volumeQuadPts);

        double margin = polygon.getMinSegmentCoordLength() / 8;
        modelTask.addDirichletBoundaryCondition(genDownSideBC(margin));
        modelTask.addDirichletBoundaryCondition(genLeftSideBC(margin));
        modelTask.addNeumannBoundaryCondition(genRightSideBC(margin));
        modelTask.addNeumannBoundaryCondition(genUpSideBC(margin));

    }

    private TriangleArrayContainers triangulate(Facet polygon) {
        FacetTriangulatorFactory factory = new FacetTriangulatorFactory();
        Factory<TriangleCell> cellFactory = new RudeFactory<>(TriangleCell.class);
        factory.setCellFactory(cellFactory);
        Factory<WingedEdge> edgeFactory = new RudeFactory<WingedEdge>(RawWingedEdge.class);
        factory.setEdgeFactory(edgeFactory);
        factory.setNodeFactory(new RudeFactory<>(MFNode.class));
        factory.setTriangleArea(triangleArea);
        factory.setPolygon(polygon);
        TriangleArrayContainers produced = factory.produce();
        return produced;
    }

    private List<MFNode> genSpaceNodes(TriangleArrayContainers trianguated) {
        return (List) trianguated.spaceNodes;
    }

    private Common2DTask.BCSpecification genDownSideBC(double margin) {
        Common2DTask.BCSpecification downSideBC = new Common2DTask.BCSpecification();

        downSideBC.from = new double[]{holeRadius - margin, -margin};
        downSideBC.to = new double[]{sideLen + margin, margin};

        downSideBC.valueFunc = new GenericFunction<double[], double[]>() {
            @Override
            public double[] value(double[] input, double[] output) {
                if (null == output) {
                    output = new double[2];
                } else {
                    Arrays.fill(output, 0);
                }
                return output;
            }
        };
        downSideBC.markFunc = new GenericFunction<double[], boolean[]>() {
            @Override
            public boolean[] value(double[] input, boolean[] output) {
                if (null == output) {
                    output = new boolean[2];
                }
                output[0] = false;
                output[1] = true;
                return output;
            }
        };
        return downSideBC;
    }

    private Common2DTask.BCSpecification genLeftSideBC(double margin) {
        Common2DTask.BCSpecification leftSideBC = new Common2DTask.BCSpecification();

        leftSideBC.from = new double[]{-margin, holeRadius - margin};
        leftSideBC.to = new double[]{margin, sideLen + margin};

        leftSideBC.valueFunc = new GenericFunction<double[], double[]>() {
            @Override
            public double[] value(double[] input, double[] output) {
                if (null == output) {
                    output = new double[2];
                } else {
                    Arrays.fill(output, 0);
                }
                return output;
            }
        };
        leftSideBC.markFunc = new GenericFunction<double[], boolean[]>() {
            @Override
            public boolean[] value(double[] input, boolean[] output) {
                if (null == output) {
                    output = new boolean[2];
                }
                output[0] = true;
                output[1] = false;
                return output;
            }
        };
        return leftSideBC;
    }

    private List<QuadraturePoint> genVolumeQuadraturePoints(TriangleArrayContainers triangulated) {
        SymmetricTriangleQuadrature stq = new SymmetricTriangleQuadrature();
        stq.setDegree(quadratureDegree);
        List<QuadraturePoint> result = new LinkedList<>();
        for (TriangleCell triangle : triangulated.triangles) {
            stq.setTriangle(triangle);
            for (QuadraturePoint pt : stq) {
                result.add(pt);
            }
        }
        return result;
    }

    private Common2DTask.BCSpecification genRightSideBC(double margin) {
        Common2DTask.BCSpecification rightBC = new Common2DTask.BCSpecification();
        rightBC.from = new double[]{-margin + sideLen, -margin};
        rightBC.to = new double[]{sideLen + margin, sideLen + margin};

        rightBC.valueFunc = new GenericFunction<double[], double[]>() {
            double[] cache = new double[3];

            @Override
            public double[] value(double[] coord, double[] output) {
                if (null == output) {
                    output = new double[2];
                }

                getStress(coord, cache);
                output[0] = cache[0];
                output[1] = cache[2];
                return output;
            }
        };
        rightBC.markFunc = null;
        return rightBC;
    }

    private Common2DTask.BCSpecification genUpSideBC(double margin) {
        Common2DTask.BCSpecification upBC = new Common2DTask.BCSpecification();
        upBC.from = new double[]{-margin, -margin + sideLen};
        upBC.to = new double[]{sideLen + margin, sideLen + margin};

        upBC.valueFunc = new GenericFunction<double[], double[]>() {
            double[] cache = new double[3];

            @Override
            public double[] value(double[] coord, double[] output) {
                if (null == output) {
                    output = new double[2];
                }

                getStress(coord, cache);
                output[0] = cache[2];
                output[1] = cache[1];
                return output;
            }
        };
        upBC.markFunc = null;
        return upBC;
    }

    private ConstitutiveLaw genConstitutiveLaw() {
        return new PlaneStress(E, mu);
    }

    public static void main(String[] args) {
        TimoshenkoHoleyPlate plate = new TimoshenkoHoleyPlate();

        SimpMFMechanicalProject project = plate.produce();
        MFLinearMechanicalProcessor processor = new MFLinearMechanicalProcessor();
        processor.setProject(project);
        processor.getSettings().put(MFConstants.KEY_ENABLE_MULTI_THREAD, false);
        processor.preprocess();
        processor.solve();

        PostProcessor pp = processor.genPostProcessor();
        MechanicalPostProcessor mpp = processor.genMechanicalPostProcessor();

        int stepNum = 20;
        double margin = 0.01;
        LinkedList<double[]> samplePts = new LinkedList<>();
        for (int i = 0; i < stepNum; i++) {
            samplePts.add(new double[]{plate.sideLen * 0.5, margin + (plate.sideLen - margin * 2) * (i * 1.0 / (stepNum - 1))});
        }

        for (int i = 0; i < stepNum; i++) {
            samplePts.add(new double[]{margin + (plate.sideLen - margin * 2) * (i * 1.0 / (stepNum - 1)), plate.sideLen * 0.5});
        }
        final ConstitutiveLaw constitutiveLaw = project.getConstitutiveLaw();
        for (double[] pt : samplePts) {
            double[] engineeringStrain = mpp.engineeringStrain(pt, null);

            double[] act = constitutiveLaw.calcStressByEngineering(engineeringStrain, null);
            double[] exp = plate.getStress(pt, null);
            System.out.println("");
            System.out.println("pt = " + Arrays.toString(pt));
            System.out.println("act = " + Arrays.toString(act));
            System.out.println("exp = " + Arrays.toString(exp));
        }
    }
}

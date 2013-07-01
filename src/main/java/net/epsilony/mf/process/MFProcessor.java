/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.epsilony.mf.model.MFNode;
import net.epsilony.mf.process.assembler.Assembler;
import net.epsilony.tb.matrix.ReverseCuthillMcKeeSolver;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MFProcessor {

    public static final Logger logger = LoggerFactory.getLogger(MFProcessor.class);
    List<MFProcessWorker> runnables;
    private DenseVector nodesValue;
    List<MFNode> modelNodes;
    List<MFNode> extraLagNodes;

    public void setRunnables(List<MFProcessWorker> runnables) {
        if (null == runnables || runnables.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.runnables = runnables;
    }

    public void setModelNodes(List<MFNode> modelNodes) {
        this.modelNodes = modelNodes;
    }

    public void setExtraLagNodes(List<MFNode> extraLagNodes) {
        this.extraLagNodes = extraLagNodes;
    }

    public void process() {
        ExecutorService executor = Executors.newFixedThreadPool(runnables.size());
        for (MFProcessWorker runnable : runnables) {
            executor.execute(runnable);
            logger.info("execute {}", runnable);
        }
        logger.info("Processing with {} threads", runnables.size());

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                executor.awaitTermination(1000, TimeUnit.MICROSECONDS);
            } catch (InterruptedException ex) {
                logger.error("Processing interrupted {}", ex);
                break;
            }
        }

        if (runnables.size() > 1) {
            logger.info("start merging {} assemblers", runnables.size());
            Iterator<MFProcessWorker> iter = runnables.iterator();
            Assembler assembler = iter.next().getAssembler();
            int count = 1;
            while (iter.hasNext()) {
                assembler.mergeWithBrother(iter.next().getAssembler());
                count++;
                logger.info("mergied {}/{} assemblers", count, runnables.size());
            }
        }
    }

    public void solve() {
        Assembler assembler = runnables.get(0).getAssembler();
        Matrix mainMatrix = assembler.getMainMatrix();
        DenseVector mainVector = assembler.getMainVector();
        ReverseCuthillMcKeeSolver rcm = new ReverseCuthillMcKeeSolver(mainMatrix, assembler.isUpperSymmertric());
        logger.info("solving main matrix:{}, bandwidth ori/opt: {}/{}",
                rcm,
                rcm.getOriginalBandWidth(),
                rcm.getOptimizedBandWidth());
        nodesValue = rcm.solve(mainVector);
        logger.info("solved main matrix");
        int nodeValueDimension = getNodeValueDimension();
        for (MFNode node : modelNodes) {

            int nodeValueIndex = node.getAssemblyIndex() * nodeValueDimension;
            if (nodeValueIndex >= 0) {
                double[] nodeValue = new double[nodeValueDimension];
                for (int i = 0; i < nodeValueDimension; i++) {
                    nodeValue[i] = nodesValue.get(i + nodeValueIndex);
                    node.setValue(nodeValue);
                }
            }

            int lagrangeValueIndex = node.getLagrangeAssemblyIndex() * nodeValueDimension;
            if (lagrangeValueIndex >= 0) {
                double[] lagrangeValue = new double[nodeValueDimension];
                for (int i = 0; i < nodeValueDimension; i++) {
                    lagrangeValue[i] = nodesValue.get(i + lagrangeValueIndex);
                    node.setLagrangleValue(lagrangeValue);
                }
            }
        }
        if (extraLagNodes != null && !extraLagNodes.isEmpty()) {
            for (MFNode node : extraLagNodes) {
                int lagrangeValueIndex = node.getLagrangeAssemblyIndex() * nodeValueDimension;
                if (lagrangeValueIndex >= 0) {
                    double[] lagrangeValue = new double[nodeValueDimension];
                    for (int i = 0; i < nodeValueDimension; i++) {
                        lagrangeValue[i] = nodesValue.get(i + lagrangeValueIndex);
                        node.setLagrangleValue(lagrangeValue);
                    }
                }
            }
        }
        logger.info("filled nodes values to nodes processor data map");
    }

    public int getNodeValueDimension() {
        return runnables.get(0).getAssembler().getNodeValueDimension();
    }
}
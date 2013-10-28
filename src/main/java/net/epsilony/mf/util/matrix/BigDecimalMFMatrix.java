/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util.matrix;

import java.math.BigDecimal;
import java.util.Iterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface BigDecimalMFMatrix extends MFMatrix {

    void add(int row, int col, BigDecimal value);

    BigDecimal getBigDecimal(int row, int col);

    void set(int row, int col, BigDecimal value);

    Iterator<BigDecimalMatrixEntry> bigDecimalIterator();
}

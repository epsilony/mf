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

package net.epsilony.mf.process;

import java.util.HashMap;
import java.util.Map;
import net.epsilony.mf.process.integrate.MFIntegrator;
import net.epsilony.mf.process.integrate.MFIntegratorFactory;
import net.epsilony.mf.process.solver.MFSolver;
import net.epsilony.mf.process.solver.RcmSolver;
import net.epsilony.mf.util.MFKey;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public enum MFPreprocessorKey implements MFKey {

    INTEGRATOR(MFIntegrator.class),
    MAIN_MATRIX_SOLVER(MFSolver.class);

    private MFPreprocessorKey(Class<?> valueType) {
        this.valueType = valueType;
    }

    private final Class<?> valueType;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Class<?> getValueType() {
        return valueType;
    }

    public static Map<MFKey, Object> getDefaultSettings() {
        Map<MFKey, Object> result = new HashMap<>();
        result.put(INTEGRATOR, new MFIntegratorFactory().produce());
        result.put(MAIN_MATRIX_SOLVER, new RcmSolver());
        return result;
    }

}

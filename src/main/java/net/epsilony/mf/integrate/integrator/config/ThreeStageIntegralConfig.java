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
package net.epsilony.mf.integrate.integrator.config;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import net.epsilony.mf.integrate.integrator.GeomQuadraturePointToAssemblyInput;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.process.assembler.AssemblyInput;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class ThreeStageIntegralConfig extends IntegralBaseConfig {

    // optional required beens:
    public static final String VOLUME_UNIT_TO_GEOM_QUADRATURE_POINTS_PROTO = "volumeUnitToGeomQuadraturePointsProto";
    public static final String NEUMANN_UNIT_TO_GEOM_QUADRATURE_POINTS_PROTO = "neumannUnitToGeomQuadraturePointsProto";
    public static final String DIRICHLET_UNIT_TO_GEOM_QUADRATURE_POINTS_PROTO = "dirichletUnitToGeomQuadraturePointsProto";

    // end of optional requirements
    @Bean(name = INTEGRAL_COLLECTION_PROTO)
    @Scope("prototype")
    public ThreeStageIntegralCollection integralCollectionProto() {
        GeomQuadraturePointToAssemblyInput pointToAsmInputProto = pointToAsmInputProto();
        GeomQuadraturePointToAssemblyInput pointToDiffAsmInputProto = pointToDiffAsmInputProto();
        Function<GeomQuadraturePoint, AssemblyInput> dirichletPointToAsm;
        if (applicationContext.containsBean(IS_LAGRANGLE_DIRICHLET)
                && !applicationContext.getBean(IS_LAGRANGLE_DIRICHLET, Boolean.class)) {
            dirichletPointToAsm = pointToAsmInputProto;
        } else {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Function<GeomQuadraturePoint, AssemblyInput> pointToLagrangleAsmInputProto = (Function) pointToLagrangleAsmInputProto();
            dirichletPointToAsm = pointToLagrangleAsmInputProto;
        }

        FunctionIntegratorGroup<GeomQuadraturePoint, AssemblyInput> pointToAssemblyInputGroup = new FunctionIntegratorGroup<>(
                pointToDiffAsmInputProto, pointToAsmInputProto, dirichletPointToAsm);

        ThreeStageIntegralCollection result = new ThreeStageIntegralCollection(getUnitToGeomQuadraturePointsGroup(),
                pointToAssemblyInputGroup, assemblerIntegratorsGroupProto());
        integralGroupCollections().add(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public FunctionIntegratorGroup<Object, Stream<GeomQuadraturePoint>> getUnitToGeomQuadraturePointsGroup() {
        Function<Object, Stream<GeomQuadraturePoint>> volume, neumann, dirichlet;
        if (applicationContext.containsBean(VOLUME_UNIT_TO_GEOM_QUADRATURE_POINTS_PROTO)) {
            volume = (Function<Object, Stream<GeomQuadraturePoint>>) applicationContext
                    .getBean(VOLUME_UNIT_TO_GEOM_QUADRATURE_POINTS_PROTO);
        } else {
            Function<Object, Collection<GeomQuadraturePoint>> t = (Function<Object, Collection<GeomQuadraturePoint>>) applicationContext
                    .getBean(CommonToPointsIntegratorConfig.COMMON_UNIT_TO_POINTS_PROTO);
            volume = t.andThen(Collection::stream);
        }
        if (applicationContext.containsBean(VOLUME_UNIT_TO_GEOM_QUADRATURE_POINTS_PROTO)) {
            neumann = (Function<Object, Stream<GeomQuadraturePoint>>) applicationContext
                    .getBean(NEUMANN_UNIT_TO_GEOM_QUADRATURE_POINTS_PROTO);
        } else {
            Function<Object, Collection<GeomQuadraturePoint>> t = (Function<Object, Collection<GeomQuadraturePoint>>) applicationContext
                    .getBean(CommonToPointsIntegratorConfig.COMMON_UNIT_TO_POINTS_PROTO);
            neumann = t.andThen(Collection::stream);
        }
        if (applicationContext.containsBean(VOLUME_UNIT_TO_GEOM_QUADRATURE_POINTS_PROTO)) {
            dirichlet = (Function<Object, Stream<GeomQuadraturePoint>>) applicationContext
                    .getBean(DIRICHLET_UNIT_TO_GEOM_QUADRATURE_POINTS_PROTO);
        } else {
            Function<Object, Collection<GeomQuadraturePoint>> t = (Function<Object, Collection<GeomQuadraturePoint>>) applicationContext
                    .getBean(CommonToPointsIntegratorConfig.COMMON_UNIT_TO_POINTS_PROTO);
            dirichlet = t.andThen(Collection::stream);
        }
        return new FunctionIntegratorGroup<Object, Stream<GeomQuadraturePoint>>(volume, neumann, dirichlet);
    }
}

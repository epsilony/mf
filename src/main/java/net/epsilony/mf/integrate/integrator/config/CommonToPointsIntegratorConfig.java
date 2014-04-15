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
import java.util.List;
import java.util.function.Function;

import net.epsilony.mf.integrate.integrator.LineToGeomQuadraturePoints;
import net.epsilony.mf.integrate.integrator.NodeToGeomQuadraturePoints;
import net.epsilony.mf.integrate.integrator.PolygonToGeomQuadraturePoints;
import net.epsilony.mf.integrate.unit.GeomQuadraturePoint;
import net.epsilony.mf.integrate.unit.PolygonIntegrateUnit;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.function.TypeMapFunction;
import net.epsilony.tb.solid.Line;
import net.epsilony.tb.solid.Node;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Configuration
public class CommonToPointsIntegratorConfig {
    public static final String COMMON_UNIT_TO_POINTS_PROTO = "commonUnitToPointsProto";

    @Bean(name = COMMON_UNIT_TO_POINTS_PROTO)
    @Scope("prototype")
    public Function<Object, Collection<? extends GeomQuadraturePoint>> commonUnitToPointsProto() {
        TypeMapFunction<Object, Collection<? extends GeomQuadraturePoint>> typeMapFunction = new TypeMapFunction<>();
        typeMapFunction.register(PolygonIntegrateUnit.class, polygonToPointsProto());
        typeMapFunction.register(Line.class, lineToPointsProto());
        typeMapFunction.register(Node.class, new NodeToGeomQuadraturePoints());
        typeMapFunction.register(GeomQuadraturePoint.class, Function.identity());
        return typeMapFunction;
    }

    public static final String QUADRATURE_DEGREE_BUS = "quadratureDegreeBus";

    @Bean(name = QUADRATURE_DEGREE_BUS)
    public WeakBus<Integer> quadratureDegreeBus() {
        return new WeakBus<>(QUADRATURE_DEGREE_BUS);
    }

    public static final String POLYGON_TO_POINTS_PROTO = "polygonToPointsProto";

    @Bean(name = POLYGON_TO_POINTS_PROTO)
    @Scope("prototype")
    public Function<PolygonIntegrateUnit, List<GeomQuadraturePoint>> polygonToPointsProto() {
        PolygonToGeomQuadraturePoints polygonToGeomQuadraturePoints = new PolygonToGeomQuadraturePoints();
        quadratureDegreeBus().register(PolygonToGeomQuadraturePoints::setDegree, polygonToGeomQuadraturePoints);
        return polygonToGeomQuadraturePoints;
    }

    public static final String LINE_TO_POINTS_PROTO = "lineToPointsProto";

    @Bean(name = LINE_TO_POINTS_PROTO)
    @Scope("prototype")
    public Function<Line, List<GeomQuadraturePoint>> lineToPointsProto() {
        LineToGeomQuadraturePoints lineToGeomQuadraturePoints = new LineToGeomQuadraturePoints();
        quadratureDegreeBus().register(LineToGeomQuadraturePoints::setQuadratureDegree, lineToGeomQuadraturePoints);
        return lineToGeomQuadraturePoints;
    }

}

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
package net.epsilony.mf.opt.persist.config;

import java.util.HashMap;
import java.util.Map;

import net.epsilony.mf.opt.config.OptBaseConfig;
import net.epsilony.mf.opt.nlopt.NloptMFuncCore;
import net.epsilony.mf.opt.persist.OptIndexialMongoDBRecorder;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Aspect
public class OptObjectMongoDBAspect {
    private static final String POINT_CUT_VALUE = "bean(" + OptBaseConfig.OPT_OBJECT_CORE
            + ") && execution(void apply(..))";

    OptIndexialMongoDBRecorder parameterRecorder;
    OptIndexialMongoDBRecorder resultGradientRecorder;

    @Before(value = POINT_CUT_VALUE)
    public void beforeApply(JoinPoint joinPoint) {
        double[] parameter = (double[]) joinPoint.getArgs()[0];
        parameterRecorder.record("parameter", parameter);
    }

    private final Map<String, Object> map = new HashMap<>();

    @AfterReturning(value = POINT_CUT_VALUE)
    public void afterApply(JoinPoint joinPoint) {
        NloptMFuncCore core = (NloptMFuncCore) joinPoint.getTarget();
        double value = core.getResults()[0];
        map.put("result", value);
        map.put("gradient", core.getGradients()[0]);
        core.logger.info("objValue = {}", value);
        resultGradientRecorder.record(map);
    }

    public void setParameterRecorder(OptIndexialMongoDBRecorder parameterRecorder) {
        this.parameterRecorder = parameterRecorder;
    }

    public void setResultGradientRecorder(OptIndexialMongoDBRecorder resultGradientRecorder) {
        this.resultGradientRecorder = resultGradientRecorder;
    }

}

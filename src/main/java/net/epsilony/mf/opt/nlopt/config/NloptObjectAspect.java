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
package net.epsilony.mf.opt.nlopt.config;

import java.util.HashMap;
import java.util.Map;

import net.epsilony.mf.opt.nlopt.NloptMFuncCore;
import net.epsilony.mf.opt.persist.OptIndexialRecorder;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Aspect
public class NloptObjectAspect {
    private static final String POINT_CUT_VALUE = "bean(" + NloptConfig.NLOPT_OBJECT_CORE
            + ") && execution(void apply(..))";

    OptIndexialRecorder recorder;

    public static final Logger logger = LoggerFactory.getLogger(NloptObjectAspect.class);

    @Before(value = POINT_CUT_VALUE)
    public void beforeApply(JoinPoint joinPoint) {
        double[] parameter = (double[]) joinPoint.getArgs()[0];
        recorder.record("parameter", parameter);
    }

    private final Map<String, Object> map = new HashMap<>();

    @AfterReturning(value = POINT_CUT_VALUE)
    public void afterApply(JoinPoint joinPoint) {
        NloptMFuncCore core = (NloptMFuncCore) joinPoint.getTarget();
        double value = core.getResults()[0];
        map.put("result", value);
        double[] gradient = core.getGradients()[0];
        map.put("gradient", gradient);
        logger.info("objValue[{}] = {}", recorder.getIndex(), value);
        recorder.update(map);
    }

    OptIndexialRecorder getRecorder() {
        return recorder;
    }

    void setRecorder(OptIndexialRecorder recorder) {
        this.recorder = recorder;
    }

}

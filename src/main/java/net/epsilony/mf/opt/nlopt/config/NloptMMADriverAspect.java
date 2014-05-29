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

import java.util.Map;

import net.epsilony.mf.opt.nlopt.NloptMMADriver;
import net.epsilony.mf.opt.persist.OptRootRecorder;
import net.epsilony.mf.util.persist.RecordUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
@Aspect
public class NloptMMADriverAspect {

    public static final String POINT_CUT = "bean(" + NloptConfig.NLOPT_MMA_DRIVER
            + ") &&  execution(void doOptimize())";

    private OptRootRecorder recorder;

    @Before(value = POINT_CUT)
    public void beforeDoOptimize(JoinPoint joinPoint) {
        NloptMMADriver nloptMMADriver = (NloptMMADriver) joinPoint.getTarget();
        Map<String, Object> valueMap = RecordUtils.readRecordFields(nloptMMADriver);
        recorder.prepareRecord(valueMap);
        recorder.record();
    }

    @AfterReturning(POINT_CUT)
    public void afterReturningDoOptimize(JoinPoint joinPoint) {
        NloptMMADriver nloptMMADriver = (NloptMMADriver) joinPoint.getTarget();
        Map<String, Object> valueMap = RecordUtils.readRecordFields(nloptMMADriver);
        recorder.update(valueMap);
    }

    public OptRootRecorder getRecorder() {
        return recorder;
    }

    public void setRecorder(OptRootRecorder recorder) {
        this.recorder = recorder;
    }

}

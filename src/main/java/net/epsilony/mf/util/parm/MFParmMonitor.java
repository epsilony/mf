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
package net.epsilony.mf.util.parm;

import java.beans.PropertyDescriptor;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public interface MFParmMonitor {
    void packSetup(Object srcBean);

    public static class MFParmTransferImp implements MFParmMonitor {
        public final Logger   logger = LoggerFactory.getLogger(MFParmTransferImp.class);
        private MFParmIndexer parmIndexer;
        private Object        target;

        @Override
        public void packSetup(Object srcBean) {
            logger.info("start setting {} -> {}", srcBean, target);

            BeanMap srcMap = new BeanMap(srcBean);
            BeanMap destMap = new BeanMap(target);
            for (Map.Entry<String, PropertyDescriptor> entry : parmIndexer.getNameToDescriptor().entrySet()) {
                String parameter = entry.getKey();
                if (!srcMap.containsKey(parameter)) {
                    continue;
                }

                Object value = srcMap.get(parameter);
                destMap.put(parameter, value);
            }
            logger.info("finished {} -> {}", srcBean, target);
        }
    }
}

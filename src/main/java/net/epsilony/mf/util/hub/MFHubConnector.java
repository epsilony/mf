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
package net.epsilony.mf.util.hub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.epsilony.mf.util.MFBeanUtils;

import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFHubConnector {
    private Map<Object, Object>              srcDestMap    = new HashMap<>();
    private Map<Object, Map<String, Object>> setupRecorder = new HashMap<>();
    public static Logger                     logger        = LoggerFactory.getLogger(MFHubConnector.class);

    public void connect(Object src, Object dest) {

        srcDestMap.put(src, dest);
        logger.info("start connecting {} -> {}", src, dest);

        BeanMap srcMap = new BeanMap(src);
        BeanMap destMap = new BeanMap(dest);

        Map<String, Object> record = new HashMap<>();
        MFBeanUtils.writablePropertyDescriptorStream(dest, true).forEach(descriptor -> {
            String name = descriptor.getName();
            Object value = srcMap.get(name);
            if (null != value) {
                destMap.put(name, value);
                record.put(name, value);
                logger.info("set {}: {}", name, value);
            }
        });

        Map<String, Object> formerDestRecord = setupRecorder.get(dest);
        if (formerDestRecord == null) {
            setupRecorder.put(dest, record);
        } else {
            formerDestRecord.putAll(record);
        }
        logger.info("finished connecting {} -> {}", src, dest);
    }

    public Map<Object, Set<String>> unsetupPropertiesMap(boolean includeOptional) {
        Map<Object, Set<String>> result = new HashMap<>();

        Set<Object> allDests = new HashSet<>(srcDestMap.values());

        for (Object dest : allDests) {
            Map<String, Object> connected = setupRecorder.get(dest);
            MFBeanUtils.writablePropertyDescriptorStream(dest, includeOptional).forEach(descriptor -> {
                String name = descriptor.getName();
                if (connected.get(name) == null) {
                    Set<String> unsetup = result.get(dest);
                    if (null == unsetup) {
                        unsetup = new HashSet<>();
                        unsetup.add(name);
                        result.put(dest, unsetup);
                    } else {
                        unsetup.add(name);
                    }
                }
            });
        }
        return result;
    }

    public Map<Object, Object> srcDestMap() {
        return srcDestMap;
    }

    public Map<Object, Map<String, Object>> setupRecorder() {
        return setupRecorder;
    }

}

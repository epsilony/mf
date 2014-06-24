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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import net.epsilony.mf.util.parm.MFParmIndex.MFParmDescriptor;

import org.springframework.context.ApplicationContext;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class MFParmContainerPool {

    private List<MFParmContainer>              parmContainers = new ArrayList<>();
    private Map<String, MFParmContainer>       globalBusContainers;
    private Map<String, List<MFParmContainer>> openParmContainers;

    public void addParmContainer(MFParmContainer container) {
        parmContainers.add(container);
    }

    public void prepare() {
        collectGlobalBuses();
        connectContainers();
    }

    public List<MFParmContainer> getParmContainers() {
        return parmContainers;
    }

    public void setOpenParm(String parm, Object value) {
        List<MFParmContainer> containers = openParmContainers.get(parm);
        for (MFParmContainer container : containers) {
            container.setParmValue(parm, value);
        }
    }

    public Map<String, MFParmContainer> getGlobalBusContainers() {
        return globalBusContainers;
    }

    public Map<String, List<MFParmContainer>> getOpenParmContainers() {
        return openParmContainers;
    }

    private void collectGlobalBuses() {
        globalBusContainers = new LinkedHashMap<>();
        for (MFParmContainer container : parmContainers) {
            addToGlobalBuses(container);
        }
    }

    private void addToGlobalBuses(MFParmContainer container) {
        TriggerParmToBusSwitcher switcher = container.parmToBusSwitcher();
        Set<String> busNames = switcher.getBusNames();
        for (String busName : busNames) {
            if (switcher.isBusGlobal(busName)) {
                addToGlobalBuses(busName, container);
            }
        }
    }

    private void addToGlobalBuses(String busName, MFParmContainer container) {
        if (globalBusContainers.containsValue(busName)) {
            throw new IllegalStateException("duplicate global bus: " + busName + " [" + container + "]");
        }
        globalBusContainers.put(busName, container);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void connectContainers() {
        openParmContainers = new LinkedHashMap<>();
        for (MFParmContainer container : parmContainers) {
            MFParmIndex parmIndex = container.parmIndex();
            Map<String, MFParmDescriptor> parmDescriptors = parmIndex.getParmDescriptors();
            for (Map.Entry<String, MFParmDescriptor> mapEntry : parmDescriptors.entrySet()) {
                String parm = mapEntry.getKey();
                MFParmContainer globalBusContainer = globalBusContainers.get(parm);
                if (null == globalBusContainer || globalBusContainer == container) {
                    addOpenParm(parm, container);
                    continue;
                }
                MFParmDescriptor descriptor = mapEntry.getValue();
                if (descriptor.isAsSubBus()) {
                    globalBusContainer.parmToBusSwitcher().registerAsSubBus(parm,
                            (BiConsumer) descriptor.getObjectValueSetter(), container);
                } else {
                    globalBusContainer.parmToBusSwitcher().register(parm, descriptor.getObjectValueSetter(), container);
                }
            }
        }
    }

    private void addOpenParm(String parm, MFParmContainer container) {
        List<MFParmContainer> containers = openParmContainers.get(parm);
        if (null == containers) {
            containers = new ArrayList<>();
            openParmContainers.put(parm, containers);
        }
        containers.add(container);
    }

    public static MFParmContainerPool fromApplicationContext(ApplicationContext ac) {
        MFParmContainerPool result = new MFParmContainerPool();
        Map<String, MFParmContainer> containers = ac.getBeansOfType(MFParmContainer.class);
        for (MFParmContainer container : containers.values()) {
            result.addParmContainer(container);
        }
        result.prepare();
        return result;
    }
}

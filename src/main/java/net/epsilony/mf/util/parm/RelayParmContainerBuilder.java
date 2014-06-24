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

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.epsilony.mf.util.parm.MFParmIndex.MFParmDescriptor;

/**
 * @author Man YUAN <epsilonyuan@gmail.com>
 *
 */
public class RelayParmContainerBuilder implements Supplier<MFParmContainer> {
    private MFParmContainerImp container = new MFParmContainerImp();

    private class MFParmContainerImp implements MFParmContainer {
        private MFParmIndex              parmIndex = new MFParmIndex();
        {
            parmIndex.setParmDescriptors(new LinkedHashMap<>());
        }
        private TriggerParmToBusSwitcher switcher  = new TriggerParmToBusSwitcher();

        @Override
        public TriggerParmToBusSwitcher parmToBusSwitcher() {
            return switcher;
        }

        @Override
        public MFParmIndex parmIndex() {
            return parmIndex;
        }

        private class SimpMFParmDescriptor implements MFParmDescriptor {
            boolean  asSubBus;
            String[] nameOrAims = new String[1];
            boolean  optional;
            Object   value;

            @Override
            public boolean isAsSubBus() {
                return asSubBus;
            }

            @Override
            public String getName() {
                return nameOrAims[0];
            }

            @Override
            public String[] getTriggerAim() {
                return nameOrAims;
            }

            public void setName(String name) {
                nameOrAims[0] = name;
            }

            @Override
            public boolean isTrigger() {
                return true;
            }

            @Override
            public boolean isOptional() {
                return optional;
            }

            public Object getValue() {
                if (asSubBus) {
                    @SuppressWarnings("unchecked")
                    Supplier<Object> asSupplier = (Supplier<Object>) value;
                    return asSupplier.get();
                } else {
                    return value;
                }
            }

            public void setValue(Object value) {
                if (isAsSubBus() && !(value instanceof Supplier)) {
                    throw new IllegalArgumentException();
                }
                this.value = value;
            }

            public void setAsSubBus(boolean asSubBus) {
                this.asSubBus = asSubBus;
            }

            public void setOptional(boolean optional) {
                this.optional = optional;
            }

            @Override
            public BiConsumer<Object, Object> getObjectValueSetter() {
                return (obj, value) -> {
                    if (obj != MFParmContainerImp.this) {
                        throw new IllegalStateException();
                    }
                    setValue(value);
                };
            }

        }
    }

    public RelayParmContainerBuilder addParm(String parm, boolean asSubBus, boolean optional) {

        MFParmContainerImp.SimpMFParmDescriptor descriptor = container.new SimpMFParmDescriptor();
        descriptor.setAsSubBus(asSubBus);
        descriptor.setOptional(optional);
        descriptor.setName(parm);
        index().getParmDescriptors().put(parm, descriptor);

        switcher().addTriggerParm(parm, descriptor.getTriggerAim());

        switcher().setBusValueSource(parm, descriptor::getValue);

        return this;
    }

    public RelayParmContainerBuilder addParm(String parm) {
        return addParm(parm, false, false);
    }

    public RelayParmContainerBuilder addParm(String parm, boolean asSubBus) {
        return addParm(parm, asSubBus, false);
    }

    public RelayParmContainerBuilder addParms(String... parms) {
        for (String parm : parms) {
            addParm(parm);
        }
        return this;
    }

    private TriggerParmToBusSwitcher switcher() {
        return container.parmToBusSwitcher();
    }

    private MFParmIndex index() {
        return container.parmIndex();
    }

    @Override
    public MFParmContainer get() {
        return container;
    }

}

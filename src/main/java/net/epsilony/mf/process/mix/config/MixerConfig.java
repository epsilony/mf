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
package net.epsilony.mf.process.mix.config;

import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.mf.model.support_domain.config.SupportDomainBaseConfig;
import net.epsilony.mf.process.mix.MFMixer;
import net.epsilony.mf.process.mix.MFMixerFunctionPack;
import net.epsilony.mf.process.mix.Mixer;
import net.epsilony.mf.shape_func.MFShapeFunction;
import net.epsilony.mf.shape_func.config.ShapeFunctionBaseConfig;
import net.epsilony.mf.util.bus.WeakBus;
import net.epsilony.mf.util.spring.ApplicationContextAwareImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class MixerConfig extends ApplicationContextAwareImpl {

    public static final String MIXER_PROTO = "mixerProto";
    public static final String MIXER_MAX_RADIUS_BUS = "mixerMaxRadiusBus";

    @Bean(name = MIXER_MAX_RADIUS_BUS)
    public WeakBus<Double> mixerMaxRadiusBus() {
        return new WeakBus<>(MIXER_MAX_RADIUS_BUS);
    }

    @Bean(name = MIXER_PROTO)
    @Scope("prototype")
    public Mixer mixerProto() {
        Mixer result = new Mixer();
        result.setShapeFunction(applicationContext.getBean(ShapeFunctionBaseConfig.SHAPE_FUNCTION_PROTO,
                MFShapeFunction.class));
        result.setSupportDomainSearcher(applicationContext.getBean(
                SupportDomainBaseConfig.INFLUENCED_SUPPORT_DOMAIN_SEARCHER_PROTO, SupportDomainSearcher.class));
        mixerMaxRadiusBus().register(Mixer::setRadius, result);
        return result;
    }

    public static final String MIXER_FUNCTION_PACK_PROTO = "mixerFunctionPackProto";

    @Bean(name = MIXER_FUNCTION_PACK_PROTO)
    @Scope("prototype")
    public MFMixerFunctionPack levelMixerFunctionPackProto() {
        MFMixerFunctionPack result = new MFMixerFunctionPack();
        result.setMixer(applicationContext.getBean(MixerConfig.MIXER_PROTO, MFMixer.class));
        return result;
    }
}

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
package net.epsilony.mf.util.spring;

import net.epsilony.mf.util.OnlyHolder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
@Configuration
public class MultiThreadConfig extends ApplicationContextAwareImpl {

    @Bean
    public Integer threadNum() {
        if (applicationContext.containsBean("threadNumHolder")) {
            @SuppressWarnings("unchecked")
            OnlyHolder<Integer> threadNumHolder = (OnlyHolder<Integer>) applicationContext.getBean("threadNumHolder");
            return threadNumHolder.getValue();
        }
        return defaultThreadNum();
    }

    public static int defaultThreadNum() {
        return Runtime.getRuntime().availableProcessors();
    }
}

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
package net.epsilony.mf.sample;

import java.util.List;

import net.epsilony.tb.Factory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public abstract class AbstractSimpJavaConfigContextFactory implements Factory<ApplicationContext> {

    protected AnnotationConfigApplicationContext context;
    private List<Class<?>> extraContextSettings;

    protected abstract void fillContextSettings();

    public AbstractSimpJavaConfigContextFactory() {
        super();
    }

    @Override
    public ApplicationContext produce() {

        context = new AnnotationConfigApplicationContext();
        fillContextSettings();
        fillExtraContextSettings();
        context.refresh();

        return context;
    }

    public void setExtraContextSettings(List<Class<?>> extraContextSettings) {
        this.extraContextSettings = extraContextSettings;
    }

    protected void fillExtraContextSettings() {
        if (null == extraContextSettings) {
            return;
        }
        context.register(extraContextSettings.toArray(new Class<?>[0]));
    }

}
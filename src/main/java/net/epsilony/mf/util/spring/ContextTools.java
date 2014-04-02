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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.epsilony.mf.util.OnlyHolder;

import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class ContextTools {
    public static GenericBeanDefinition definition(Class<?> beanClass, Object... constructArgs) {
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(beanClass);
        ConstructorArgumentValues values = new ConstructorArgumentValues();
        for (Object arg : constructArgs) {
            values.addGenericArgumentValue(arg);
        }
        definition.setConstructorArgumentValues(values);
        return definition;
    }

    public static GenericBeanDefinition listDefinition(Object... objects) {
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(ArrayList.class);
        ConstructorArgumentValues values = new ConstructorArgumentValues();
        values.addGenericArgumentValue((Arrays.asList(objects)));
        definition.setConstructorArgumentValues(values);
        return definition;
    }

    public static Object beanOrInHolderBean(ApplicationContext applicationContext, String beanName) {
        if (applicationContext.containsBean(beanName)) {
            return applicationContext.getBean(beanName);
        }
        final String beanHolderName = beanName + "Holder";
        if (applicationContext.containsBean(beanName + "Holder")) {
            OnlyHolder<?> holder = (OnlyHolder<?>) applicationContext.getBean(beanHolderName);
            return holder.getValue();
        }

        return null;
    }

    public static List<String> notReallyProtoBeans(ApplicationContext ac, String suffix) {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        ArrayList<String> result = new ArrayList<>();
        Arrays.stream(beanDefinitionNames).filter((name) -> name.endsWith(suffix) && !ac.isPrototype(name))
                .forEach(result::add);
        return result;
    }

    public static List<String> notReallyProtoBeans(ApplicationContext ac) {
        return notReallyProtoBeans(ac, "Proto");
    }
}

/**
 * Copyright 2013-2014 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 * Email: subchen@gmail.com
 * URL: http://subchen.github.io/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrick.ioc.object;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import jetbrick.bean.KlassInfo;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.injector.*;

public final class ClassSingletonObject extends SingletonObject {
    protected final Class<?> beanClass;
    private Set<String> propNames;

    public ClassSingletonObject(Ioc ioc, Class<?> beanClass) {
        this(ioc, beanClass, null);
    }

    public ClassSingletonObject(Ioc ioc, Class<?> beanClass, Set<String> propNames) {
        super(ioc);
        this.beanClass = beanClass;
        this.propNames = propNames;
    }

    @Override
    protected Object doGetObject() throws Exception {
        KlassInfo klass = KlassInfo.create(beanClass);
        CtorInjector ctorInjector = IocObjectUtils.doGetCtorInjector(ioc, klass);
        List<FieldInjector> fieldInjectors = IocObjectUtils.doGetFieldInjectors(ioc, klass);
        List<PropertyInjector> propertyInjectors = IocObjectUtils.doGetPropertyInjectors(ioc, klass, propNames);
        Method initializeMethod = IocObjectUtils.doGetInitializeMethod(klass);

        propNames = null;

        Object object;
        if (ctorInjector == null) {
            object = beanClass.newInstance();
        } else {
            object = ctorInjector.newInstance();
        }

        for (PropertyInjector injector : propertyInjectors) {
            injector.set(object);
        }
        for (FieldInjector injector : fieldInjectors) {
            injector.set(object);
        }

        if (initializeMethod != null) {
            initializeMethod.invoke(object, (Object[]) null);
        }

        return object;
    }
}

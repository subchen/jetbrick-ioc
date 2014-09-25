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
import jetbrick.bean.KlassInfo;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.injector.CtorInjector;
import jetbrick.ioc.injector.FieldInjector;

public final class ClassInstanceObject extends InstanceObject {
    private final Class<?> beanClass;

    private CtorInjector ctorInjector;
    private List<FieldInjector> fieldInjectors;
    private Method initializeMethod;

    public ClassInstanceObject(Ioc ioc, Class<?> beanClass) {
        super(ioc);
        this.beanClass = beanClass;
    }

    @Override
    protected void initialize() {
        KlassInfo klass = KlassInfo.create(beanClass);
        ctorInjector = IocObjectUtils.doGetCtorInjector(ioc, klass);
        fieldInjectors = IocObjectUtils.doGetFieldInjectors(ioc, klass);
        initializeMethod = IocObjectUtils.doGetInitializeMethod(klass);
    }

    @Override
    protected Object doGetObject() throws Exception {
        Object object;
        if (ctorInjector == null) {
            object = beanClass.newInstance();
        } else {
            object = ctorInjector.newInstance();
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

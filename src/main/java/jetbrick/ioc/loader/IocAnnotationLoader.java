/**
 * Copyright 2013-2014 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 *   Author: Guoqiang Chen
 *    Email: subchen@gmail.com
 *   WebURL: https://github.com/subchen
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
package jetbrick.ioc.loader;

import java.lang.annotation.Annotation;
import java.util.Collection;
import jetbrick.io.finder.ClassFinder;
import jetbrick.ioc.MutableIoc;
import jetbrick.ioc.annotation.IocBean;
import jetbrick.util.annotation.ValueConstants;

public final class IocAnnotationLoader implements IocLoader {
    private Collection<Class<?>> klasses;

    public IocAnnotationLoader(String... packageNames) {
        @SuppressWarnings("unchecked")
        Class<? extends Annotation>[] annotations = new Class[] { IocBean.class };
        this.klasses = ClassFinder.getClasses(packageNames, true, annotations, true);
    }

    public IocAnnotationLoader(Collection<Class<?>> klasses) {
        this.klasses = klasses;
    }

    @Override
    public void load(MutableIoc ioc) {
        for (Class<?> klass : klasses) {
            IocBean anno = klass.getAnnotation(IocBean.class);
            if (anno != null) {
                String name = ValueConstants.defaultValue(anno.value(), klass.getName());
                ioc.addBean(name, klass, anno.singleton());
            }
        }

        // free
        klasses = null;
    }
}

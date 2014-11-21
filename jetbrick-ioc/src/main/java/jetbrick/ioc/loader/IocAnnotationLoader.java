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
import java.util.*;
import jetbrick.io.finder.ClassFinder;
import jetbrick.ioc.MutableIoc;
import jetbrick.ioc.annotation.IocBean;
import jetbrick.util.annotation.ValueConstants;

public final class IocAnnotationLoader implements IocLoader {
    private Collection<Class<?>> classes;

    public IocAnnotationLoader(String... packageNames) {
        List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>(1);
        annotations.add(IocBean.class);
        this.classes = ClassFinder.getClasses(Arrays.asList(packageNames), true, annotations, true);
    }

    public IocAnnotationLoader(Collection<Class<?>> classes) {
        this.classes = classes;
    }

    @Override
    public void load(MutableIoc ioc) {
        for (Class<?> cls : classes) {
            IocBean anno = cls.getAnnotation(IocBean.class);
            if (anno != null) {
                String name = ValueConstants.defaultValue(anno.value(), cls.getName());
                ioc.addBean(name, cls, anno.singleton());
            }
        }

        // free
        classes = null;
    }
}

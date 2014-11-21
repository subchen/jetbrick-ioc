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
package jetbrick.ioc.injector;

import java.lang.annotation.Annotation;
import jetbrick.bean.FieldInfo;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotation.Inject;
import jetbrick.util.Validate;
import jetbrick.util.annotation.ValueConstants;

//注入 @Inject 标注的字段
public final class InjectFieldInjector implements FieldInjector {
    private Ioc ioc;
    private String name;
    private FieldInfo field;
    private boolean required;

    @Override
    public void initialize(FieldContext ctx) {
        Annotation annotation = ctx.getAnnotation();
        Validate.isInstanceOf(Inject.class, annotation);

        Inject inject = (Inject) annotation;
        this.ioc = ctx.getIoc();
        this.field = ctx.getField();
        this.name = ValueConstants.defaultValue(inject.value(), ctx.getRawFieldTypeName()); // 默认是字段类型名
        this.required = inject.required();
    }

    @Override
    public void set(Object object) {
        Object value = ioc.getBean(name);
        if (value == null && required) {
            throw new IllegalStateException("Can't inject bean: " + name + " for field: " + field);
        }
        field.set(object, value);
    }
}

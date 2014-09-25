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
package jetbrick.ioc.injector;

import java.lang.annotation.Annotation;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotation.Inject;
import jetbrick.util.Validate;
import jetbrick.util.annotation.ValueConstants;

//注入 @Inject 标注的参数
public final class InjectParameterInjector implements ParameterInjector {
    private Ioc ioc;
    private String name;
    private boolean required;

    @Override
    public void initialize(ParameterContext ctx) {
        Annotation annotation = ctx.getAnnotation();
        Validate.isInstanceOf(Inject.class, annotation);

        Inject inject = (Inject) annotation;
        this.ioc = ctx.getIoc();
        this.required = inject.required();
        this.name = ValueConstants.defaultValue(inject.value(), ctx.getRawParameterTypeName()); // ，默认参数类型名
    }

    @Override
    public Object getObject() {
        Object value = ioc.getBean(name);
        if (value == null && required) {
            throw new IllegalStateException("Can't inject @Inject parameter: " + name);
        }
        return value;
    }
}

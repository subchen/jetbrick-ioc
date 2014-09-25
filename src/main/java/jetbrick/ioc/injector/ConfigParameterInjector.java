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
import java.util.Collection;
import java.util.List;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotation.Config;
import jetbrick.typecast.TypeCastUtils;
import jetbrick.util.Validate;
import jetbrick.util.annotation.ValueConstants;

// 注入 @Config 标注的参数
public final class ConfigParameterInjector implements ParameterInjector {
    private ParameterContext ctx;

    @Override
    public void initialize(ParameterContext ctx) {
        this.ctx = ctx;

        Annotation annotation = ctx.getAnnotation();
        Validate.isInstanceOf(Config.class, annotation);
    }

    @Override
    public Object getObject() {
        Config config = (Config) ctx.getAnnotation();
        String name = config.value();
        boolean required = config.required();

        Ioc ioc = ctx.getIoc();
        Class<?> type = ctx.getRawParameterType();
        String defaultValue = ValueConstants.trimToNull(config.defaultValue());

        Object value;
        if (type == List.class || type == Collection.class || type.isArray()) {
            Class<?> elementType;
            if (type.isArray()) {
                elementType = type.getComponentType();
            } else {
                elementType = ctx.getRawParameterComponentType(0);
            }

            value = ioc.getConfigAsList(config.value(), elementType);

            // list to array
            if (type.isArray()) {
                value = TypeCastUtils.convertToArray(value, elementType);
            }
        } else {
            value = ioc.getConfigAsValue(config.value(), type, defaultValue);
        }

        if (value == null && required) {
            throw new IllegalStateException("Can't inject @Config parameter: " + name);
        }

        return value;
    }
}

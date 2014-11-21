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
import java.util.Collection;
import java.util.List;
import jetbrick.bean.FieldInfo;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotation.Config;
import jetbrick.typecast.TypeCastUtils;
import jetbrick.util.Validate;
import jetbrick.util.annotation.ValueConstants;

// 注入 @Config 标注的字段
public final class ConfigFieldInjector implements FieldInjector {
    private FieldInfo field;
    private boolean required;
    private Object value;

    @Override
    public void initialize(FieldContext ctx) {
        Annotation annotation = ctx.getAnnotation();
        Validate.isInstanceOf(Config.class, annotation);

        Config config = (Config) annotation;
        this.field = ctx.getField();
        this.required = config.required();

        // 类型转换
        Ioc ioc = ctx.getIoc();
        Class<?> type = ctx.getRawFieldType();
        if (type == List.class || type == Collection.class || type.isArray()) {
            Class<?> elementType;
            if (type.isArray()) {
                elementType = type.getComponentType();
            } else {
                elementType = ctx.getRawFieldComponentType(0);
            }

            value = ioc.getConfigAsList(config.value(), elementType);

            // list to array
            if (type.isArray()) {
                value = TypeCastUtils.convertToArray(value, elementType);
            }
        } else {
            String defaultValue = ValueConstants.trimToNull(config.defaultValue());
            value = ioc.getConfigAsValue(config.value(), type, defaultValue);
        }
    }

    @Override
    public void set(Object object) {
        if (value == null && required) {
            throw new IllegalStateException("Can't inject field: " + field);
        }
        field.set(object, value);
    }
}

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
import jetbrick.bean.KlassInfo;
import jetbrick.ioc.Ioc;

// 负责注入字段
public interface FieldInjector {

    public void initialize(FieldContext ctx);

    public void set(Object object);

    public static final class FieldContext {
        final Ioc ioc;
        final KlassInfo declaringKlass;
        final FieldInfo field;
        final Annotation annotation;

        public FieldContext(Ioc ioc, KlassInfo declaringKlass, FieldInfo field, Annotation annotation) {
            this.ioc = ioc;
            this.declaringKlass = declaringKlass;
            this.field = field;
            this.annotation = annotation;
        }

        public Ioc getIoc() {
            return ioc;
        }

        public KlassInfo getDeclaringKlass() {
            return declaringKlass;
        }

        public FieldInfo getField() {
            return field;
        }

        public Annotation getAnnotation() {
            return annotation;
        }

        public String getFieldName() {
            return field.getName();
        }

        public Class<?> getRawFieldType() {
            return field.getRawType(declaringKlass);
        }

        public Class<?> getRawFieldComponentType(int index) {
            return field.getRawComponentType(declaringKlass.getType(), index);
        }

        public String getRawFieldTypeName() {
            return getRawFieldType().getName();
        }
    }
}

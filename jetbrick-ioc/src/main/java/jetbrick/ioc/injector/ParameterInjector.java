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
import jetbrick.bean.KlassInfo;
import jetbrick.bean.ParameterInfo;
import jetbrick.ioc.Ioc;

// 负责注入参数
public interface ParameterInjector {

    public static final ParameterInjector[] EMPTY_ARRAY = new ParameterInjector[0];

    public void initialize(ParameterContext ctx);

    public Object getObject();

    public static final class ParameterContext {
        final Ioc ioc;
        final KlassInfo declaringKlass;
        final ParameterInfo parameter;
        final Annotation annotation;

        public ParameterContext(Ioc ioc, KlassInfo declaringKlass, ParameterInfo parameter, Annotation annotation) {
            this.ioc = ioc;
            this.declaringKlass = declaringKlass;
            this.parameter = parameter;
            this.annotation = annotation;
        }

        public Ioc getIoc() {
            return ioc;
        }

        public KlassInfo getDeclaringKlass() {
            return declaringKlass;
        }

        public ParameterInfo getParameter() {
            return parameter;
        }

        public Annotation getAnnotation() {
            return annotation;
        }

        public String getParameterName() {
            return parameter.getName();
        }

        public Class<?> getRawParameterType() {
            return parameter.getRawType(declaringKlass);
        }

        public Class<?> getRawParameterComponentType(int index) {
            return parameter.getRawComponentType(declaringKlass.getType(), index);
        }

        public String getRawParameterTypeName() {
            return getRawParameterType().getName();
        }
    }

}

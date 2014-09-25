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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import jetbrick.bean.*;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotation.*;
import jetbrick.ioc.injector.*;
import jetbrick.ioc.injector.FieldInjector.FieldContext;
import jetbrick.ioc.injector.ParameterInjector.ParameterContext;
import jetbrick.util.ExceptionUtils;
import jetbrick.util.StringUtils;

public final class IocObjectUtils {

    //---------------------------------------------------------------------------
    // @Inject 标注的构造函数
    public static CtorInjector doGetCtorInjector(Ioc ioc, KlassInfo klass) {
        ConstructorInfo found = null;

        // 找到对应的构造函数
        for (ConstructorInfo ctor : klass.getDeclaredConstructors()) {
            Inject ref = ctor.getAnnotation(Inject.class);
            if (ref != null) {
                if (found != null) {
                    throw new IllegalStateException("More than two constructors are annotated as injection points in bean: " + klass);
                }
                found = ctor;
            }
        }
        if (found == null) {
            return null;
        }

        // 构造函数参数
        ParameterInjector[] injectors = ParameterInjector.EMPTY_ARRAY;
        List<ParameterInfo> parameters = found.getParameters();
        int size = parameters.size();
        if (size > 0) {
            injectors = new ParameterInjector[size];
            for (int i = 0; i < size; i++) {
                ParameterInfo parameter = parameters.get(i);

                Class<?> parameterInjectorClass = DefaultParameterInjector.class;
                Annotation parameterAnnotation = null;
                // 查找 @Inject/@Config 等标注
                for (Annotation annotation : parameter.getAnnotations()) {
                    InjectParameterWith with = annotation.annotationType().getAnnotation(InjectParameterWith.class);
                    if (with != null) {
                        parameterInjectorClass = with.value();
                        parameterAnnotation = annotation;
                        break;
                    }
                }
                try {
                    injectors[i] = (ParameterInjector) parameterInjectorClass.newInstance();
                    ParameterContext ctx = new ParameterContext(ioc, klass, parameter, parameterAnnotation);
                    injectors[i].initialize(ctx);
                } catch (Exception e) {
                    throw ExceptionUtils.unchecked(e);
                }
            }
        }

        //
        return new CtorInjector(found, injectors);
    }

    // @Inject/@Config 等标注的字段
    public static List<FieldInjector> doGetFieldInjectors(Ioc ioc, KlassInfo klass) {
        List<FieldInjector> injectors = new ArrayList<FieldInjector>(8);
        for (FieldInfo field : klass.getFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                InjectFieldWith with = annotation.annotationType().getAnnotation(InjectFieldWith.class);
                if (with != null) {
                    try {
                        FieldInjector injector = with.value().newInstance();
                        FieldContext ctx = new FieldContext(ioc, klass, field, annotation);
                        injector.initialize(ctx);
                        injectors.add(injector);
                    } catch (Exception e) {
                        throw ExceptionUtils.unchecked(e);
                    }
                }
            }
        }
        if (injectors.size() == 0) {
            return Collections.emptyList();
        }
        return injectors;
    }

    // 注入配置文件中自定义的属性字段
    public static List<PropertyInjector> doGetPropertyInjectors(Ioc ioc, KlassInfo klass, Set<String> propNames) {
        if (propNames == null || propNames.size() == 0) {
            return Collections.emptyList();
        }
        List<PropertyInjector> injectors = new ArrayList<PropertyInjector>();
        for (String name : propNames) {

            String propName = StringUtils.substringAfter(name, ".");
            PropertyInfo prop = klass.getProperty(propName);
            if (prop == null) {
                throw new IllegalStateException("Property not found: " + klass + "#" + propName);
            }
            if (!prop.writable()) {
                throw new IllegalStateException("Property not writable: " + prop);
            }

            Object value;
            Class<?> rawType = prop.getRawType(klass.getType());
            if (List.class.isAssignableFrom(rawType)) {
                value = ioc.getConfigAsList(name, prop.getRawComponentType(klass.getType(), 0));
            } else {
                value = ioc.getConfigAsValue(name, rawType);
            }
            injectors.add(new PropertyInjector(prop, value));
        }
        return injectors;
    }

    // @Initialize 标注的函数
    public static Method doGetInitializeMethod(KlassInfo klass) {
        MethodInfo found = null;
        for (MethodInfo method : klass.getMethods()) {
            IocInit ref = method.getAnnotation(IocInit.class);
            if (ref != null) {
                if (found != null) {
                    throw new IllegalStateException("More than two methods are annotated @Initialize in bean: " + klass);
                }
                if (method.getParameterCount() != 0) {
                    throw new IllegalStateException("@Initialize method parameters must be empty.");
                }
                found = method;
            }
        }
        return (found == null) ? null : found.getMethod();
    }
}

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
package jetbrick.ioc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import jetbrick.bean.KlassInfo;
import jetbrick.ioc.injector.CtorInjector;
import jetbrick.ioc.injector.FieldInjector;
import jetbrick.ioc.loader.IocLoader;
import jetbrick.ioc.object.*;
import jetbrick.util.ExceptionUtils;
import jetbrick.util.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MutableIoc implements Ioc {
    private final Logger log = LoggerFactory.getLogger(Ioc.class);
    private final Map<String, IocObject> pool = new HashMap<String, IocObject>();
    private final IocConfig config = new IocConfig(this);

    public void load(IocLoader loader) {
        loader.load(this);
    }

    public void loadConfig(Map<String, String> map) {
        config.load(map);
    }

    // 添加用户自定义的对象
    public void addBean(Object beanObject) {
        Validate.notNull(beanObject);
        addBean(beanObject.getClass().getName(), beanObject);
    }

    // 添加用户自定义的对象
    public void addBean(Class<?> beanClass, Object beanObject) {
        Validate.notNull(beanClass);
        addBean(beanClass.getName(), beanObject);
    }

    // 添加用户自定义的对象
    public void addBean(String name, Object beanObject) {
        Validate.notNull(beanObject);
        addBean(name, new ValueObject(beanObject));
    }

    // 添加用户自定义的对象
    public void addBean(String name, IocObject object) {
        Validate.notNull(name);
        Validate.notNull(object);

        log.debug("addBean: {}", name);

        if (pool.put(name, object) != null) {
            log.warn("Duplicated Bean: {}", name);
        }
    }

    // 注册 @IocBean 标注的对象
    public void addBean(Class<?> beanClass) {
        addBean(beanClass, true);
    }

    // 注册 @IocBean 标注的对象
    public void addBean(Class<?> beanClass, boolean singleton) {
        Validate.notNull(beanClass);
        addBean(beanClass.getName(), beanClass, singleton);
    }

    // 注册 @IocBean 标注的对象
    public void addBean(String name, Class<?> beanClass, boolean singleton) {
        Validate.notNull(name);
        Validate.notNull(beanClass);
        Validate.isFalse(beanClass.isInterface(), "Must not be interface: %s", beanClass.getName());
        Validate.isFalse(Modifier.isAbstract(beanClass.getModifiers()), "Must not be abstract class: %s", beanClass.getName());

        log.debug("addBean: {}", name, beanClass.getName());

        IocObject iocObject = doGetIocObject(beanClass, singleton);
        if (pool.put(name, iocObject) != null) {
            log.warn("Duplicated Bean: {}", name);
        }
    }

    private IocObject doGetIocObject(Class<?> beanClass, boolean singleton) {
        if (IocFactory.class.isAssignableFrom(beanClass)) {
            if (singleton) {
                return new FactorySingletonObject(this, beanClass);
            } else {
                return new FactoryInstanceObject(this, beanClass);
            }
        } else {
            if (singleton) {
                return new ClassSingletonObject(this, beanClass);
            } else {
                return new ClassInstanceObject(this, beanClass);
            }
        }
    }

    // 获取一个 Bean
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> beanClass) {
        Validate.notNull(beanClass);
        return (T) getBean(beanClass.getName());
    }

    // 获取一个 Bean
    @Override
    public Object getBean(String name) {
        IocObject iocObject = pool.get(name);
        if (iocObject == null) {
            return null;
        }
        return iocObject.getObject();
    }

    @Override
    public IocConfig getConfig() {
        return config;
    }

    @Override
    public <T> T getConfigAsValue(String name, Class<T> targetClass) {
        return config.getValue(name, targetClass, null);
    }

    @Override
    public <T> T getConfigAsValue(String name, Class<T> targetClass, String defaultValue) {
        return config.getValue(name, targetClass, defaultValue);
    }

    @Override
    public <T> List<T> getConfigAsList(String name, Class<T> elementType) {
        return config.getList(name, elementType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T newInstance(Class<T> type) {
        KlassInfo klass = KlassInfo.create(type);
        CtorInjector ctorInjector = IocObjectUtils.doGetCtorInjector(this, klass);

        if (ctorInjector == null) {
            try {
                return type.newInstance();
            } catch (Exception e) {
                throw ExceptionUtils.unchecked(e);
            }
        } else {
            return (T) ctorInjector.newInstance();
        }
    }

    @Override
    public void injectSetters(Object object) {
        KlassInfo klass = KlassInfo.create(object.getClass());
        List<FieldInjector> fieldInjectors = IocObjectUtils.doGetFieldInjectors(this, klass);

        for (FieldInjector injector : fieldInjectors) {
            injector.set(object);
        }
    }

    @Override
    public void initialize(Object object) {
        KlassInfo klass = KlassInfo.create(object.getClass());
        Method initializeMethod = IocObjectUtils.doGetInitializeMethod(klass);

        if (initializeMethod != null) {
            try {
                initializeMethod.invoke(object, (Object[]) null);
            } catch (Exception e) {
                throw ExceptionUtils.unchecked(e);
            }
        }
    }
}

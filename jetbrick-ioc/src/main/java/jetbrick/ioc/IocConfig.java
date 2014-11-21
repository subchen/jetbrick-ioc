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

import java.util.*;
import jetbrick.config.AbstractConfig;
import jetbrick.util.ClassLoaderUtils;

public final class IocConfig extends AbstractConfig {
    private final Ioc ioc;

    public IocConfig(Ioc ioc) {
        super(Collections.<String, String> emptyMap());
        this.ioc = ioc;
    }

    public Ioc getIoc() {
        return ioc;
    }

    protected void load(Map<String, String> map) {
        config.putAll(map);
    }

    // -----------------------------------------------------------------
    public <T> T getValue(String name, Class<T> targetClass, String defaultValue) {
        return doGetValue(name, targetClass, defaultValue);
    }

    public <T> List<T> getList(String name, Class<T> elementType) {
        return doGetList(name, elementType, null);
    }

    public <T> T createObject(String aliasName, Class<T> targetClass) {
        if (!aliasName.startsWith("$")) {
            aliasName = "$" + aliasName;
        }

        String className = doGetValue(aliasName, String.class, null);
        Set<String> propNames = keySet(aliasName.concat("."));

        Class<?> cls;
        try {
            cls = ClassLoaderUtils.loadClassEx(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        if (!targetClass.isAssignableFrom(cls)) {
            throw new IllegalStateException("cannot convert `" + className + "` to " + targetClass);
        }

        return super.newInstance(aliasName, cls, propNames);
    }

    // -----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    protected <T> T newInstance(String aliasName, Class<?> cls, Set<String> propNames) {
        if (aliasName.startsWith("$")) {
            return (T) ioc.getBean(aliasName.substring(1));
        } else {
            return super.newInstance(aliasName, cls, propNames);
        }
    }

    @Override
    protected <T> T objectNewInstance(Class<T> cls) throws Exception {
        return ioc.newInstance(cls);
    }

    @Override
    protected void objectInitialize(Object object) {
        ioc.injectSetters(object);
        ioc.initialize(object);
    }

}

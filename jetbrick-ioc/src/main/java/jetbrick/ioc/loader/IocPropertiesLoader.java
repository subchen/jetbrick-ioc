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

import jetbrick.config.Config;
import jetbrick.config.ConfigLoader;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.MutableIoc;
import jetbrick.ioc.object.SingletonObject;

public final class IocPropertiesLoader implements IocLoader {
    private final Config config;

    public IocPropertiesLoader(String location) {
        this.config = new ConfigLoader().load(location).asConfig();
    }

    public IocPropertiesLoader(Config config) {
        this.config = config;
    }

    @Override
    public void load(MutableIoc ioc) {
        ioc.loadConfig(config.asMap());

        for (String name : config.keySet()) {
            if (name.startsWith("$") && name.indexOf('.') == -1) {
                ConfigSingletonObject ref = new ConfigSingletonObject(ioc, name);
                ioc.addBean(name.substring(1), ref);
            }
        }
    }

    static final class ConfigSingletonObject extends SingletonObject {
        private final String aliasName;

        public ConfigSingletonObject(Ioc ioc, String aliasName) {
            super(ioc);
            this.aliasName = aliasName;
        }

        @Override
        protected Object doGetObject() throws Exception {
            return ioc.getConfig().createObject(aliasName, Object.class);
        }
    }
}

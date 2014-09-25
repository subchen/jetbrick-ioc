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

import jetbrick.bean.PropertyInfo;

// 注入配置文件中的属性
public final class PropertyInjector {
    private final PropertyInfo prop;
    private final Object value;

    public PropertyInjector(PropertyInfo prop, Object value) {
        this.prop = prop;
        this.value = value;
    }

    public void set(Object object) throws Exception {
        prop.set(object, value);
    }
}

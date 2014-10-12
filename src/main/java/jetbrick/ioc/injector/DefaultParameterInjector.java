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

import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotation.IocConstants;

/**
 * 注入没有任何标注的参数(默认是  <code>@Inject</code>，然后根据类型名注入)
 */
public final class DefaultParameterInjector implements ParameterInjector {
    private Ioc ioc;
    private String name;

    @Override
    public void initialize(ParameterContext ctx) {
        this.ioc = ctx.getIoc();
        this.name = ctx.getRawParameterTypeName();
    }

    @Override
    public Object getObject() {
        Object value = ioc.getBean(name);
        if (value == null && IocConstants.REQUIRED) {
            throw new IllegalStateException("Can't inject parameter.");
        }
        return value;
    }
}

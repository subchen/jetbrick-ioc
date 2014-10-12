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

import jetbrick.bean.ConstructorInfo;
import jetbrick.util.ArrayUtils;

//注入 @Inject 标注的构造函数
public final class CtorInjector {
    private final ConstructorInfo ctor;
    private final ParameterInjector[] parameters;

    public CtorInjector(ConstructorInfo ctor, ParameterInjector[] parameters) {
        this.ctor = ctor;
        this.parameters = parameters;
    }

    public Object newInstance() {
        Object[] paramObjects = ArrayUtils.EMPTY_OBJECT_ARRAY;
        int length = parameters.length;
        if (length > 0) {
            paramObjects = new Object[length];
            for (int i = 0; i < length; i++) {
                paramObjects[i] = parameters[i].getObject();
            }
        }
        return ctor.newInstance(paramObjects);
    }
}

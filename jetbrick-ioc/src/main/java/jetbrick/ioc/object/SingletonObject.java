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
package jetbrick.ioc.object;

import jetbrick.ioc.Ioc;
import jetbrick.util.ExceptionUtils;
import jetbrick.util.concurrent.ConcurrentInitializer;
import jetbrick.util.concurrent.LazyInitializer;

// 单例模式
public abstract class SingletonObject implements IocObject {
    protected final Ioc ioc;

    private final ConcurrentInitializer<Object> object = new LazyInitializer<Object>() {
        private boolean initializing = false;

        @Override
        protected Object initialize() {
            if (initializing) {
                throw new IllegalStateException("Cycle dependencies on singleton bean detected: " + toString());
            }
            try {
                initializing = true;
                return doGetObject();
            } catch (Exception e) {
                throw ExceptionUtils.unchecked(e);
            } finally {
                initializing = false;
            }
        }
    };

    public SingletonObject(Ioc ioc) {
        this.ioc = ioc;
    }

    @Override
    public Object getObject() {
        return object.get();
    }

    protected abstract Object doGetObject() throws Exception;
}

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

import jetbrick.ioc.Ioc;

// 每次产生一个新的 instance
public abstract class InstanceObject implements IocObject {
    protected final Ioc ioc;
    private boolean initialized;

    public InstanceObject(Ioc ioc) {
        this.ioc = ioc;
        this.initialized = false;
    }

    @Override
    public Object getObject() {
        try {
            if (initialized == false) {
                synchronized (this) {
                    if (initialized == false) {
                        initialize();
                        initialized = true;
                    }
                }
            }
            return doGetObject();
        } catch (RuntimeException e) {
            return e;
        } catch (Exception e) {
            return new RuntimeException(e);
        }
    }

    protected abstract void initialize() throws Exception;

    protected abstract Object doGetObject() throws Exception;
}

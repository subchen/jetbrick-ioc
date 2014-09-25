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
package jetbrick.ioc.annotation;

import java.lang.annotation.*;
import jetbrick.ioc.injector.InjectFieldInjector;
import jetbrick.ioc.injector.InjectParameterInjector;
import jetbrick.util.annotation.ValueConstants;

/**
 * 将 @IocBean 标注的对象注入到字段中/构造函数参数.
 *
 * @author Guoqiang Chen
 */
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@InjectFieldWith(InjectFieldInjector.class)
@InjectParameterWith(InjectParameterInjector.class)
public @interface Inject {

    String value() default ValueConstants.EMPTY;

    boolean required() default IocConstants.REQUIRED;

}

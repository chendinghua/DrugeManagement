/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.druge.ui.api;

import android.app.Activity;

import java.lang.reflect.Field;

/**
 * 侵入式View的调用工具类
 *
 * @author kymjs (http://www.kymjs.com/).
 */
public class InjectUtil {




        public static void  InjectView(Activity activity){

            //获取类

            Class< ? extends Activity> recfClass=activity.getClass();

            //获取类中的所有field（域/变量）

            Field[] fields=recfClass.getDeclaredFields();

            //对获取到的field做遍历

            for(Field field:fields){

                //对遍历到的field做判断，是否带特定注解标识

                if(field.isAnnotationPresent(InjectView.class)){

                    //获取到该field的注解

                    InjectView injectView= field.getAnnotation(InjectView.class);

                    //获取到该field的注解的value

                    int id= injectView.value();

                    //设置属性

                    field.setAccessible(true);

                    try {

                        //对该field做控件绑定操作

                        field.set(activity,activity.findViewById(id));

                    } catch (IllegalAccessException e) {

                        e.printStackTrace();

                    } catch (IllegalArgumentException e) {

                        e.printStackTrace();

                    }

                }



            }

        }




}

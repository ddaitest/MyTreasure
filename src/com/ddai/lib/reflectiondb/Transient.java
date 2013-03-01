package com.ddai.lib.reflectiondb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于标记不存入DB的属性
 * @author Daining daining@1000chi.com
 * 2012-5-14
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient {

}

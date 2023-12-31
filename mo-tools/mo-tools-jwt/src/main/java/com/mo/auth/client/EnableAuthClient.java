package com.mo.auth.client;

import com.mo.auth.client.config.AuthClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用授权client
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({AuthClientConfiguration.class})
@Documented
@Inherited
public @interface EnableAuthClient {
}

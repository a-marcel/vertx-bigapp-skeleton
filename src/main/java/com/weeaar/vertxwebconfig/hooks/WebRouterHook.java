package com.weeaar.vertxwebconfig.hooks;

import io.vertx.ext.web.Router;

public interface WebRouterHook
{
    public void before( Integer port, Router router );

    public void after( Integer port, Router router );
}

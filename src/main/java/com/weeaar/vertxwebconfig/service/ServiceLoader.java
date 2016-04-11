package com.weeaar.vertxwebconfig.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.service.ServiceVerticleFactory;

public class ServiceLoader
{

    Logger logger = LoggerFactory.getLogger( ServiceLoader.class );

    ServiceVerticleFactory factory;

    public static final String FACTORIES_VERTICLES_LOCATION = "META-INF/vertx.verticle";

    public List<String> findVerticles()
    {
        List<String> verticles = new ArrayList<String>();
        Scanner scan = null;

        try
        {
            Enumeration<URL> urls = ClassLoader.getSystemResources( FACTORIES_VERTICLES_LOCATION );

            while ( urls.hasMoreElements() )
            {
                URL url = urls.nextElement();

                scan = new Scanner( url.openStream() );
                while ( scan.hasNextLine() )
                {
                    String verticleName = scan.nextLine().trim();
                    verticles.add( verticleName );
                    logger.debug( "Found verticle : " + verticleName );
                }
            }
        }
        catch ( IOException e )
        {
            logger.error( "Error at opening file", e );
        }
        finally
        {
            if ( scan != null )
            {
                scan.close();
            }
        }

        return verticles;
    }
}

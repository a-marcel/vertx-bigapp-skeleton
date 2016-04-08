package com.weeaar.vertx.codec.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.service.ServiceVerticleFactory;

public class ServiceLoader
{

    Logger logger = LoggerFactory.getLogger( ServiceLoader.class );

    ServiceVerticleFactory factory;

    public List<String> findVerticles()
    {
        List<String> verticles = new ArrayList<String>();

        InputStream input = null;
        Scanner scan = null;
        String filename = "META-INF/vertx.verticle";

        try
        {
            input = getClass().getClassLoader().getResourceAsStream( filename );

            if ( input == null )
            {
                logger.debug( "Could not found " + filename );

                return null;
            }

            scan = new Scanner( input );
            while ( scan.hasNextLine() )
            {
                String verticleName = scan.nextLine().trim();
                verticles.add( verticleName );
                logger.debug( "Found verticle : " + verticleName );
            }
        }
        finally
        {
            if ( input != null )
            {
                try
                {
                    input.close();
                }
                catch ( IOException e )
                {
                    logger.error( "Cannot close InputStream", e );
                }
            }

            if ( scan != null )
            {
                scan.close();
            }
        }

        return verticles;
    }
}

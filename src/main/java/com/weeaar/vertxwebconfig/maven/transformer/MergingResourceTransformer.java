package com.weeaar.vertxwebconfig.maven.transformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/*import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;

public class MergingResourceTransformer
    implements ResourceTransformer
{
    private final Set<String> data = new HashSet<String>();

    private String resource;

    @Override
    public boolean canTransformResource( String resource )
    {
        if ( resource.contains( this.resource ) )
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasTransformedResource()
    {
        return !this.data.isEmpty();
    }

    @Override
    public void modifyOutputStream( JarOutputStream os )
        throws IOException
    {
        os.putNextEntry(new JarEntry(this.resource));
//        this.data.store(os, "Merged by PropertiesMergingResourceTransformer");
              
        StringWriter writer = new StringWriter();

        for ( String string : data )
        {
            writer.write( string );
            writer.write( "\r\n" );            
        }

        byte[] bytes = writer.toString().getBytes( "UTF-8" );

        os.write( bytes, 0, bytes.length );
        
        os.flush();
        this.data.clear();
    }

    @Override
    public void processResource( String resource, InputStream is, List<Relocator> relocators )
        throws IOException
    {
        StringBuilder out = new StringBuilder();

        BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );

        String line;
        while ( ( line = reader.readLine() ) != null )
        {
            out.append( line );
        }
        this.data.add( out.toString() );

        is.close();
    }

    public String getResource()
    {
        return this.resource;
    }

    public void setResource( String resource )
    {
        this.resource = resource;
    }

}*/

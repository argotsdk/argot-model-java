package com.argot.model.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import junit.framework.TestCase;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeMap;
import com.argot.TypeMapperCore;
import com.argot.TypeMapperDynamic;
import com.argot.TypeMapperError;
import com.argot.TypeOutputStream;
import com.argot.data.MixedData;

public class ModelProxyTest
extends TestCase
{

    public void testClassloader() throws TypeException, IOException, ScriptException {

        // Create the type library and compile/bind the switch data types.
        final TypeLibrary typeLibrary = new TypeLibrary( );

        typeLibrary.loadLibrary(new ModelProxyDataLoader());


        final TypeMap map = new TypeMap( typeLibrary, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())));

        // Create a stream of data representing an instance of Mixed Data.
        final byte[] data = new byte[9];
        data[0] = 0;
        data[1] = 0x0a;
        data[2] = 0x32;
        data[3] = 0x05;
        data[4] = 0x48;
        data[5] = 0x65;
        data[6] = 0x6c;
        data[7] = 0x6c;
        data[8] = 0x6f;

        // Read in the Object.
        final ByteArrayInputStream bais = new ByteArrayInputStream( data );
        final TypeInputStream in = new TypeInputStream( bais, map );
        final Object o = in.readObject( MixedData.TYPENAME );

        // Execute LuaJ script.
        System.out.println("instance created: " + o.getClass().getName());
        final String script = " t:settext('hello Argot model object') \n t:setshort(1000) \n t:setbyte(100) \n x=t:gettext() \n print(x)";

        System.out.println("Compiling script: \n" + script );
        final ScriptEngineManager sem = new ScriptEngineManager();
        final ScriptEngine e = sem.getEngineByName("luaj");
        final CompiledScript cs = ((Compilable)e).compile(script);

        System.out.println("Executing script. Output:" );
        final long startTime = new Date().getTime();

        final Bindings b1 = e.createBindings();
        b1.put("t", o);
        cs.eval(b1);
        final long endTime = new Date().getTime();
        System.out.println("Script execution complete" );

        // Write out object to stream.
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final TypeOutputStream out = new TypeOutputStream( baos, map );
        out.writeObject( MixedData.TYPENAME, o );
        baos.flush();
        baos.close();

        // Show bytes.
        final byte[] msgData = baos.toByteArray();
        System.out.println("msgSize = " + msgData.length );
        printByteData(msgData);
    }

    private void printByteData( final byte[] data ) {

        int count=0;
        //System.out.println("Core Size: " + data.length);
        for (int x=0; x<data.length;x++)
        {
            count++;
            if (data[x] >= 48 && data[x] <= 122 )
            {
                final String value = String.valueOf((char)data[x]);
                System.out.print( value + "  ");
            }
            else
            {
                String value = Integer.toString( data[x], 16 );
                if (value.length()==1) {
                    value = "0" + value;
                }
                value = "" + value;

                System.out.print( "" + value + " ");
            }
            if (count>23)
            {
                count=0;
                System.out.println("");
            }
        }

        System.out.println("");
    }
}

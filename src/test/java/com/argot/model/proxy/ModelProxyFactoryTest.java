package com.argot.model.proxy;

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
import com.argot.TypeLibrary;
import com.argot.TypeMap;
import com.argot.TypeMapperCore;
import com.argot.TypeMapperDynamic;
import com.argot.TypeMapperError;
import com.argot.TypeOutputStream;
import com.argot.compiler.ArgotCompilerLoader;
import com.argot.data.MixedData;

public class ModelProxyFactoryTest
extends TestCase
{

    public void testClassloader()
    throws TypeException, IOException, ScriptException
    {

        // Create the type library and compile/bind the switch data types.
        final TypeLibrary typeLibrary = new TypeLibrary( );

        typeLibrary.loadLibrary(new ModelProxyDataLoader());

        final ModelProxyFactory factory = new ModelProxyFactory(typeLibrary,"data","1.0");
        final Object o = factory.newInstance();

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
        final TypeMap map = new TypeMap( typeLibrary, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())));
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final TypeOutputStream out = new TypeOutputStream( baos, map );
        out.writeObject( MixedData.TYPENAME, o );
        baos.flush();
        baos.close();

        // Show bytes.
        final byte[] msgData = baos.toByteArray();
        System.out.println("msgSize = " + msgData.length );
        ModelProxyTest.printByteData(msgData);
    }


    private static class TestCompilerLoader
    extends ArgotCompilerLoader {


        public TestCompilerLoader(final String resource) {
            super(resource);

        }

        @Override
        public void bind(final TypeLibrary library) throws TypeException {
            super.bind(library);

            final ModelClassLoader loader = new ModelClassLoader(library);

            final int typeId = library.getDefinitionId("test.Temperature", "1.0");
            library.bind(typeId,
                    new ModelProxyReader( loader ),
                    new ModelProxyWriter( loader ),
                    loader.getClass(library.getStructure(typeId))
                );

            final int locId = library.getDefinitionId("Location", "1.0");
            library.bind(locId,
                    new ModelProxyReader( loader ),
                    new ModelProxyWriter( loader ),
                    loader.getClass(library.getStructure(locId))
                );
        }

    }

}

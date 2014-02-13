package com.argot.model.proxy;

import java.io.IOException;
import java.lang.reflect.Proxy;
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
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.model.ModelDataLoader;
import com.argot.model.ModelObject;
import com.argot.model.TagData;
import com.argot.model.data.SequenceData;

public class ModelCreatorTest
extends TestCase
{
    private TypeLibrary typeLibrary;


    @Override
    public void setUp()
    throws TypeException
    {
        // Create the type library and compile/bind the switch data types.
        typeLibrary = new TypeLibrary( );

        typeLibrary.loadLibrary(new ModelDataLoader());
    }

    public void testClassloader() throws TypeException, IOException, ScriptException {

        final String argotTypeName = "data";
        final String argotTypeVersion = "1.0";

        System.out.println("Generate interface class from Argot type");
        final MetaSequence sequence = (MetaSequence) typeLibrary.getStructure(typeLibrary.getTypeId("data", "1.0"));

        final byte[] classData = SequenceCreator.getBytecode(typeLibrary, sequence, ModelObject.class);

        System.out.println("Loading interface class for type: " + argotTypeName );
        final ModelClassLoader classLoader = new ModelClassLoader();
        final Class aClass = classLoader.defineClass("data", classData);
        System.out.println("Loaded class name: " + aClass.getName());

        System.out.println("Creating instance for type: " + argotTypeName );
        final Object[] values = new Object[sequence.size()];
        for (int x=0; x< values.length;x++) {
            values[x] = new TagData((MetaTag)sequence.getElement(x), null);
        }
        final SequenceProxy handler = new SequenceProxy( sequence, new SequenceData( sequence, values));
        final Object o = Proxy.newProxyInstance(aClass.getClassLoader(),
                new Class[] { aClass }, handler );

        System.out.println("instance created: " + o.getClass().getName());
        final String script = " t:settext('blah') \n x=t:gettext() \n print(x)";

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
        //System.out.println(" start time: " + startTime);
        //System.out.println(" end time: " + endTime );
        System.out.println(" elapsed time (ms): " + (endTime - startTime) );
    }

    private class ModelClassLoader
    extends ClassLoader {
        public Class defineClass(final String name, final byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

}

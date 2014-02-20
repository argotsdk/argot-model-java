package com.argot.model.proxy;

import java.util.HashMap;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaName;
import com.argot.model.ModelObject;

public class ModelClassLoader
{

    private final TypeLibrary _typeLibrary;
    private final HashMap<Integer,Class> _classMap;

    public ModelClassLoader(final TypeLibrary library )
    {
        _typeLibrary = library;
        _classMap = new HashMap<Integer,Class>();
    }

    public Class defineClass( final byte[] b) {
        //return defineClass(name, b, 0, b.length);
      //override classDefine (as it is protected) and define the class.
        Class clazz = null;
        try {
          final ClassLoader loader = this.getClass().getClassLoader();//ClassLoader.getSystemClassLoader();
          final Class cls = Class.forName("java.lang.ClassLoader");
          final java.lang.reflect.Method method =
            cls.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class });

          // protected method invocaton
          method.setAccessible(true);
          try {
            final Object[] args = new Object[] { null, b, new Integer(0), new Integer(b.length)};
            clazz = (Class) method.invoke(loader, args);
          } finally {
            method.setAccessible(false);
          }
        } catch (final Exception e) {
          e.printStackTrace();
          System.exit(1);
        }
        return clazz;
    }

    public Class getClass(final TypeElement element)
    throws TypeException
    {
        final MetaExpression expression = (MetaExpression) element;
        final Class clss = _classMap.get(expression.getMemberTypeId());
        if (clss != null) {
            return clss;
        }

        final MetaName name = _typeLibrary.getName(expression.getMemberTypeId());
        final String typeName = element.getTypeName();

        final byte[] classData = SequenceCreator.getBytecode(_typeLibrary, element, ModelObject.class);
        final Class newClss = defineClass(classData);
        _classMap.put(expression.getMemberTypeId(), newClss );
        return newClss;
    }
}

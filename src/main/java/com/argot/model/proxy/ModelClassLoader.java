package com.argot.model.proxy;

import java.util.HashMap;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaName;
import com.argot.model.ModelObject;

public class ModelClassLoader
extends ClassLoader {

    private final TypeLibrary _typeLibrary;
    private final HashMap<Integer,Class> _classMap;

    public ModelClassLoader(final TypeLibrary library )
    {
        _typeLibrary = library;
        _classMap = new HashMap<Integer,Class>();
    }

    public Class defineClass(final String name, final byte[] b) {
        return defineClass(name, b, 0, b.length);
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
        final Class newClss = defineClass(name.getFullName(),classData,0,classData.length);
        _classMap.put(expression.getMemberTypeId(), newClss );
        return newClss;
    }
}

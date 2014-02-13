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
    private final HashMap<TypeElement,Class> _classMap;

    public ModelClassLoader(final TypeLibrary library )
    {
        _typeLibrary = library;
        _classMap = new HashMap<TypeElement,Class>();
    }

    public Class defineClass(final String name, final byte[] b) {
        return defineClass(name, b, 0, b.length);
    }

    public Class getClass(final TypeElement element)
    throws TypeException
    {
        final Class clss = _classMap.get(element);
        if (clss != null) {
            return clss;
        }

        final MetaExpression expression = (MetaExpression) element;
        final MetaName name = _typeLibrary.getName(expression.getMemberTypeId());
        final String typeName = element.getTypeName();
        System.out.println("typeName = " + typeName + " - " + element.getTypeId() + " " + element.getTypeDefinition());

        final byte[] classData = SequenceCreator.getBytecode(_typeLibrary, element, ModelObject.class);
        final Class newClss = defineClass(name.getFullName(),classData,0,classData.length);
        _classMap.put(element, clss );
        return newClss;
    }
}

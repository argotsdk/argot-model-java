package com.argot.model.proxy;

import java.lang.reflect.Proxy;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.model.ModelFactory;
import com.argot.model.TagData;
import com.argot.model.data.SequenceData;

/**
 * This is a simple implementation of an instance creator.  Restricted to objects that
 * have a sequence of reference types.
 *
 * @author davidryan
 *
 */
public class ModelProxyFactory
implements ModelFactory
{

    private final TypeLibrary _library;
    private final MetaSequence _structure;
    private final Class _clss;

    public ModelProxyFactory(final TypeLibrary library, final String typeName, final String version)
    throws TypeException
    {
        _library = library;

        final int id = _library.getTypeId(typeName, version);
        final TypeElement element = _library.getStructure(id);
        if (!(element instanceof MetaSequence)) {
            throw new TypeException("Can only create types defined by sequence");
        }
        _structure = (MetaSequence) element;
        _clss = _library.getClass(id);
    }

    @Override
    public Object newInstance()
    throws TypeException
    {
        final SequenceProxy handler = new SequenceProxy( _structure, createSequenceData(_structure) );
        return Proxy.newProxyInstance(_clss.getClassLoader(), new Class[] { _clss}, handler );
    }


    public Object createSequenceData( final MetaSequence sequence)
    throws TypeException
    {
        final Object[] parameters = new Object[sequence.size()];
        for (int x=0; x<sequence.size();x++) {
            final TypeElement tagElement = sequence.getElement(x);
            if (!(tagElement instanceof MetaTag)) {
                throw new TypeException("Sequence must contain tag elements");
            }
            final MetaTag tag = (MetaTag) tagElement;
            parameters[x] = new TagData(tag,null);
        }
        return new SequenceData(sequence,parameters);
    }

}

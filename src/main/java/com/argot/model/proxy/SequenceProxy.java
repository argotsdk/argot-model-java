package com.argot.model.proxy;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.InvalidParameterException;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaSequence;
import com.argot.model.TagData;
import com.argot.model.data.SequenceData;
import com.argot.model.data.SequenceData.SequenceDataExpressionReader;
import com.argot.model.data.SequenceData.SequenceDataExpressionWriter;

public class SequenceProxy implements InvocationHandler {

    private final boolean[] changed;
    private final SequenceData data;


    public SequenceProxy( final TypeElement structure, final Object values ) {
        if (!(structure instanceof MetaSequence)) {
            throw new InvalidParameterException("Structure must be a MetaSequence");
        }
        final MetaSequence sequence = (MetaSequence) structure;

        data = (SequenceData) values;

        changed = new boolean[sequence.size()];
        for (int x=0;x<sequence.size();x++) {
            changed[x] = false;
        }
    }

    private SequenceData getData()
    {
        return data;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
    throws Throwable
    {
        final Annotation[] annotations = method.getAnnotations();
        if (annotations == null || annotations.length != 1)
        {
            // No annotation.  Assume object will respond to method call.

            throw new InvalidParameterException("Methods must have ModelAnnotation");
        }

        if (!(annotations[0] instanceof ModelAnnotation))
        {
            throw new InvalidParameterException("Methods must have ModelAnnotation");
        }

        final ModelAnnotation modelAnnotation = (ModelAnnotation) annotations[0];
        final int index = modelAnnotation.value();
        if (index < 0) {
            // TODO.  Find method in this class to call based on name.
            return null;
        } else {
            final char m = method.getName().charAt(0);
            switch (m) {
            case 'g':
                return ((TagData)data.getSequenceData()[index]).getData();
            case 's':
                final Object value = ((TagData)data.getSequenceData()[index]).getData();
                if (value == null || !value.equals(args[0])) {
                    changed[index] = true;
                }
                ((TagData)data.getSequenceData()[index]).setData( args[0] );
                return null;
            case 'h':
                return changed[index];
            }
        }

        throw new InvalidParameterException("Methods must have ModelAnnotation");
    }

    public static class SequenceProxyExpressionReader
    extends SequenceDataExpressionReader
    {
        private final ModelClassLoader _classLoader;

        public SequenceProxyExpressionReader( final ModelClassLoader classLoader )
        {
            _classLoader = classLoader;
        }

        @Override
        public TypeReader getExpressionReader(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
        throws TypeException
        {
            final TypeReader reader = super.getExpressionReader(map, resolver, element);
            final Class clss = _classLoader.getClass(element);

            return new SequenceProxyReader(reader, element, clss.getClassLoader(), new Class[] { clss } );
        }
    }

    public static class SequenceProxyReader
    implements TypeReader
    {
        private final TypeReader _reader;
        private final MetaSequence _structure;
        private final ClassLoader _classLoader;
        private final Class[] _interfaces;

        public SequenceProxyReader(final TypeReader reader, final TypeElement structure, final ClassLoader classLoader, final Class[] interfaces)
        {
            _reader = reader;
            _structure = (MetaSequence) structure;
            _classLoader = classLoader;
            _interfaces = interfaces;
        }
        @Override
        public Object read(final TypeInputStream in)
        throws TypeException, IOException
        {
            final SequenceProxy handler = new SequenceProxy( _structure, _reader.read(in) );
            return Proxy.newProxyInstance(_classLoader, _interfaces, handler );
        }

    }

    public static class SequenceProxyExpressionWriter
    extends SequenceDataExpressionWriter
    {
        @Override
        public TypeWriter getExpressionWriter(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
        throws TypeException
        {
            final TypeWriter writer = super.getExpressionWriter(map, resolver, element);

            return new SequenceProxyWriter(writer);
        }
    }

    private static class SequenceProxyWriter
    implements TypeWriter
    {
        private final TypeWriter _writer;

        public SequenceProxyWriter(final TypeWriter writer) {
            _writer = writer;
        }

        @Override
        public void write(final TypeOutputStream out, final Object o)
        throws TypeException, IOException
        {
            final SequenceProxy proxy = (SequenceProxy) Proxy.getInvocationHandler(o);
            _writer.write(out, proxy.getData() );
        }

    }

}

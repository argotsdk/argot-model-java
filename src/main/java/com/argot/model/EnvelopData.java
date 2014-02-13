package com.argot.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaEnvelope;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaExpressionWriter;

public class EnvelopData
implements ModelObject
{
	private final MetaEnvelope _metaEnvelop;
	private final Object _data;

	public EnvelopData( final MetaEnvelope metaEnvelop, final Object data )
	{
		_metaEnvelop = metaEnvelop;
		_data = data;
	}

	@Override
    public Object getData()
	{
		return _data;
	}

	@Override
    public MetaExpression getStructure()
	{
		return _metaEnvelop;
	}

	public static class EnvelopDataExpressionReader
	implements MetaExpressionReader
	{
		@Override
        public TypeReader getExpressionReader(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaEnvelope metaEnvelop = (MetaEnvelope) element;
			return new EnvelopDataReader(metaEnvelop, resolver.getExpressionReader(map, metaEnvelop.getSizeExpression()), resolver.getExpressionReader(map, metaEnvelop.getTypeExpression()));
		}

	}

    private static class EnvelopDataReader
    implements TypeReader
    {
    	private final MetaEnvelope _metaEnvelop;
    	private final TypeReader _size;
    	private final TypeReader _type;

    	private EnvelopDataReader( final MetaEnvelope metaEnvelop, final TypeReader size, final TypeReader type)
    	{
    		_metaEnvelop = metaEnvelop;
    		_size = size;
    		_type = type;
    	}

		@Override
        public Object read(final TypeInputStream in)
		throws TypeException, IOException
		{
		    Object sizeObject = _size.read( in );
		    if (sizeObject instanceof ModelObject)
		    {
		    	sizeObject = ((ModelObject)sizeObject).getData();
		    }
		    if (sizeObject instanceof SimpleData)
		    {
		    	sizeObject = ((SimpleData)sizeObject).getData();
		    }

			int size = 0;

			if ( sizeObject instanceof Byte )
			{
				size = ((Byte)sizeObject).intValue();
			}
			else if (sizeObject instanceof Short)
			{
				size = ((Short)sizeObject).intValue();

			}
			else if (sizeObject instanceof Integer )
			{
				size = ((Integer)sizeObject).intValue();
			}
			else
			{
				throw new TypeException("meta.envelop not able to use size object");
			}

			// Read in the buffer.
			final byte[] buffer = new byte[size];
			in.read( buffer,0,buffer.length );

			// Read in the data from the buffer.
			final ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
			final TypeInputStream tin = new TypeInputStream(bin,in.getTypeMap());
			final Object o = _type.read(tin);

			return new EnvelopData( _metaEnvelop, o );
		}

    }


	public static class EnvelopDataExpressionWriter
	implements MetaExpressionWriter
	{
		@Override
        public TypeWriter getExpressionWriter(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaEnvelope metaEnvelop = (MetaEnvelope) element;
			return new EnvelopDataWriter(getSizeWriter(map, resolver, metaEnvelop.getSizeExpression()), resolver.getExpressionWriter(map, metaEnvelop.getTypeExpression()));
		}

		private TypeWriter getSizeWriter(final TypeMap map,final MetaExpressionResolver resolver, final MetaExpression expression)
		throws TypeException
		{
	    	final TypeLibraryWriter writer = map.getLibrary().getWriter(expression.getTypeId());
			if(!(writer instanceof MetaExpressionWriter))
			{
				throw new TypeException("MetaExpressionReader expected. Found: " + writer.getClass().getName() );
			}
			final MetaExpressionWriter expressionWriter = (MetaExpressionWriter) writer;
			return expressionWriter.getExpressionWriter(map, resolver, expression);
		}
	}

	private static class EnvelopDataWriter
	implements TypeWriter
	{
		private final TypeWriter _size;
		private final TypeWriter _type;

		public EnvelopDataWriter(final TypeWriter size, final TypeWriter type )
		{
			_size = size;
			_type = type;
		}

		@Override
        public void write(final TypeOutputStream out, final Object o)
		throws TypeException, IOException
		{
			final EnvelopData data = (EnvelopData) o;
			final ByteArrayOutputStream bout = new ByteArrayOutputStream();

			final TypeOutputStream tmos = new TypeOutputStream( bout, out.getTypeMap() );

			_type.write( tmos, data.getData() );
			bout.close();

			final byte b[] = bout.toByteArray();
			_size.write( out, new Integer(b.length) );
			out.getStream().write( b );
		}

	}

}

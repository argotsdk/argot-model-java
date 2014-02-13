package com.argot.model;

import java.io.IOException;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaExpressionWriter;
import com.argot.meta.MetaReference;

public class ReferenceData
implements ModelObject
{
	private final MetaReference _reference;
	private final Object _data;

	public ReferenceData( final MetaReference reference, final Object data )
	{
		_reference = reference;
		_data = data;
	}

	@Override
    public MetaExpression getStructure()
	{
		return _reference;
	}

	public MetaReference getReference()
	{
		return _reference;
	}

	@Override
    public Object getData()
	{
		return _data;
	}

	public static class ReferenceDataExpressionReader
	implements MetaExpressionReader
	{
		@Override
        public TypeReader getExpressionReader(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaReference metaReference = (MetaReference) element;
			return new ReferenceDataReader(metaReference, map.getReader( map.getStreamId(metaReference.getType())));
		}
	}

	public static class ReferenceDataReader
	implements TypeReader
	{
		MetaReference _reference;
		TypeReader _reader;

		public ReferenceDataReader(final MetaReference reference, final TypeReader reader)
		{
			_reference = reference;
			_reader = reader;
		}

		@Override
        public Object read(final TypeInputStream in)
		throws TypeException,IOException
		{
			//return new ReferenceData( _reference, _reader.read(in));
			return _reader.read(in);
		}
	}

	public static class ReferenceDataExpressionWriter
	implements MetaExpressionWriter
	{
		@Override
        public TypeWriter getExpressionWriter(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaReference metaReference = (MetaReference) element;
			return new ReferenceDataWriter(map.getWriter( map.getStreamId(metaReference.getType())));
		}
	}

	private static class ReferenceDataWriter
	implements TypeWriter
	{
		private final TypeWriter _writer;

		public ReferenceDataWriter(final TypeWriter type )
		{
			_writer = type;
		}

		@Override
        public void write(final TypeOutputStream out, final Object o)
		throws TypeException, IOException
		{
			/*if (!(o instanceof ReferenceData))
			{
				throw new TypeException("Expected ReferenceData received " + o.getClass().getName());
			}
			final ReferenceData data = (ReferenceData) o;*/
			//System.out.println("Reference Type:" + out.getTypeMap().getLibrary().getName( data.getReference().getType()) + " " + data.getData().getClass().getName() );
			_writer.write(out, o);
		}
	}
}

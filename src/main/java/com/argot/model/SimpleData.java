package com.argot.model;

import java.io.IOException;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaExpression;

public class SimpleData
implements ModelObject
{
	private final int _typeId;
	private final MetaExpression _definition;
	private final Object _data;

	public SimpleData( final int id, final MetaExpression expression, final Object data )
	{
		_typeId = id;
		_definition = expression;
		_data = data;
	}

	@Override
    public Object getData()
	{
		return _data;
	}

	public int getType()
	{
		return _typeId;
	}

	@Override
    public MetaExpression getStructure()
	{
		return _definition;
	}

	public static class SimpleDataExpressionReader
	implements TypeLibraryReader, TypeBound
	{
		int _id;
		MetaExpression _expression;

		@Override
        public TypeReader getReader(final TypeMap map)
		throws TypeException
		{
			return new SimpleDataReader(_id,_expression, map.getLibrary().getReader(_id).getReader(map));
		}

		@Override
        public void bind(final TypeLibrary library, final int definitionId, final TypeElement definition)
		throws TypeException
		{
			_id = definitionId;
			_expression = (MetaExpression) definition;
		}
	}

	private static class SimpleDataReader
	implements TypeReader
	{
		private final int _id;
		private final MetaExpression _expression;
		private final TypeReader _reader;

		public SimpleDataReader(final int id, final MetaExpression expression, final TypeReader reader)
		{
			_id = id;
			_expression = expression;
			_reader = reader;
		}

		@Override
        public Object read(final TypeInputStream in)
		throws TypeException, IOException
		{
	        return new SimpleData( _id, _expression,_reader.read( in ));
		}
	}

	public static class SimpleDataExpressionWriter
	implements TypeLibraryWriter,TypeBound
	{
		int _id;
		MetaExpression _expression;

		@Override
        public TypeWriter getWriter(final TypeMap map)
		throws TypeException
		{
			return new SimpleDataWriter(map.getLibrary().getWriter(_id).getWriter(map));
		}

		@Override
        public void bind(final TypeLibrary library, final int definitionId, final TypeElement definition)
		throws TypeException
		{
			_id = definitionId;
		}
	}

	private static class SimpleDataWriter
	implements TypeWriter
	{
		private final TypeWriter _writer;

		public SimpleDataWriter(final TypeWriter type )
		{
			_writer = type;
		}

		@Override
        public void write(final TypeOutputStream out, final Object o)
		throws TypeException, IOException
		{
			final SimpleData data = (SimpleData) o;
			_writer.write(out, data.getData());
		}

	}



}

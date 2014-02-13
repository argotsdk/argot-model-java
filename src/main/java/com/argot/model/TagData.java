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
import com.argot.meta.MetaTag;

public class TagData
implements ModelObject
{
	private final MetaTag _metaTag;
	private Object _data;

	public TagData( final MetaTag metaTag, final Object data )
	{
		_metaTag = metaTag;
		_data = data;
	}

	@Override
    public Object getData()
	{
		return _data;
	}

	public void setData(final Object v)
	{
	    _data = v;
	}

	@Override
    public MetaExpression getStructure()
	{
		return _metaTag;
	}

	public static class TagDataExpressionReader
	implements MetaExpressionReader
	{
		@Override
        public TypeReader getExpressionReader(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaTag metaTag = (MetaTag) element;
			return new TagDataReader(metaTag, resolver.getExpressionReader(map, metaTag.getExpression()));
		}

	}

	private static class TagDataReader
	implements TypeReader
	{
		private final MetaTag _metaTag;
		private final TypeReader _expression;

		public TagDataReader(final MetaTag metaTag, final TypeReader expression)
		{
			_metaTag = metaTag;
			_expression = expression;
		}

		@Override
        public Object read(final TypeInputStream in)
		throws TypeException, IOException
		{
	        return new TagData( _metaTag,_expression.read( in ));
		}
	}

	public static class TagDataExpressionWriter
	implements MetaExpressionWriter
	{
		@Override
        public TypeWriter getExpressionWriter(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaTag metaTag = (MetaTag) element;
			return new TagDataWriter(resolver.getExpressionWriter(map, metaTag.getExpression()));
		}
	}

	private static class TagDataWriter
	implements TypeWriter
	{
		private final TypeWriter _writer;

		public TagDataWriter(final TypeWriter type )
		{
			_writer = type;
		}

		@Override
        public void write(final TypeOutputStream out, final Object o)
		throws TypeException, IOException
		{
			final TagData data = (TagData) o;
			_writer.write(out, data.getData());
		}

	}



}

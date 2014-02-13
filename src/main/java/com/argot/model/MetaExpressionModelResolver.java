package com.argot.model;

import java.util.HashMap;
import java.util.Map;

import com.argot.TypeException;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionLibraryResolver;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionWriter;

public class MetaExpressionModelResolver
extends MetaExpressionLibraryResolver
{
	private final Map<Class,MetaExpressionReader> _readerMap;
	private final Map<Class,MetaExpressionWriter> _writerMap;

	public MetaExpressionModelResolver()
	{
		_readerMap = new HashMap<Class,MetaExpressionReader>();
		_writerMap = new HashMap<Class,MetaExpressionWriter>();
	}

	public void addExpressionMap( final Class clss, final MetaExpressionReader reader, final MetaExpressionWriter writer)
	{
		_readerMap.put(clss, reader);
		_writerMap.put(clss, writer);
	}

	private MetaExpressionReader getReader(final Class clss)
	{
		return  _readerMap.get(clss);
	}

	private MetaExpressionWriter getWriter(final Class clss)
	{
		return  _writerMap.get(clss);
	}

	@Override
    public TypeReader getExpressionReader(final TypeMap map, final MetaExpression expression)
	throws TypeException
	{
		final MetaExpressionReader reader = getReader(expression.getClass());
		if ( reader == null )
		{
			return super.getExpressionReader(map, expression);
		}
		return reader.getExpressionReader(map, this, expression);
	}

	@Override
    public TypeWriter getExpressionWriter(final TypeMap map, final MetaExpression expression)
	throws TypeException
	{
		final MetaExpressionWriter writer = getWriter(expression.getClass());
		if ( writer == null )
		{
			return super.getExpressionWriter(map, expression);
		}
		return writer.getExpressionWriter(map, this, expression);
	}

}

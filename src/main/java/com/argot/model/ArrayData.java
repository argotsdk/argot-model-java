package com.argot.model;

import java.io.IOException;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaExpressionWriter;

public class ArrayData
implements ModelObject
{
	private final MetaArray _array;
	private final Object _size;
	private final Object[] _data;

	public ArrayData( final MetaArray array, final Object size, final Object[] data )
	{
		_array = array;
		_size = size;
		_data = data;
	}

	@Override
    public MetaExpression getStructure()
	{
		return _array;
	}

	public MetaArray getMetaArray()
	{
		return _array;
	}

	public Object getSizeData()
	{
		return _size;
	}

	@Override
    public Object getData()
	{
		return _data;
	}

	public Object[] getArrayData()
	{
		return _data;
	}

	public static class ArrayDataExpressionReader
	implements MetaExpressionReader
	{
		@Override
        public TypeReader getExpressionReader(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaArray metaArray = (MetaArray) element;
			return new MetaArrayReader( metaArray, resolver.getExpressionReader(map, metaArray.getSizeExpression()), resolver.getExpressionReader(map, metaArray.getTypeExpression()));
		}
	}

	private static class MetaArrayReader
	implements TypeReader
	{
		MetaArray _metaArray;
		TypeReader _size;
		TypeReader _data;

		private MetaArrayReader(final MetaArray array, final TypeReader size, final TypeReader data)
		{
			_metaArray = array;
			_size = size;
			_data = data;
		}

		@Override
        public Object read(final TypeInputStream in)
		throws TypeException, IOException
		{
		    Object sizeObject =  _size.read(in);
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
			else if (sizeObject instanceof Long )
			{
			    size = ((Long)sizeObject).intValue();
			}
			else
			{
				throw new TypeException("MetaArray not able to use size object");
			}

			final Object[] objects = new Object[ size ];
			for ( int x = 0 ; x < size; x ++ )
			{
				objects[x] = _data.read( in );
			}

			return new ArrayData( _metaArray, sizeObject, objects);
		}

	}

	public static class ArrayDataExpressionWriter
	implements MetaExpressionWriter
	{
		@Override
        public TypeWriter getExpressionWriter(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaArray metaArray = (MetaArray) element;
			return new ArrayDataWriter(metaArray, getSizeWriter(map, resolver, metaArray.getSizeExpression()), resolver.getExpressionWriter(map, metaArray.getTypeExpression()));
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


	private static class ArrayDataWriter
	implements TypeWriter
	{
		private final TypeWriter _size;
		private final TypeWriter _data;

		public ArrayDataWriter(final MetaArray metaArray, final TypeWriter size, final TypeWriter data)
		{
			_size = size;
			_data = data;
		}

		@Override
        public void write(final TypeOutputStream out, final Object o)
		throws TypeException, IOException
		{
			final ArrayData arrayData = (ArrayData) o;
			final Object[] data = arrayData.getArrayData();

	 		_size.write(out, new Integer(data.length));

	 		for(int x=0; x< data.length; x++)
	 		{
	 			_data.write(out, data[x]);
	 		}
		}
	}

}

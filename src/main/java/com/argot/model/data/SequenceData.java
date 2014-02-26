package com.argot.model.data;

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
import com.argot.meta.MetaSequence;
import com.argot.model.ModelObject;

public class SequenceData
implements ModelObject
{
	protected final MetaSequence _sequence;
	protected final Object[] _data;

	public SequenceData(final TypeElement sequence, final Object[] data)
	{
		_sequence = (MetaSequence) sequence;
		_data = data;
	}

	@Override
    public MetaExpression getStructure()
	{
		return _sequence;
	}

	public MetaSequence getMetaSequence()
	{
		return _sequence;
	}

	@Override
    public Object getData()
	{
		return _data;
	}

	public Object[] getSequenceData()
	{
		return _data;
	}

	public static class SequenceDataExpressionReader
	implements MetaExpressionReader
	{
		@Override
        public TypeReader getExpressionReader(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaSequence sequence = (MetaSequence) element;

			final TypeReader[] readers = new TypeReader[ sequence.size() ];
			for ( int x=0; x < sequence.size() ; x++ )
			{
				readers[x] = resolver.getExpressionReader(map, sequence.getElement(x));
			}
			return new SequenceDataReader(sequence, readers);
		}
	}

	private static class SequenceDataReader
	implements TypeReader
	{
		private final MetaSequence _sequence;
		private final TypeReader[] _readers;

		public SequenceDataReader( final MetaSequence sequence, final TypeReader[] readers )
		{
			_sequence = sequence;
			_readers = readers;
		}

		@Override
        public Object read(final TypeInputStream in)
		throws TypeException, IOException
		{
			final Object[] objects = new Object[ _readers.length ];

			for ( int x=0; x < _readers.length ; x++ )
			{

				objects[x] = _readers[x].read(in);
			}
			return new SequenceData(_sequence,objects);
		}

	}

	public static class SequenceDataExpressionWriter
	implements MetaExpressionWriter
	{
		@Override
        public TypeWriter getExpressionWriter(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaSequence sequence = (MetaSequence) element;

			final TypeWriter[] writers = new TypeWriter[ sequence.size() ];
			for ( int x=0; x < sequence.size() ; x++ )
			{
				writers[x] = resolver.getExpressionWriter(map, sequence.getElement(x));
			}

			return new SequenceDataWriter(writers);
		}
	}


	private static class SequenceDataWriter
	implements TypeWriter
	{
		private final TypeWriter[] _writers;

		public SequenceDataWriter(final TypeWriter[] readers )
		{
			_writers = readers;
		}

		@Override
        public void write(final TypeOutputStream out, final Object o)
		throws TypeException, IOException
		{
			final SequenceData data = (SequenceData)o;
			final Object[] sequenceData = data.getSequenceData();
			for ( int x=0; x < _writers.length ; x++ )
			{
				_writers[x].write(out,sequenceData[x]);
			}
		}
	}
}

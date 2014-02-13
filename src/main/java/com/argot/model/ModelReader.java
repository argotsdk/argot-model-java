/*
 * Copyright 2003-2005 (c) Live Media Pty Ltd. <argot@einet.com.au>
 *
 * This software is licensed under the Argot Public License
 * which may be found in the file LICENSE distributed
 * with this software.
 *
 * More information about this license can be found at
 * http://www.einet.com.au/License
 *
 * The Developer of this software is Live Media Pty Ltd,
 * PO Box 4591, Melbourne 3001, Australia.  The license is subject
 * to the law of Victoria, Australia, and subject to exclusive
 * jurisdiction of the Victorian courts.
 */
package com.argot.model;

import java.util.Iterator;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaEnvelope;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.model.data.SequenceData;

public class ModelReader
implements TypeBound,TypeLibraryReader
{
	private final MetaExpressionResolver _expressionResolver;
	private MetaExpression _metaExpression;

	public ModelReader( final MetaExpressionResolver resolver )
	{
		_expressionResolver = resolver;
		_metaExpression = null;
	}

	public ModelReader()
	{
		this( getModelResolver() );
	}

	@Override
    public void bind(final TypeLibrary library, final int definitionId, final TypeElement definition)
	throws TypeException
	{
		_metaExpression = (MetaExpression) definition;
	}

	@Override
    public TypeReader getReader(final TypeMap map)
	throws TypeException
	{
		return _expressionResolver.getExpressionReader(map, _metaExpression);
	}

	public static MetaExpressionResolver getModelResolver()
	{
		final MetaExpressionModelResolver resolver = new MetaExpressionModelResolver();
		resolver.addExpressionMap(MetaSequence.class, new SequenceData.SequenceDataExpressionReader(), null);
		resolver.addExpressionMap(MetaReference.class, new ReferenceData.ReferenceDataExpressionReader(), null);
		resolver.addExpressionMap(MetaArray.class, new ArrayData.ArrayDataExpressionReader(), null);
		resolver.addExpressionMap(MetaEnvelope.class, new EnvelopData.EnvelopDataExpressionReader(), null);
		resolver.addExpressionMap(MetaAbstract.class, new AbstractData.AbstractDataExpressionReader(), null);
		resolver.addExpressionMap(MetaTag.class, new TagData.TagDataExpressionReader(), null);
		return resolver;
	}

	public static void configureTypeMap( final TypeMap map )
	throws TypeException
	{
		final Iterator<Integer> iter = map.getIdList().iterator();
		while (iter.hasNext())
		{
			final int id = iter.next().intValue();
			if (!map.isSimpleType(id))
			{
				map.setReader(id, new ModelReader() );
				map.setWriter(id, new ModelWriter() );
			}
			else
			{
				map.setReader(id, new SimpleData.SimpleDataExpressionReader() );
				map.setWriter(id, new SimpleData.SimpleDataExpressionWriter() );
			}
		}
	}
}

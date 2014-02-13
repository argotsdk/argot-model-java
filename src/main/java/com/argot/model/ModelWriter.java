package com.argot.model;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeWriter;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaEnvelope;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.model.data.SequenceData;

public class ModelWriter
implements TypeBound, TypeLibraryWriter
{
	private final MetaExpressionResolver _expressionResolver;
	private MetaExpression _metaExpression;

	public ModelWriter( final MetaExpressionResolver resolver )
	{
		_expressionResolver = resolver;
		_metaExpression = null;
	}

	public ModelWriter()
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
    public TypeWriter getWriter(final TypeMap map)
	throws TypeException
	{
		return _expressionResolver.getExpressionWriter(map, _metaExpression);
	}

	public static MetaExpressionResolver getModelResolver()
	{
		final MetaExpressionModelResolver resolver = new MetaExpressionModelResolver();
		resolver.addExpressionMap(MetaSequence.class, new SequenceData.SequenceDataExpressionReader(), new SequenceData.SequenceDataExpressionWriter());
		resolver.addExpressionMap(MetaReference.class, new ReferenceData.ReferenceDataExpressionReader(), new ReferenceData.ReferenceDataExpressionWriter());
		resolver.addExpressionMap(MetaArray.class, new ArrayData.ArrayDataExpressionReader(), new ArrayData.ArrayDataExpressionWriter());
		resolver.addExpressionMap(MetaEnvelope.class, new EnvelopData.EnvelopDataExpressionReader(), new EnvelopData.EnvelopDataExpressionWriter());
		resolver.addExpressionMap(MetaAbstract.class, new AbstractData.AbstractDataExpressionReader(), new AbstractData.AbstractDataExpressionWriter());
		resolver.addExpressionMap(MetaTag.class, new TagData.TagDataExpressionReader(), new TagData.TagDataExpressionWriter());
		return resolver;
	}
}


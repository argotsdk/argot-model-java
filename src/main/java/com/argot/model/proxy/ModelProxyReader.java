package com.argot.model.proxy;

import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaEnvelope;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.model.AbstractData;
import com.argot.model.ArrayData;
import com.argot.model.EnvelopData;
import com.argot.model.MetaExpressionModelResolver;
import com.argot.model.ModelReader;
import com.argot.model.ReferenceData;
import com.argot.model.TagData;

public class ModelProxyReader
extends ModelReader {

    public ModelProxyReader(final ModelClassLoader loader)
    {
        super(getProxyResolver(loader));
    }


    public static MetaExpressionResolver getProxyResolver(final ModelClassLoader loader)
    {
        final MetaExpressionModelResolver resolver = new MetaExpressionModelResolver();
        resolver.addExpressionMap(MetaSequence.class, new SequenceProxy.SequenceProxyExpressionReader(loader), new SequenceProxy.SequenceProxyExpressionWriter());
        resolver.addExpressionMap(MetaReference.class, new ReferenceData.ReferenceDataExpressionReader(), new ReferenceData.ReferenceDataExpressionWriter());
        resolver.addExpressionMap(MetaArray.class, new ArrayData.ArrayDataExpressionReader(), new ArrayData.ArrayDataExpressionWriter());
        resolver.addExpressionMap(MetaEnvelope.class, new EnvelopData.EnvelopDataExpressionReader(), new EnvelopData.EnvelopDataExpressionWriter());
        resolver.addExpressionMap(MetaAbstract.class, new AbstractData.AbstractDataExpressionReader(), new AbstractData.AbstractDataExpressionWriter());
        resolver.addExpressionMap(MetaTag.class, new TagData.TagDataExpressionReader(), new TagData.TagDataExpressionWriter());
        return resolver;
    }
}

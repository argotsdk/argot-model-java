package com.argot.model.proxy;

import java.lang.reflect.Proxy;

import junit.framework.TestCase;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.model.ModelDataLoader;
import com.argot.model.TagData;
import com.argot.model.data.SequenceData;

public class ModelProxyHandlerTest
extends TestCase
{

    private TypeLibrary typeLibrary;


    @Override
    public void setUp()
    throws TypeException
    {
        // Create the type library and compile/bind the switch data types.
        typeLibrary = new TypeLibrary( );

        typeLibrary.loadLibrary(new ModelDataLoader());
    }


    public void testProxyHandler() throws TypeException {

        final MetaSequence sequence = (MetaSequence) typeLibrary.getStructure(typeLibrary.getTypeId("data", "1.0"));

        final Object[] values = new Object[sequence.size()];
        for (int x=0; x< values.length;x++) {
            values[x] = new TagData((MetaTag)sequence.getElement(x), null);
        }
        final SequenceProxy handler = new SequenceProxy( sequence, new SequenceData( sequence, values));

        final MixedData mixedData = (MixedData) Proxy.newProxyInstance(MixedData.class.getClassLoader(),
                new Class[] { MixedData.class }, handler );

        assertEquals(mixedData.hasByteChanged(), false);
        assertEquals(mixedData.hasShortChanged(), false);
        assertEquals(mixedData.hasTextChanged(), false);

        mixedData.setByte((byte)1);
        mixedData.setShort((short)10);
        mixedData.setText("test");

        assertEquals(mixedData.getByte(),1);
        assertEquals(mixedData.getShort(),10);
        assertEquals(mixedData.getText(),"test");

        assertEquals(mixedData.hasByteChanged(), true);
        assertEquals(mixedData.hasShortChanged(), true);
        assertEquals(mixedData.hasTextChanged(), true);
    }
}

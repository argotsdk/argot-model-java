package com.argot.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeMap;
import com.argot.TypeMapperCore;
import com.argot.TypeMapperDynamic;
import com.argot.TypeMapperError;
import com.argot.TypeOutputStream;
import com.argot.data.MixedData;
import com.argot.model.data.SequenceData;

public class ModelDataTest
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


    public void testWriteMessage()
    throws Exception
    {

        final TypeMap map = new TypeMap( typeLibrary, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())));

        final byte[] data = new byte[9];
        data[0] = 0;
        data[1] = 0x0a;
        data[2] = 0x32;
        data[3] = 0x05;
        data[4] = 0x48;
        data[5] = 0x65;
        data[6] = 0x6c;
        data[7] = 0x6c;
        data[8] = 0x6f;
        printByteData(data);

        final ByteArrayInputStream bais = new ByteArrayInputStream( data );
        final TypeInputStream in = new TypeInputStream( bais, map );
        final Object o = in.readObject( MixedData.TYPENAME );
        final SequenceData model = (SequenceData) o;
        System.out.println("o instanceof " + o.getClass().getName());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final TypeOutputStream out = new TypeOutputStream( baos, map );
        out.writeObject( MixedData.TYPENAME, model );
        baos.flush();
        baos.close();

        final byte[] msgData = baos.toByteArray();
        System.out.println("msgSize = " + msgData.length );
        printByteData(msgData);
    }

    private void printByteData( final byte[] data ) {

        int count=0;

        for (int x=0; x<data.length;x++)
        {
            count++;
            if (data[x] >= 48 && data[x] <= 122 )
            {
                final String value = String.valueOf((char)data[x]);
                System.out.print( value + "  ");
            }
            else
            {
                String value = Integer.toString( data[x], 16 );
                if (value.length()==1) {
                    value = "0" + value;
                }
                value = "" + value;

                System.out.print( "" + value + " ");
            }
            if (count>23)
            {
                count=0;
                System.out.println("");
            }
        }

        System.out.println("");
    }

}


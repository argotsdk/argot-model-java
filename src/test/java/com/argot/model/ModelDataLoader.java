package com.argot.model;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.data.MixedData;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryName;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaName;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.meta.MetaVersion;

public class ModelDataLoader
implements TypeLibraryLoader
{

    @Override
    public String getName()
    {
        return "mixed_data";
    }

    @Override
    public void load(final TypeLibrary library)
    throws TypeException
    {

        final int id = library.register( new DictionaryName(MetaName.parseName(library,MixedData.TYPENAME)), new MetaIdentity() );

        library.register(
                new DictionaryDefinition(id, MetaName.parseName(library,MixedData.TYPENAME), MetaVersion.parseVersion("1.0")),
                new MetaSequence(
                    new MetaExpression[]{
                        new MetaTag( "short", new MetaReference( library.getTypeId("int16"))),
                        new MetaTag( "byte", new MetaReference( library.getTypeId("int8"))),
                        new MetaTag( "text", new MetaReference( library.getTypeId("u8utf8")))
                    }
                ),
            new ModelReader( ),
            new ModelWriter(),
            MixedData.class
        );
    }

}
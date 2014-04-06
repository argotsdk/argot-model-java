package com.argot.model.proxy;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.model.MixedData;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryName;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaName;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.meta.MetaVersion;

public class ModelProxyDataLoader
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
        final ModelClassLoader loader = new ModelClassLoader(library);
        final int id = library.register( new DictionaryName(MetaName.parseName(library,MixedData.TYPENAME)), new MetaIdentity() );

        final TypeElement structure = new MetaSequence(
                    new MetaExpression[]{
                        new MetaTag( "short", new MetaReference( library.getTypeId("int16"))),
                        new MetaTag( "byte", new MetaReference( library.getTypeId("int8"))),
                        new MetaTag( "text", new MetaReference( library.getTypeId("u8utf8")))
                    }
                );
        final int newId = library.register(
                new DictionaryDefinition(id, MetaName.parseName(library,MixedData.TYPENAME), MetaVersion.parseVersion("1.0")),
                structure);

        library.bind(newId,
            new ModelProxyReader( loader ),
            new ModelProxyWriter( loader ),
            loader.getClass(structure)
        );
    }

}
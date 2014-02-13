package com.argot.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.UInt16;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaExpressionWriter;

public class AbstractData
implements ModelObject
{
	private final MetaAbstract _metaAbstract;
	private final int _type;
	private final Object _data;

	public AbstractData( final MetaAbstract metaAbstract, final int type, final Object data )
	{
		_metaAbstract = metaAbstract;
		_type = type;
		_data = data;
	}

	@Override
    public Object getData()
	{
		return _data;
	}

	@Override
    public MetaExpression getStructure()
	{
		return _metaAbstract;
	}

	public static class AbstractDataExpressionReader
	implements MetaExpressionReader
	{
		@Override
        public TypeReader getExpressionReader(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaAbstract metaAbstract = (MetaAbstract) element;
			return new AbstractDataReader(metaAbstract, map.getReader(map.getStreamId(UInt16.TYPENAME)));
		}
	}

	private static class AbstractDataReader
	implements TypeReader
	{
		private final MetaAbstract _metaAbstract;
		private final TypeReader _uint16;
		private final Map<Integer,TypeReader> _mapCache;

		public AbstractDataReader(final MetaAbstract metaAbstract, final TypeReader uint16)
		{
			_metaAbstract = metaAbstract;
			_uint16 = uint16;
			_mapCache = new HashMap<Integer,TypeReader>();
		}

		@Override
        public Object read(final TypeInputStream in)
		throws TypeException, IOException
		{
			final Object u16obj = _uint16.read(in);
			Integer type = null;
			if (u16obj instanceof SimpleData)
			{
				type = (Integer) ((SimpleData)u16obj).getData();
			}
			else
			{
				type = (Integer) u16obj;
			}
	        //Integer type = (Integer) _uint16.read(in);
	        TypeReader mappedReader = _mapCache.get(type);
	        if ( mappedReader == null )
	        {
	        	final int id = mapType( in.getTypeMap(), type );
	        	mappedReader = in.getTypeMap().getReader(id);
	        	_mapCache.put(type, mappedReader);
	        }
	        final int systemType = in.getTypeMap().getDefinitionId(type.intValue());
	        return new AbstractData( _metaAbstract,systemType,mappedReader.read( in ));
		}

		private int mapType(final TypeMap map, final Integer type)
		throws TypeException
		{
	        final int mapId = map.getDefinitionId( type.intValue() );
			final int concrete =  _metaAbstract.getConcreteId( mapId );
	        if ( concrete == TypeLibrary.NOTYPE )
	        {
	        	throw new TypeException("type not mapped:" + type.intValue() + " " + map.getName( type.intValue() ) );
	        }
			return map.getStreamId( mapId );
		}

	}


	public static class AbstractDataExpressionWriter
	implements MetaExpressionWriter
	{
		@Override
        public TypeWriter getExpressionWriter(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element)
		throws TypeException
		{
			final MetaAbstract metaAbstract = (MetaAbstract) element;
			return new AbstractDataWriter(metaAbstract, map.getWriter(map.getStreamId(UInt16.TYPENAME)));
		}
	}

	private static class CacheEntry
	{
		Integer mapId;
		TypeWriter idWriter;
	}

    private static class AbstractDataWriter
    implements TypeWriter
    {
    	private final MetaAbstract _metaAbstract;
    	private final TypeWriter _uint16;
		private final Map<Integer,CacheEntry> _mapCache;

    	public AbstractDataWriter(final MetaAbstract metaAbstract, final TypeWriter uint16)
    	{
    		_metaAbstract = metaAbstract;
    		_uint16 = uint16;
    		_mapCache = new HashMap<Integer,CacheEntry>();
    	}

		@Override
        public void write(final TypeOutputStream out, final Object o)
		throws TypeException, IOException
		{
			if (!(o instanceof AbstractData))
			{
				throw new TypeException("bad data");
			}
			final AbstractData data = (AbstractData) o;

			//int type = data.getStructure().getMemberTypeId();
			final int type = data._type;
			CacheEntry entry = _mapCache.get(new Integer(type));
			if (entry == null )
			{
				entry = mapType(out.getTypeMap(), type);
				_mapCache.put(new Integer(type), entry);
			}
			_uint16.write(out, entry.mapId);
			entry.idWriter.write(out, data.getData());
		}

		private CacheEntry mapType(final TypeMap map, final int type)
		throws TypeException
		{
			final int mapId = _metaAbstract.getConcreteId( type );
	        if ( mapId == TypeLibrary.NOTYPE )
	        {
				throw new TypeException( "can't write abstract type directly.:" );
			}

	        final CacheEntry entry = new CacheEntry();

	        // This is very important.  When working as a remote client we must ensure
	        // that the server also has the mapping from concrete to map value.  By
	        // getting the System Id of the MapId it will automatically resolve if the
	        // server has the correct value.
	        map.getStreamId(mapId);

	        // The Id written to file is the mapped concrete id.
	        entry.mapId = new Integer(map.getStreamId(type));

	        // Cache the writer function.
	        //System.out.println("Abstract getting writer for: " + map.getName(map.getId(type)) );
	        entry.idWriter = map.getWriter(map.getStreamId( type ));
			return entry;
		}

    }

}

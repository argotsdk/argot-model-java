package com.argot.model;

import com.argot.meta.MetaExpression;

public interface ModelObject
{
	public abstract MetaExpression getStructure();

	public abstract Object getData();
}

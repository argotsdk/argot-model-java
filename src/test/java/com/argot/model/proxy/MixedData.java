package com.argot.model.proxy;

import com.argot.model.ModelObject;

public interface MixedData
extends ModelObject
{

    @ModelAnnotation(0)
    public byte getByte();

    @ModelAnnotation(1)
    public short getShort();

    @ModelAnnotation(2)
    public String getText();

    @ModelAnnotation(0)
    public void setByte(byte value);

    @ModelAnnotation(1)
    public void setShort(short value);

    @ModelAnnotation(2)
    public void setText(String value);

    @ModelAnnotation(0)
    public boolean hasByteChanged();

    @ModelAnnotation(1)
    public boolean hasShortChanged();

    @ModelAnnotation(2)
    public boolean hasTextChanged();
}

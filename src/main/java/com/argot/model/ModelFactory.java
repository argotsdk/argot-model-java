package com.argot.model;

import com.argot.TypeException;

public interface ModelFactory {

    Object newInstance() throws TypeException;
}

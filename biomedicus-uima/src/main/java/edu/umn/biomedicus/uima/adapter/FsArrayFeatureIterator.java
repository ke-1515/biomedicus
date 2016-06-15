/*
 * Copyright (c) 2016 Regents of the University of Minnesota.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.umn.biomedicus.uima.adapter;

import org.apache.uima.jcas.tcas.Annotation;

import java.util.Iterator;
import java.util.function.Function;

/**
 *
 */
class FsArrayFeatureIterator<T extends Annotation, U> implements Iterator<U> {
    private final Function<Integer, T> getter;
    private final Function<T, U> adapter;
    private final int size;

    private int index = 0;

    public FsArrayFeatureIterator(Function<Integer, T> getter, Function<T, U> adapter, int size) {
        this.getter = getter;
        this.adapter = adapter;
        this.size = size;
    }

    @Override
    public boolean hasNext() {
        return index < size;
    }

    @Override
    public U next() {
        T annotation = getter.apply(index);
        index++;
        return adapter.apply(annotation);
    }
}

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

package edu.umn.biomedicus.common.terms;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public interface TermIndex {
    boolean contains(String string);

    @Nullable
    String getTerm(IndexedTerm indexedTerm);

    IndexedTerm getIndexedTerm(@Nullable CharSequence term);

    TermsBag getTermsBag(Iterable<? extends CharSequence> terms);

    List<String> getTerms(TermsBag termsBag);

    Iterator<IndexedTerm> iterator();

    Stream<IndexedTerm> stream();

    int size();
}

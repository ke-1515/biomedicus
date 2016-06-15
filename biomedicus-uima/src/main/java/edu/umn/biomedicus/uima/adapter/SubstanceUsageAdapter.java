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

import edu.umn.biomedicus.common.semantics.SubstanceUsage;
import edu.umn.biomedicus.common.semantics.SubstanceUsageType;
import edu.umn.biomedicus.common.text.Sentence;
import edu.umn.biomedicus.common.text.TextSpan;
import edu.umn.biomedicus.type.SubstanceUsageAnnotation;
import org.apache.uima.jcas.JCas;

/**
 * Adapts the UIMA {@link SubstanceUsageAnnotation} type to {@link SubstanceUsage}.
 *
 * @author Ben Knoll
 * @since 1.4
 */
class SubstanceUsageAdapter extends AnnotationAdapter<SubstanceUsageAnnotation> implements SubstanceUsage {

    /**
     * Protected constructor for AnnotationAdapter. Initializes the two fields, {@code jCas} and {@code annotation}.
     *
     * @param jCas       the {@link JCas} document the annotation is stored in.
     * @param annotation the {@link org.apache.uima.jcas.tcas.Annotation} itself.
     */
    protected SubstanceUsageAdapter(JCas jCas, SubstanceUsageAnnotation annotation) {
        super(jCas, annotation);
    }

    @Override
    public Sentence getSentence() {
        return new SentenceAdapter(getJCas(), getAnnotation().getSentence());
    }

    @Override
    public SubstanceUsageType getSubstanceUsageType() {
        return SubstanceUsageType.valueOf(getAnnotation().getSubstanceType());
    }

    @Override
    public Iterable<TextSpan> getAmounts() {
        int size = annotation.getAmounts().size();
        return () -> new FsArrayFeatureIterator<>(annotation::getAmounts, UimaAdapters::textSpanFromAnnotation, size);
    }

    @Override
    public Iterable<TextSpan> getFrequencies() {
        int size = annotation.getFrequencies().size();
        return () -> new FsArrayFeatureIterator<>(annotation::getAmounts, UimaAdapters::textSpanFromAnnotation, size);
    }

    @Override
    public Iterable<TextSpan> getTypes() {
        int size = annotation.getTypes().size();
        return () -> new FsArrayFeatureIterator<>(annotation::getAmounts, UimaAdapters::textSpanFromAnnotation, size);
    }

    @Override
    public Iterable<TextSpan> getStatuses() {
        int size = annotation.getStatuses().size();
        return () -> new FsArrayFeatureIterator<>(annotation::getAmounts, UimaAdapters::textSpanFromAnnotation, size);
    }

    @Override
    public Iterable<TextSpan> getTemporal() {
        int size = annotation.getTemporal().size();
        return () -> new FsArrayFeatureIterator<>(annotation::getAmounts, UimaAdapters::textSpanFromAnnotation, size);
    }

    @Override
    public Iterable<TextSpan> getMethods() {
        int size = annotation.getMethods().size();
        return () -> new FsArrayFeatureIterator<>(annotation::getAmounts, UimaAdapters::textSpanFromAnnotation, size);
    }
}

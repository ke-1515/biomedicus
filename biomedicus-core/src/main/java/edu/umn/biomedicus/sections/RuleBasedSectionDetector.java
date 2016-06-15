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

package edu.umn.biomedicus.sections;

import com.google.inject.Inject;
import edu.umn.biomedicus.annotations.DocumentScoped;
import edu.umn.biomedicus.application.DocumentProcessor;
import edu.umn.biomedicus.common.text.Span;
import edu.umn.biomedicus.common.text.Document;
import edu.umn.biomedicus.exc.BiomedicusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Section detector based off rules for clinical notes.
 *
 * @author Ben Knoll
 * @author Yan Wang (rules)
 * @since 1.4
 */
@DocumentScoped
public class RuleBasedSectionDetector implements DocumentProcessor {

    private final Document document;

    /**
     * The section title/headers pattern.
     */
    private final Pattern headers;

    /**
     * Injectable constructor.
     *
     * @param document the document to process.
     * @param ruleBasedSectionDetectorModel patterns.
     */
    @Inject
    RuleBasedSectionDetector(Document document, RuleBasedSectionDetectorModel ruleBasedSectionDetectorModel) {
        this.document = document;
        this.headers = ruleBasedSectionDetectorModel.getSectionHeaderPattern();
    }


    @Override
    public void process() throws BiomedicusException {
        String text = document.getText();
        Matcher matcher = headers.matcher(text);
        int prevBegin = 0;
        int prevEnd = 0;
        while (matcher.find()) {
            int begin = matcher.start();
            if (!text.substring(prevBegin, begin).isEmpty()) {
                document.createSection(Span.create(prevBegin, begin))
                        .withContentStart(prevEnd)
                        .withSectionTitle(text.substring(prevBegin, prevEnd).trim())
                        .withHasSubsections(false)
                        .withLevel(0)
                        .build();
            }

            prevBegin = begin;
            prevEnd = matcher.end();
        }

        int textEnd = text.length();
        if (!text.substring(prevBegin, textEnd).isEmpty()) {
            document.createSection(Span.create(prevBegin, textEnd))
                    .withContentStart(prevEnd)
                    .withSectionTitle(text.substring(prevBegin, prevEnd).trim())
                    .withHasSubsections(false)
                    .withLevel(0)
                    .build();
        }
    }
}

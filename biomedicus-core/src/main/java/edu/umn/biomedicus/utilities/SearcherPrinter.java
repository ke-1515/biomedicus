/*
 * Copyright (c) 2018 Regents of the University of Minnesota.
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

package edu.umn.biomedicus.utilities;

import edu.umn.biomedicus.annotations.ComponentSetting;
import edu.umn.biomedicus.framework.SearchExpr;
import edu.umn.biomedicus.framework.SearchExprFactory;
import edu.umn.biomedicus.framework.Searcher;
import edu.umn.nlpengine.Document;
import edu.umn.nlpengine.DocumentTask;
import edu.umn.nlpengine.Label;
import edu.umn.nlpengine.Span;
import javax.inject.Inject;
import javax.annotation.Nonnull;

/**
 *
 */
public class SearcherPrinter implements DocumentTask {

  private final SearchExpr searchExpr;

  @Inject
  public SearcherPrinter(
      SearchExprFactory searchExprFactory,
      @ComponentSetting("searchPattern") String searchPattern
  ) {
    searchExpr = searchExprFactory.parse(searchPattern);
  }

  @Override
  public void run(@Nonnull Document document) {
    Searcher searcher = searchExpr.createSearcher(document);

    while (true) {
      boolean found = searcher.search();
      if (!found) {
        break;
      }
      System.out
          .println("Matching Text: " + searcher.getSpan().get().coveredString(document.getText()));

      for (String group : searcher.getGroupNames()) {
        System.out.println("\tGroup Name: " + group);

        Span span = searcher.getSpan(group);
        if (span != null) {
          System.out.println("\t\tCovered Text: " + span.coveredString(document.getText()));
        }

        Label label = searcher.getLabel(group);
        if (label != null) {
          System.out.println("\t\tStored Label: " + label.toString());
        }
      }
    }
  }
}

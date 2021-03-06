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

package edu.umn.biomedicus.uima.rtf;

import edu.umn.biomedicus.common.DocumentIdentifiers;
import edu.umn.biomedicus.rtf.beans.keywords.ControlKeywordsDescription;
import edu.umn.biomedicus.rtf.beans.properties.PropertiesDescription;
import edu.umn.biomedicus.rtf.exc.RtfReaderException;
import edu.umn.biomedicus.rtf.reader.IndexListener;
import edu.umn.biomedicus.rtf.reader.KeywordAction;
import edu.umn.biomedicus.rtf.reader.OutputDestinationFactory;
import edu.umn.biomedicus.rtf.reader.RtfKeywordParser;
import edu.umn.biomedicus.rtf.reader.RtfParser;
import edu.umn.biomedicus.rtf.reader.RtfSource;
import edu.umn.biomedicus.rtf.reader.State;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;

/**
 * Parses RTF documents into UIMA CAS objects.
 *
 * @author Ben Knoll
 * @since 1.3.0
 */
class RtfParserFactory {

  /**
   * The initial properties to set a state to.
   */
  private final Map<String, Map<String, Integer>> initialProperties;

  /**
   * The keyword actions for specific keywords.
   */
  private final RtfKeywordParser rtfKeywordParser;

  /**
   * The mappings from destination name to output destinations.
   */
  private final CasMappings casMappings;

  private final boolean writeTables;

  /**
   * Creates a CAS rtf parser with the given properties.
   *
   * @param initialProperties The initial properties to set a state to.
   * @param rtfKeywordParser The keyword actions for specific keywords.
   * @param casMappings The mappings from destination name to output destinations.
   */
  RtfParserFactory(
      Map<String, Map<String, Integer>> initialProperties,
      RtfKeywordParser rtfKeywordParser,
      CasMappings casMappings,
      boolean writeTables
  ) {
    this.initialProperties = initialProperties;
    this.rtfKeywordParser = rtfKeywordParser;
    this.casMappings = casMappings;
    this.writeTables = writeTables;
  }

  /**
   * Creates a CAS rtf parser by loading from the given descriptor files.
   *
   * @param propertiesDescriptionClasspathRef The classpath reference to the properties descriptor
   * file.
   * @param controlKeywordsDescriptionClasspathRef The classpath reference to the control keywords
   * descriptor file.
   * @param casMappingsDescriptionClassPathRef the classpath reference to the cas mappings
   * descriptor file.
   * @return newly created rtf parser.
   */
  static RtfParserFactory createByLoading(
      String propertiesDescriptionClasspathRef,
      String controlKeywordsDescriptionClasspathRef,
      String casMappingsDescriptionClassPathRef,
      boolean writeTables
  ) {
    PropertiesDescription propertiesDescription = PropertiesDescription
        .loadFromFile(propertiesDescriptionClasspathRef);

    Map<String, Map<String, Integer>> properties = propertiesDescription
        .createProperties();

    ControlKeywordsDescription controlKeywordsDescription = ControlKeywordsDescription
        .loadFromFile(controlKeywordsDescriptionClasspathRef);

    Map<String, KeywordAction> keywordActionMap = controlKeywordsDescription
        .getKeywordActionsAsMap();

    CasMappings casMappings = CasMappings.loadFromFile(casMappingsDescriptionClassPathRef);

    RtfKeywordParser rtfKeywordParser = new RtfKeywordParser(keywordActionMap);

    return new RtfParserFactory(properties, rtfKeywordParser, casMappings, writeTables);
  }

  /**
   * Parses the rtf source into a set of UIMA CAS views.
   *
   * @param cas parent jCas view to create destination views in.
   * @param rtfSource the source rtf document.
   * @throws IOException if there is a problem reading.
   * @throws RtfReaderException if there is a problem parsing.
   */
  RtfParser createParser(CAS cas, RtfSource rtfSource) throws RtfReaderException {
    List<DestinationCasMapping> destinationCasMappings = casMappings.getDestinationCasMappings();

    Map<String, Type> annotationTypeForSymbolName = casMappings
        .getControlWordCasMappings()
        .stream()
        .collect(Collectors.toMap(ControlWordCasMapping::getControlWord,
            p -> cas.getTypeSystem()
                .getType(p.getAnnotationName())));

    OutputDestinationFactory outputDestinationFactory = new CasOutputDestinationFactory(
        destinationCasMappings,
        annotationTypeForSymbolName,
        casMappings.getPropertyCasMappings(),
        cas,
        writeTables
    );

    CAS originalDocumentView = cas.getView(DocumentIdentifiers.ORIGINAL_DOCUMENT);
    IndexListener indexListener = new CasIndexListener(originalDocumentView);

    State initialState = State.createState(outputDestinationFactory, initialProperties,
        indexListener);
    return new RtfParser(rtfKeywordParser, rtfSource, initialState);
  }
}

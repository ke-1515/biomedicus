package edu.umn.biomedicus.acronym;

import edu.umn.biomedicus.model.semantics.PartOfSpeech;
import edu.umn.biomedicus.model.simple.SimpleToken;
import edu.umn.biomedicus.model.text.Document;
import edu.umn.biomedicus.model.text.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Will tag tokens as acronym/abbreviations or not
 *
 * @author Greg Finley
 * @since 1.5.0
 */
@Singleton
public class AcronymDetector {
    /**
     * class logger
     */
    private static final Logger LOGGER = LogManager.getLogger(AcronymVectorModel.class);

    /*
     * All part of speech tags to exclude from consideration as acronyms.
     * Some of the verbs may have to change, but PRP and CC are key (esp. for tokens like "it", "or")
     */
    private static final Set<String> EXCLUDE_POS = new HashSet<>();

    static {
        Collections.addAll(EXCLUDE_POS,
                "PRP",  // personal pronoun
                "DT",   // determiner
                "CC",   // coordinating conjunction
                "IN",   // preposition
                "UH",   // interjection
                "TO",   // to
                "RP",   // particle
                "PDT",  // predeterminer
                "WP",   // wh-pronoun
                "WP$",  // wh-pronoun, poss
                "WDT",  // wh-determiner
                "MD"    // modal
        );
    }

    /*
     * The model that contains everything known about acronyms
     */
    private final AcronymModel model;

    /*
     * Contains orthographic rules to identify unseen abbreviations
     */
    private final OrthographicAcronymModel orthographicModel;

    /**
     * Constructor to initialize the acronym detector
     *
     * @param model             an AcronymModel that contains lists of acronyms and their senses
     * @param orthographicModel optional - an orthographic model for detecting unknown abbreviations
     */
    @Inject
    public AcronymDetector(AcronymModel model, @Nullable OrthographicAcronymModel orthographicModel) {
        if (orthographicModel == null) {
            this.orthographicModel = new OrthographicAcronymModel();
        } else {
            this.orthographicModel = orthographicModel;
        }
        this.model = model;
    }

    /**
     * Go through a Document and mark abbreviations as such using the Token isAcronymAbbrev annotation
     *
     * @param document a tokenized document`
     */
    public void detectAcronyms(Document document) {

        LOGGER.info("Detecting acronyms");

        // Look one and two tokens back for multi-token abbreviations (could make more?)
        Token prevToken = null;
        Token prevPrevToken = null;

        for (Token token : document.getTokens()) {
            // Determine if this is an abbreviation or acronym.
            // Ask the acronym model if this one is recognized.
            // If it isn't, ask the orthographic model if it seems like one.
            boolean isAcr = model.hasAcronym(token) || orthographicModel != null && orthographicModel.seemsLikeAbbreviation(token);

            // See if this token and the previous together constitute an abbreviation
            if (prevToken != null) {
                Token twoWordToken = new SimpleToken(document.getText(), prevToken.getBegin(), token.getEnd());
                if (model.hasAcronym(twoWordToken)) {
                    isAcr = true;
                    prevToken.setIsAcronym(true);
                }
                // ...and if still not, try making a three-word 'token' and seeing if that's an abbreviation
                else if (prevPrevToken != null) {
                    Token threeWordToken = new SimpleToken(document.getText(), prevPrevToken.getBegin(), token.getEnd());
                    if (model.hasAcronym(threeWordToken)) {
                        isAcr = true;
                        prevToken.setIsAcronym(true);
                        prevPrevToken.setIsAcronym(true);
                    }
                }
            }

            PartOfSpeech partOfSpeech = token.getPartOfSpeech();
            if (partOfSpeech != null && EXCLUDE_POS.contains(partOfSpeech.toString()))
                isAcr = false;

            token.setIsAcronym(isAcr);

            prevPrevToken = prevToken;
            prevToken = token;
        }
    }
}


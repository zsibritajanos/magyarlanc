package com.precognox.kconnect.gate.magyarlanc;

import gate.Annotation;
import gate.Document;
import gate.creole.ANNIEConstants;
import gate.util.GateException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import splitter.MySplitter;
import static org.junit.Assert.*;

/**
 *
 * @author akulcsar
 */
public class HungarianTokenizerSentenceSplitterTest extends GateTest {

    @BeforeClass
    public static void setUpClass() throws GateException {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testIsAnnotationNotEmpty() throws Exception {
        for (Document document : CORPUS) {
            LongStream annotationsLengths = document.getAnnotations().stream().mapToLong(a -> a.getEndNode().getOffset() - a.getStartNode().getOffset());
            annotationsLengths.min().ifPresent(minLength -> assertTrue(getMessage(document, "There should be no empty annotation"), minLength > 0));
        }
    }

    @Test
    public void testIsSentenceTrimmed() throws Exception {
        for (Document document : CORPUS) {
            List<Annotation> sentenceAnnotations = document.getAnnotations().get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE).inDocumentOrder();
            for (Annotation sentenceAnnotation : sentenceAnnotations) {
                String sentence = getCoveredText(document, sentenceAnnotation);
                assertEquals(getMessage(document, "Token"), sentence.trim(), sentence);
            }
        }
    }

    @Test
    public void testSentenceCoverage() {
        for (Document document : CORPUS) {
            String text = STRING_CLEANER.cleanString(document.getContent().toString().trim());
            List<Annotation> sentenceAnnotations = document.getAnnotations().get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE).inDocumentOrder();
            List<List<String>> expectedSentences = MySplitter.getInstance().split(text);
            assertEquals(getMessage(document, "Sentence count"), sentenceAnnotations.size(), expectedSentences.size());
            for (int i = 0; i < sentenceAnnotations.size(); i++) {
                Annotation sentenceAnnotation = sentenceAnnotations.get(i);
                List<String> expectedSentence = expectedSentences.get(i);
                int start = sentenceAnnotation.getStartNode().getOffset().intValue();
                int end = sentenceAnnotation.getEndNode().getOffset().intValue();
                String sentence = text.substring(start, end).replaceAll("\\p{javaWhitespace}", "");
                int tokenOffset = 0;
                for (String token : expectedSentence) {
                    String t = token.replaceAll("\\p{javaWhitespace}", "");
                    assertTrue(getMessage(document, "Sentence should contain " + token), sentence.startsWith(t, tokenOffset));
                    tokenOffset += t.length();
                }
                assertEquals(getMessage(document, "Part of sentence not in tokens: " + sentence.substring(tokenOffset)), sentence.length(), tokenOffset);
            }
        }
    }

    @Test
    public void testIsTokenTrimmed() {
        for (Document document : CORPUS) {
            for (Annotation tokenAnnotation : document.getAnnotations().get(ANNIEConstants.TOKEN_ANNOTATION_TYPE).inDocumentOrder()) {
                String token = getCoveredText(document, tokenAnnotation);
                assertEquals(getMessage(document, "Token"), token.trim(), token);
            }
        }
    }

    @Test
    public void testTokenCoverage() {
        for (Document document : CORPUS) {
            String text = STRING_CLEANER.cleanString(document.getContent().toString().trim());
            List<Annotation> tokenAnnotations = document.getAnnotations().get(ANNIEConstants.TOKEN_ANNOTATION_TYPE).inDocumentOrder();
            List<String> expectedTokens = new ArrayList();
            for (List<String> tokens : MySplitter.getInstance().split(text)) {
                expectedTokens.addAll(tokens);
            }

            assertEquals(getMessage(document, "Tokens count"), expectedTokens.size(), tokenAnnotations.size());
            for (int i = 0; i < tokenAnnotations.size(); i++) {
                String expectedToken = expectedTokens.get(i);
                String token = tokenAnnotations.get(i).getFeatures().get(ANNIEConstants.TOKEN_STRING_FEATURE_NAME).toString();
                assertEquals(getMessage(document, "Token"), expectedToken, token);
            }
        }
    }

    @Test
    public void testTokenFeatureString() {
        for (Document document : CORPUS) {
            for (Annotation tokenAnnotation : document.getAnnotations().get(ANNIEConstants.TOKEN_ANNOTATION_TYPE)) {
                assertEquals(getMessage(document, "String feature"),
                        STRING_CLEANER.cleanString(getCoveredText(document, tokenAnnotation).replaceAll("\\p{javaWhitespace}", "")),
                        tokenAnnotation.getFeatures().get(ANNIEConstants.TOKEN_STRING_FEATURE_NAME));

            }
        }
    }

    @Test
    public void testTokenFeatureLength() {
        for (Document document : CORPUS) {
            for (Annotation tokenAnnotation : document.getAnnotations().get(ANNIEConstants.TOKEN_ANNOTATION_TYPE)) {
                assertEquals(getMessage(document, "String feature"),
                        String.valueOf(getCoveredText(document, tokenAnnotation).length()),
                        String.valueOf(tokenAnnotation.getFeatures().get(ANNIEConstants.TOKEN_LENGTH_FEATURE_NAME)));
            }
        }
    }
}

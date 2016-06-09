package com.precognox.kconnect.gate.magyarlanc;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Utils;
import gate.creole.ANNIEConstants;
import gate.creole.ExecutionException;
import hu.u_szeged.magyarlanc.Magyarlanc;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author akulcsar
 */
public class HungarianLemmatizerPosTaggerTest extends GateTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws ExecutionException {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMorphs() {
        for (Document document : CORPUS) {
            AnnotationSet sentenceAnnotations = document.getAnnotations().get(ANNIEConstants.SENTENCE_ANNOTATION_TYPE);
            for (Annotation sentenceAnnotation : sentenceAnnotations) {
                List<Annotation> tokenAnnotations = Utils.getOverlappingAnnotations(document.getAnnotations(), sentenceAnnotation, ANNIEConstants.TOKEN_ANNOTATION_TYPE).inDocumentOrder();
                String[][] morphs = Magyarlanc.morphParseSentence(STRING_CLEANER.cleanString(Utils.stringFor(document, sentenceAnnotation).trim()));

                assertEquals(getMessage(document, "Token count"), morphs.length, tokenAnnotations.size());
                for (int i = 0; i < tokenAnnotations.size(); i++) {
                    Annotation tokenAnnotation = tokenAnnotations.get(i);
                    assertEquals(getMessage(document, "Lemma"), morphs[i][1], tokenAnnotation.getFeatures().get(HungarianLemmatizerPosTagger.TOKEN_LEMMA_FEATURE_NAME));
                    assertEquals(getMessage(document, "Pos"), morphs[i][2], tokenAnnotation.getFeatures().get(HungarianLemmatizerPosTagger.TOKEN_POS_FEATURE_NAME));
                    assertEquals(getMessage(document, "ConLL Feature"), morphs[i][3], tokenAnnotation.getFeatures().get(HungarianLemmatizerPosTagger.TOKEN_CONLLCODE_FEATURE_NAME));
                }
            }
        }
    }

}

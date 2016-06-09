package com.precognox.kconnect.gate.magyarlanc;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.Utils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.metadata.CreoleResource;
import hu.u_szeged.pos.purepos.MyPurePos;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import splitter.archive.StringCleaner;

@CreoleResource(name = "Hungarian Lemmatizer POS Tagger", comment = "Lemmatizer and POS Tagger for Hungarian language base on Magyarlanc")
public class HungarianLemmatizerPosTagger extends AbstractLanguageAnalyser {

    public static final String TOKEN_LEMMA_FEATURE_NAME = "lemma";
    public static final String TOKEN_POS_FEATURE_NAME = "pos";
    public static final String TOKEN_CONLLCODE_FEATURE_NAME = "conll";

    private static enum Morphs {
        // TOKEN(0) unused (exuals to string feature/covered text)
        LEMMA(TOKEN_LEMMA_FEATURE_NAME, 1),
        POS(TOKEN_POS_FEATURE_NAME, 2),
        FEATURES(TOKEN_CONLLCODE_FEATURE_NAME, 3);

        private final String label;
        private final int index;

        Morphs(String label, int index) {
            this.label = label;
            this.index = index;
        }

        String getFrom(String[] morphs) {
            return morphs[index];
        }

    }
    private static final StringCleaner STRING_CLEANER = new StringCleaner();

    private static final BiFunction<Document, Collection<Annotation>, List<String>> ANNOTS_TO_STRINGS
            = (doc, as) -> as.stream()
            .map(a -> a.getFeatures().getOrDefault(TOKEN_STRING_FEATURE_NAME, STRING_CLEANER.cleanString(Utils.stringFor(doc, a).trim())).toString())
            .collect(Collectors.toList());

    @Override
    public void execute() throws ExecutionException {
        try {
            Document doc = getDocument();
            AnnotationSet annotations = doc.getAnnotations();
            for (Annotation sentenceAnnotation : annotations.get(SENTENCE_ANNOTATION_TYPE)) {
                List<Annotation> tokens = Utils.getOverlappingAnnotations(annotations, sentenceAnnotation, TOKEN_ANNOTATION_TYPE).inDocumentOrder();

                String[][] morphParsedSentence = MyPurePos.getInstance().morphParseSentence(ANNOTS_TO_STRINGS.apply(doc, tokens));
                for (int i = 0; i < tokens.size(); i++) {
                    String[] morph = morphParsedSentence[i];
                    FeatureMap featureMap = tokens.get(i).getFeatures();
                    for (Morphs m : Morphs.values()) {
                        featureMap.put(m.label, m.getFrom(morph));
                    }
                }
            }
        } catch (Exception ex) {
            throw new ExecutionException(ex);
        }
    }

}

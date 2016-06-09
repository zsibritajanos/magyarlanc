package com.precognox.kconnect.gate.magyarlanc;

import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.ANNIEConstants;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleResource;
import gate.util.InvalidOffsetException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import splitter.MySplitter;
import splitter.archive.StringCleaner;
import splitter.ling.sentencesplitter.DefaultSentenceSplitter;
import splitter.ling.tokenizer.DefaultWordTokenizer;

@CreoleResource(name = "Hungarian Tokenizer and Sentence Splitter", comment = "Tokenizer and Sentence Splitter for Hungarian language base on Magyarlanc")
public class HungarianTokenizerSentenceSplitter extends AbstractLanguageAnalyser {

    private DefaultSentenceSplitter splitter;
    private DefaultWordTokenizer tokenizer;
    private StringCleaner stringCleaner;

    @Override
    public Resource init() throws ResourceInstantiationException {
        try {
            splitter = new DefaultSentenceSplitter();
            tokenizer = new DefaultWordTokenizer();
            stringCleaner = new StringCleaner();
            return super.init();
        } catch (Exception ex) {
            throw new ResourceInstantiationException(ex);
        }
    }

    @Override
    public void execute() throws ExecutionException {
        try {
            MySplitter mySplitter = MySplitter.getInstance();
            String text = stringCleaner.cleanString(document.getContent().toString().trim());

            long previousTokenEnd = 0;
            int[] sentenceOffsets = splitter.findSentenceOffsets(text, mySplitter.split(text));
            for (Entry<Integer, Integer> sentenceOffset : trimOffsets(text, sentenceOffsets)) {
                Integer ss = sentenceOffset.getKey();
                Integer se = sentenceOffset.getValue();
                getDocument().getAnnotations().add(ss.longValue(), se.longValue(), SENTENCE_ANNOTATION_TYPE, Factory.newFeatureMap());

                String sentence = text.substring(ss, se);
                Iterator<Entry<Integer, Integer>> tokenIter = trimOffsets(sentence, tokenizer.findWordOffsets(sentence, mySplitter.tokenize(sentence))).iterator();
                while (tokenIter.hasNext()) {
                    Entry<Integer, Integer> token = tokenIter.next();
                    long tokenStart = token.getKey().longValue() + ss;
                    long tokenEnd = token.getValue().longValue() + ss;

                    addTokenAnnotation(tokenStart, tokenEnd, TOKEN_ANNOTATION_TYPE);
                    if (previousTokenEnd != tokenStart) {
                        addTokenAnnotation(previousTokenEnd, tokenStart, SPACE_TOKEN_ANNOTATION_TYPE);
                    }
                    previousTokenEnd = tokenEnd;
                }
            }
        } catch (Exception ex) {
            throw new ExecutionException(ex);
        }
    }

    private void addTokenAnnotation(long start, long end, String annotationType) throws InvalidOffsetException {
        FeatureMap tokenFeatures = Factory.newFeatureMap();
        tokenFeatures.put(ANNIEConstants.TOKEN_LENGTH_FEATURE_NAME, end - start);
        String cleanedCoveredText = stringCleaner.cleanString(getDocument().getContent().getContent(start, end).toString().replaceAll("\\p{javaWhitespace}", ""));
        tokenFeatures.put(ANNIEConstants.TOKEN_STRING_FEATURE_NAME, cleanedCoveredText);
        getDocument().getAnnotations().add(start, end, annotationType, tokenFeatures);
    }

    private List<Entry<Integer, Integer>> trimOffsets(String text, int[] offsets) {
        List offsetList = new ArrayList(offsets.length);
        for (int i = 1; i < offsets.length; ++i) {
            int start = offsets[i - 1];
            int end = offsets[i];
            while (start < end && text.charAt(start) <= ' ') {
                start++;
            }
            while (start < end && text.charAt(end - 1) <= ' ') {
                end--;
            }
            offsetList.add(new SimpleEntry(start, end));
        }
        return offsetList;
    }

}

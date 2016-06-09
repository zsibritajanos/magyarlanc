package com.precognox.kconnect.gate.magyarlanc;

import gate.Annotation;
import gate.Corpus;
import gate.CreoleRegister;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import splitter.archive.StringCleaner;

public abstract class GateTest {

    private static final Logger log = LoggerFactory.getLogger(GateTest.class);
    private static final int TEST_CORPUS_SIZE = Integer.parseInt(System.getProperty("corpus.size", "10"));
    private static final String TEST_RESOURCES = "/test-resources";
    private static final String WIKIPEDIA_RANDOM_PAGE_HU = "https://hu.wikipedia.org/wiki/Speci%C3%A1lis:Lap_tal%C3%A1lomra";

    protected static final Corpus CORPUS;
    protected static final StringCleaner STRING_CLEANER = new StringCleaner();

    static {
        try {
            Gate.runInSandbox(true);
            Gate.init();
            CreoleRegister creoleRegister = Gate.getCreoleRegister();
            creoleRegister.registerComponent(HungarianTokenizerSentenceSplitter.class);
            creoleRegister.registerComponent(HungarianLemmatizerPosTagger.class);

            CORPUS = Factory.newCorpus("testCorpus");
            populateCorpus();

            SerialAnalyserController pipeline = (SerialAnalyserController) Factory.createResource(SerialAnalyserController.class.getName());
            pipeline.setCorpus(CORPUS);
            addPRtoPipe(pipeline, HungarianTokenizerSentenceSplitter.class);
            addPRtoPipe(pipeline, HungarianLemmatizerPosTagger.class);
            pipeline.execute();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static void populateCorpus() throws Exception {
        try (InputStream is = GateTest.class.getResourceAsStream(TEST_RESOURCES)) {
            List<String> lines = IOUtils.readLines(is);
            lines.removeIf(l -> l.startsWith("#"));
            for (String url : lines) {
                try {
                    CORPUS.add(Factory.newDocument(new URL(url)));
                } catch (MalformedURLException ex) {
                    log.debug("Failed to resource: {}", url, ex);
                }
            }
        } catch (IOException ex) {
            log.warn("Failed to open {}", TEST_RESOURCES, ex);
        }

        URL resourcesDir = GateTest.class.getResource("/sources");
        for (File f : new File(resourcesDir.toURI()).listFiles()) {
            CORPUS.add(Factory.newDocument(f.toURI().toURL()));
        }
        URL wikiUrl = new URL(WIKIPEDIA_RANDOM_PAGE_HU);
        while (CORPUS.size() < TEST_CORPUS_SIZE) {
            CORPUS.add(Factory.newDocument(wikiUrl));
        }
        log.info("{} random Hungarian document successfully downloaded", CORPUS.size());
    }

    private static <T extends ProcessingResource> void addPRtoPipe(SerialAnalyserController pipeline, Class<T> aClass) throws GateException {
        Gate.getCreoleRegister().registerComponent(aClass);
        pipeline.add((T) Factory.createResource(aClass.getName()));
    }

    protected static final String getCoveredText(Document document, Annotation annotation) {
        try {
            return document.getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
        } catch (InvalidOffsetException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static final String getMessage(Document document, String message) {
        return "In document " + document.getName() + "\n" + document.getContent().toString() + "\n" + message;
    }

}

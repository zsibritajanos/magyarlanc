package hu.u_szeged.cons;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.berkeley.nlp.PCFGLA.CoarseToFineNBestParser;
import edu.berkeley.nlp.PCFGLA.Option;
import edu.berkeley.nlp.PCFGLA.OptionParser;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.util.Numberer;
import hu.u_szeged.cons.util.ConstTool;
import hu.u_szeged.util.CollectionTool;
import ims.productParser.BerkeleyParserWrapper;
import ims.productParser.ProductParserData;
import ims.productParser.ProductParserTrainer;
import ims.productParser.ProductParserTrainer.Options;

public class PPReplaceTrainer extends ProductParserTrainer implements Runnable{	

	public PPReplaceTrainer(int _randSeed) {
		super(_randSeed);
	}

	public PPReplaceTrainer(int _randSeed, CoarseToFineNBestParser origparser) {
		super(_randSeed, origparser);
	}
	
	public static Map<String, Integer> wordFreqs(List<Tree<String>> corpus) {
		Map<String, Integer> freqs = new HashMap<String, Integer>();
		
		for (Tree<String> tree : corpus) {
			for (String word : tree.getTerminalYield()) {
				CollectionTool.incValueInMap(freqs, word);
			}
		}
		
		return freqs;
	}
	
	public static ProductParserData train(List<Tree<String>> corpus, int nParser, ProductParserData origGrammars, int replaceThreshold){
		trainTrees = corpus;
		if(origGrammars != null){
			numSplits=1;
		}
		
		PPReplaceModel ppd = new PPReplaceModel();
		ppd.setWordFreqs(wordFreqs(corpus));
		ppd.setThreshold(replaceThreshold);
		ConstTool.replaceAllPreTerm(corpus, ppd.wordFreqs, replaceThreshold);
		
		List<ProductParserTrainer> threads = new LinkedList<ProductParserTrainer>();
		for(int id=0;id<nParser;++id){
			CoarseToFineNBestParser origP = origGrammars==null ? null : origGrammars.getParsers().get(id);
			ProductParserTrainer p = new ProductParserTrainer(id, origP);
			threads.add(p);
			p.run();
			//(new Thread(p)).start();
		}

		
		for(ProductParserTrainer p : threads){
			ppd.getParsers().add(p.getParser());
		}
		
		return ppd;
	}	
	
	public static class ReplaceOptions {

		@Option(name = "-out", required = true, usage = "Output File for Grammar (Required)")
		public String outFileName;

		@Option(name = "-path", required = true, usage = "Path to Corpus (Required)")
		public String path = null;

		@Option(name = "-nParser", usage = "The number of parsers (Default: 4)")
		public int nParser = 4;

		@Option(name = "-uwm", usage = "Lexicon's unknownLevel")
		public int unknownLevel = 5;

		@Option(name = "-SMcycles", usage = "The number of split&merge iterations (Default: 3)")
		public int numSplits = 3;

		@Option(name = "-in", usage = "Input File for Grammar")
		public String inFile = null;
		
		@Option(name = "-rep", usage = "The treshold of replace (Default = 20)")
		public int replaceThreshold = 20;
	}
	
	public static void main(String[] args) {
		OptionParser optParser = new OptionParser(ReplaceOptions.class);
		ReplaceOptions opts = (ReplaceOptions) optParser.parse(args, true);

		ProductParserTrainer.unknownLevel = opts.unknownLevel;
		List<Tree<String>> corpus = BerkeleyParserWrapper.readCorpus(opts.path);
		ProductParserTrainer.numSplits = opts.numSplits;
		ProductParserData origGrammars = null;
		if (opts.inFile != null)
			origGrammars = ProductParserData.loadModel(opts.inFile);
		ProductParserData ppd = train(corpus, opts.nParser, origGrammars, opts.replaceThreshold);
		Map ns = Numberer.getNumberers();
		ppd.saveModel(opts.outFileName);
	}
}

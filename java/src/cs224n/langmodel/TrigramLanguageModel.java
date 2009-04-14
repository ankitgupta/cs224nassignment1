/**
 * 
 */
package cs224n.langmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cs224n.smoother.LaplaceSmoothing;
import cs224n.util.Counter;
import cs224n.util.CounterMap;
import cs224n.util.Pair;

/**
 * @author ankit
 *
 */
public class TrigramLanguageModel implements LanguageModel {

	public static final String STOP = "</S>";

	HashMap<String, CounterMap<String, String>> trigramCounter;
	CounterMap<String, String> bigramCounter;
	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#checkModel()
	 */
	public double checkModel() {
		// TODO Auto-generated method stub
		
		int nsamples = 5;
		double tolerance = 0.005;
		String firstword;
		Iterator<String> iter = trigramCounter.keySet().iterator();
		for(int i=0; i<nsamples; i++) {
			firstword = iter.next();
			double probability = 0.0;
			String secondword = trigramCounter.get(firstword).keySet().iterator().next();
			double denominator = bigramCounter.getCount(firstword, secondword);
			
			Counter<String> currentCounter = trigramCounter.get(firstword).getCounter(secondword);
			for(String word : currentCounter.keySet()) {
				probability += currentCounter.getCount(word)/denominator;
			}
			if(Math.abs(1 - probability) > tolerance)
				return probability;
		}
		return 1.0;
	}

	/**
	 * Returns a random sentence sampled according to the model.  We generate
	 * words until the stop token is generated, and return the concatenation.
	 */
	public List<String> generateSentence() {
		List<String> sentence = new ArrayList<String>();
		String firstword = STOP, secondword = STOP;

		String word = generateWord(firstword, secondword);
		while (!word.equals(STOP)) {
			sentence.add(word);
			firstword = secondword;
			secondword = word;
			word = generateWord(firstword,secondword);
		}
		return sentence;
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#generateSentence()
	 */
	private String generateWord(String firstword, String secondword) {
		double sample = Math.random();
		double sum = 0.0;
		for (String word : bigramCounter.keySet()) {
			sum += trigramCounter.get(firstword).getCount(secondword,word) / bigramCounter.getCount(firstword, secondword);
			if (sum > sample) {
				return word;
			}
		}
		return "*UNKNOWN*";   // a little probability mass was reserved for unknowns
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#getSentenceProbability(java.util.List)
	 */
	public double getSentenceProbability(List<String> sentence) {
		List<String> stoppedSentence = new ArrayList<String>(sentence);
		stoppedSentence.add(STOP);

		double probability = 0.0;
		for (int index = 0; index < stoppedSentence.size(); index++) {
			probability += Math.log(getWordProbability(stoppedSentence, index));
		}
		return Math.exp(probability);
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#getUnigramCounter()
	 */
	public Counter<String> getUnigramCounter() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#getWordCounter()
	 */
	public CounterMap<String, String> getBigramCounter() {
		// TODO Auto-generated method stub
		return bigramCounter;
	}
	
	public 	HashMap<String, CounterMap<String, String>> getTrigramCounter() {
		return trigramCounter;
	}
	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#getWordProbability(java.util.List, int)
	 */
	public double getWordProbability(List<String> sentence, int index) {
		String firstword = STOP, secondword = STOP;
		if(index >= 1)
			secondword = sentence.get(index-1);
		if(index >= 2)
			firstword = sentence.get(index-2);
		String currentword = sentence.get(index);
		double numerator;
		if(!trigramCounter.containsKey(firstword)) {
			return 1.0/(bigramCounter.keySet().size() + 1.0);
		}
		if(!trigramCounter.get(firstword).keySet().contains(secondword)) {
			return 1.0/(bigramCounter.keySet().size() + 1.0);			
		}
		numerator = trigramCounter.get(firstword).getCount(secondword, currentword);
		double denominator = bigramCounter.getCount(firstword, secondword);
		if(numerator == 0)
			return 1.0 / (denominator + 1.0);
		
		return numerator / (denominator + 1.0);
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#train(java.util.Collection)
	 */
	public void train(Collection<List<String>> trainingSentences) {
		// TODO Auto-generated method stub
		trigramCounter = new HashMap<String, CounterMap<String, String>>();
		bigramCounter = new CounterMap<String, String>();
		String firstword = STOP, secondword = STOP;
		
		for (List<String> sentence : trainingSentences) {
			List<String> stoppedSentence = new ArrayList<String>(sentence);
			stoppedSentence.add(STOP);
			firstword = STOP;
			secondword = STOP;
			bigramCounter.incrementCount(firstword, secondword, 1.0);
			
			for (String word : stoppedSentence) {
				bigramCounter.incrementCount(secondword, word, 1.0);
				if(!trigramCounter.containsKey(firstword)) {
					trigramCounter.put(firstword, new CounterMap<String, String>());
				}
				trigramCounter.get(firstword).incrementCount(secondword, word, 1.0);
				firstword = secondword;
				secondword = word;
			}
		}
//		System.out.println("\nStarting Smoothing");
//		List<LanguageModel> langmodels = new ArrayList<LanguageModel>();
//		langmodels.add(this);
//		new LaplaceSmoothing().smooth(langmodels);
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#updateWordProbability(java.lang.String, double)
	 */
	public void updateWordProbability(String word, double probability) {
		// TODO Auto-generated method stub

	}

}

/**
 * 
 */
package cs224n.langmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cs224n.smoother.LaplaceSmoothing;
import cs224n.util.Counter;

/**
 * @author ankit
 *
 */
public class BigramLanguageModel implements LanguageModel {

	public static final String START = "<S>";
	public static final String STOP = "</S>";

	private Counter<String> bigramCounter;
	private Counter<String> unigramCounter;
	private double total;
	double tolerance = 0.05;

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#checkModel()
	 */
	public double checkModel() {
		// TODO Auto-generated method stub
		int nsamples = 1;
		int i=0;
		
		for(String w1 : unigramCounter.keySet()) {
			i++;
			double probability = 0.0;

			for (String w2 : unigramCounter.keySet()) {
				probability += getWordProbability(w1+" "+w2);
			}
			if(Math.abs(probability - 1) > tolerance)
				return probability;
			if (i==nsamples)
				break;
		}
		return 1.0;
	}

	private String generateWord(String prevword) {
		double sample = Math.random();
		double sum = 0.0;
		for (String word : unigramCounter.keySet()) {
			sum += getWordProbability(prevword+" "+word);
			if (sum > sample) {
				return word;
			}
		}
		return "*UNKNOWN*";   // a little probability mass was reserved for unknowns
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#generateSentence()
	 */
	public List<String> generateSentence() {
		// TODO Auto-generated method stub
		List<String> sentence = new ArrayList<String>();
		String word = generateWord(START);
		while (!word.equals(STOP)) {
			sentence.add(word);
			word = generateWord(word);
		}
		return sentence;
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#getSentenceProbability(java.util.List)
	 */
	public double getSentenceProbability(List<String> sentence) {
		// TODO Auto-generated method stub
		double probability = 0.0;

		for (int index = 0; index <= sentence.size(); index++) {
			probability +=  Math.log(getWordProbability(sentence,index));
		}
		return Math.exp(probability);
	}

	public double getWordProbability(String bigram) {
		// TODO Auto-generated method stub
		double bigramcount = bigramCounter.getCount(bigram);
		double unigramcount = unigramCounter.getCount(bigram.split(" ")[0]);
		return bigramcount / unigramcount;
	}
	
	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#getWordProbability(java.util.List, int)
	 */
	public double getWordProbability(List<String> sentence, int index) {
		// TODO Auto-generated method stub
		String w1;
		if(index == 0)
			w1 = START;
		else
			w1 = sentence.get(index-1);
		String w2;
		if(index==sentence.size())
			w2 = STOP;
		else
			w2 = sentence.get(index);
		double bigramcount = bigramCounter.getCount(w1+" "+w2);
		double unigramcount = unigramCounter.getCount(w1);
		return bigramcount / unigramcount;
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#train(java.util.Collection)
	 */
	public void train(Collection<List<String>> trainingSentences) {
		// TODO Auto-generated method stub
		unigramCounter = new Counter<String>();
		bigramCounter = new Counter<String>();
		for (List<String> sentence : trainingSentences) {
			List<String> stoppedSentence = new ArrayList<String>();
			stoppedSentence.add(START);
			stoppedSentence.addAll(sentence);
			stoppedSentence.add(STOP);

			String prevword = "";
			for (String word : stoppedSentence) {
				unigramCounter.incrementCount(word, 1.0);
				if(prevword.equals("")) {
					prevword = START;
					continue;
				}
				bigramCounter.incrementCount(prevword+" "+word, 1.0);
				prevword = word;
			}
		}
		total = bigramCounter.totalCount();
		List<LanguageModel> langmodels = new ArrayList<LanguageModel>();
		langmodels.add(this);
		new LaplaceSmoothing().smooth(langmodels);
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#updateWordProbability(java.lang.String, double)
	 */
	public void updateWordProbability(String word, double probability) {
		// TODO Auto-generated method stub

	}

	public Counter<String> getWordCounter() {
		// TODO Auto-generated method stub
		return bigramCounter;
	}

	public Counter<String> getUnigramCounter() {
		// TODO Auto-generated method stub
		return unigramCounter;
	}

}

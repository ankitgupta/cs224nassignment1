package cs224n.langmodel;

import java.util.Collection;
import java.util.List;

import cs224n.util.Counter;
import cs224n.util.CounterMap;

/**
 * Language models assign probabilities to sentences and generate sentences.
 *
 * @author Dan Klein
 */
public interface LanguageModel {

	public void train(Collection<List<String>> trainingSentences);

	public double getSentenceProbability(List<String> sentence);

	public double getWordProbability(List<String> sentence, int index);

	public double checkModel();

	public List<String> generateSentence();

	public void updateWordProbability(String word, double probability);
	
//	public Counter<String> getWordCounter(); 

	public Counter<String> getUnigramCounter(); 
	
	public CounterMap<String, String> getBigramCounter(); 
	
//	public Counter<String> getTrigramCounter(); 
}

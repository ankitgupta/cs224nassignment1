/**
 * 
 */
package cs224n.langmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cs224n.smoother.LaplaceSmoothing;
import cs224n.util.Counter;
import cs224n.util.CounterMap;

/**
 * @author ankit
 *
 */
public class BigramLanguageModel implements LanguageModel {

//	public static final String START = "<S>";
	public static final String STOP = "</S>";
	public static final String UNKNOWN = "**UNKNOWN**";


	private CounterMap<String, String> bigramCounter;
	private Counter<String> unigramCounter;
	private double total;
	double tolerance = 0.05;

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#checkModel()
	 */
	public double checkModel() {
//		if (true)
//		return 1.0 ; 
		
		// TODO Auto-generated method stub
		
		int nsamples = 1;
		int i=0;

		for(String w1 : unigramCounter.keySet()) {
			i++;
			double probability = 0.0;

			for (String w2 : unigramCounter.keySet()) {
				double p = getWordProbability( w1, w2) ; 
				probability += p;
				/*if (p <= 0.00000000000000001){
					System.out.println(w1+ " : " + w2 + "has count = 0 " );
				}*/
			}
			
			
			
			// Grouping technique 
/*			for (String w2 : bigramCounter.getCounter(w1).keySet() ) {
				probability += getWordProbability( w1, w2);
			}
			probability += (1)/  (1.0 + (double) unigramCounter.getCount( w1));*/
			// End of Grouping technique 
			
			
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
		if (prevword.equals("")){
			prevword = STOP ; 
		}
		for (String word : unigramCounter.keySet()) {
			sum += getWordProbability( prevword, word);
			if (sum >= sample) {
				return word.intern();
			}
		}
		return ("*UNKNOWN*").intern();   // a little probability mass was reserved for unknowns
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#generateSentence()
	 */
	public List<String> generateSentence() {
		List<String> sentence = new ArrayList<String>();
		String word = generateWord("");
		String prevWord = null ;
		while (!word.equals(STOP)) {
			sentence.add(word);
			prevWord = word ;
			word = generateWord(prevWord);
		}
		return sentence;

	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#getSentenceProbability(java.util.List)
	 */
	public double getSentenceProbability(List<String> sentence) {
		// TODO Auto-generated method stub
		double probability = 0.0;

		for (int index = 0; index < sentence.size(); index++) {
			probability +=  Math.log(getWordProbability(sentence,index));
		}
		return Math.exp(probability);
	}

	public double getWordProbabilityCustomSmoothing(String w1, String w2) {
		
		//CustomSmoothing
		
//		double absoluteDiscounting = 0.75 ; 
		double bigramcount = bigramCounter.getCount(w1, w2);
		double unigramcount = unigramCounter.getCount( w1);
		double probability = 0.0 ; 
		double vocab  = unigramCounter.size() ;
		double numBigramsWithZeroCount =   vocab - (bigramCounter.getCounter(w1)).size();
//			unigramCounter.size()*unigramCounter.size() - bigramCounter.size() ;
		if (bigramcount > 0) {
			probability=  ( ((double)bigramcount) / (   1.0 + (double)unigramcount));
		} else { 
			probability = (1/numBigramsWithZeroCount)/  (1.0 + (double) unigramcount);
		}
		return probability ;
	}

	public double getWordProbability(String w1, String w2) {

		// AbsoluteDiscounting
		double discount = 0.70; 
		double bigramcount = bigramCounter.getCount(w1, w2);
		double unigramcount = unigramCounter.getCount( w1);
		double probability = 0.0 ; 
		double vocab  = unigramCounter.size() ;
		double numBigramsWithZeroCount =   vocab - (bigramCounter.getCounter(w1)).size();
//			unigramCounter.size()*unigramCounter.size() - bigramCounter.size() ;
		if (  (unigramCounter.getCount( w1) == 0)){
			probability = 1/vocab ; 
			return probability ; 
		}
		
		if (bigramcount > 0) {
			probability=  ( ((double)bigramcount - discount) / ( (double)unigramcount));
		} else { 
			probability = ( ( discount*(bigramCounter.getCounter(w1)).size() )/numBigramsWithZeroCount)/  ((double) unigramcount);
//			probability = ( 1/numBigramsWithZeroCount)/  ( 1 + (double) unigramcount);
		}
			if (probability <= 0.000000000000000000000001){
				System.out.println(w1 + " " + w2 + " has probability = " + probability);
			}
		return probability ;
	}

	
	public double getWordProbabilityLaplaceSmoothing(String w1, String w2) {

		// LaplaceSmoothing
		double bigramcount = bigramCounter.getCount(w1, w2);
		double unigramcount = unigramCounter.getCount( w1);
		double probability = 0.0 ; 
		double vocab  = unigramCounter.size() ;
//		double numBigramsWithZeroCount =   vocab - (bigramCounter.getCounter(w1)).size();
//			unigramCounter.size()*unigramCounter.size() - bigramCounter.size() ;
	
		if (  (unigramCounter.getCount( w1) == 0)){
			probability = 1/vocab ; 
			return probability ; 
		}
		probability=  ( ((double)bigramcount + 1) / ( (double)unigramcount + vocab));
		return probability ;
	}

	
	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#getWordProbability(java.util.List, int)
	 */
	public double getWordProbability(List<String> sentence, int index) {

		String w1= null ;
		String w2 = null ;
		if (index == 0){
			w1 = STOP ; 
		} else { 
			w1 = sentence.get(index - 1);
		}
		w2 = sentence.get(index);
		return getWordProbability(w1, w2) ;
//		double bigramcount = bigramCounter.getCount( w1, w2);
//		double unigramcount = unigramCounter.getCount(w2);
//		return (((double)bigramcount) / ((double)unigramcount));
	}

	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#train(java.util.Collection)
	 */
	public void train(Collection<List<String>> sentences) {
//		wordCounter = new Counter<String>();
		bigramCounter = new CounterMap<String, String>() ; 
		unigramCounter = new Counter<String>() ;

		String prevWord = "" ;
		for (List<String> sentence : sentences) {
			List<String> stoppedSentence = new ArrayList<String>(sentence);
			stoppedSentence.add(STOP);
//			String prevword = STOP;

			boolean isFirstWord= true ;

			for (String word: stoppedSentence){
//				unigramCounter.incrementCount(word, 1.0);

				if ( isFirstWord == true) {
//					unigramCounter.incrementCount(word, 1.0);
					bigramCounter.incrementCount(STOP, word, 1.0);
				} else { 
					bigramCounter.incrementCount(prevWord, word, 1.0);
				}
				prevWord = word ; 
				isFirstWord = false ;
			}
		} 
		
		for (Iterator iterator = bigramCounter.keySet().iterator(); iterator.hasNext();) {
			String w1 = (String) iterator.next();
			Counter<String> counter = bigramCounter.getCounter(w1);
			double totalCount = 0 ;
			for (Iterator it = counter.keySet().iterator(); it.hasNext();) {
				String w2 = (String) it.next();
				totalCount+= counter.getCount(w2);
			}
			unigramCounter.setCount(w1, totalCount) ;
		}
		unigramCounter.setCount(UNKNOWN, 1.0);
		// Laplace Smoothing 
//		List<LanguageModel> langmodels = new ArrayList<LanguageModel>();
//		langmodels.add(this);
//		new LaplaceSmoothing().smooth(langmodels);
		
		
		
	}		
//	total = bigramCounter.totalCount();
//	List<LanguageModel> langmodels = new ArrayList<LanguageModel>();
//	langmodels.add(this);
//	new LaplaceSmoothing().smooth(langmodels);

//	for (String word : stoppedSentence) {
//	wordCounter.incrementCount(word, 1.0);
//	}
//	}
//	total = wordCounter.totalCount();





	/* (non-Javadoc)
	 * @see cs224n.langmodel.LanguageModel#updateWordProbability(java.lang.String, double)
	 */
	public void updateWordProbability(String word, double probability) {
		// TODO Auto-generated method stub

	}

	public CounterMap<String, String> getBigramCounter() {
		// TODO Auto-generated method stub
		return null;
	}

	public Counter<String> getUnigramCounter() {
		// TODO Auto-generated method stub
		return unigramCounter;
	}

}

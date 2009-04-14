/**
 * 
 */
package cs224n.smoother;

import java.util.Collection;
import java.util.Iterator;

import cs224n.langmodel.BigramLanguageModel;
import cs224n.langmodel.LanguageModel;
import cs224n.util.Counter;
import cs224n.util.CounterMap;

/**
 * @author ankit
 *
 */
public class LaplaceSmoothing implements SmoothingScheme {

	/* (non-Javadoc)
	 * @see cs224n.smoother.SmoothingScheme#smooth(java.util.Collection, cs224n.langmodel.LanguageModel)
	 */
	public void smooth(Collection<LanguageModel> languageModels) {
		// TODO Auto-generated method stub
		LanguageModel current = languageModels.iterator().next();
		if(current.getClass() == BigramLanguageModel.class) {
			Counter<String> unigramCounter = current.getUnigramCounter();
			CounterMap<String,String> bigramCounter = current.getBigramCounter();
			
			for(String w1 : unigramCounter.keySet()) {
				for(String w2 : unigramCounter.keySet()) {
					bigramCounter.incrementCount(w1, w2, 1.0);
				}
				unigramCounter.incrementCount(w1, unigramCounter.size());
			}
		}
	}


}

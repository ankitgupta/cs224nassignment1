/**
 * 
 */
package cs224n.smoother;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import cs224n.langmodel.BigramLanguageModel;
import cs224n.langmodel.LanguageModel;
import cs224n.langmodel.TrigramLanguageModel;
import cs224n.util.Counter;
import cs224n.util.CounterMap;
import cs224n.util.Pair;

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
		TrigramLanguageModel current = (TrigramLanguageModel)languageModels.iterator().next();
		if(current.getClass() == TrigramLanguageModel.class) {
			CounterMap<String, String> bigramCounter = current.getBigramCounter();
			HashMap<String, CounterMap<String, String>> trigramCounter = current.getTrigramCounter();
			
			for(String firstword : bigramCounter.keySet()) {
				if(!trigramCounter.containsKey(firstword)) {
					trigramCounter.put(firstword, new CounterMap<String, String>());
				}
				for(String secondword : bigramCounter.keySet()) {
					for(String thirdword : bigramCounter.keySet()) {
						trigramCounter.get(firstword).incrementCount(secondword, thirdword, 1.0);
					}
					bigramCounter.incrementCount(firstword, secondword, bigramCounter.keySet().size());
				}
			}
		}

	}


}

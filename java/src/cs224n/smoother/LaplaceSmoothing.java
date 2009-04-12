/**
 * 
 */
package cs224n.smoother;

import java.util.Collection;
import java.util.Iterator;

import cs224n.langmodel.BigramLanguageModel;
import cs224n.langmodel.LanguageModel;
import cs224n.util.Counter;

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
			Counter<String> bigramCounter = current.getWordCounter();
			for(String w1 : unigramCounter.keySet()) {
				if(w1.equals(BigramLanguageModel.STOP))
					continue;
				for(String w2 : unigramCounter.keySet()) {
					if(w2.equals(BigramLanguageModel.START))
						continue;
					bigramCounter.incrementCount(w1+" "+w2, bigramCounter.getCount(w1+" "+w2) + 1.0);
				}
				unigramCounter.incrementCount(w1, unigramCounter.keySet().size()-1);
			}
		}
	}


}

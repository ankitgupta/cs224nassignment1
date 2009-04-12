/**
 * 
 */
package cs224n.smoother;

import java.util.Collection;

import cs224n.langmodel.LanguageModel;
import cs224n.util.Counter;

/**
 * @author ankit
 *
 */
public interface SmoothingScheme {
	public void smooth(Collection<LanguageModel> languageModels );
}

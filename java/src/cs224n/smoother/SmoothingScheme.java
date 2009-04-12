/**
 * 
 */
package cs224n.smoother;

import java.util.Collection;

import cs224n.langmodel.LanguageModel;

/**
 * @author ankit
 *
 */
public interface SmoothingScheme {
	public void smooth(Collection<String> tokens, LanguageModel languageModel);
}

package ch.hsr.isf.serepo.markdown.yamlfrontmatter.plugin;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.support.StringBuilderVar;
import org.pegdown.Parser;
import org.pegdown.plugins.BlockPluginParser;

/**
 * Adapted from: http://www.mattgreer.org/articles/creating-a-pegdown-plugin/
 * 
 * @author Andreas
 *
 */
public class YamlFrontmatterParser extends Parser implements BlockPluginParser {

	private static final String TAG = "---";

	public YamlFrontmatterParser() {
		super(ALL, 2000l, DefaultParseRunnerProvider);
	}

	@Override
	public Rule[] blockPluginRules() {
		return new Rule[] { yamlFrontmatter() };
	}

	public Rule yamlFrontmatter() {

		return NodeSequence(TAG, Newline(), content(), TAG, push(new YamlFrontmatterNode((String) pop())));

	}

	public Rule content() {

		StringBuilderVar rawBody = new StringBuilderVar();

		return Sequence(OneOrMore(TestNot(TAG), BaseParser.ANY, rawBody.append(matchedChar())),
				push(rawBody.getString().trim()));

	}

}

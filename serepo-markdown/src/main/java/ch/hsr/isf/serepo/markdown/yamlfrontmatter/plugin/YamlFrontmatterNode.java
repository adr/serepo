package ch.hsr.isf.serepo.markdown.yamlfrontmatter.plugin;

import java.util.ArrayList;
import java.util.List;

import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;

public class YamlFrontmatterNode extends AbstractNode {

	private String content;

	public YamlFrontmatterNode(String content) {
		this.content = content;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit((Node) this);
	}

	@Override
	public List<Node> getChildren() {
		return new ArrayList<>();
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "YamlFrontmatterNode [\n" + content + "\n]";
	}

	public boolean isEmpty() {
		return content == null || content.isEmpty();
	}
	
}

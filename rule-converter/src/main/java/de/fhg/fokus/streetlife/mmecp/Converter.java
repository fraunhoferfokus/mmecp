/**
 * Created by Kevin van Bernum (bke) on 04.08.2014.
 * Copyright (c) 2014 Fraunhofer FOKUS
 */
package de.fhg.fokus.streetlife.mmecp;

import org.dmg.pmml.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jpmml.model.ImportFilter;
import org.jpmml.model.JAXBUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import java.io.*;
import java.util.List;

public class Converter {

	private final Element schema;
	private Element nodeWrapper;
	private String targetName;
	private boolean onlyBestScore;
	private Integer id;

	public Converter(String schemaPath) throws IOException, SAXException, JDOMException {
		// parse rule converter schema and validate
		File file = new File(schemaPath);
		SAXBuilder sax = new SAXBuilder(XMLReaders.DTDVALIDATING);
		Document doc = sax.build(file);
		schema = doc.getRootElement();
		onlyBestScore = (schema.getAttributeValue("rule_for").equals("bestScore")) ? true : false;
	}

	public String convert(List<String> pmmlPathList) throws Exception {
		String knowledgeBase = "";
		for (String pmmlPath : pmmlPathList) {
			id = 0;
			PMML pmml = loadPmml(pmmlPath);
			List<Model> models = pmml.getModels();
			for (Model model : models) {
				if (model instanceof TreeModel) {
					knowledgeBase += format((TreeModel)model);
				} else {
					throw new IllegalArgumentException("Can't convert the model (" + model.getModelName() +
							"). Rule Converter coverts only tree models!");
				}
			}
		}
		knowledgeBase = replacePlaceholder(schema.getChild("body").getChild("mapText").getText(), schema.getChild("body").getAttributeValue("ph_nodeWrapper"), knowledgeBase);
        if (schema.getChild("head") != null)
		    return schema.getChild("head").getText() + knowledgeBase;
        else
            return knowledgeBase;
	}

	private PMML loadPmml(String pmmlPath) throws IOException, SAXException, JAXBException {
		InputStream is = new FileInputStream(pmmlPath);
		Source source = ImportFilter.apply(new InputSource(is));
		PMML pmml = JAXBUtil.unmarshalPMML(source);
		is.close();
		return pmml;
	}

	private String format(TreeModel treeModel) throws Exception {
		targetName = getTargetName(treeModel);

		// get node wrapper for current tree model
		nodeWrapper = getChildWithAttribute(schema.getChild("body"), "nodeWrapper", "model", treeModel.getModelName());
		if (nodeWrapper == null)
			throw new NullPointerException("There is no nodeWrapper definition for the model '" + treeModel.getModelName() + "'!");

		// format without root
		String output = "";
		for (Node child : treeModel.getNode().getNodes())
			output += format(child, "");
		return output;
	}

	private String format(Node node, String result) {
		String output = "";
		List<Node> children = node.getNodes();

		// Predicate
		Predicate predicate = node.getPredicate();
		if (predicate == null) throw new IllegalArgumentException("Missing predicate");
		if (nodeWrapper.getChild("node").getAttributeValue("ph_predicate") != null) {
            try {
                String presicateString = format(predicate);
                result += nodeWrapper.getChild("node").getChild("mapText").getText();
                result = replacePlaceholder(result, nodeWrapper.getChild("node").getAttributeValue("ph_predicate"), presicateString);
                if (nodeWrapper.getChild("node").getAttributeValue("ph_score") != null)
                    result = replacePlaceholder(result, nodeWrapper.getChild("node").getAttributeValue("ph_score"), "");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
		}

		// check child nodes
		if (!children.isEmpty()) {
			// have child's -> make more premises
			for (Node child : children) {
				output += format(child, result);
			}
		} else {
			// no child's, node is leaf -> make complete rule
            if (nodeWrapper.getChild("node").getAttributeValue("ph_score") != null) {
                result += nodeWrapper.getChild("node").getChild("mapText").getText();
                result = replacePlaceholder(result, nodeWrapper.getChild("node").getAttributeValue("ph_predicate"), "");
            }

			if (nodeWrapper.getAttribute("ph_node") != null)
				result = replacePlaceholder(nodeWrapper.getChild("mapText").getText(), nodeWrapper.getAttributeValue("ph_node"), result);

			if (nodeWrapper.getAttribute("ph_id") != null) {
				id++;
				result = replacePlaceholder(result, nodeWrapper.getAttributeValue("ph_id"), nodeWrapper.getAttributeValue("prefix_id") + id.toString());
			}

			// make score
			if (nodeWrapper.getChild("node").getAttributeValue("ph_score") != null) {
				for (ScoreDistribution scoreDistribution : node.getScoreDistributions()) {
					if (!onlyBestScore || (onlyBestScore && scoreDistribution.getValue().equals(node.getScore())))
						output += replacePlaceholder(result, nodeWrapper.getChild("node").getAttributeValue("ph_score"), format(scoreDistribution));
				}
			} else {
				output = result;
			}
		}

		return output;
	}

	private String format(Predicate predicate) {

		if (predicate instanceof SimplePredicate) {
			return format((SimplePredicate) predicate);
		} else if (predicate instanceof SimpleSetPredicate) {
			return format((SimpleSetPredicate) predicate);
		} else if (predicate instanceof True) {
			return "true";
		} else if (predicate instanceof False) {
			return "false";
		}

		throw new IllegalArgumentException("Unsupported predicate '" + predicate.getClass().getName() + "'!");
	}

	private String format(SimplePredicate simplePredicate) {
		Element predicateDef = getChildWithAttribute(nodeWrapper.getChild("node"), "predicate", "field", simplePredicate.getField().getValue());

		if (predicateDef == null)
			throw new NullPointerException("There is no predicate definition for '" +simplePredicate.getField().getValue() + "'!");
		String predicate = predicateDef.getChild("mapText").getText();

		// field
		if (predicateDef.getAttribute("ph_field") != null)
			predicate = replacePlaceholder(predicate, predicateDef.getAttributeValue("ph_field"), simplePredicate.getField().getValue());

		// operator
		if (predicateDef.getAttribute("ph_operator") != null)
			predicate = replacePlaceholder(predicate, predicateDef.getAttributeValue("ph_operator"), format(simplePredicate.getOperator()));

		// value
		if (predicateDef.getAttribute("ph_value") != null)
			predicate = replacePlaceholder(predicate, predicateDef.getAttributeValue("ph_value"), simplePredicate.getValue());

		return predicate;
	}

	private String format(SimpleSetPredicate simpleSetPredicate) {
		Element predicateDef = getChildWithAttribute(nodeWrapper.getChild("node"), "predicate", "field", simpleSetPredicate.getField().getValue());

		if (predicateDef == null)
			throw new NullPointerException("There is no predicate definition for the nodeWrapper of the model '" + nodeWrapper.getAttributeValue("model") + "'!");
		String predicate = predicateDef.getChild("mapText").getText();

		// field
		if (predicateDef.getAttribute("ph_field") != null)
			predicate = replacePlaceholder(predicate, predicateDef.getAttributeValue("ph_field"), simpleSetPredicate.getField().getValue());

		// operator
		if (predicateDef.getAttribute("ph_operator") != null)
			predicate = replacePlaceholder(predicate, predicateDef.getAttributeValue("ph_operator"), format(simpleSetPredicate.getBooleanOperator()));

		// value
		if (predicateDef.getAttribute("ph_value") != null)
			predicate = replacePlaceholder(predicate, predicateDef.getAttributeValue("ph_value"), simpleSetPredicate.getArray().getValue());

		return predicate;
	}

	private String format(ScoreDistribution scoreDistribution) {
		Element scoreDef = nodeWrapper.getChild("node").getChild("score");
		String score = scoreDef.getChild("mapText").getText();

		// field
		if (scoreDef.getAttribute("ph_field") != null)
			score = replacePlaceholder(score, scoreDef.getAttributeValue("ph_field"), targetName);

		// ToDo score confidence

		// value
		if (scoreDef.getAttribute("ph_value") != null)
			score = replacePlaceholder(score, scoreDef.getAttributeValue("ph_value"), scoreDistribution.getValue());

		return score;
	}

	private String format(SimplePredicate.Operator operator) {

		switch (operator) {
		case EQUAL:
			return "==";
		case NOT_EQUAL:
			return "!=";
		case LESS_THAN:
			return "<";
		case LESS_OR_EQUAL:
			return "<=";
		case GREATER_THAN:
			return ">";
		case GREATER_OR_EQUAL:
			return ">=";
		default:
			throw new IllegalArgumentException();
		}
	}

	private String format(SimpleSetPredicate.BooleanOperator operator) {

		switch (operator) {
		case IS_IN:
			return "isIn";
		case IS_NOT_IN:
			return "isNotIn";
		default:
			throw new IllegalArgumentException();
		}
	}

	private Element getChildWithAttribute(Element root, String childName, String attributeName, String attributeValue) {
		for (Element child : root.getChildren(childName)) {
			if (child.getAttribute(attributeName) != null &&
					child.getAttributeValue(attributeName).contains(attributeValue))
				return child;
		}
		return null;
	}

	private String replacePlaceholder(String text, String placeholder, String value) {
		StringBuffer sb = new StringBuffer(text);
		int index = sb.indexOf(placeholder);
		if (index >= 0)
			sb.replace(index, index + placeholder.length(), value);
		return sb.toString();
	}

	private String getTargetName(TreeModel treeModel) throws NullPointerException {
		String name = "";
		if (treeModel.getTargets() != null) {
			return treeModel.getTargets().getTargets().get(0).getField().getValue();
		} else {
			for (MiningField field : treeModel.getMiningSchema().getMiningFields()) {
				if (field.getUsageType() == FieldUsageType.PREDICTED)
					return field.getName().getValue();
			}
		}

		throw new NullPointerException("No target found for the model '" + treeModel.getModelName() + "'!");
	}

}

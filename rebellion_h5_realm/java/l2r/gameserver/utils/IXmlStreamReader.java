package l2r.gameserver.utils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Interface for XML parsers using stream
 * @author netvirus, Zoey76
 */

public interface IXmlStreamReader {

    /**
     * Parses a stream of node.
     * @param nodeList the node list to parse
     * @return if the node is not null, the values of the parsed nodes
     */
    default Stream<Node> nodeListStream(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item);
    }

    /**
     * Parses a stream of node, filtered by node name.
     * @param nodeList the node list to parse
     * @param nodeName the name of node
     * @return if the node is not null, the values of the parsed nodes
     */
    default Stream<Node> nodeListStreamFilteredByName(NodeList nodeList, String nodeName) {
        return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item).filter(node -> node.getNodeName().equals(nodeName));
    }

    /**
     * Parses a stream of node, filtered by node name.
     * @param nodeList the node list to parse
     * @param nodeName Map with names of nodes
     * @return if the node is not null, the values of the parsed nodes
     */
    default Stream<Node> nodeListStreamFilteredByMap(NodeList nodeList, Map nodeName) {
        return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item).filter(node -> node.getNodeName().equals(nodeName.get(node.getNodeName())));
    }

    /**
     * Parses a stream of node map attributes.
     * @param attrs the node map to parse
     * @return if the attrs is not null, the values of the attributes
     */
    default Stream<Node> attributeStream(NamedNodeMap attrs) {
        return attrs == null ? Stream.empty() : IntStream.range(0, attrs.getLength()).mapToObj(attrs::item);
    }
}

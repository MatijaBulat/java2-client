package hr.algebra.client.utils;

import hr.algebra.client.model.Player;
import hr.algebra.client.model.ScoreType;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

public class XMLDomUtils {
    public static void saveGameState(Player player1, Player player2, String absolutePath) {
        try {
            Document document = createDocument("yahtzee");
            document.getDocumentElement().appendChild(createScoreStateElement(player1, player2, document));
            saveDocument(document, absolutePath);
        } catch (ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(XMLDomUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Document createDocument(String root) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        return domImplementation.createDocument(null, root, null);
    }

    private static void saveDocument(Document document, String absolutePath) throws TransformerConfigurationException, TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(document), new StreamResult(new File(absolutePath)));
    }

    private static Node createScoreStateElement(Player player1, Player player2, Document document) {
        Element element = document.createElement("game_state");

        Node player1Node = createElement(document, "player_1_score", player1);
        element.appendChild(player1Node);

        Node player2Node = createElement(document, "player_2_score", player2);
        element.appendChild(player2Node);

        return element;
    }

    private static Node createElement(Document document, String tagName, Player player) {
        Element element = document.createElement(tagName);

        Element nameElement = document.createElement("playerName");
        nameElement.appendChild(document.createTextNode(String.valueOf(player.getName())));
        element.appendChild(nameElement);

        player.getScores().forEach((scoreType, score) -> {
            Element scoreElement = document.createElement("scoreSet");

            Element typeElement = document.createElement("scoreType");
            typeElement.appendChild(document.createTextNode(String.valueOf(scoreType)));
            scoreElement.appendChild(typeElement);

            Element valueElement = document.createElement("scoreValue");
            valueElement.appendChild(document.createTextNode(String.valueOf(score)));
            scoreElement.appendChild(valueElement);

            element.appendChild(scoreElement);
        });
        return element;
    }

    public static List<Player> loadGameState(String absolutePath) {
        List<Player> players = new ArrayList<Player>();
        try {
            Document document = createDocument(new File(absolutePath));
            NodeList nodes = document.getElementsByTagName("game_state");

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    Node player1Node = element.getElementsByTagName("player_1_score").item(0);
                    Player player1 = createPlayerFromElement(player1Node);
                    players.add(player1);

                    Node player2Node = element.getElementsByTagName("player_2_score").item(0);
                    Player player2 = createPlayerFromElement(player2Node);
                    players.add(player2);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(XMLDomUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return players;
    }

    private static Player createPlayerFromElement(Node playerNode) {
        Player player = new Player();

        if (playerNode.getNodeType() == Node.ELEMENT_NODE) {
            Element playerElement = (Element) playerNode;

            Node nameNode = playerElement.getElementsByTagName("playerName").item(0);
            if (nameNode != null) {
                String playerName = nameNode.getTextContent();
                player.setName(playerName);
            }

            NodeList scoreSetNodes = playerElement.getElementsByTagName("scoreSet");

            for (int i = 0; i < scoreSetNodes.getLength(); i++) {
                Node scoreSetNode = scoreSetNodes.item(i);

                if (scoreSetNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element scoreSetElement = (Element) scoreSetNode;

                    Node typeNode = scoreSetElement.getElementsByTagName("scoreType").item(0);
                    Node valueNode = scoreSetElement.getElementsByTagName("scoreValue").item(0);

                    if (typeNode != null && valueNode != null) {
                        String type = typeNode.getTextContent();
                        int value = Integer.parseInt(valueNode.getTextContent());

                        ScoreType scoreType = ScoreType.valueOf(type);

                        player.setScore(scoreType, value);
                    }
                }
            }
        }
        return player;
    }

    private static Document createDocument(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        return document;
    }
}

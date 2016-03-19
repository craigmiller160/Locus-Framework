/*
 * Copyright 2016 Craig Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.craigmiller160.locus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * An implementation of ConfigurationReader that uses Java's
 * DOM parsing tools to read the XML configuration file and store
 * its values in the Configuration object.
 *
 * Created by craig on 3/15/16.
 */
public class DOMConfigurationReader implements ConfigurationReader{

    private static final Logger logger = LoggerFactory.getLogger(DOMConfigurationReader.class);

    /*
     * These private constants represent the names used in the
     * XML schema. They should be used for all references to XML
     * nodes in the code to maintain consistency.
     */
    private static final String PACKAGES_NODE = "packages";
    private static final String ROOT_NODE = "Locus";
    private static final String PACKAGE_NODE = "package";
    private static final String PACKAGE_NAME_ATTR = "name";

    DOMConfigurationReader(){}

    @Override
    public LocusConfiguration readConfiguration(String fileName) throws LocusParsingException{
        logger.debug("Reading configuration file: " + fileName);
        LocusConfiguration locusConfig = new LocusConfiguration();
        InputStream iStream = null;
        try{
            iStream = getClass().getClassLoader().getResourceAsStream(fileName);
            if(iStream == null){
                throw new LocusParsingException("Unable to find Locus configuration file on classpath. File Name: " + fileName);
            }

            //Create DOM document & root element
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(iStream);
            Element rootElement = doc.getDocumentElement();

            //Get all "packages" elements. There should only be one, but just in case
            NodeList packagesElements = rootElement.getElementsByTagName(PACKAGES_NODE);
            for(int i = 0; i < packagesElements.getLength(); i++){
                //Get each "packages" element and get all of its "package" sub-elements
                Element packagesNode = (Element) packagesElements.item(i);
                NodeList packageNodes = packagesNode.getElementsByTagName(PACKAGE_NODE);

                for(int i2 = 0; i2 < packageNodes.getLength(); i2++){
                    //For each "package" element, get its name attribute and add it to configuration
                    Node node = packageNodes.item(i);
                    if(node.getNodeType() == Node.ELEMENT_NODE && node.hasAttributes()){
                        NamedNodeMap attributes = node.getAttributes();
                        Node packageNameAttr = attributes.getNamedItem(PACKAGE_NAME_ATTR);
                        if(packageNameAttr != null){
                            String name = packageNameAttr.getTextContent();
                            logger.debug("Adding package to scan: " + name);
                            locusConfig.addPackageName(name);
                        }
                    }
                }
            }
        }
        catch(ParserConfigurationException | SAXException | IOException ex){
            throw new LocusParsingException("Unable tp parse Locus configuration file", ex);
        }
        finally{
            if(iStream != null){
                try{
                    iStream.close();
                }
                catch(IOException ex){
                    logger.error("Unable to close InputStream in ConfigurationReader", ex);
                }
            }
        }

        return locusConfig;
    }


}

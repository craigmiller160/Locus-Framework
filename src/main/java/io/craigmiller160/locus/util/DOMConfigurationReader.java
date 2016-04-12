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
    private static final String NAMESPACE = "io.craigmiller160.github.com/locus-schema";
    private static final String PACKAGES_NODE = "packages";
    private static final String ROOT_NODE = "Locus";
    private static final String PACKAGE_NODE = "package";
    private static final String EXCLUSIONS_NODE = "exclusions";
    private static final String EXCLUSION_NODE = "exclusion";
    private static final String INCLUSIONS_NODE = "inclusions";
    private static final String INCLUSION_NODE = "inclusion";
    private static final String SCANNING_FILTERS_NODE = "scanning-filters";
    private static final String UI_THREAD_EXECUTOR_NODE = "uiThreadExecutor";
    private static final String PACKAGE_NAME_ATTR = "name";
    private static final String PREFIX_ATTR = "prefix";
    private static final String CLASS_ATTR = "class";

    DOMConfigurationReader(){}

    @Override
    public LocusConfiguration readConfiguration(String fileName) throws LocusParsingException{
        logger.debug("Reading configuration file: {}", fileName);
        LocusConfiguration locusConfig = new LocusConfiguration();
        InputStream iStream = null;
        try{
            iStream = getClass().getClassLoader().getResourceAsStream(fileName);
            if(iStream == null){
                throw new LocusParsingException("Unable to find Locus configuration file on classpath. File Name: " + fileName);
            }

            //Create DOM document & root element
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);
            Document doc = docBuilderFactory.newDocumentBuilder().parse(iStream);
            Element rootElement = doc.getDocumentElement();

            //Get the "packages" element
            NodeList packagesElementList = rootElement.getElementsByTagNameNS(NAMESPACE, PACKAGES_NODE);
            if(packagesElementList.getLength() > 0){
                Element packagesElement = (Element) packagesElementList.item(0);
                parsePackagesElement(packagesElement, locusConfig);
            }

            //Get the "scanning-filters" element, and parse it
            NodeList scanningFiltersNodes = rootElement.getElementsByTagNameNS(NAMESPACE, SCANNING_FILTERS_NODE);
            if(scanningFiltersNodes.getLength() > 0){
                Element scanningFiltersElement = (Element) scanningFiltersNodes.item(0);
                parseScanningFiltersElement(scanningFiltersElement, locusConfig);
            }

            //Get the "uiThreadExecutor" element, and parse it
            NodeList uiThreadNodes = rootElement.getElementsByTagName(UI_THREAD_EXECUTOR_NODE);
            if(uiThreadNodes.getLength() > 0){
                Element uiThreadElement = (Element) uiThreadNodes.item(0);
                parseUIThreadElement(uiThreadElement, locusConfig);
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

    private void parseUIThreadElement(Element uiThreadElement, LocusConfiguration locusConfiguration){
        NamedNodeMap attrs = uiThreadElement.getAttributes();
        Node clazz = attrs.getNamedItem(CLASS_ATTR);
        if(clazz != null){
            locusConfiguration.setUIThreadExecutorClassName(clazz.getTextContent());
        }
    }

    private void parsePackagesElement(Element packagesElement, LocusConfiguration locusConfig){
        //Get the "package" nodes, and parse them
        NodeList packageNodes = packagesElement.getElementsByTagNameNS(NAMESPACE, PACKAGE_NODE);
        parsePackageNodes(packageNodes, locusConfig);
    }

    private void parseScanningFiltersElement(Element scanningFiltersElement, LocusConfiguration locusConfig){
        //Get the "exclusions" element, if it exists, and parse all individual exclusions
        NodeList exclusionsElementList = scanningFiltersElement.getElementsByTagNameNS(NAMESPACE, EXCLUSIONS_NODE);
        if(exclusionsElementList.getLength() > 0){
            Element exclusionsElement = (Element) exclusionsElementList.item(0);
            NodeList exclusionNodes = exclusionsElement.getElementsByTagNameNS(NAMESPACE, EXCLUSION_NODE);
            parseExclusionNodes(exclusionNodes, locusConfig);
        }

        //Get the "inclusions" element, and parse all individual inclusions
        NodeList inclusionsElementList = scanningFiltersElement.getElementsByTagNameNS(NAMESPACE, INCLUSIONS_NODE);
        if(inclusionsElementList.getLength() > 0){
            Element inclusionsElement = (Element) inclusionsElementList.item(0);
            NodeList inclusionNodes = inclusionsElement.getElementsByTagNameNS(NAMESPACE, INCLUSION_NODE);
            parseInclusionNodes(inclusionNodes, locusConfig);
        }
    }

    private void parseInclusionNodes(NodeList inclusionNodes, LocusConfiguration locusConfig){
        for(int i = 0; i < inclusionNodes.getLength(); i++){
            //For each "inclusion" element, get its prefix attribute and add it to configuration
            Node node = inclusionNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE && node.hasAttributes()){
                NamedNodeMap attributes = node.getAttributes();
                Node prefixAttr = attributes.getNamedItem(PREFIX_ATTR);
                if(prefixAttr != null){
                    String prefix = prefixAttr.getTextContent();
                    logger.debug("Adding scanner inclusion prefix: {}", prefix);
                    locusConfig.addScannerInclusion(prefix);
                }
            }
        }
    }

    private void parseExclusionNodes(NodeList exclusionNodes, LocusConfiguration locusConfig){
        for(int i = 0; i < exclusionNodes.getLength(); i++){
            //For each "exclusion" element, get its prefix attribute and add it to configuration
            Node node = exclusionNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE && node.hasAttributes()){
                NamedNodeMap attributes = node.getAttributes();
                Node prefixAttr = attributes.getNamedItem(PREFIX_ATTR);
                if(prefixAttr != null){
                    String prefix = prefixAttr.getTextContent();
                    logger.debug("Adding scanner exclusion prefix: {}", prefix);
                    locusConfig.addScannerExclusion(prefix);
                }
            }
        }
    }

    private void parsePackageNodes(NodeList packageNodes, LocusConfiguration locusConfig){
        for(int i = 0; i < packageNodes.getLength(); i++){
            //For each "package" element, get its name attribute and add it to configuration
            Node node = packageNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE && node.hasAttributes()){
                NamedNodeMap attributes = node.getAttributes();
                Node packageNameAttr = attributes.getNamedItem(PACKAGE_NAME_ATTR);
                if(packageNameAttr != null){
                    String name = packageNameAttr.getTextContent();
                    logger.debug("Adding package to scan: {}", name);
                    locusConfig.addPackageName(name);
                }
            }
        }
    }


}

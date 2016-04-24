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

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>An implementation of ConfigurationReader that uses Java's
 * DOM parsing tools to read the XML configuration file and store
 * its values in the Configuration object.</p>
 *
 * <p><b>THREAD SAFETY:</b> This class has no mutable state, and is therefore
 * completely thread-safe.</p>
 *
 * @author craigmiller
 * @version 1.2
 */
@ThreadSafe
public class DOMConfigurationReader implements ConfigurationReader{

    private static final Logger logger = LoggerFactory.getLogger(DOMConfigurationReader.class);

    /*
     * These private constants represent the names used in the
     * XML schema. They should be used for all references to XML
     * nodes in the code to maintain consistency.
     */
    private static final String NAMESPACE = "io.craigmiller160.github.com/locus-schema";
    private static final String PACKAGES_NODE = "packages";
    private static final String CLASSES_NODE = "classes";
    private static final String CLASS_NODE = "class";
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

    /**
     * The DOMConfigurationReader should be created by the ConfigurationReaderFactory
     * class. This package-private constructor exists for testing purposes.
     */
    DOMConfigurationReader(){}

    @Override
    public LocusConfiguration readConfiguration(InputStream configSource) throws LocusParsingException{
        logger.debug("Reading Locus configuration");
        LocusConfiguration locusConfig = new LocusConfiguration();
        try{
            //Create DOM document & root element
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);
            Document doc = docBuilderFactory.newDocumentBuilder().parse(configSource);
            Element rootElement = doc.getDocumentElement();

            //Get the "packages" element. This will be mutually exclusive with the "classes" element
            NodeList packagesElementList = rootElement.getElementsByTagNameNS(NAMESPACE, PACKAGES_NODE);
            if(packagesElementList.getLength() > 0){
                logger.debug("Reading configuration for packages to scan");
                Element packagesElement = (Element) packagesElementList.item(0);
                parsePackagesElement(packagesElement, locusConfig);
            }

            //Get the "classes" element. This will be mutually exclusive with the "packages" element
            NodeList classesElementList = rootElement.getElementsByTagNameNS(NAMESPACE, CLASSES_NODE);
            if(classesElementList.getLength() > 0){
                logger.debug("Reading configuration for classes to scan");
                Element classesElement = (Element) classesElementList.item(0);
                parseClassesElement(classesElement, locusConfig);
            }

            //Get the "scanning-filters" element, and parse it
            NodeList scanningFiltersNodes = rootElement.getElementsByTagNameNS(NAMESPACE, SCANNING_FILTERS_NODE);
            if(scanningFiltersNodes.getLength() > 0){
                logger.debug("Reading configuration for scanning filters");
                Element scanningFiltersElement = (Element) scanningFiltersNodes.item(0);
                parseScanningFiltersElement(scanningFiltersElement, locusConfig);
            }

            //Get the "uiThreadExecutor" element, and parse it
            NodeList uiThreadNodes = rootElement.getElementsByTagNameNS(NAMESPACE, UI_THREAD_EXECUTOR_NODE);
            if(uiThreadNodes.getLength() > 0){
                logger.debug("Reading configuration for UIThreadExecutor");
                Element uiThreadElement = (Element) uiThreadNodes.item(0);
                parseUIThreadElement(uiThreadElement, locusConfig);
            }
        }
        catch(ParserConfigurationException | SAXException | IOException ex){
            throw new LocusParsingException("Unable tp parse Locus configuration file", ex);
        }

        return locusConfig;
    }

    /**
     * Parse the XML element containing information on the provided
     * UIThreadExecutor.
     *
     * @param uiThreadElement the XML element to parse.
     * @param locusConfiguration the LocusConfiguration.
     */
    private void parseUIThreadElement(Element uiThreadElement, LocusConfiguration locusConfiguration){
        NamedNodeMap attrs = uiThreadElement.getAttributes();
        Node clazz = attrs.getNamedItem(CLASS_ATTR);
        if(clazz != null){
            logger.trace("Adding UIThreadExecutor class name: {}", clazz.getTextContent());
            locusConfiguration.setUIThreadExecutorClassName(clazz.getTextContent());
        }
    }

    /**
     * Parse the XML element containing the names of the classes
     * to scan.
     *
     * @param classesElement the XML element to parse.
     * @param locusConfig the LocusConfiguration.
     */
    private void parseClassesElement(Element classesElement, LocusConfiguration locusConfig){
        //Get the "class" nodes, and parse them
        NodeList classNodes = classesElement.getElementsByTagNameNS(NAMESPACE, CLASS_NODE);
        parseClassNodes(classNodes, locusConfig);
    }

    /**
     * Parse the XML element containing the names of the packages
     * to scan.
     *
     * @param packagesElement the XML element to parse.
     * @param locusConfig the LocusConfiguration.
     */
    private void parsePackagesElement(Element packagesElement, LocusConfiguration locusConfig){
        //Get the "package" nodes, and parse them
        NodeList packageNodes = packagesElement.getElementsByTagNameNS(NAMESPACE, PACKAGE_NODE);
        parsePackageNodes(packageNodes, locusConfig);
    }

    /**
     * Parse the XML element containing the scanning filters.
     *
     * @param scanningFiltersElement the XML element to parse.
     * @param locusConfig the LocusConfiguration.
     */
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

    /**
     * Parse the XML elements containing the scanner inclusions.
     *
     * @param inclusionNodes the XML elements to parse.
     * @param locusConfig the LocusConfiguration.
     */
    private void parseInclusionNodes(NodeList inclusionNodes, LocusConfiguration locusConfig){
        for(int i = 0; i < inclusionNodes.getLength(); i++){
            //For each "inclusion" element, get its prefix attribute and add it to configuration
            Node node = inclusionNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE && node.hasAttributes()){
                NamedNodeMap attributes = node.getAttributes();
                Node prefixAttr = attributes.getNamedItem(PREFIX_ATTR);
                if(prefixAttr != null){
                    String prefix = prefixAttr.getTextContent();
                    logger.trace("Adding scanner inclusion prefix: {}", prefix);
                    locusConfig.addScannerInclusion(prefix);
                }
            }
        }
    }

    /**
     * Parse the XML elements containing the scanner exclusions.
     *
     * @param exclusionNodes the XML elements to parse.
     * @param locusConfig the LocusConfiguration.
     */
    private void parseExclusionNodes(NodeList exclusionNodes, LocusConfiguration locusConfig){
        for(int i = 0; i < exclusionNodes.getLength(); i++){
            //For each "exclusion" element, get its prefix attribute and add it to configuration
            Node node = exclusionNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE && node.hasAttributes()){
                NamedNodeMap attributes = node.getAttributes();
                Node prefixAttr = attributes.getNamedItem(PREFIX_ATTR);
                if(prefixAttr != null){
                    String prefix = prefixAttr.getTextContent();
                    logger.trace("Adding scanner exclusion prefix: {}", prefix);
                    locusConfig.addScannerExclusion(prefix);
                }
            }
        }
    }

    /**
     * Parse the XML elements that each contain an individual class
     * to scan.
     *
     * @param classNodes the XML elements to parse.
     * @param locusConfig the LocusConfiguration.
     */
    private void parseClassNodes(NodeList classNodes, LocusConfiguration locusConfig){
        for(int i = 0; i < classNodes.getLength(); i++){
            //For each "class" element, get its class attribute and add it to the configuration
            Node node = classNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE && node.hasAttributes()){
                NamedNodeMap attributes = node.getAttributes();
                Node classNameAttr = attributes.getNamedItem(CLASS_ATTR);
                if(classNameAttr != null){
                    String name = classNameAttr.getTextContent();
                    logger.trace("Added class name to scan: {}", name);
                    locusConfig.addClassName(name);
                }
            }
        }
    }

    /**
     * Parse the XML elements each containing the name of an
     * individual package to scan.
     *
     * @param packageNodes the XML elements to parse.
     * @param locusConfig the LocusConfiguration.
     */
    private void parsePackageNodes(NodeList packageNodes, LocusConfiguration locusConfig){
        for(int i = 0; i < packageNodes.getLength(); i++){
            //For each "package" element, get its name attribute and add it to the configuration
            Node node = packageNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE && node.hasAttributes()){
                NamedNodeMap attributes = node.getAttributes();
                Node packageNameAttr = attributes.getNamedItem(PACKAGE_NAME_ATTR);
                if(packageNameAttr != null){
                    String name = packageNameAttr.getTextContent();
                    logger.trace("Adding package to scan: {}", name);
                    locusConfig.addPackageName(name);
                }
            }
        }
    }


}

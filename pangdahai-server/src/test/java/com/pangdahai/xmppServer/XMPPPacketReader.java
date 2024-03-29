//package com.pangdahai.xmppServer;
//
//import org.dom4j.*;
//import org.dom4j.io.DispatchHandler;
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//
//import java.io.*;
//import java.net.URL;
//
///**
// * Author: gsj
// * DateTime: 12-8-29 下午3:16
// */
//public class XMPPPacketReader {
//
//    /**
//     * <code>DocumentFactory</code> used to create new document objects
//     */
//    private DocumentFactory factory;
//
//    /**
//     * <code>XmlPullParser</code> used to parse XML
//     */
//    private MXParser xppParser;
//
//    /**
//     * <code>XmlPullParser</code> used to parse XML
//     */
//    private XmlPullParserFactory xppFactory;
//
//    /**
//     * DispatchHandler to call when each <code>Element</code> is encountered
//     */
//    private DispatchHandler dispatchHandler;
//
//    /**
//     * Last time a full Document was read or a heartbeat was received. Hearbeats
//     * are represented as whitespaces received while a Document is not being parsed.
//     */
//    private long lastActive = System.currentTimeMillis();
//
//
//    public XMPPPacketReader() {
//    }
//
//    public XMPPPacketReader(DocumentFactory factory) {
//        this.factory = factory;
//    }
//
//
//    /**
//     * <p>Reads a Document from the given <code>File</code></p>
//     *
//     * @param file is the <code>File</code> to read from.
//     * @return the newly created Document instance
//     * @throws org.dom4j.DocumentException              if an error occurs during parsing.
//     * @throws java.net.MalformedURLException if a URL could not be made for the given File
//     */
//    public Document read(File file) throws DocumentException, IOException, XmlPullParserException {
//        String systemID = file.getAbsolutePath();
//        return read(new BufferedReader(new FileReader(file)), systemID);
//    }
//
//    /**
//     * <p>Reads a Document from the given <code>URL</code></p>
//     *
//     * @param url <code>URL</code> to read from.
//     * @return the newly created Document instance
//     * @throws DocumentException if an error occurs during parsing.
//     */
//    public Document read(URL url) throws DocumentException, IOException, XmlPullParserException {
//        String systemID = url.toExternalForm();
//        return read(createReader(url.openStream()), systemID);
//    }
//
//    /**
//     * <p>Reads a Document from the given URL or filename.</p>
//     * <p/>
//     * <p/>
//     * If the systemID contains a <code>':'</code> character then it is
//     * assumed to be a URL otherwise its assumed to be a file name.
//     * If you want finer grained control over this mechansim then please
//     * explicitly pass in either a {@link URL} or a {@link File} instance
//     * instead of a {@link String} to denote the source of the document.
//     * </p>
//     *
//     * @param systemID is a URL for a document or a file name.
//     * @return the newly created Document instance
//     * @throws DocumentException              if an error occurs during parsing.
//     * @throws java.net.MalformedURLException if a URL could not be made for the given File
//     */
//    public Document read(String systemID) throws DocumentException, IOException, XmlPullParserException {
//        if (systemID.indexOf(':') >= 0) {
//            // lets assume its a URL
//            return read(new URL(systemID));
//        }
//        else {
//            // lets assume that we are given a file name
//            return read(new File(systemID));
//        }
//    }
//
//    /**
//     * <p>Reads a Document from the given stream</p>
//     *
//     * @param in <code>InputStream</code> to read from.
//     * @return the newly created Document instance
//     * @throws DocumentException if an error occurs during parsing.
//     */
//    public Document read(InputStream in) throws DocumentException, IOException, XmlPullParserException {
//        return read(createReader(in));
//    }
//
//    /**
//     * <p>Reads a Document from the given stream</p>
//     *
//     * @param charSet the charSet that the input is encoded in
//     * @param in <code>InputStream</code> to read from.
//     * @return the newly created Document instance
//     * @throws DocumentException if an error occurs during parsing.
//     */
//    public Document read(String charSet, InputStream in)
//            throws DocumentException, IOException, XmlPullParserException
//    {
//        return read(createReader(in, charSet));
//    }
//
//    /**
//     * <p>Reads a Document from the given <code>Reader</code></p>
//     *
//     * @param reader is the reader for the input
//     * @return the newly created Document instance
//     * @throws DocumentException if an error occurs during parsing.
//     */
//    public Document read(Reader reader) throws DocumentException, IOException, XmlPullParserException {
//        getXPPParser().setInput(reader);
//        return parseDocument();
//    }
//
//    /**
//     * <p>Reads a Document from the given array of characters</p>
//     *
//     * @param text is the text to parse
//     * @return the newly created Document instance
//     * @throws DocumentException if an error occurs during parsing.
//     */
//    public Document read(char[] text) throws DocumentException, IOException, XmlPullParserException {
//        getXPPParser().setInput(new CharArrayReader(text));
//        return parseDocument();
//    }
//
//    /**
//     * <p>Reads a Document from the given stream</p>
//     *
//     * @param in       <code>InputStream</code> to read from.
//     * @param systemID is the URI for the input
//     * @return the newly created Document instance
//     * @throws DocumentException if an error occurs during parsing.
//     */
//    public Document read(InputStream in, String systemID) throws DocumentException, IOException, XmlPullParserException {
//        return read(createReader(in), systemID);
//    }
//
//    /**
//     * <p>Reads a Document from the given <code>Reader</code></p>
//     *
//     * @param reader   is the reader for the input
//     * @param systemID is the URI for the input
//     * @return the newly created Document instance
//     * @throws DocumentException if an error occurs during parsing.
//     */
//    public Document read(Reader reader, String systemID) throws DocumentException, IOException, XmlPullParserException {
//        Document document = read(reader);
//        document.setName(systemID);
//        return document;
//    }
//
//
//    // Properties
//    //-------------------------------------------------------------------------
//
//    public MXParser getXPPParser() throws XmlPullParserException {
//        if (xppParser == null) {
//            xppParser = (MXParser) getXPPFactory().newPullParser();
//        }
//        return xppParser;
//    }
//
//    public XmlPullParserFactory getXPPFactory() throws XmlPullParserException {
//        if (xppFactory == null) {
//            xppFactory = XmlPullParserFactory.newInstance(MXParser.class.getName(), null);
//        }
//        xppFactory.setNamespaceAware(true);
//        return xppFactory;
//    }
//
//    public void setXPPFactory(XmlPullParserFactory xppFactory) {
//        this.xppFactory = xppFactory;
//    }
//
//    /**
//     * @return the <code>DocumentFactory</code> used to create document objects
//     */
//    public DocumentFactory getDocumentFactory() {
//        if (factory == null) {
//            factory = DocumentFactory.getInstance();
//        }
//        return factory;
//    }
//
//    /**
//     * <p>This sets the <code>DocumentFactory</code> used to create new documents.
//     * This method allows the building of custom DOM4J tree objects to be implemented
//     * easily using a custom derivation of {@link DocumentFactory}</p>
//     *
//     * @param factory <code>DocumentFactory</code> used to create DOM4J objects
//     */
//    public void setDocumentFactory(DocumentFactory factory) {
//        this.factory = factory;
//    }
//
//
//    /**
//     * Adds the <code>ElementHandler</code> to be called when the
//     * specified path is encounted.
//     *
//     * @param path    is the path to be handled
//     * @param handler is the <code>ElementHandler</code> to be called
//     *                by the event based processor.
//     */
//    public void addHandler(String path, ElementHandler handler) {
//        getDispatchHandler().addHandler(path, handler);
//    }
//
//    /**
//     * Removes the <code>ElementHandler</code> from the event based
//     * processor, for the specified path.
//     *
//     * @param path is the path to remove the <code>ElementHandler</code> for.
//     */
//    public void removeHandler(String path) {
//        getDispatchHandler().removeHandler(path);
//    }
//
//    /**
//     * When multiple <code>ElementHandler</code> instances have been
//     * registered, this will set a default <code>ElementHandler</code>
//     * to be called for any path which does <b>NOT</b> have a handler
//     * registered.
//     *
//     * @param handler is the <code>ElementHandler</code> to be called
//     *                by the event based processor.
//     */
//    public void setDefaultHandler(ElementHandler handler) {
//        getDispatchHandler().setDefaultHandler(handler);
//    }
//
//    /**
//     * Returns the last time a full Document was read or a heartbeat was received. Hearbeats
//     * are represented as whitespaces or \n received while a Document is not being parsed.
//     *
//     * @return the time in milliseconds when the last document or heartbeat was received.
//     */
//    public long getLastActive() {
//        long lastHeartbeat = 0;
//        try {
//            lastHeartbeat = getXPPParser().getLastHeartbeat();
//        }
//        catch (XmlPullParserException e) {}
//        return lastActive > lastHeartbeat ? lastActive : lastHeartbeat;
//    }
//
//    /*
//     * DANIELE: Add parse document by string
//     */
//    public Document parseDocument(String xml) throws DocumentException {
//        /*
//        // Long way with reuse of DocumentFactory.
//        DocumentFactory df = getDocumentFactory();
//        SAXReader reader = new SAXReader( df );
//        Document document = reader.read( new StringReader( xml );*/
//
//        // Simple way
//        // TODO Optimize. Do not create a sax reader for each parsing
//        Document document = DocumentHelper.parseText(xml);
//
//        return document;
//    }
//
//    // Implementation methods
//    //-------------------------------------------------------------------------
//    public Document parseDocument() throws DocumentException, IOException, XmlPullParserException {
//        DocumentFactory df = getDocumentFactory();
//        Document document = df.createDocument();
//        Element parent = null;
//        XmlPullParser pp = getXPPParser();
//        int count = 0;
//        while (true) {
//            int type = -1;
//            type = pp.nextToken();
//            switch (type) {
//                case XmlPullParser.PROCESSING_INSTRUCTION: {
//                    String text = pp.getText();
//                    int loc = text.indexOf(" ");
//                    if (loc >= 0) {
//                        document.addProcessingInstruction(text.substring(0, loc),
//                                text.substring(loc + 1));
//                    }
//                    else {
//                        document.addProcessingInstruction(text, "");
//                    }
//                    break;
//                }
//                case XmlPullParser.COMMENT: {
//                    if (parent != null) {
//                        parent.addComment(pp.getText());
//                    }
//                    else {
//                        document.addComment(pp.getText());
//                    }
//                    break;
//                }
//                case XmlPullParser.CDSECT: {
//                    String text = pp.getText();
//                    if (parent != null) {
//                        parent.addCDATA(text);
//                    }
//                    else {
//                        if (text.trim().length() > 0) {
//                            throw new DocumentException("Cannot have text content outside of the root document");
//                        }
//                    }
//                    break;
//
//                }
//                case XmlPullParser.ENTITY_REF: {
//                    String text = pp.getText();
//                    if (parent != null) {
//                        parent.addText(text);
//                    }
//                    else {
//                        if (text.trim().length() > 0) {
//                            throw new DocumentException("Cannot have an entityref outside of the root document");
//                        }
//                    }
//                    break;
//                }
//                case XmlPullParser.END_DOCUMENT: {
//                    return document;
//                }
//                case XmlPullParser.START_TAG: {
//                    QName qname = (pp.getPrefix() == null) ? df.createQName(pp.getName(), pp.getNamespace()) : df.createQName(pp.getName(), pp.getPrefix(), pp.getNamespace());
//                    Element newElement = null;
//                    // Do not include the namespace if this is the start tag of a new packet
//                    // This avoids including "jabber:client", "jabber:server" or
//                    // "jabber:component:accept"
//                    if ("jabber:client".equals(qname.getNamespaceURI()) ||
//                            "jabber:server".equals(qname.getNamespaceURI()) ||
//                            "jabber:connectionmanager".equals(qname.getNamespaceURI()) ||
//                            "jabber:component:accept".equals(qname.getNamespaceURI()) ||
//                            "http://jabber.org/protocol/httpbind".equals(qname.getNamespaceURI())) {
//                        newElement = df.createElement(pp.getName());
//                    }
//                    else {
//                        newElement = df.createElement(qname);
//                    }
//                    int nsStart = pp.getNamespaceCount(pp.getDepth() - 1);
//                    int nsEnd = pp.getNamespaceCount(pp.getDepth());
//                    for (int i = nsStart; i < nsEnd; i++) {
//                        if (pp.getNamespacePrefix(i) != null) {
//                            newElement
//                                    .addNamespace(pp.getNamespacePrefix(i), pp.getNamespaceUri(i));
//                        }
//                    }
//                    for (int i = 0; i < pp.getAttributeCount(); i++) {
//                        QName qa = (pp.getAttributePrefix(i) == null) ? df.createQName(pp.getAttributeName(i)) : df.createQName(pp.getAttributeName(i), pp.getAttributePrefix(i), pp.getAttributeNamespace(i));
//                        newElement.addAttribute(qa, pp.getAttributeValue(i));
//                    }
//                    if (parent != null) {
//                        parent.add(newElement);
//                    }
//                    else {
//                        document.add(newElement);
//                    }
//                    parent = newElement;
//                    count++;
//                    break;
//                }
//                case XmlPullParser.END_TAG: {
//                    if (parent != null) {
//                        parent = parent.getParent();
//                    }
//                    count--;
//                    if (count < 1) {
//                        // Update the last time a Document was received
//                        lastActive = System.currentTimeMillis();
//                        return document;
//                    }
//                    break;
//                }
//                case XmlPullParser.TEXT: {
//                    String text = pp.getText();
//                    if (parent != null) {
//                        parent.addText(text);
//                    }
//                    else {
//                        if (text.trim().length() > 0) {
//                            throw new DocumentException("Cannot have text content outside of the root document");
//                        }
//                    }
//                    break;
//                }
//                default:
//                {
//                    ;
//                }
//            }
//        }
//    }
//
//    protected DispatchHandler getDispatchHandler() {
//        if (dispatchHandler == null) {
//            dispatchHandler = new DispatchHandler();
//        }
//        return dispatchHandler;
//    }
//
//    protected void setDispatchHandler(DispatchHandler dispatchHandler) {
//        this.dispatchHandler = dispatchHandler;
//    }
//
//    /**
//     * Factory method to create a Reader from the given InputStream.
//     */
//    protected Reader createReader(InputStream in) throws IOException {
//        return new BufferedReader(new InputStreamReader(in));
//    }
//
//    private Reader createReader(InputStream in, String charSet) throws UnsupportedEncodingException {
//        return new BufferedReader(new InputStreamReader(in, charSet));
//    }
//}

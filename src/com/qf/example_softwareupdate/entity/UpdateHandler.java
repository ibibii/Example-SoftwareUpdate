package com.qf.example_softwareupdate.entity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UpdateHandler extends DefaultHandler{
private Update update;
	
	private static final int VERSION = 1;
	private static final int TITLE = 2;
	private static final int DESC = 3;
	private static final int URL = 4;
	private int currentIndex = 0;
	
	public Update getUpdate() {
		return update;
	}

	@Override
	public void startDocument() throws SAXException {
		update = new Update();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("version")) {
			currentIndex = VERSION;
		} else if (qName.equals("title")) {
			currentIndex = TITLE;
		} else if (qName.equals("desc")) {
			currentIndex = DESC;
		} else if (qName.equals("url")) {
			currentIndex = URL;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String value = new String(ch, start, length);
		switch (currentIndex) {
		case VERSION:
			update.setVersion(Integer.parseInt(value));
			break;
		case TITLE:
			update.setTitle(value);
			break;
		case DESC:
			update.setDesc(value);
			break;
		case URL:
			update.setUrl(value);
			break;
		default:
			break;
		}
		
		currentIndex = 0;
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
	}

}

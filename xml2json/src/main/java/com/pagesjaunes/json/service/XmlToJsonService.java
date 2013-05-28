package com.pagesjaunes.json.service;

/*
 Copyright (c) 2002 JSON.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pagesjaunes.json.JSONException;
import com.pagesjaunes.json.JSONObject;
import com.pagesjaunes.json.XMLTokener;
import com.pagesjaunes.json.config.JsonConf;
import com.pagesjaunes.json.config.Types;

/**
 * Use JSON.org class.
 * 
 * Convert a well-formed (but not necessarily valid) XML string into a
 * JSONObject. Some information may be lost in this transformation because JSON
 * is a data format and XML is a document format. XML uses elements, attributes,
 * and content text, while JSON uses unordered collections of name/value pairs
 * and arrays of values. JSON does not does not like to distinguish between
 * elements and attributes. Sequences of similar elements are represented as
 * JSONArrays. Content text may be placed in a "content" member. Comments,
 * prologs, DTDs, and <code>&lt;[ [ ]]></code> are ignored.
 * 
 * @param string
 *            The XML source string.
 * @return A JSON string containing the structured data from the XML string.
 * @throws JSONException
 */

public class XmlToJsonService {

	/** The Character '&amp;'. */
	public static final Character AMP = new Character('&');

	/** The Character '''. */
	public static final Character APOS = new Character('\'');

	/** The Character '!'. */
	public static final Character BANG = new Character('!');

	/** The Character '='. */
	public static final Character EQ = new Character('=');

	/** The Character '>'. */
	public static final Character GT = new Character('>');

	/** The Character '&lt;'. */
	public static final Character LT = new Character('<');

	/** The Character '?'. */
	public static final Character QUEST = new Character('?');

	/** The Character '"'. */
	public static final Character QUOT = new Character('"');

	/** The Character '/'. */
	public static final Character SLASH = new Character('/');

	protected static final Logger LOG = Logger.getLogger(XmlToJsonService.class);

	protected Map<String, JsonConf> configurationMap;

	/**
	 * @param pConfigurationMap
	 */
	public XmlToJsonService(Map<String, JsonConf> pConfigurationMap) {
		configurationMap = pConfigurationMap;
	}

	/**
	 * Scan the content following the named tag, attaching it to the context.
	 * 
	 * @param x
	 *            The XMLTokener containing the source string.
	 * @param context
	 *            The JSONObject that will include the new material.
	 * @param name
	 *            The tag name.
	 * @return true if the close tag is processed.
	 * @throws JSONException
	 */
	private boolean parse(XMLTokener x, JSONObject context, String name,
			Deque<String> queue) throws JSONException {
		char c;
		int i;
		JSONObject jsonobject = null;
		String string;
		String tagName;
		Object token;

		// Test for and skip past these forms:
		// <!-- ... -->
		// <! ... >
		// <![ ... ]]>
		// <? ... ?>
		// Report errors for these forms:
		// <>
		// <=
		// <<

		token = x.nextToken();
		// <!

		if (token == BANG) {
			c = x.next();
			if (c == '-') {
				if (x.next() == '-') {
					x.skipPast("-->");
					return false;
				}
				x.back();
			} else if (c == '[') {
				token = x.nextToken();
				if ("CDATA".equals(token)) {
					if (x.next() == '[') {
						string = x.nextCDATA();
						if (string.length() > 0) {
							context.accumulate("content", string, false);
						}
						return false;
					}
				}
				throw x.syntaxError("Expected 'CDATA['");
			}
			i = 1;
			do {
				token = x.nextMeta();
				if (token == null) {
					throw x.syntaxError("Missing '>' after '<!'.");
				} else if (token == LT) {
					i += 1;
				} else if (token == GT) {
					i -= 1;
				}
			} while (i > 0);
			return false;
		} else if (token == QUEST) {

			// <?

			x.skipPast("?>");
			return false;
		} else if (token == SLASH) {

			// Close tag </

			token = x.nextToken();
			queue.removeLast();
			if (name == null) {
				throw x.syntaxError("Mismatched close tag " + token);
			}
			if (!token.equals(name)) {
				throw x.syntaxError("Mismatched " + name + " and " + token);
			}
			if (x.nextToken() != GT) {
				throw x.syntaxError("Misshaped close tag");
			}
			return true;

		} else if (token instanceof Character) {
			throw x.syntaxError("Misshaped tag");

			// Open tag <

		} else {
			tagName = (String) token;
			addQueue(queue, tagName);
			token = null;
			jsonobject = new JSONObject();

			JsonConf jsonConf = configurationMap.get(queue.getLast());
			LOG.debug("Queue = "+ queue.getLast());
			if (LOG.isDebugEnabled() && null != jsonConf) {
				LOG.debug("Queue = " + queue.getLast() + ", " + jsonConf);
			}
			boolean isArray = (jsonConf != null ? jsonConf.getTypes().equals(
					Types.ARRAY) : false);

			for (;;) {
				if (token == null) {
					token = x.nextToken();
				}

				// attribute = value

				if (token instanceof String) {
					string = (String) token;
					addQueue(queue, string);
					token = x.nextToken();
					if (token == EQ) {
						token = x.nextToken();
						if (!(token instanceof String)) {
							throw x.syntaxError("Missing value");
						}
						jsonobject.accumulate(
								string,
								stringToValue(tagName, (String) token,
										queue.getLast()), false);
						token = null;
					} else {
						jsonobject.accumulate(string, "", false);
					}
					queue.removeLast();

					// Empty tag <.../>

				} else if (token == SLASH) {
					queue.removeLast();
					if (x.nextToken() != GT) {
						throw x.syntaxError("Misshaped tag");
					}
					if (jsonobject.length() > 0) {
						context.accumulate(tagName, jsonobject, false);
					} else {
						// Les blocs vides ne sont pas ajoutés au flux JSON
						// context.accumulate(tagName, "");
					}
					return false;

					// Content, between <...> and </...>

				} else if (token == GT) {
					for (;;) {
						token = x.nextContent();
						if (token == null) {
							if (tagName != null) {
								throw x.syntaxError("Unclosed tag " + tagName);
							}
							return false;
						} else if (token instanceof String) {
							string = (String) token;
							addQueue(queue, "content");
							if (string.length() > 0) {
								jsonobject.accumulate(
										"content",
										stringToValue(tagName, string,
												queue.getLast()), false);
							}
							queue.removeLast();

							// Nested element

						} else if (token == LT) {
							if (parse(x, jsonobject, tagName, queue)) {
								if (jsonobject.length() == 0) {
									// Les blocs vides ne sont pas ajoutés au
									// flux JSON
									// context.accumulate(tagName, "");
								} else if (jsonobject.length() == 1
										&& jsonobject.opt("content") != null) {
									context.accumulate(tagName,
											jsonobject.opt("content"), isArray);
								} else {
									context.accumulate(tagName, jsonobject,
											isArray);
								}
								return false;
							}
						}
					}
				} else {
					throw x.syntaxError("Misshaped tag");
				}
			}
		}
	}

	private void addQueue(Deque<String> queue, String tagName) {
		String before = queue.peekLast();
		if (null != before) {
			queue.add(new StringBuilder(before).append(".").append(tagName)
					.toString());
		} else {
			queue.add(tagName);
		}
	}

	/**
	 * Try to convert a string into a number, boolean, or null. If the string
	 * can't be converted, return the string. This is much less ambitious than
	 * JSONObject.stringToValue, especially because it does not attempt to
	 * convert plus forms, octal forms, hex forms, or E forms lacking decimal
	 * points.
	 * 
	 * @param value
	 *            A String.
	 * @return A simple JSON value.
	 */
	private Object stringToValue(String field, String value,
			String completeField) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(field + ", " + value + ", " + completeField + ", "
					+ configurationMap.get(completeField));
		}
		try {
			if (null == value || "".equals(value) || null == completeField) {
				return value;
			}

			JsonConf jsonConf = configurationMap.get(completeField);
			if (null == jsonConf || null == jsonConf.getTypes()
					|| jsonConf.getTypes().equals(Types.UNKNOW)) {
				return value;
			}

			String valueLowerCase = value.toLowerCase();
			if ("null".equals(valueLowerCase)) {
				return JSONObject.NULL;
			}

			switch (jsonConf.getTypes()) {
			case BOOLEAN:
				if ("true".equals(valueLowerCase) || "1".equals(valueLowerCase)) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}
			case DATE:
				if (jsonConf.getFormat() != null) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							jsonConf.getFormat());
					return dateFormat.parse(value);
				} else {
					return value;
				}
			case NUMBER:
				return new BigDecimal(value);
			default:
				return value;
			}
		} catch (Exception e) {
			LOG.warn("Erreur lors du parsing de la valeur du champ " + field
					+ " : " + value + "(" + completeField + ")", e);
			return value;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public JSONObject toJSONObject(String string) throws JSONException {
		JSONObject jo = new JSONObject();
		Deque<String> queue = new ArrayDeque<String>();
		XMLTokener x = new XMLTokener(string);
		while (x.more() && x.skipPast("<")) {
			parse(x, jo, null, queue);
		}
		return jo;
	}

}

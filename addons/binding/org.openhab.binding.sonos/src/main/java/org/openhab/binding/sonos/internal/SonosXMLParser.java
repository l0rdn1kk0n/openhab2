/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sonos.internal;

import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * The {@link SonosXMLParser} is a class of helper functions
 * to parse XML data returned by the Zone Players
 * 
 * @author Karel Goderis - Initial contribution
 */
public class SonosXMLParser {

	static final Logger logger = LoggerFactory.getLogger(SonosXMLParser.class);

	private static  MessageFormat METADATA_FORMAT = new MessageFormat(
			"<DIDL-Lite xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
					"xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
					"xmlns:r=\"urn:schemas-rinconnetworks-com:metadata-1-0/\" " +
					"xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\">" +
					"<item id=\"{0}\" parentID=\"{1}\" restricted=\"true\">" +
					"<dc:title>{2}</dc:title>" +
					"<upnp:class>{3}</upnp:class>" +
					"<desc id=\"cdudn\" nameSpace=\"urn:schemas-rinconnetworks-com:metadata-1-0/\">" +
					"RINCON_AssociatedZPUDN</desc>" +
			"</item></DIDL-Lite>");

	private enum Element {
		TITLE, 
		CLASS,
		ALBUM, 
		ALBUM_ART_URI,
		CREATOR,
		RES,
		TRACK_NUMBER
	}

	private enum CurrentElement {
		item,
		res,
		streamContent,
		albumArtURI,
		title,
		upnpClass,
		creator,
		album,
		albumArtist;
	}

	/**
	 * @param xml
	 * @return a list of alarms from the given xml string.
	 * @throws IOException
	 * @throws SAXException
	 */
	public static List<SonosAlarm> getAlarmsFromStringResult(String xml) {
		AlarmHandler handler = new AlarmHandler();
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(new StringReader(xml)));
		} catch (IOException e) {
			logger.error("Could not parse Alarms from string '{}",xml);
		} catch (SAXException s) {
			logger.error("Could not parse Alarms from string '{}'",xml);
		}
		return handler.getAlarms();
	}


	/**
	 * @param xml
	 * @return a list of Entries from the given xml string.
	 * @throws IOException
	 * @throws SAXException
	 */
	public static List<SonosEntry> getEntriesFromString(String xml)  {
		EntryHandler handler = new EntryHandler();
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(new StringReader(xml)));
		} catch (IOException e) {
			logger.error("Could not parse Entries from string '{}'",xml);
		}
		catch (SAXException s) {
			logger.error("Could not parse Entries from string '{}'",xml);
		}
		
		return handler.getArtists();
	}

	/**
	 * @param controller
	 * @param xml
	 * @return zone group from the given xml
	 * @throws IOException
	 * @throws SAXException
	 */
	public static List<SonosZoneGroup> getZoneGroupFromXML(String xml) {
		ZoneGroupHandler handler = new ZoneGroupHandler();
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(new StringReader(xml)));
		} catch (IOException e) {
			// This should never happen - we're not performing I/O!
			logger.error("Could not parse ZoneGroup from string '{}'",xml);
		} catch (SAXException s) {
			logger.error("Could not parse ZoneGroup from string '{}'",xml);
		}

		return handler.getGroups();

	}

	public static List<String> getRadioTimeFromXML(String xml) {
		OpmlHandler handler = new OpmlHandler();
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(new StringReader(xml)));
		} catch (IOException e) {
			// This should never happen - we're not performing I/O!
			logger.error("Could not parse RadioTime from string '{}'",xml);
		} catch (SAXException s) {
			logger.error("Could not parse RadioTime from string '{}'",xml);
		}

		return handler.getTextFields();

	}

	public static Map<String, String> getRenderingControlFromXML(String xml) {
		RenderingControlEventHandler handler = new RenderingControlEventHandler();
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(new StringReader(xml)));
		} catch (IOException e) {
			// This should never happen - we're not performing I/O!
			logger.debug("Could not parse Rendering Control from string '{}'", xml);
		} catch (SAXException s) {
			logger.error("Could not parse Rendering Control from string '{}'",xml);
		}
		return handler.getChanges();
	}

	public static Map<String, String> getAVTransportFromXML(String xml) {
		AVTransportEventHandler handler = new AVTransportEventHandler();
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(new StringReader(xml)));
		} catch (IOException e) {
			// This should never happen - we're not performing I/O!
			logger.error("Could not parse AV Transport from string '{}'",xml);
		} catch (SAXException s) {
			logger.error("Could not parse AV Transport from string '{}'",xml);
		}
		return handler.getChanges();
	}

	public static SonosMetaData getMetaDataFromXML(String xml)  {
		MetaDataHandler handler = new MetaDataHandler();
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(new StringReader(xml)));
		} catch (IOException e) {
			// This should never happen - we're not performing I/O!
			logger.error("Could not parse MetaData from string '{}'",xml);
		} catch (SAXException s) {
			logger.error("Could not parse MetaData from string '{}'",xml);
		}

		return handler.getMetaData();  }


	static private class EntryHandler extends DefaultHandler {

		// Maintain a set of elements about which it is unuseful to complain about.
		// This list will be initialized on the first failure case
		private static List<String> ignore = null;

		private String id;
		private String parentId;
		private StringBuilder upnpClass = new StringBuilder();
		private StringBuilder res = new StringBuilder();
		private StringBuilder title = new StringBuilder();
		private StringBuilder album = new StringBuilder();
		private StringBuilder albumArtUri = new StringBuilder();
		private StringBuilder creator = new StringBuilder();
		private StringBuilder trackNumber = new StringBuilder();
		private Element element = null;

		private List<SonosEntry> artists = new ArrayList<SonosEntry>();

		EntryHandler() {
			// shouldn't be used outside of this package.
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equals("container") || qName.equals("item")) {
				id = attributes.getValue("id");
				parentId = attributes.getValue("parentID");
			} else if (qName.equals("res")) {
				element = Element.RES;
			} else if (qName.equals("dc:title")) {
				element = Element.TITLE;
			} else if (qName.equals("upnp:class")) {
				element = Element.CLASS;
			} else if (qName.equals("dc:creator")) {
				element = Element.CREATOR;
			} else if (qName.equals("upnp:album")) {
				element = Element.ALBUM;
			} else if (qName.equals("upnp:albumArtURI")) {
				element = Element.ALBUM_ART_URI;
			} else if (qName.equals("upnp:originalTrackNumber")) {
				element = Element.TRACK_NUMBER;
			} else {
				if (ignore == null) {
					ignore = new ArrayList<String>();
					ignore.add("DIDL-Lite");
				}

				if (!ignore.contains(localName)) {
					logger.warn("Did not recognise element named {}",localName);
				}
				element = null;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (element == null) {
				return;
			}
			switch (element) {
			case TITLE: 
				title.append(ch, start, length);
				break;
			case CLASS:
				upnpClass.append(ch, start, length);
				break;
			case RES:
				res.append(ch, start, length);
				break;
			case ALBUM:
				album.append(ch, start, length);
				break;
			case ALBUM_ART_URI:
				albumArtUri.append(ch, start, length);
				break;
			case CREATOR:
				creator.append(ch, start, length);
				break;
			case TRACK_NUMBER:
				trackNumber.append(ch, start, length);
				break;
				// no default
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("container") || qName.equals("item")) {
				element = null;

				int trackNumberVal = 0;
				try {
					trackNumberVal = Integer.parseInt(trackNumber.toString());
				} catch (Exception e) {
				}

				artists.add(new SonosEntry(id, title.toString(), parentId, album.toString(), 
						albumArtUri.toString(), creator.toString(), upnpClass.toString(), res.toString(), trackNumberVal));
				title= new StringBuilder();
				upnpClass = new StringBuilder();
				res = new StringBuilder();
				album = new StringBuilder();
				albumArtUri = new StringBuilder();
				creator = new StringBuilder();
				trackNumber = new StringBuilder();
			}
		}

		public List<SonosEntry> getArtists() {
			return artists;
		}
	}

	static private class AlarmHandler extends DefaultHandler {

		private String id;
		private String startTime ;
		private String duration;
		private String recurrence ;
		private String enabled ;
		private String roomUUID ;
		private String programURI;
		private String programMetaData;
		private String playMode ;
		private String volume;
		private String includeLinkedZones ;

		private List<SonosAlarm> alarms = new ArrayList<SonosAlarm>();

		AlarmHandler() {
			// shouldn't be used outside of this package.
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

			if (qName.equals("Alarm")) {
				id = attributes.getValue("ID");
				duration = attributes.getValue("Duration");
				recurrence = attributes.getValue("Recurrence");
				startTime = attributes.getValue("StartTime");
				enabled = attributes.getValue("Enabled");
				roomUUID = attributes.getValue("RoomUUID");
				programURI = attributes.getValue("ProgramURI");
				programMetaData = attributes.getValue("ProgramMetaData");
				playMode = attributes.getValue("PlayMode");
				volume = attributes.getValue("Volume");
				includeLinkedZones = attributes.getValue("IncludeLinkedZones");
			} 
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {

			if (qName.equals("Alarm")) {

				int finalID = 0;
				int finalVolume = 0;
				boolean finalEnabled = false;
				boolean finalIncludeLinkedZones = false;

				try {
					finalID = Integer.parseInt(id);
					finalVolume = Integer.parseInt(volume);
					if(enabled.equals("0")) {
						finalEnabled = false;
					} else {
						finalEnabled = true;
					}

					if(includeLinkedZones.equals("0")) {
						finalIncludeLinkedZones = false;
					} else {
						finalIncludeLinkedZones = true;
					}
				} catch (Exception e) {
					logger.debug("Error parsing Integer");
				}

				alarms.add(new SonosAlarm(finalID,  startTime,  duration,  recurrence,
						finalEnabled,  roomUUID,  programURI, 
						programMetaData,  playMode,  finalVolume, finalIncludeLinkedZones));
			}

		}

		public List<SonosAlarm> getAlarms() {
			return alarms;
		}
	}

	static private class ZoneGroupHandler extends DefaultHandler {

		private final List<SonosZoneGroup> groups = new ArrayList<SonosZoneGroup>();
		private final List<String> currentGroupPlayers = new ArrayList<String>();
		private String coordinator;
		private String groupId;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equals("ZoneGroup")) {
				groupId = attributes.getValue("ID");
				coordinator = attributes.getValue("Coordinator");
			} else if (qName.equals("ZoneGroupMember")) {
				currentGroupPlayers.add(attributes.getValue("UUID"));
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("ZoneGroup")) {
				groups.add(new SonosZoneGroup(groupId, coordinator, currentGroupPlayers));
				currentGroupPlayers.clear();
			}
		}

		public List<SonosZoneGroup> getGroups() {
			return groups;
		}
	}

	static private class OpmlHandler extends DefaultHandler {

		//		<opml version="1">
		//		<head>
		//		<status>200</status>
		//		
		//		</head>
		//		<body>
		//	<outline type="text" text="Q-Music 103.3" guide_id="s2398" key="station" image="http://radiotime-logos.s3.amazonaws.com/s87683q.png" preset_id="s2398"/>
		//	<outline type="text" text="Bjorn Verhoeven" guide_id="p257265" seconds_remaining="2230" duration="7200" key="show"/>
		//	<outline type="text" text="Top 40-Pop"/>
		//	<outline type="text" text="37m remaining"/>
		//	<outline type="object" text="NowPlaying">
		//	<nowplaying>
		//	  <logo>http://radiotime-logos.s3.amazonaws.com/s87683.png</logo>
		//	  <twitter_id />
		//	</nowplaying>
		//	</outline>
		//		</body>
		//	</opml>

		private final List<String> textFields = new ArrayList<String>();
		private String textField;
		private String type;
		private String logo;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equals("outline")) {
				type = attributes.getValue("type");
				if(type.equals("text")) {
					textField = attributes.getValue("text");
				} else {
					textField = null;
				}
			} else if (qName.equals("logo")) {
				//logo = attributes.getValue("UUID");
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("outline")) {
				if(textField != null) {
					textFields.add(textField);
				}
			}
		}

		public List<String> getTextFields() {
			return textFields;
		}

	}

	static private class AVTransportEventHandler extends DefaultHandler {

		/*
		  <Event xmlns="urn:schemas-upnp-org:metadata-1-0/AVT/" xmlns:r="urn:schemas-rinconnetworks-com:metadata-1-0/">
		  <InstanceID val="0">
		    <TransportState val="PLAYING"/>
		    <CurrentPlayMode val="NORMAL"/>
		    <CurrentPlayMode val="0"/>
		    <NumberOfTracks val="29"/>
		    <CurrentTrack val="12"/>
		    <CurrentSection val="0"/>
		    <CurrentTrackURI val="x-file-cifs://192.168.1.1/Storage4/Sonos%20Music/Queens%20Of%20The%20Stone%20Age/Lullabies%20To%20Paralyze/Queens%20Of%20The%20Stone%20Age%20-%20Lullabies%20To%20Paralyze%20-%2012%20-%20Broken%20Box.wma"/>
		    <CurrentTrackDuration val="0:03:02"/>
		    <CurrentTrackMetaData val="&lt;DIDL-Lite xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot; xmlns:upnp=&quot;urn:schemas-upnp-org:metadata-1-0/upnp/&quot; xmlns:r=&quot;urn:schemas-rinconnetworks-com:metadata-1-0/&quot; xmlns=&quot;urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/&quot;&gt;&lt;item id=&quot;-1&quot; parentID=&quot;-1&quot; restricted=&quot;true&quot;&gt;&lt;res protocolInfo=&quot;x-file-cifs:*:audio/x-ms-wma:*&quot; duration=&quot;0:03:02&quot;&gt;x-file-cifs://192.168.1.1/Storage4/Sonos%20Music/Queens%20Of%20The%20Stone%20Age/Lullabies%20To%20Paralyze/Queens%20Of%20The%20Stone%20Age%20-%20Lullabies%20To%20Paralyze%20-%2012%20-%20Broken%20Box.wma&lt;/res&gt;&lt;r:streamContent&gt;&lt;/r:streamContent&gt;&lt;dc:title&gt;Broken Box&lt;/dc:title&gt;&lt;upnp:class&gt;object.item.audioItem.musicTrack&lt;/upnp:class&gt;&lt;dc:creator&gt;Queens Of The Stone Age&lt;/dc:creator&gt;&lt;upnp:album&gt;Lullabies To Paralyze&lt;/upnp:album&gt;&lt;r:albumArtist&gt;Queens Of The Stone Age&lt;/r:albumArtist&gt;&lt;/item&gt;&lt;/DIDL-Lite&gt;"/><r:NextTrackURI val="x-file-cifs://192.168.1.1/Storage4/Sonos%20Music/Queens%20Of%20The%20Stone%20Age/Lullabies%20To%20Paralyze/Queens%20Of%20The%20Stone%20Age%20-%20Lullabies%20To%20Paralyze%20-%2013%20-%20&apos;&apos;You%20Got%20A%20Killer%20Scene%20There,%20Man...&apos;&apos;.wma"/><r:NextTrackMetaData val="&lt;DIDL-Lite xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot; xmlns:upnp=&quot;urn:schemas-upnp-org:metadata-1-0/upnp/&quot; xmlns:r=&quot;urn:schemas-rinconnetworks-com:metadata-1-0/&quot; xmlns=&quot;urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/&quot;&gt;&lt;item id=&quot;-1&quot; parentID=&quot;-1&quot; restricted=&quot;true&quot;&gt;&lt;res protocolInfo=&quot;x-file-cifs:*:audio/x-ms-wma:*&quot; duration=&quot;0:04:56&quot;&gt;x-file-cifs://192.168.1.1/Storage4/Sonos%20Music/Queens%20Of%20The%20Stone%20Age/Lullabies%20To%20Paralyze/Queens%20Of%20The%20Stone%20Age%20-%20Lullabies%20To%20Paralyze%20-%2013%20-%20&amp;apos;&amp;apos;You%20Got%20A%20Killer%20Scene%20There,%20Man...&amp;apos;&amp;apos;.wma&lt;/res&gt;&lt;dc:title&gt;&amp;apos;&amp;apos;You Got A Killer Scene There, Man...&amp;apos;&amp;apos;&lt;/dc:title&gt;&lt;upnp:class&gt;object.item.audioItem.musicTrack&lt;/upnp:class&gt;&lt;dc:creator&gt;Queens Of The Stone Age&lt;/dc:creator&gt;&lt;upnp:album&gt;Lullabies To Paralyze&lt;/upnp:album&gt;&lt;r:albumArtist&gt;Queens Of The Stone Age&lt;/r:albumArtist&gt;&lt;/item&gt;&lt;/DIDL-Lite&gt;"/><r:EnqueuedTransportURI val="x-rincon-playlist:RINCON_000E582126EE01400#A:ALBUMARTIST/Queens%20Of%20The%20Stone%20Age"/><r:EnqueuedTransportURIMetaData val="&lt;DIDL-Lite xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot; xmlns:upnp=&quot;urn:schemas-upnp-org:metadata-1-0/upnp/&quot; xmlns:r=&quot;urn:schemas-rinconnetworks-com:metadata-1-0/&quot; xmlns=&quot;urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/&quot;&gt;&lt;item id=&quot;A:ALBUMARTIST/Queens%20Of%20The%20Stone%20Age&quot; parentID=&quot;A:ALBUMARTIST&quot; restricted=&quot;true&quot;&gt;&lt;dc:title&gt;Queens Of The Stone Age&lt;/dc:title&gt;&lt;upnp:class&gt;object.container&lt;/upnp:class&gt;&lt;desc id=&quot;cdudn&quot; nameSpace=&quot;urn:schemas-rinconnetworks-com:metadata-1-0/&quot;&gt;RINCON_AssociatedZPUDN&lt;/desc&gt;&lt;/item&gt;&lt;/DIDL-Lite&gt;"/>
		    <PlaybackStorageMedium val="NETWORK"/>
		    <AVTransportURI val="x-rincon-queue:RINCON_000E5812BC1801400#0"/>
		    <AVTransportURIMetaData val=""/>
		    <CurrentTransportActions val="Play, Stop, Pause, Seek, Next, Previous"/>
		    <TransportStatus val="OK"/>
		    <r:SleepTimerGeneration val="0"/>
		    <r:AlarmRunning val="0"/>
		    <r:SnoozeRunning val="0"/>
		    <r:RestartPending val="0"/>
		    <TransportPlaySpeed val="NOT_IMPLEMENTED"/>
		    <CurrentMediaDuration val="NOT_IMPLEMENTED"/>
		    <RecordStorageMedium val="NOT_IMPLEMENTED"/>
		    <PossiblePlaybackStorageMedia val="NONE, NETWORK"/>
		    <PossibleRecordStorageMedia val="NOT_IMPLEMENTED"/>
		    <RecordMediumWriteStatus val="NOT_IMPLEMENTED"/>
		    <CurrentRecordQualityMode val="NOT_IMPLEMENTED"/>
		    <PossibleRecordQualityModes val="NOT_IMPLEMENTED"/>
		    <NextAVTransportURI val="NOT_IMPLEMENTED"/>
		    <NextAVTransportURIMetaData val="NOT_IMPLEMENTED"/>
		  </InstanceID>
		</Event>
		 */

		private final Map<String, String> changes = new HashMap<String, String>();

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			/* The events are all of the form <qName val="value"/> so we can get all
			 * the info we need from here.
			 */
			try {
				if(atts.getValue("val") != null) {
					changes.put(localName, atts.getValue("val"));
				}
			} catch (IllegalArgumentException e) {
				// this means that localName isn't defined in EventType, which is expected for some elements
				logger.info("{} is not defined in EventType. ",localName);
			}
		}

		public Map<String, String> getChanges() {
			return changes;
		}

	}


	static private class MetaDataHandler extends DefaultHandler {

		private CurrentElement currentElement = null;

		private String id = "-1";
		private String parentId = "-1";
		private StringBuilder resource = new StringBuilder();
		private StringBuilder streamContent = new StringBuilder();
		private StringBuilder albumArtUri = new StringBuilder();
		private StringBuilder title = new StringBuilder();
		private StringBuilder upnpClass = new StringBuilder();
		private StringBuilder creator = new StringBuilder();
		private StringBuilder album = new StringBuilder();
		private StringBuilder albumArtist = new StringBuilder();

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			if ("item".equals(localName)) {
				currentElement = CurrentElement.item;
				id = atts.getValue("id");
				parentId = atts.getValue("parentID");
			} else if ("res".equals(localName)) {
				currentElement = CurrentElement.res;
			} else if ("streamContent".equals(localName)) {
				currentElement = CurrentElement.streamContent;
			} else if ("albumArtURI".equals(localName)) {
				currentElement = CurrentElement.albumArtURI;
			} else if ("title".equals(localName)) {
				currentElement = CurrentElement.title;
			} else if ("class".equals(localName)) {
				currentElement = CurrentElement.upnpClass;
			} else if ("creator".equals(localName)) {
				currentElement = CurrentElement.creator;
			} else if ("album".equals(localName)) {
				currentElement = CurrentElement.album;
			} else if ("albumArtist".equals(localName)) {
				currentElement = CurrentElement.albumArtist;
			} else {
				// unknown element
				currentElement = null;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (currentElement != null) {
				switch (currentElement) {
				case item: 
					break; 
				case res: resource.append(ch, start, length);
				break;
				case streamContent: streamContent.append(ch, start, length);
				break;
				case albumArtURI: albumArtUri.append(ch, start, length);
				break;
				case title: title.append(ch, start, length);
				break;
				case upnpClass: upnpClass.append(ch, start, length);
				break;
				case creator: creator.append(ch, start, length);
				break;
				case album: album.append(ch, start, length);
				break;
				case albumArtist: albumArtist.append(ch, start, length);
				break;
				}
			}
		}

		public SonosMetaData getMetaData() {
			return new SonosMetaData(id, parentId, resource.toString(), 
					streamContent.toString(), albumArtUri.toString(), 
					title.toString(), upnpClass.toString(), creator.toString(),
					album.toString(), albumArtist.toString());
		}
	}

	static private class RenderingControlEventHandler extends DefaultHandler {

		private final Map<String, String> changes = new HashMap<String, String>();

		private boolean getPresetName=false;
		private String presetName;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

			if ("Volume".equals(qName)) {			
				changes.put(qName+atts.getValue("channel"), atts.getValue("val"));
			} else if ("Mute".equals(qName)) {
				changes.put(qName+atts.getValue("channel"), atts.getValue("val"));
			} else if ("Bass".equals(qName)) {
				changes.put(qName, atts.getValue("val"));
			} else if ("Treble".equals(qName)) {
				changes.put(qName, atts.getValue("val"));
			} else if ("Loudness".equals(qName)) {
				changes.put(qName+atts.getValue("channel"), atts.getValue("val"));
			} else if ("OutputFixed".equals(qName)) {
				changes.put(qName, atts.getValue("val"));
			} else if ("PresetNameList".equals(qName)) {
				getPresetName=true;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (getPresetName) {
				presetName = new String(ch, start, length);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (getPresetName) {
				getPresetName = false;
				changes.put(qName, presetName);
			}
		}

		public Map<String, String> getChanges() {
			return changes;
		}


	}

	public static  String compileMetadataString(SonosEntry entry) {
		String upnpClass = entry.getUpnpClass();

		String metadata = METADATA_FORMAT.format(new Object[] {entry.getId(), entry.getParentId(), entry.getTitle(), upnpClass});
		return metadata;
	}


}

/*******************************************************************************
 * BalisesLib is Copyright 2012 by Pedro M.
 * 
 * This file is part of BalisesLib.
 *
 * BalisesLib is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * BalisesLib is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * Commercial Distribution License
 * If you would like to distribute BalisesLib (or portions thereof) under a
 * license other than the "GNU Lesser General Public License, version 3", please
 * contact Pedro M (pedro.pub@free.fr).
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with BalisesLib. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.pedro.balises.romma;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.pedro.balises.Releve;
import org.pedro.balises.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * 
 * @author pedro.m
 */
public final class ReleveRommaContentHandler implements ContentHandler
{
  // Directions
  private static final String       DIR_N              = "N";
  private static final String       DIR_NNE            = "NNE";
  private static final String       DIR_NE             = "NE";
  private static final String       DIR_ENE            = "ENE";
  private static final String       DIR_E              = "E";
  private static final String       DIR_ESE            = "ESE";
  private static final String       DIR_SE             = "SE";
  private static final String       DIR_SSE            = "SSE";
  private static final String       DIR_S              = "S";
  private static final String       DIR_SSO            = "SSO";
  private static final String       DIR_SO             = "SO";
  private static final String       DIR_OSO            = "OSO";
  private static final String       DIR_O              = "O";
  private static final String       DIR_ONO            = "ONO";
  private static final String       DIR_NO             = "NO";
  private static final String       DIR_NNO            = "NNO";

  // Constantes
  private static final String       STRING_MOINS_MOINS = "--";
  private static final String       STRING_VIDE        = "";
  private static final String       RELEVE_TAG         = "releve";
  private static final String       STATION_ID_TAG     = "stationID";
  private static final String       DATE_TAG           = "date";
  private static final String       VITESSE_MOY_TAG    = "vitesseVentMoy10min";
  private static final String       DIRECTION_TAG      = "directionVentInst";
  private static final String       TEMPERATURE_TAG    = "temperature";
  private static final DateFormat   RELEVE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

  // Membres
  private final Map<String, Releve> releves            = new HashMap<String, Releve>();
  private Releve                    releve;
  private String                    currentString      = STRING_VIDE;

  /**
   * Recuperation de la liste des balises
   * 
   * @return
   */
  public Map<String, Releve> getReleves()
  {
    return releves;
  }

  @Override
  public void characters(final char[] ch, final int start, final int length) throws SAXException
  {
    currentString += new String(ch, start, length);
  }

  @Override
  public void endElement(final String uri, final String localName, final String qName) throws SAXException
  {
    String finalName = qName;
    if ((finalName == null) || (finalName.length() == 0))
    {
      finalName = localName;
    }

    try
    {
      if (STATION_ID_TAG.equals(finalName))
      {
        releve.setId(currentString);
        releves.put(currentString, releve);
      }

      else if (DATE_TAG.equals(finalName))
      {
        releve.date = RELEVE_DATE_FORMAT.parse(currentString);

        // Decalage dans le fuseau horaire UTC
        Utils.toUTC(releve.date, XmlRommaProvider.sourceTimeZone);
      }

      else if (VITESSE_MOY_TAG.equals(finalName))
      {
        releve.ventMoyen = (STRING_MOINS_MOINS.equals(currentString) ? null : Utils.parseDouble(currentString));
      }

      else if (DIRECTION_TAG.equals(finalName))
      {
        final int direction = parseDirection(currentString);
        releve.directionMoyenne = (direction < 0 ? null : Integer.valueOf(direction));
      }

      else if (TEMPERATURE_TAG.equals(finalName))
      {
        releve.temperature = Utils.parseDouble(currentString);
      }
    }
    catch (final ParseException pe)
    {
      pe.printStackTrace(System.err);
    }
    catch (final NumberFormatException nfe)
    {
      nfe.printStackTrace(System.err);
    }

    // RAZ
    currentString = STRING_VIDE;
  }

  /**
   * 
   * @param direction
   * @return
   */
  private static int parseDirection(final String direction)
  {
    if (DIR_N.equalsIgnoreCase(direction))
    {
      return 0;
    }
    else if (DIR_NNE.equalsIgnoreCase(direction))
    {
      return 22;
    }
    else if (DIR_NE.equalsIgnoreCase(direction))
    {
      return 45;
    }
    else if (DIR_ENE.equalsIgnoreCase(direction))
    {
      return 67;
    }
    else if (DIR_E.equalsIgnoreCase(direction))
    {
      return 90;
    }
    else if (DIR_ESE.equalsIgnoreCase(direction))
    {
      return 112;
    }
    else if (DIR_SE.equalsIgnoreCase(direction))
    {
      return 135;
    }
    else if (DIR_SSE.equalsIgnoreCase(direction))
    {
      return 157;
    }
    else if (DIR_S.equalsIgnoreCase(direction))
    {
      return 180;
    }
    else if (DIR_SSO.equalsIgnoreCase(direction))
    {
      return 202;
    }
    else if (DIR_SO.equalsIgnoreCase(direction))
    {
      return 225;
    }
    else if (DIR_OSO.equalsIgnoreCase(direction))
    {
      return 247;
    }
    else if (DIR_O.equalsIgnoreCase(direction))
    {
      return 270;
    }
    else if (DIR_ONO.equalsIgnoreCase(direction))
    {
      return 292;
    }
    else if (DIR_NO.equalsIgnoreCase(direction))
    {
      return 315;
    }
    else if (DIR_NNO.equalsIgnoreCase(direction))
    {
      return 337;
    }

    return -1;
  }

  @Override
  public void startDocument() throws SAXException
  {
    releves.clear();
    currentString = STRING_VIDE;
  }

  @Override
  public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException
  {
    String finalName = qName;
    if ((finalName == null) || (finalName.length() == 0))
    {
      finalName = localName;
    }

    if (RELEVE_TAG.equals(finalName))
    {
      releve = new Releve();
    }

    // RAZ
    currentString = STRING_VIDE;
  }

  @Override
  public void startPrefixMapping(final String arg0, final String arg1) throws SAXException
  {
    // Nothing to do
  }

  @Override
  public void endDocument() throws SAXException
  {
    // Nothing to do
  }

  @Override
  public void endPrefixMapping(final String arg0) throws SAXException
  {
    // Nothing to do
  }

  @Override
  public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException
  {
    // Nothing to do
  }

  @Override
  public void processingInstruction(final String arg0, final String arg1) throws SAXException
  {
    // Nothing to do
  }

  @Override
  public void setDocumentLocator(final Locator arg0)
  {
    // Nothing to do
  }

  @Override
  public void skippedEntity(final String arg0) throws SAXException
  {
    // Nothing to do
  }
}

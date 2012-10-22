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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.pedro.balises.AbstractBaliseProvider;
import org.pedro.balises.Balise;
import org.pedro.balises.Releve;
import org.pedro.balises.Utils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * 
 * @author pedro.m
 */
public final class XmlRommaProvider extends AbstractBaliseProvider
{
  private static final String             URL_ROMMA_KEY           = "rommaKey";
  private static final String             URL_ROMMA_KEY_GROUP     = "\\{" + URL_ROMMA_KEY + "\\}";

  //private static final String                URL_BALISES             = "file:C:/Temp/balise_list.xml";
  private static final String             URL_BALISES             = "http://www.romma.fr/stations_romma_xml.php?id={" + URL_ROMMA_KEY + "}";

  //private static final String                URL_RELEVES             = "file:C:/Temp/relevemeteo-20110721.xml";
  private static final String             URL_RELEVES             = "http://www.romma.fr/releves_romma_xml.php?id={" + URL_ROMMA_KEY + "}";

  private static final String             SUFFIXE_COMPRESSION     = ".gz";

  private static final String             URL_BALISE_ID_KEY       = "idBalise";
  private static final String             URL_BALISE_ID_KEY_GROUP = "\\{" + URL_BALISE_ID_KEY + "\\}";
  private static final String             URL_DETAIL_BALISE       = "http://www.romma.fr/station_24.php?id={" + URL_BALISE_ID_KEY + "}";
  private static final String             URL_HISTORIQUE_BALISE   = "http://www.romma.fr/station_24_graphe.php?id={" + URL_BALISE_ID_KEY + "}";

  private static final List<String>       COUNTRIES               = new ArrayList<String>();

  // Fuseaux hoaires
  private static final String             TIME_ZONE_ID            = "Europe/Paris";
  protected static final TimeZone         sourceTimeZone          = TimeZone.getTimeZone(TIME_ZONE_ID);

  private final SAXParserFactory          factory;
  private final SAXParser                 parser;
  private final BaliseRommaContentHandler baliseHandler;
  private final ReleveRommaContentHandler releveHandler;

  private final String                    rommaKey;
  private final boolean                   useZippedData;

  /**
   * 
   */
  static
  {
    COUNTRIES.add(Locale.FRANCE.getCountry());
  }

  /**
   * 
   * @param name
   * @param country
   * @param rommaKey
   * @param useZippedData
   */
  public XmlRommaProvider(final String name, final String country, final String rommaKey, final boolean useZippedData)
  {
    // Initialisation
    super(name, country, null, 150);
    this.rommaKey = rommaKey;
    this.useZippedData = useZippedData;

    // Initialisation SAX
    try
    {
      factory = SAXParserFactory.newInstance();
      parser = factory.newSAXParser();
      baliseHandler = new BaliseRommaContentHandler();
      releveHandler = new ReleveRommaContentHandler();
    }
    catch (final SAXException se)
    {
      throw new RuntimeException(se);
    }
    catch (final ParserConfigurationException pce)
    {
      throw new RuntimeException(pce);
    }
  }

  @Override
  public boolean isAvailable()
  {
    boolean retour = false;

    try
    {
      final String finalUrl = URL_BALISES.replaceAll(URL_ROMMA_KEY_GROUP, rommaKey);
      final URL url = new URL(finalUrl);
      final URLConnection cnx = url.openConnection();
      if (cnx != null)
      {
        cnx.setConnectTimeout(Utils.CONNECT_TIMEOUT);
        cnx.setReadTimeout(Utils.READ_TIMEOUT);
        cnx.connect();
        retour = true;
      }
    }
    catch (final IOException ioe)
    {
      retour = false;
    }

    return retour;
  }

  @Override
  public boolean updateBalisesUpdateDate() throws IOException
  {
    return true;
  }

  /**
   * 
   * @param url
   * @return
   * @throws IOException
   */
  private InputStream getUnzippedInputStream(final String url) throws IOException
  {
    // Initialisations
    InputStream retour = null;

    // Donnees compressees ou pas ?
    if (useZippedData)
    {
      final URLConnection cnx = new URL(url + SUFFIXE_COMPRESSION).openConnection();
      cnx.setConnectTimeout(Utils.CONNECT_TIMEOUT);
      cnx.setReadTimeout(Utils.READ_TIMEOUT);
      retour = new GZIPInputStream(cnx.getInputStream());
    }
    else
    {
      final URLConnection cnx = new URL(url).openConnection();
      cnx.setConnectTimeout(Utils.CONNECT_TIMEOUT);
      cnx.setReadTimeout(Utils.READ_TIMEOUT);
      retour = cnx.getInputStream();
    }

    return retour;
  }

  @Override
  public long getBalisesUpdateDate() throws IOException
  {
    return Utils.toUTC(System.currentTimeMillis());
  }

  @Override
  public boolean updateBalises() throws IOException
  {
    // Initialisations
    boolean updated = false;
    InputStream input = null;

    try
    {
      final String finalUrl = URL_BALISES.replaceAll(URL_ROMMA_KEY_GROUP, rommaKey);
      input = getUnzippedInputStream(finalUrl);
      final Map<String, Balise> newBalises = parseBalisesMap(new InputSource(input));

      // Tout est OK
      setBalisesMap(newBalises);
      updated = true;
    }
    finally
    {
      if (input != null)
      {
        input.close();
      }
    }

    return updated;
  }

  /**
   * 
   * @param source
   * @return
   * @throws IOException
   */
  private Map<String, Balise> parseBalisesMap(final InputSource source) throws IOException
  {
    try
    {
      // Analyse XML
      final XMLReader reader = parser.getXMLReader();
      reader.setContentHandler(baliseHandler);
      reader.parse(source);

      // Stockage
      return baliseHandler.getBalises();
    }
    catch (final SAXException se)
    {
      throw new IOException(se.getMessage());
    }
  }

  @Override
  public String getBaliseDetailUrl(final String id)
  {
    return URL_DETAIL_BALISE.replaceAll(URL_BALISE_ID_KEY_GROUP, id);
  }

  @Override
  public String getBaliseHistoriqueUrl(final String id)
  {
    return URL_HISTORIQUE_BALISE.replaceAll(URL_BALISE_ID_KEY_GROUP, id);
  }

  @Override
  public boolean updateRelevesUpdateDate() throws IOException
  {
    return true;
  }

  @Override
  public long getRelevesUpdateDate() throws IOException
  {
    return Utils.toUTC(System.currentTimeMillis());
  }

  @Override
  public boolean updateReleves() throws IOException
  {
    // Initialisations
    boolean updated = false;
    InputStream input = null;

    try
    {
      final String finalUrl = URL_RELEVES.replaceAll(URL_ROMMA_KEY_GROUP, rommaKey);
      input = getUnzippedInputStream(finalUrl);
      final Map<String, Releve> newReleves = parseRelevesMap(new InputSource(input));

      // Tout est OK
      refreshReleves(newReleves);
      updated = true;
    }
    finally
    {
      if (input != null)
      {
        input.close();
      }
    }

    return updated;
  }

  /**
   * 
   * @param source
   * @return
   * @throws IOException
   */
  private Map<String, Releve> parseRelevesMap(final InputSource source) throws IOException
  {
    try
    {
      // Analyse XML
      final XMLReader reader = parser.getXMLReader();
      reader.setContentHandler(releveHandler);
      reader.parse(source);

      // Stockage
      return releveHandler.getReleves();
    }
    catch (final SAXException se)
    {
      throw new IOException(se.getMessage());
    }
  }

  @Override
  public Balise newBalise()
  {
    return new Balise();
  }

  @Override
  public Releve newReleve()
  {
    return new Releve();
  }

  @Override
  public String filterExceptionMessage(final String message)
  {
    // Null
    if (message == null)
    {
      return null;
    }

    return message.replaceAll(rommaKey, "xxx");
  }

  /**
   * 
   * @return
   */
  public static List<String> getAvailableCountries()
  {
    return COUNTRIES;
  }

  @Override
  public boolean isMultiCountries()
  {
    return false;
  }
}

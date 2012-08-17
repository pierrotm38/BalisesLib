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
package org.pedro.balises;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 * @author pedro.m
 */
public abstract class Utils
{
  private static final String   STRING_DEUX_POINTS = ":";

  private static final TimeZone localTimeZone      = TimeZone.getDefault();

  public static final int       READ_BUFFER_SIZE   = 1024;
  public static final int       CONNECT_TIMEOUT    = 10000;
  public static final int       READ_TIMEOUT       = 10000;

  /**
   * 
   * @param hm
   * @return
   */
  public static long hmToMillis(final String hm)
  {
    // Split
    final String[] array = hm.split(STRING_DEUX_POINTS);

    // Verification
    if ((array == null) || (array.length != 2))
    {
      return -1;
    }

    try
    {
      final int hours = Integer.parseInt(array[0], 10);
      final int minutes = Integer.parseInt(array[1], 10);

      return (hours * 3600 + minutes * 60) * 1000;
    }
    catch (final NumberFormatException nfe)
    {
      return -1;
    }
  }

  /**
   * 
   * @param date
   * @param sourceTimeZone
   */
  public static void toUTC(final Date date, final TimeZone sourceTimeZone)
  {
    // Decalage dans le fuseau horaire UTC
    final Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    final int sourceOffset = sourceTimeZone.getOffset(cal.getTimeInMillis());
    cal.add(Calendar.MILLISECOND, -sourceOffset);

    // Fin
    date.setTime(cal.getTime().getTime());
  }

  /**
   * 
   * @param date
   */
  public static void toUTC(final Date date)
  {
    toUTC(date, localTimeZone);
  }

  /**
   * 
   * @param date
   * @param sourceTimeZone
   */
  public static long toUTC(final long millis, final TimeZone sourceTimeZone)
  {
    // Decalage dans le fuseau horaire UTC
    final int sourceOffset = sourceTimeZone.getOffset(millis);

    return millis - sourceOffset;
  }

  /**
   * 
   * @param millis
   * @return
   */
  public static long toUTC(final long millis)
  {
    return toUTC(millis, localTimeZone);
  }

  /**
   * 
   * @param date
   * @param destTimeZone
   */
  public static void fromUTC(final Date date, final TimeZone destTimeZone)
  {
    // Decalage dans le fuseau horaire UTC
    final Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    final int sourceOffset = destTimeZone.getOffset(cal.getTimeInMillis());
    cal.add(Calendar.MILLISECOND, sourceOffset);

    // Fin
    date.setTime(cal.getTime().getTime());
  }

  /**
   * 
   * @param date
   */
  public static void fromUTC(final Date date)
  {
    fromUTC(date, localTimeZone);
  }

  /**
   * 
   * @param date
   * @param sourceTimeZone
   */
  public static long fromUTC(final long millis, final TimeZone sourceTimeZone)
  {
    // Decalage dans le fuseau horaire UTC
    final int sourceOffset = sourceTimeZone.getOffset(millis);

    return millis + sourceOffset;
  }

  /**
   * 
   * @param millis
   * @return
   */
  public static long fromUTC(final long millis)
  {
    return fromUTC(millis, localTimeZone);
  }

  /**
   * 
   * @param date
   * @param sourceTimeZone
   * @param destTimeZone
   * @return
   */
  public static void decalageHoraire(final Date date, final TimeZone sourceTimeZone, final TimeZone destTimeZone)
  {
    // Decalage dans le fuseau horaire UTC
    final Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    final int sourceOffset = sourceTimeZone.getOffset(cal.getTimeInMillis());
    cal.add(Calendar.MILLISECOND, -sourceOffset);

    // Decalage dans le fuseau horaire du terminal
    final int localOffset = destTimeZone.getOffset(cal.getTimeInMillis());
    cal.add(Calendar.MILLISECOND, localOffset);

    // Fin
    date.setTime(cal.getTime().getTime());
  }

  /**
   * 
   * @param text
   * @return
   */
  public static Integer parseInteger(final String text)
  {
    if ((text == null) || (text.trim().length() == 0))
    {
      return null;
    }

    return new Integer(Integer.parseInt(text, 10));
  }

  /**
   * 
   * @param text
   * @return
   */
  public static Double parseDouble(final String text)
  {
    if ((text == null) || (text.trim().length() == 0))
    {
      return null;
    }

    return new Double(Double.parseDouble(text));
  }

  /**
   * 
   * @param degres
   * @param minutes
   * @param secondes
   * @return
   */
  public static double degresMinutesSecondesToFraction(final double degres, final double minutes, final double secondes)
  {
    return degres + (minutes / 60) + (secondes / 3600);
  }

  /**
   * 
   * @param degres
   * @param minutes
   * @param secondes
   * @return
   */
  public static double degresMinutesToFraction(final double degres, final double minutes)
  {
    return degres + (minutes / 60);
  }

  /**
   * 
   * @param chaine
   * @return
   */
  public static boolean isStringVide(final String chaine)
  {
    if (chaine == null)
    {
      return true;
    }

    if (chaine.length() == 0)
    {
      return true;
    }

    if (chaine.trim().length() == 0)
    {
      return true;
    }

    return false;
  }

  /**
   * 
   * @param url
   * @param encoding
   * @param buffer
   * @return
   * @throws IOException
   */
  public static int readData(final URL url, final String encoding, final StringBuilder buffer) throws IOException
  {
    // Initialisations
    URLConnection cnx = null;
    InputStream is = null;
    int total = 0;

    try
    {
      // Connexion
      cnx = url.openConnection();
      cnx.setConnectTimeout(CONNECT_TIMEOUT);
      cnx.setReadTimeout(READ_TIMEOUT);
      cnx.connect();

      // InputStream
      is = new BufferedInputStream(cnx.getInputStream());

      // Lecture
      final byte[] buf = new byte[READ_BUFFER_SIZE];
      int read = is.read(buf);
      while (read > 0)
      {
        final String chaine = (encoding == null ? new String(buf, 0, read) : new String(buf, 0, read, encoding));
        buffer.append(chaine);
        total += read;
        read = is.read(buf);
      }

      return total;
    }
    finally
    {
      if (is != null)
      {
        is.close();
      }
    }
  }

  /**
   * 
   * @param chaine
   * @param length
   * @param c
   * @return
   */
  public static String formatLeft(final String chaine, final int length, final char c)
  {
    if (chaine == null)
    {
      return null;
    }

    if (chaine.length() == length)
    {
      return chaine;
    }

    if (chaine.length() > length)
    {
      chaine.substring(0, length);
    }

    final StringBuilder buffer = new StringBuilder(length << 1);
    buffer.append(chaine);
    for (int i = 0; i < length - chaine.length(); i++)
    {
      buffer.append(c);
    }

    return buffer.toString();
  }
}

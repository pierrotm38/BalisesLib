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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.pedro.saveable.Saveable;

/**
 * 
 * @author pedro.m
 */
public abstract class BaliseSaveableCache extends BaliseSerializableCache
{
  @Override
  @SuppressWarnings("unchecked")
  public Map<String, Balise> restoreBalises(final String key, final CachedProvider provider) throws IOException
  {
    try
    {
      // Nouvelle version
      return loadBalises(new GZIPInputStream(getCacheInputStream(key)), provider);
    }
    catch (final IOException ioe)
    {
      // Plantage, essai avec l'ancienne version
      final Map<String, Balise> balises = (Map<String, Balise>)deserializeObject(getCacheInputStream(key));
      saveBalises(getCacheOutputStream(key), balises);
      return balises;
    }
  }

  @Override
  public void storeBalises(final String key, final Map<String, Balise> balises) throws IOException
  {
    saveBalises(new GZIPOutputStream(getCacheOutputStream(key)), balises);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, Releve> restoreReleves(final String key, final CachedProvider provider) throws IOException
  {
    try
    {
      // Nouvelle version
      return loadReleves(new GZIPInputStream(getCacheInputStream(key)), provider);
    }
    catch (final IOException ioe)
    {
      // Plantage, essai avec l'ancienne version
      final Map<String, Releve> releves = (Map<String, Releve>)deserializeObject(getCacheInputStream(key));
      saveReleves(getCacheOutputStream(key), releves);
      return releves;
    }
  }

  @Override
  public void storeReleves(final String key, final Map<String, Releve> releves) throws IOException
  {
    saveReleves(new GZIPOutputStream(getCacheOutputStream(key)), releves);
  }

  /**
   * 
   * @param os
   * @param balises
   * @throws IOException
   */
  private static void saveBalises(final OutputStream os, final Map<String, Balise> balises) throws IOException
  {
    DataOutputStream out = null;

    try
    {
      out = new DataOutputStream(os);
      for (final Saveable saveable : balises.values())
      {
        saveable.saveSaveable(out);
      }
    }
    catch (final IOException ioe)
    {
      throw ioe;
    }
    catch (final Throwable th)
    {
      throw new IOException(th.getMessage());
    }
    finally
    {
      if (out != null)
      {
        out.close();
      }
    }
  }

  /**
   * 
   * @param is
   * @param provider
   * @return
   * @throws IOException
   */
  private static Map<String, Balise> loadBalises(final InputStream is, final CachedProvider provider) throws IOException
  {
    final Map<String, Balise> retour = new HashMap<String, Balise>();
    DataInputStream in = null;

    try
    {
      in = new DataInputStream(is);
      while (in.available() > 0)
      {
        final Balise balise = provider.newBalise();
        balise.loadSaveable(in);
        retour.put(balise.id, balise);
      }

      return retour;
    }
    catch (final IOException ioe)
    {
      throw ioe;
    }
    catch (final Throwable th)
    {
      throw new IOException(th.getMessage());
    }
    finally
    {
      if (in != null)
      {
        in.close();
      }
    }
  }

  /**
   * 
   * @param os
   * @param releves
   * @throws IOException
   */
  private static void saveReleves(final OutputStream os, final Map<String, Releve> releves) throws IOException
  {
    DataOutputStream out = null;

    try
    {
      out = new DataOutputStream(os);
      for (final Saveable saveable : releves.values())
      {
        saveable.saveSaveable(out);
      }
    }
    catch (final IOException ioe)
    {
      throw ioe;
    }
    catch (final Throwable th)
    {
      throw new IOException(th.getMessage());
    }
    finally
    {
      if (out != null)
      {
        out.close();
      }
    }
  }

  /**
   * 
   * @param is
   * @param provider
   * @return
   * @throws IOException
   */
  private static Map<String, Releve> loadReleves(final InputStream is, final CachedProvider provider) throws IOException
  {
    final Map<String, Releve> retour = new HashMap<String, Releve>();
    DataInputStream in = null;

    try
    {
      in = new DataInputStream(is);
      while (in.available() > 0)
      {
        final Releve releve = provider.newReleve();
        releve.loadSaveable(in);
        retour.put(releve.id, releve);
      }

      return retour;
    }
    catch (final IOException ioe)
    {
      throw ioe;
    }
    catch (final Throwable th)
    {
      throw new IOException(th.getMessage());
    }
    finally
    {
      if (in != null)
      {
        in.close();
      }
    }
  }
}

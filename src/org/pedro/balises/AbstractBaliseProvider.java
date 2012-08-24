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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author pedro.m
 */
public abstract class AbstractBaliseProvider implements BaliseProvider
{
  private final String              name;
  protected final String            country;
  private Map<String, Balise>       balises;
  private Map<String, Releve>       releves;
  private final Map<String, Releve> updatedReleves = new HashMap<String, Releve>();

  /**
   * 
   * @param name
   * @param country
   * @param balisesSize
   */
  public AbstractBaliseProvider(final String name, final String country, final int balisesSize)
  {
    this.name = name;
    this.country = country.toUpperCase();
    balises = new HashMap<String, Balise>(balisesSize);
    releves = new HashMap<String, Releve>(balisesSize);
  }

  @Override
  public final Collection<Balise> getBalises()
  {
    return balises.values();
  }

  @Override
  public final Balise getBaliseById(final String id)
  {
    return balises.get(id);
  }

  /**
   * @return the balises
   */
  protected final Map<String, Balise> getBalisesMap()
  {
    return balises;
  }

  /**
   * @param balises the balises to set
   */
  protected final void setBalisesMap(final Map<String, Balise> balises)
  {
    this.balises = balises;
  }

  @Override
  public final Collection<Releve> getReleves()
  {
    return releves.values();
  }

  @Override
  public final Releve getReleveById(final String id)
  {
    return releves.get(id);
  }

  /**
   * @return the releves
   */
  protected final Map<String, Releve> getRelevesMap()
  {
    return releves;
  }

  /**
   * @param releves the releves to set
   */
  protected final void setRelevesMap(final Map<String, Releve> releves)
  {
    this.releves = releves;
  }

  /**
   * 
   * @param newReleves
   */
  protected final void refreshReleves(final Map<String, Releve> newReleves)
  {
    // Detection des releves mis a jour
    findUpdatedReleves(newReleves);

    // On conserve les anciens (seuls ceux mis a jour sont ecrases)
    releves.putAll(newReleves);
  }

  /**
   * 
   * @param newReleves
   */
  private void findUpdatedReleves(final Map<String, Releve> newReleves)
  {
    // Initialisation
    updatedReleves.clear();

    // Seulement si les anciens releves sont presents
    if (releves != null)
    {
      for (final Entry<String, Releve> entry : newReleves.entrySet())
      {
        // Recherche dans les anciens
        final Releve ancien = releves.get(entry.getKey());
        final boolean updated;

        // Nouveau releve
        if (ancien == null)
        {
          updated = true;
        }
        // Ancien releve, comparaison de la date
        else
        {
          final long newStamp = (entry.getValue().date == null ? -1 : entry.getValue().date.getTime());
          final long oldStamp = (ancien.date == null ? -1 : ancien.date.getTime());
          updated = (newStamp > oldStamp);
        }

        // Sauvegarde
        if (updated)
        {
          updatedReleves.put(entry.getKey(), entry.getValue());
        }
      }
    }
  }

  @Override
  public String filterExceptionMessage(final String message)
  {
    return message;
  }

  @Override
  public String getCountry()
  {
    return country;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public Collection<Releve> getUpdatedReleves()
  {
    return updatedReleves.values();
  }
}

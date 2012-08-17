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
import java.io.Serializable;
import java.util.Date;

import org.pedro.saveable.Saveable;
import org.pedro.saveable.SaveableUtils;

/**
 * 
 * @author pedro.m
 */
public class Releve implements Serializable, Saveable
{
  private static final long serialVersionUID = 2997319509019353515L;

  public static final int   PLUIE_AUCUNE     = 0;
  public static final int   PLUIE_FAIBLE     = 1;
  public static final int   PLUIE_MOYENNE    = 2;
  public static final int   PLUIE_FORTE      = 3;
  public static final int   PLUIE_TRES_FORTE = 4;

  public String             id;
  public Date               date;
  public Double             ventMoyen;
  public Double             ventMini;
  public Double             ventMaxi;
  public Date               dateHeureVentMaxi;
  public Integer            directionMoyenne;
  public Integer            directionInstantanee;
  public Integer            directionVentVariation1;
  public Integer            directionVentVariation2;
  public Double             temperature;
  public Double             pointRosee;
  public Integer            pluie;                                  // 0:aucune, 1:pluie faible, 2:pluie, 3:pluie forte, 4:deluge/orage
  public Double             hydrometrie;
  public Integer            nuages;                                 // En 8emes
  public Integer            plafondNuages;
  public Boolean            nuagesBourgeonnants;
  public Double             pression;
  public String             luminosite;
  public Integer            humidite;

  private int               hashCode;

  @Override
  public long getSerialUID()
  {
    return serialVersionUID;
  }

  @Override
  public void loadSaveable(final DataInputStream in) throws IOException
  {
    SaveableUtils.checkSerialUID(in, this);

    id = SaveableUtils.readString(in);
    calculateHashCode();

    date = SaveableUtils.readDate(in);
    ventMoyen = SaveableUtils.readDouble(in);
    ventMini = SaveableUtils.readDouble(in);
    ventMaxi = SaveableUtils.readDouble(in);
    dateHeureVentMaxi = SaveableUtils.readDate(in);
    directionMoyenne = SaveableUtils.readInteger(in);
    directionInstantanee = SaveableUtils.readInteger(in);
    directionVentVariation1 = SaveableUtils.readInteger(in);
    directionVentVariation2 = SaveableUtils.readInteger(in);
    temperature = SaveableUtils.readDouble(in);
    pointRosee = SaveableUtils.readDouble(in);
    pluie = SaveableUtils.readInteger(in);
    nuages = SaveableUtils.readInteger(in);
    plafondNuages = SaveableUtils.readInteger(in);
    nuagesBourgeonnants = SaveableUtils.readBoolean(in);
    hydrometrie = SaveableUtils.readDouble(in);
    pression = SaveableUtils.readDouble(in);
    luminosite = SaveableUtils.readString(in);
    humidite = SaveableUtils.readInteger(in);
  }

  @Override
  public void saveSaveable(final DataOutputStream out) throws IOException
  {
    SaveableUtils.saveSerialUID(out, this);
    SaveableUtils.writeString(out, id);
    SaveableUtils.writeDate(out, date);
    SaveableUtils.writeDouble(out, ventMoyen);
    SaveableUtils.writeDouble(out, ventMini);
    SaveableUtils.writeDouble(out, ventMaxi);
    SaveableUtils.writeDate(out, dateHeureVentMaxi);
    SaveableUtils.writeInteger(out, directionMoyenne);
    SaveableUtils.writeInteger(out, directionInstantanee);
    SaveableUtils.writeInteger(out, directionVentVariation1);
    SaveableUtils.writeInteger(out, directionVentVariation2);
    SaveableUtils.writeDouble(out, temperature);
    SaveableUtils.writeDouble(out, pointRosee);
    SaveableUtils.writeInteger(out, pluie);
    SaveableUtils.writeInteger(out, nuages);
    SaveableUtils.writeInteger(out, plafondNuages);
    SaveableUtils.writeBoolean(out, nuagesBourgeonnants);
    SaveableUtils.writeDouble(out, hydrometrie);
    SaveableUtils.writeDouble(out, pression);
    SaveableUtils.writeString(out, luminosite);
    SaveableUtils.writeInteger(out, humidite);
  }

  @Override
  public String toString()
  {
    return "id=" + id + ", date=" + date + ", moy=" + ventMoyen + ", maxi=" + ventMaxi + ", dateHeureVentMaxi=" + dateHeureVentMaxi + ", mini=" + ventMini + ", dirMoy=" + directionMoyenne + ", dirInst=" + directionInstantanee
        + ", dirVar1=" + directionVentVariation1 + ", dirVar2=" + directionVentVariation2 + ", temp=" + temperature + ", rosee=" + pointRosee + ", pluie=" + pluie + ", nuages=" + nuages + ", plafondNuages=" + plafondNuages + ", cum/cb="
        + nuagesBourgeonnants + ", hydro=" + hydrometrie + ", pression=" + pression;
  }

  /**
   * 
   * @param b
   * @return
   */
  @Override
  public boolean equals(final Object object)
  {
    if (object == null)
    {
      return false;
    }

    if (!Releve.class.isAssignableFrom(object.getClass()))
    {
      return false;
    }

    final Releve releve = (Releve)object;

    return id.equals(releve.id) && ((date == null) || date.equals(releve.date));
  }

  /**
   * 
   */
  private void calculateHashCode()
  {
    hashCode = id.hashCode();
  }

  @Override
  public int hashCode()
  {
    return hashCode;
  }

  /**
   * @param id the id to set
   */
  public final void setId(final String id)
  {
    this.id = id;
    calculateHashCode();
  }
}

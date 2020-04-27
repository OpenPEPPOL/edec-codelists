/**
 * Copyright (C) 2020 OpenPeppol AISBL (www.peppol.eu)
 * Copyright (C) 2015-2020 Philip Helger (www.helger.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.peppol.codelist.model;

import java.net.URI;

import javax.annotation.Nonnull;

import com.helger.commons.string.StringHelper;
import com.helger.commons.url.URLHelper;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

import eu.peppol.codelist.field.ECodeListDataType;
import eu.peppol.codelist.gc.GCHelper;
import eu.peppol.codelist.gc.GCRowExt;

/**
 * Single row of a process in a code list version independent format.
 *
 * @author Philip Helger
 */
public final class ProcessRow implements IModelRow
{
  private static final String SCHEME = "scheme";
  private static final String VALUE = "value";

  public static final String CODE_LIST_NAME = "PeppolProcessIdentifiers";
  public static final URI CODE_LIST_URI = URLHelper.getAsURI ("urn:peppol.eu:names:identifier:process");
  public static final String ROOT_ELEMENT_NAME = "processes";

  private String m_sScheme;
  private String m_sValue;

  public void checkConsistency ()
  {
    if (StringHelper.hasNoText (m_sScheme))
      throw new IllegalStateException ("Scheme is required");
    if (StringHelper.hasNoText (m_sValue))
      throw new IllegalStateException ("Value is required");
  }

  @Nonnull
  public IMicroElement getAsElement ()
  {
    final IMicroElement ret = new MicroElement ("process");
    ret.setAttribute (SCHEME, m_sScheme);
    ret.setAttribute (VALUE, m_sValue);
    return ret;
  }

  @Nonnull
  public IJsonObject getAsJson ()
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (SCHEME, m_sScheme);
    ret.add (VALUE, m_sValue);
    return ret;
  }

  public static void addColumns (@Nonnull final CodeListDocument aCLDoc)
  {
    final ColumnSet aColumnSet = aCLDoc.getColumnSet ();
    GCHelper.addHeaderColumn (aColumnSet, SCHEME, true, true, "Peppol Identifier Scheme", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, VALUE, true, true, "Peppol Identifier Value", ECodeListDataType.STRING);
  }

  @Nonnull
  public Row getAsGCRow (@Nonnull final ColumnSet aColumnSet)
  {
    // Create Genericode row
    final GCRowExt ret = new GCRowExt (aColumnSet);
    ret.add (SCHEME, m_sScheme);
    ret.add (VALUE, m_sValue);
    return ret;
  }

  @Nonnull
  public static ProcessRow createFromID (@Nonnull final IProcessIdentifier aProcID)
  {
    final ProcessRow ret = new ProcessRow ();
    ret.m_sScheme = aProcID.getScheme ();
    ret.m_sValue = aProcID.getValue ();
    return ret;
  }
}

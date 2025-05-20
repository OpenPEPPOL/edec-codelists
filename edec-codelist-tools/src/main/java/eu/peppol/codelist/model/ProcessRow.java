/*
 * Copyright (C) 2020-2025 OpenPeppol AISBL (www.peppol.org)
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

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.URLHelper;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
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
  // New in V8
  private static final String STATE = "state";

  public static final String CODE_LIST_NAME = ModelHelper.CODELIST_NAME_PREFIX + "Processes";
  public static final URI CODE_LIST_URI = URLHelper.getAsURI ("urn:peppol.eu:names:identifier:process");
  public static final String ROOT_ELEMENT_NAME = "processes";

  private String m_sScheme;
  private String m_sValue;
  private ERowState m_eState;

  @Nonnull
  public ERowState getState ()
  {
    return m_eState;
  }

  @Nonnull
  @Nonempty
  public String getUniqueKey ()
  {
    return m_sScheme + ':' + m_sValue;
  }

  public void checkConsistency ()
  {
    if (StringHelper.hasNoText (m_sScheme))
      throw new IllegalStateException ("Scheme is required");
    if (StringHelper.hasNoText (m_sValue))
      throw new IllegalStateException ("Value is required");
    if (m_eState == null)
      throw new IllegalStateException ("State is required");

    if (!PeppolIdentifierFactory.INSTANCE.isProcessIdentifierSchemeValid (m_sScheme))
      throw new IllegalStateException ("Scheme does not match Peppol requirements");
    if (!PeppolIdentifierFactory.INSTANCE.isProcessIdentifierValueValid (m_sScheme, m_sValue))
      throw new IllegalStateException ("Value does not match Peppol requirements");
  }

  @Nonnull
  public IMicroElement getAsElement ()
  {
    final IMicroElement ret = new MicroElement ("process");
    ret.setAttribute (SCHEME, m_sScheme);
    ret.setAttribute (VALUE, m_sValue);
    ret.setAttribute (STATE, m_eState.getID ());
    return ret;
  }

  @Nonnull
  public IJsonObject getAsJson ()
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (SCHEME, m_sScheme);
    ret.add (VALUE, m_sValue);
    ret.add (STATE, m_eState.getID ());
    return ret;
  }

  public static void addGCColumns (@Nonnull final CodeListDocument aCLDoc)
  {
    final ColumnSet aColumnSet = aCLDoc.getColumnSet ();
    GCHelper.addHeaderColumn (aColumnSet, SCHEME, true, true, "Peppol Identifier Scheme", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, VALUE, true, true, "Peppol Identifier Value", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, STATE, false, true, "State", ECodeListDataType.STRING);
  }

  @Nonnull
  public Row getAsGCRow (@Nonnull final ColumnSet aColumnSet)
  {
    // Create Genericode row
    final GCRowExt ret = new GCRowExt (aColumnSet);
    ret.add (SCHEME, m_sScheme);
    ret.add (VALUE, m_sValue);
    ret.add (STATE, m_eState.getID ());
    return ret;
  }

  @Nonnull
  public static HCRow getAsHtmlTableHeaderRow ()
  {
    final HCRow aRow = new HCRow (true);
    aRow.addCell ("Peppol Identifier Scheme");
    aRow.addCell ("Peppol Identifier Value");
    aRow.addCell ("State");
    return aRow;
  }

  @Nonnull
  public HCRow getAsHtmlTableBodyRow ()
  {
    final HCRow aRow = new HCRow ();
    aRow.addCell (m_sScheme);
    aRow.addCell (m_sValue);
    aRow.addCell (m_eState.getDisplayName ());
    if (m_eState.isRemoved ())
      aRow.addClass (DefaultCSSClassProvider.create ("table-danger"));
    else
      if (m_eState.isDeprecated () || m_eState.isScheduledForDeprecation ())
        aRow.addClass (DefaultCSSClassProvider.create ("table-warning"));
    return aRow;
  }

  @Nonnull
  public static ProcessRow createFromID (@Nonnull final IProcessIdentifier aProcID,
                                         @Nonnull @Nonempty final ICommonsList <DocTypeRow> aAllDocTypes)
  {
    final ProcessRow ret = new ProcessRow ();
    ret.m_sScheme = aProcID.getScheme ();
    ret.m_sValue = aProcID.getValue ();
    if (aAllDocTypes.containsAny (x -> x.getState ().isActive ()))
    {
      // If any document type is active, the process ID is also active
      ret.m_eState = ERowState.ACTIVE;
    }
    else
      if (aAllDocTypes.containsAny (x -> x.getState ().isScheduledForDeprecation ()))
      {
        // If no document types is active but at least one is scheduled for
        // deprecation, this process is also scheduled for deprecation
        ret.m_eState = ERowState.SCHEDULED_FOR_DEPRECATION;
      }
      else
        if (aAllDocTypes.containsAny (x -> x.getState ().isDeprecated ()))
        {
          // If no document types is active but at least one is deprecated, this
          // process is also deprecated
          ret.m_eState = ERowState.DEPRECATED;
        }
        else
        {
          // All document types must be removed
          ret.m_eState = ERowState.REMOVED;
        }
    return ret;
  }
}

/*
 * Copyright (C) 2020 OpenPeppol AISBL (www.peppol.eu)
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
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

import eu.peppol.codelist.field.ECodeListDataType;
import eu.peppol.codelist.gc.GCHelper;
import eu.peppol.codelist.gc.GCRowExt;

/**
 * Single row of a transport profile in a code list version independent format.
 *
 * @author Philip Helger
 */
public final class TransportProfileRow implements IModelRow
{
  private static final String PROTOCOL = "protocol";
  private static final String PROFILE_VERSION = "profile-version";
  private static final String PROFILE_ID = "profile-id";
  private static final String SINCE = "since";
  private static final String DEPRECATED = "deprecated";
  private static final String DEPRECATED_SINCE = "deprecated-since";

  public static final String CODE_LIST_NAME = "Peppol Code Lists - Transport profiles";
  public static final URI CODE_LIST_URI = URLHelper.getAsURI ("urn:peppol.eu:names:identifier:transport-profile");
  public static final String ROOT_ELEMENT_NAME = "transport-profiles";

  private String m_sProtcol;
  private String m_sProfileVersion;
  private String m_sProfileID;
  private String m_sSince;
  private boolean m_bDeprecated;
  private String m_sDeprecatedSince;

  public void checkConsistency ()
  {
    if (StringHelper.hasNoText (m_sProtcol))
      throw new IllegalStateException ("Protocol is required");
    if (StringHelper.hasNoText (m_sProfileVersion))
      throw new IllegalStateException ("Profile Version is required");
    if (StringHelper.hasNoText (m_sProfileID))
      throw new IllegalStateException ("Profile ID is required");
    if (StringHelper.hasNoText (m_sSince))
      throw new IllegalStateException ("Since is required");

    if (m_bDeprecated && StringHelper.hasNoText (m_sDeprecatedSince))
      throw new IllegalStateException ("Code list entry is deprecated but there is no deprecated-since entry");
  }

  @Nonnull
  public IMicroElement getAsElement ()
  {
    final IMicroElement ret = new MicroElement ("transport-profile");
    ret.setAttribute (PROTOCOL, m_sProtcol);
    ret.setAttribute (PROFILE_VERSION, m_sProfileVersion);
    ret.setAttribute (PROFILE_ID, m_sProfileID);
    ret.setAttribute (SINCE, m_sSince);
    ret.setAttribute (DEPRECATED, m_bDeprecated);
    ret.setAttribute (DEPRECATED_SINCE, m_sDeprecatedSince);
    return ret;
  }

  @Nonnull
  public IJsonObject getAsJson ()
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (PROTOCOL, m_sProtcol);
    ret.add (PROFILE_VERSION, m_sProfileVersion);
    ret.add (PROFILE_ID, m_sProfileID);
    ret.add (SINCE, m_sSince);
    ret.add (DEPRECATED, m_bDeprecated);
    if (StringHelper.hasText (m_sDeprecatedSince))
      ret.add (DEPRECATED_SINCE, m_sDeprecatedSince);
    return ret;
  }

  public static void addGCColumns (@Nonnull final CodeListDocument aCLDoc)
  {
    final ColumnSet aColumnSet = aCLDoc.getColumnSet ();
    GCHelper.addHeaderColumn (aColumnSet, PROTOCOL, false, true, "Protocol", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, PROFILE_VERSION, false, true, "Profile Version", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, PROFILE_ID, true, true, "Profile ID", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, SINCE, false, true, "Since", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, DEPRECATED, false, true, "Deprecated?", ECodeListDataType.BOOLEAN);
    GCHelper.addHeaderColumn (aColumnSet, DEPRECATED_SINCE, false, false, "Deprecated since", ECodeListDataType.STRING);
  }

  @Nonnull
  public Row getAsGCRow (@Nonnull final ColumnSet aColumnSet)
  {
    // Create Genericode row
    final GCRowExt ret = new GCRowExt (aColumnSet);
    ret.add (PROTOCOL, m_sProtcol);
    ret.add (PROFILE_VERSION, m_sProfileVersion);
    ret.add (PROFILE_ID, m_sProfileID);
    ret.add (SINCE, m_sSince);
    ret.add (DEPRECATED, m_bDeprecated);
    ret.add (DEPRECATED_SINCE, m_sDeprecatedSince);
    return ret;
  }

  @Nonnull
  public static HCRow getAsHtmlTableHeaderRow ()
  {
    final HCRow aRow = new HCRow (true);
    aRow.addCell ("Protocol");
    aRow.addCell ("Profile Version");
    aRow.addCell ("Profile ID");
    aRow.addCell ("Since");
    aRow.addCell ("Deprecated?");
    aRow.addCell ("Deprecated since");
    return aRow;
  }

  @Nonnull
  public HCRow getAsHtmlTableBodyRow ()
  {
    final HCRow aRow = new HCRow ();
    aRow.addCell (m_sProtcol);
    aRow.addCell (m_sProfileVersion);
    aRow.addCell (m_sProfileID);
    aRow.addCell (m_sSince);
    aRow.addCell (Boolean.toString (m_bDeprecated));
    aRow.addCell (m_sDeprecatedSince);
    if (m_bDeprecated)
      aRow.addClass (DefaultCSSClassProvider.create ("table-warning"));
    return aRow;
  }

  @Nonnull
  public static TransportProfileRow createV7 (@Nonnull final String [] aRow)
  {
    final TransportProfileRow ret = new TransportProfileRow ();
    ret.m_sProtcol = aRow[0];
    ret.m_sProfileVersion = aRow[1];
    ret.m_sProfileID = aRow[2];
    ret.m_sSince = aRow[3];
    ret.m_bDeprecated = ModelHelper.parseDeprecated (aRow[4]);
    ret.m_sDeprecatedSince = aRow[5];
    return ret;
  }
}

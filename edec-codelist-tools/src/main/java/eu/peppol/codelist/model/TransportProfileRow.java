/*
 * Copyright (C) 2020-2023 OpenPeppol AISBL (www.peppol.eu)
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
import java.time.LocalDate;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.datetime.PDTWebDateHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.URLHelper;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
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
public final class TransportProfileRow extends AbstractModelRow
{
  private static final String PROTOCOL = "protocol";
  private static final String PROFILE_VERSION = "profile-version";
  private static final String PROFILE_ID = "profile-id";
  // New in V8
  private static final String INITIAL_RELEASE = "initial-release";
  // New in V8
  private static final String STATE = "state";
  // New in V8
  private static final String DEPRECATION_RELEASE = "deprecation-release";
  // New in V8
  private static final String REMOVAL_DATE = "removal-date";
  // New in V8.4
  private static final String COMMENT = "comment";

  public static final String CODE_LIST_NAME = ModelHelper.CODELIST_NAME_PREFIX + "Transport profiles";
  public static final URI CODE_LIST_URI = URLHelper.getAsURI ("urn:peppol.eu:names:identifier:transport-profile");
  public static final String ROOT_ELEMENT_NAME = "transport-profiles";

  private String m_sProtcol;
  private String m_sProfileVersion;
  private String m_sProfileID;
  private String m_sInitialRelease;
  private ERowState m_eState;
  private String m_sDeprecationRelease;
  private LocalDate m_aRemovalDate;
  private String m_sComment;

  @Nonnull
  public ERowState getState ()
  {
    return m_eState;
  }

  @Nonnull
  @Nonempty
  public String getUniqueKey ()
  {
    return m_sProfileID;
  }

  public void checkConsistency ()
  {
    if (StringHelper.hasNoText (m_sProtcol))
      throw new IllegalStateException ("Protocol is required");
    if (StringHelper.hasNoText (m_sProfileVersion))
      throw new IllegalStateException ("Profile Version is required");
    if (StringHelper.hasNoText (m_sProfileID))
      throw new IllegalStateException ("Profile ID is required");
    if (StringHelper.hasNoText (m_sInitialRelease))
      throw new IllegalStateException ("Initial release is required");
    if (m_eState == null)
      throw new IllegalStateException ("State is required");

    if (m_eState.isDeprecated () && StringHelper.hasNoText (m_sDeprecationRelease))
      throw new IllegalStateException ("Code list entry has state 'deprecated' but there is no Deprecation release set");
    if (m_eState.isRemoved () && m_aRemovalDate == null)
      throw new IllegalStateException ("Code list entry has state 'removed' but there is no Removal date set");
  }

  @Nonnull
  public IMicroElement getAsElement ()
  {
    final IMicroElement ret = new MicroElement ("transport-profile");
    ret.setAttribute (PROTOCOL, m_sProtcol);
    ret.setAttribute (PROFILE_VERSION, m_sProfileVersion);
    ret.setAttribute (PROFILE_ID, m_sProfileID);
    ret.setAttribute (INITIAL_RELEASE, m_sInitialRelease);
    ret.setAttribute (STATE, m_eState.getID ());
    if (StringHelper.hasText (m_sDeprecationRelease))
      ret.setAttribute (DEPRECATION_RELEASE, m_sDeprecationRelease);
    if (m_aRemovalDate != null)
      ret.setAttribute (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    if (StringHelper.hasText (m_sComment))
      ret.setAttribute (COMMENT, m_sComment);
    return ret;
  }

  @Nonnull
  public IJsonObject getAsJson ()
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (PROTOCOL, m_sProtcol);
    ret.add (PROFILE_VERSION, m_sProfileVersion);
    ret.add (PROFILE_ID, m_sProfileID);
    ret.add (INITIAL_RELEASE, m_sInitialRelease);
    ret.add (STATE, m_eState.getID ());
    if (StringHelper.hasText (m_sDeprecationRelease))
      ret.add (DEPRECATION_RELEASE, m_sDeprecationRelease);
    if (m_aRemovalDate != null)
      ret.add (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    if (StringHelper.hasText (m_sComment))
      ret.add (COMMENT, m_sComment);
    return ret;
  }

  public static void addGCColumns (@Nonnull final CodeListDocument aCLDoc)
  {
    final ColumnSet aColumnSet = aCLDoc.getColumnSet ();
    GCHelper.addHeaderColumn (aColumnSet, PROTOCOL, false, true, "Protocol", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, PROFILE_VERSION, false, true, "Profile Version", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, PROFILE_ID, true, true, "Profile ID", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, INITIAL_RELEASE, false, true, "Initial release", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, STATE, false, true, "State", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet,
                              DEPRECATION_RELEASE,
                              false,
                              false,
                              "Deprecation release",
                              ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, REMOVAL_DATE, false, false, "Removal date", ECodeListDataType.DATE);
    GCHelper.addHeaderColumn (aColumnSet, COMMENT, false, false, "Comment", ECodeListDataType.STRING);
  }

  @Nonnull
  public Row getAsGCRow (@Nonnull final ColumnSet aColumnSet)
  {
    // Create Genericode row
    final GCRowExt ret = new GCRowExt (aColumnSet);
    ret.add (PROTOCOL, m_sProtcol);
    ret.add (PROFILE_VERSION, m_sProfileVersion);
    ret.add (PROFILE_ID, m_sProfileID);
    ret.add (INITIAL_RELEASE, m_sInitialRelease);
    ret.add (STATE, m_eState.getID ());
    ret.add (DEPRECATION_RELEASE, m_sDeprecationRelease);
    ret.add (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    ret.add (COMMENT, m_sComment);
    return ret;
  }

  @Nonnull
  public static HCRow getAsHtmlTableHeaderRow ()
  {
    final HCRow aRow = new HCRow (true);
    aRow.addCell ("Protocol");
    aRow.addCell ("Profile Version");
    aRow.addCell ("Profile ID");
    aRow.addCell ("Initial release");
    aRow.addCell ("State");
    aRow.addCell ("Deprecation release");
    aRow.addCell ("Removal date");
    aRow.addCell ("Comment");
    return aRow;
  }

  @Nonnull
  public HCRow getAsHtmlTableBodyRow ()
  {
    final HCRow aRow = new HCRow ();
    aRow.addCell (m_sProtcol);
    aRow.addCell (m_sProfileVersion);
    aRow.addCell (m_sProfileID);
    aRow.addAndReturnCell (m_sInitialRelease).addClass (ModelHelper.CSS_TEXT_END);
    aRow.addCell (m_eState.getDisplayName ());
    if (StringHelper.hasText (m_sDeprecationRelease))
      aRow.addAndReturnCell (m_sDeprecationRelease).addClass (ModelHelper.CSS_TEXT_END);
    else
      aRow.addCell ();
    aRow.addAndReturnCell (PDTWebDateHelper.getAsStringXSD (m_aRemovalDate)).addClass (ModelHelper.CSS_TEXT_END);
    if (m_eState.isRemoved ())
      aRow.addClass (ModelHelper.CSS_TABLE_DANGER);
    else
      if (m_eState.isDeprecated ())
        aRow.addClass (ModelHelper.CSS_TABLE_WARNING);
    aRow.addCell (m_sComment);
    return aRow;
  }

  @Nonnull
  public static TransportProfileRow createV8 (@Nonnull final String [] aRow)
  {
    final TransportProfileRow ret = new TransportProfileRow ();
    ret.m_sProtcol = aRow[0];
    ret.m_sProfileVersion = aRow[1];
    ret.m_sProfileID = aRow[2];
    ret.m_sInitialRelease = aRow[3];
    ret.m_eState = ERowState.getFromIDOrThrow (aRow[4]);
    ret.m_sDeprecationRelease = aRow[5];
    ret.m_aRemovalDate = getLocalDateFromExcel (aRow[6]);
    ret.m_sComment = safeGetAtIndex (aRow, 7);
    return ret;
  }
}

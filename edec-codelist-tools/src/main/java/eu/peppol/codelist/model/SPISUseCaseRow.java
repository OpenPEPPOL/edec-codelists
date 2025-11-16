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
import java.time.LocalDate;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.base.string.StringHelper;
import com.helger.base.url.URLHelper;
import com.helger.datetime.web.PDTWebDateHelper;
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
 * Single row of a SPIS Use Case in a code list version independent format.
 *
 * @author Philip Helger
 * @since v9.3
 */
public final class SPISUseCaseRow extends AbstractModelRow
{
  private static final String USE_CASE_ID = "use-case-id";
  private static final String INITIAL_RELEASE = "initial-release";
  private static final String STATE = "state";
  private static final String DEPRECATION_RELEASE = "deprecation-release";
  private static final String REMOVAL_DATE = "removal-date";
  private static final String COMMENT = "comment";

  public static final String CODE_LIST_NAME = ModelHelper.CODELIST_NAME_PREFIX + "SPIS Use Case";
  public static final URI CODE_LIST_URI = URLHelper.getAsURI ("urn:peppol.eu:names:identifier:spis-use-case");
  public static final String ROOT_ELEMENT_NAME = "spis-use-case";

  private String m_sUseCaseID;
  private String m_sInitialRelease;
  private ERowState m_eState;
  private String m_sDeprecationRelease;
  private LocalDate m_aRemovalDate;
  private String m_sComment;

  @NonNull
  public ERowState getState ()
  {
    return m_eState;
  }

  @Nonempty
  public String getUniqueKey ()
  {
    return m_sUseCaseID;
  }

  public void checkConsistency ()
  {
    if (StringHelper.isEmpty (m_sUseCaseID))
      throw new IllegalStateException ("Use Case ID is required");
    if (StringHelper.isEmpty (m_sInitialRelease))
      throw new IllegalStateException ("Initial release is required");
    if (m_eState == null)
      throw new IllegalStateException ("State is required");

    if (m_eState.isScheduledForDeprecation () && StringHelper.isEmpty (m_sDeprecationRelease))
      throw new IllegalStateException ("Code list entry has state 'scheduled for deprecation' but there is no Deprecation date set");
    if (m_eState.isDeprecated () && StringHelper.isEmpty (m_sDeprecationRelease))
      throw new IllegalStateException ("Code list entry has state 'deprecated' but there is no Deprecation release set");
    if (m_eState.isRemoved () && m_aRemovalDate == null)
      throw new IllegalStateException ("Code list entry has state 'removed' but there is no Removal date set");
  }

  @NonNull
  public IMicroElement getAsElement ()
  {
    // Must differ from root element
    final IMicroElement ret = new MicroElement ("item");
    ret.setAttribute (USE_CASE_ID, m_sUseCaseID);
    ret.setAttribute (INITIAL_RELEASE, m_sInitialRelease);
    ret.setAttribute (STATE, m_eState.getID ());
    if (StringHelper.isNotEmpty (m_sDeprecationRelease))
      ret.setAttribute (DEPRECATION_RELEASE, m_sDeprecationRelease);
    if (m_aRemovalDate != null)
      ret.setAttribute (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    if (StringHelper.isNotEmpty (m_sComment))
      ret.setAttribute (COMMENT, m_sComment);
    return ret;
  }

  @NonNull
  public IJsonObject getAsJson ()
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (USE_CASE_ID, m_sUseCaseID);
    ret.add (INITIAL_RELEASE, m_sInitialRelease);
    ret.add (STATE, m_eState.getID ());
    if (StringHelper.isNotEmpty (m_sDeprecationRelease))
      ret.add (DEPRECATION_RELEASE, m_sDeprecationRelease);
    if (m_aRemovalDate != null)
      ret.add (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    if (StringHelper.isNotEmpty (m_sComment))
      ret.add (COMMENT, m_sComment);
    return ret;
  }

  public static void addGCColumns (@NonNull final CodeListDocument aCLDoc)
  {
    final ColumnSet aColumnSet = aCLDoc.getColumnSet ();
    GCHelper.addHeaderColumn (aColumnSet, USE_CASE_ID, false, true, "Use Case ID", ECodeListDataType.STRING);
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

  @NonNull
  public Row getAsGCRow (@NonNull final ColumnSet aColumnSet)
  {
    // Create Genericode row
    final GCRowExt ret = new GCRowExt (aColumnSet);
    ret.add (USE_CASE_ID, m_sUseCaseID);
    ret.add (INITIAL_RELEASE, m_sInitialRelease);
    ret.add (STATE, m_eState.getID ());
    ret.add (DEPRECATION_RELEASE, m_sDeprecationRelease);
    ret.add (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    ret.add (COMMENT, m_sComment);
    return ret;
  }

  @NonNull
  public static HCRow getAsHtmlTableHeaderRow ()
  {
    final HCRow aRow = new HCRow (true);
    aRow.addCell ("Use Case ID");
    aRow.addCell ("Initial release");
    aRow.addCell ("State");
    aRow.addCell ("Deprecation release");
    aRow.addCell ("Removal date");
    aRow.addCell ("Comment");
    return aRow;
  }

  @NonNull
  public HCRow getAsHtmlTableBodyRow ()
  {
    final HCRow aRow = new HCRow ();
    aRow.addCell (m_sUseCaseID);
    aRow.addAndReturnCell (m_sInitialRelease).addClass (ModelHelper.CSS_TEXT_END);
    aRow.addCell (m_eState.getDisplayName ());
    if (StringHelper.isNotEmpty (m_sDeprecationRelease))
      aRow.addAndReturnCell (m_sDeprecationRelease).addClass (ModelHelper.CSS_TEXT_END);
    else
      aRow.addCell ();
    aRow.addAndReturnCell (PDTWebDateHelper.getAsStringXSD (m_aRemovalDate)).addClass (ModelHelper.CSS_TEXT_END);
    if (m_eState.isRemoved ())
      aRow.addClass (ModelHelper.CSS_TABLE_DANGER);
    else
      if (m_eState.isDeprecated () || m_eState.isScheduledForDeprecation ())
        aRow.addClass (ModelHelper.CSS_TABLE_WARNING);
    aRow.addCell (m_sComment);
    return aRow;
  }

  @NonNull
  public static SPISUseCaseRow createV9 (@NonNull final String [] aRow)
  {
    final SPISUseCaseRow ret = new SPISUseCaseRow ();
    ret.m_sUseCaseID = aRow[0];
    ret.m_sInitialRelease = aRow[1];
    ret.m_eState = ERowState.getFromIDOrThrow (aRow[2]);
    ret.m_sDeprecationRelease = getDeprecationReleaseOrDate (aRow[3]);
    ret.m_aRemovalDate = getLocalDateFromExcel (aRow[4]);
    ret.m_sComment = safeGetAtIndex (aRow, 5);
    return ret;
  }
}

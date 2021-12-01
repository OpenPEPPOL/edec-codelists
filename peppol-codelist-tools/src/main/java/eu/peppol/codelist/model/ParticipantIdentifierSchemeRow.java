/*
 * Copyright (C) 2020-2021 OpenPeppol AISBL (www.peppol.eu)
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

import com.helger.commons.datetime.PDTWebDateHelper;
import com.helger.commons.regex.RegExHelper;
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
 * Single row of a participant identifier scheme in a code list version
 * independent format.
 *
 * @author Philip Helger
 */
public final class ParticipantIdentifierSchemeRow implements IModelRow
{
  private static final String SCHEME_ID = "schemeid";
  private static final String ISO6523 = "iso6523";
  private static final String COUNTRY = "country";
  private static final String SCHEME_NAME = "scheme-name";
  private static final String ISSUING_AGENCY = "issuing-agency";

  @Deprecated
  @SuppressWarnings ("unused")
  // Deprecated in V8
  private static final String SINCE = "since";
  // New in V8
  private static final String INITIAL_RELEASE = "initial-release";

  @Deprecated
  @SuppressWarnings ("unused")
  // Deprecated in V8
  private static final String DEPRECATED = "deprecated";
  // New in V8
  private static final String STATE = "state";

  @Deprecated
  @SuppressWarnings ("unused")
  // Deprecated in V8
  private static final String DEPRECATED_SINCE = "deprecated-since";
  // New in V8
  private static final String DEPRECATION_RELEASE = "deprecation-release";

  // New in V8
  private static final String REMOVAL_DATE = "removal-date";

  private static final String STRUCTURE = "structure";
  private static final String DISPLAY = "display";
  private static final String EXAMPLES = "examples";
  private static final String VALIDATION_RULES = "validation-rules";
  private static final String USAGE = "usage";

  public static final String CODE_LIST_NAME = "Peppol Code Lists - Participant identifier schemes";
  public static final URI CODE_LIST_URI = URLHelper.getAsURI ("urn:peppol.eu:names:identifier:participant-identifier-scheme");
  public static final String ROOT_ELEMENT_NAME = "participant-identifier-schemes";

  private String m_sSchemeID;
  private String m_sISO6523;
  private String m_sCountry;
  private String m_sSchemeName;
  private String m_sIssuingAgency;
  private String m_sInitialRelease;
  private ERowState m_eState;
  private String m_sDeprecationRelease;
  private LocalDate m_aRemovalDate;
  private String m_sStructure;
  private String m_sDisplay;
  private String m_sExamples;
  private String m_sValidationRules;
  private String m_sUsage;

  @Nonnull
  public ERowState getState ()
  {
    return m_eState;
  }

  public void checkConsistency ()
  {
    if (StringHelper.hasNoText (m_sSchemeID))
      throw new IllegalStateException ("Scheme ID is required");
    if (m_sSchemeID.indexOf (' ') >= 0)
      throw new IllegalStateException ("Scheme IDs are not supposed to contain spaces!");

    if (StringHelper.hasNoText (m_sISO6523))
      throw new IllegalStateException ("ISO 6523 code is required");
    if (!RegExHelper.stringMatchesPattern ("[0-9]{4}", m_sISO6523))
      throw new IllegalStateException ("The ISO 6523 code '" + m_sISO6523 + "' does not consist of 4 numbers");

    if (StringHelper.hasNoText (m_sCountry))
      throw new IllegalStateException ("Country is required");
    if (StringHelper.hasNoText (m_sSchemeName))
      throw new IllegalStateException ("Scheme Name is required");
    if (StringHelper.hasNoText (m_sInitialRelease))
      throw new IllegalStateException ("Initial Release is required");
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
    final IMicroElement ret = new MicroElement ("participant-identifier-scheme");
    ret.setAttribute (SCHEME_ID, m_sSchemeID);
    ret.setAttribute (ISO6523, m_sISO6523);
    ret.setAttribute (COUNTRY, m_sCountry);
    ret.setAttribute (SCHEME_NAME, m_sSchemeName);
    ret.setAttribute (ISSUING_AGENCY, m_sIssuingAgency);
    ret.setAttribute (INITIAL_RELEASE, m_sInitialRelease);
    ret.setAttribute (STATE, m_eState.getID ());
    if (StringHelper.hasText (m_sDeprecationRelease))
      ret.setAttribute (DEPRECATION_RELEASE, m_sDeprecationRelease);
    if (m_aRemovalDate != null)
      ret.setAttribute (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    if (StringHelper.hasText (m_sStructure))
      ret.appendElement (STRUCTURE).appendText (m_sStructure);
    if (StringHelper.hasText (m_sDisplay))
      ret.appendElement (DISPLAY).appendText (m_sDisplay);
    if (StringHelper.hasText (m_sExamples))
      ret.appendElement (EXAMPLES).appendText (m_sExamples);
    if (StringHelper.hasText (m_sValidationRules))
      ret.appendElement (VALIDATION_RULES).appendText (m_sValidationRules);
    if (StringHelper.hasText (m_sUsage))
      ret.appendElement (USAGE).appendText (m_sUsage);
    return ret;
  }

  @Nonnull
  public IJsonObject getAsJson ()
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (SCHEME_ID, m_sSchemeID);
    ret.add (ISO6523, m_sISO6523);
    ret.add (COUNTRY, m_sCountry);
    ret.add (SCHEME_NAME, m_sSchemeName);
    if (StringHelper.hasText (m_sIssuingAgency))
      ret.add (ISSUING_AGENCY, m_sIssuingAgency);
    ret.add (INITIAL_RELEASE, m_sInitialRelease);
    ret.add (STATE, m_eState.getID ());
    if (StringHelper.hasText (m_sDeprecationRelease))
      ret.add (DEPRECATION_RELEASE, m_sDeprecationRelease);
    if (m_aRemovalDate != null)
      ret.add (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    if (StringHelper.hasText (m_sStructure))
      ret.add (STRUCTURE, m_sStructure);
    if (StringHelper.hasText (m_sDisplay))
      ret.add (DISPLAY, m_sDisplay);
    if (StringHelper.hasText (m_sExamples))
      ret.add (EXAMPLES, m_sExamples);
    if (StringHelper.hasText (m_sValidationRules))
      ret.add (VALIDATION_RULES, m_sValidationRules);
    if (StringHelper.hasText (m_sUsage))
      ret.add (USAGE, m_sUsage);
    return ret;
  }

  public static void addGCColumns (@Nonnull final CodeListDocument aCLDoc)
  {
    final ColumnSet aColumnSet = aCLDoc.getColumnSet ();
    GCHelper.addHeaderColumn (aColumnSet, SCHEME_ID, true, true, "Scheme ID", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, ISO6523, false, true, "ICD Value", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, COUNTRY, true, true, "Country Code", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, SCHEME_NAME, true, true, "Scheme Name", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, ISSUING_AGENCY, false, false, "Issuing Organisation", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, INITIAL_RELEASE, false, true, "Initial Release", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, STATE, false, true, "State", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, DEPRECATION_RELEASE, false, false, "Deprecation release", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, REMOVAL_DATE, false, false, "Removal date", ECodeListDataType.DATE);
    GCHelper.addHeaderColumn (aColumnSet, STRUCTURE, false, false, "Structure of Code", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, DISPLAY, false, false, "Display Requirements", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, EXAMPLES, false, false, "Peppol Examples", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, VALIDATION_RULES, false, false, "Validation Rules", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, USAGE, false, false, "Usage Notes", ECodeListDataType.STRING);
  }

  @Nonnull
  public Row getAsGCRow (@Nonnull final ColumnSet aColumnSet)
  {
    // Create Genericode row
    final GCRowExt ret = new GCRowExt (aColumnSet);
    ret.add (SCHEME_ID, m_sSchemeID);
    ret.add (ISO6523, m_sISO6523);
    ret.add (COUNTRY, m_sCountry);
    ret.add (SCHEME_NAME, m_sSchemeName);
    ret.add (ISSUING_AGENCY, m_sIssuingAgency);
    ret.add (INITIAL_RELEASE, m_sInitialRelease);
    ret.add (STATE, m_eState.getID ());
    ret.add (DEPRECATION_RELEASE, m_sDeprecationRelease);
    ret.add (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    ret.add (STRUCTURE, m_sStructure);
    ret.add (DISPLAY, m_sDisplay);
    ret.add (EXAMPLES, m_sExamples);
    ret.add (VALIDATION_RULES, m_sValidationRules);
    ret.add (USAGE, m_sUsage);
    return ret;
  }

  @Nonnull
  public static HCRow getAsHtmlTableHeaderRow ()
  {
    final HCRow aRow = new HCRow (true);
    aRow.addCell ("Scheme ID");
    aRow.addCell ("ICD Value");
    aRow.addCell ("Country Code");
    aRow.addCell ("Scheme Name");
    aRow.addCell ("Issuing Organisation");
    aRow.addCell ("Initial release");
    aRow.addCell ("State");
    aRow.addCell ("Deprecation release");
    aRow.addCell ("Removal date");
    aRow.addCell ("Structure of Code");
    aRow.addCell ("Display Requirements");
    aRow.addCell ("Peppol Examples");
    aRow.addCell ("Validation Rules");
    aRow.addCell ("Usage Notes");
    return aRow;
  }

  @Nonnull
  public HCRow getAsHtmlTableBodyRow ()
  {
    final HCRow aRow = new HCRow ();
    aRow.addCell (m_sSchemeID);
    aRow.addCell (m_sISO6523);
    aRow.addCell (m_sCountry);
    aRow.addCell (m_sSchemeName);
    aRow.addCell (m_sIssuingAgency);
    aRow.addAndReturnCell (m_sInitialRelease).addClass (ModelHelper.CSS_TEXT_END);
    aRow.addCell (m_eState.getDisplayName ());
    aRow.addAndReturnCell (m_sDeprecationRelease).addClass (ModelHelper.CSS_TEXT_END);
    aRow.addAndReturnCell (PDTWebDateHelper.getAsStringXSD (m_aRemovalDate)).addClass (ModelHelper.CSS_TEXT_END);
    aRow.addCell (m_sStructure);
    aRow.addCell (m_sDisplay);
    aRow.addCell (m_sExamples);
    aRow.addCell (m_sValidationRules);
    aRow.addCell (m_sUsage);
    if (m_eState.isRemoved ())
      aRow.addClass (ModelHelper.CSS_TABLE_DANGER);
    else
      if (m_eState.isDeprecated ())
        aRow.addClass (ModelHelper.CSS_TABLE_WARNING);
    return aRow;
  }

  @Nonnull
  @Deprecated
  public static ParticipantIdentifierSchemeRow createV7 (@Nonnull final String [] aRow)
  {
    final ParticipantIdentifierSchemeRow ret = new ParticipantIdentifierSchemeRow ();
    ret.m_sSchemeID = aRow[0];
    ret.m_sISO6523 = aRow[1];
    ret.m_sCountry = aRow[2];
    ret.m_sSchemeName = aRow[3];
    ret.m_sIssuingAgency = aRow[4];
    ret.m_sInitialRelease = aRow[5];
    ret.m_eState = ModelHelper.parseDeprecated (aRow[6]) ? ERowState.DEPRECATED : ERowState.ACTIVE;
    ret.m_sDeprecationRelease = aRow[7];
    ret.m_aRemovalDate = null;
    ret.m_sStructure = aRow[8];
    ret.m_sDisplay = aRow[9];
    ret.m_sExamples = aRow[10];
    ret.m_sValidationRules = aRow[11];
    ret.m_sUsage = aRow[12];
    return ret;
  }

  @Nonnull
  public static ParticipantIdentifierSchemeRow createV8 (@Nonnull final String [] aRow)
  {
    final ParticipantIdentifierSchemeRow ret = new ParticipantIdentifierSchemeRow ();
    ret.m_sSchemeID = aRow[0];
    ret.m_sISO6523 = aRow[1];
    ret.m_sCountry = aRow[2];
    ret.m_sSchemeName = aRow[3];
    ret.m_sIssuingAgency = aRow[4];
    ret.m_sInitialRelease = aRow[5];
    ret.m_eState = ERowState.getFromIDOrThrow (aRow[6]);
    ret.m_sDeprecationRelease = aRow[7];
    ret.m_aRemovalDate = PDTWebDateHelper.getLocalDateFromXSD (aRow[8]);
    ret.m_sStructure = aRow[0];
    ret.m_sDisplay = aRow[10];
    ret.m_sExamples = aRow[11];
    ret.m_sValidationRules = aRow[12];
    ret.m_sUsage = aRow[13];
    return ret;
  }
}

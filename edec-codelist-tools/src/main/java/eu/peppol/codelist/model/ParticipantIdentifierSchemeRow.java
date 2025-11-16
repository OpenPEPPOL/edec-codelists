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
import com.helger.base.string.StringParser;
import com.helger.base.url.URLHelper;
import com.helger.cache.regex.RegExHelper;
import com.helger.datetime.web.PDTWebDateHelper;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
import com.helger.html.hc.ext.HCExtHelper;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

import eu.peppol.codelist.field.ECodeListDataType;
import eu.peppol.codelist.gc.GCHelper;
import eu.peppol.codelist.gc.GCRowExt;

/**
 * Single row of a participant identifier scheme in a code list version independent format.
 *
 * @author Philip Helger
 */
public final class ParticipantIdentifierSchemeRow extends AbstractModelRow
{
  private static final String SCHEME_ID = "schemeid";
  private static final String ISO6523 = "iso6523";
  private static final String COUNTRY = "country";
  private static final String SCHEME_NAME = "scheme-name";
  private static final String ISSUING_AGENCY = "issuing-agency";
  // New in V8
  private static final String INITIAL_RELEASE = "initial-release";
  // New in V8
  private static final String STATE = "state";
  // New in V8
  private static final String DEPRECATION_RELEASE = "deprecation-release";
  // New in V8
  private static final String REMOVAL_DATE = "removal-date";
  private static final String STRUCTURE = "structure";
  private static final String DISPLAY = "display";
  private static final String EXAMPLES = "examples";
  private static final String VALIDATION_RULES = "validation-rules";
  private static final String USAGE = "usage";
  private static final String REGISTRABLE = "registrable";

  public static final String CODE_LIST_NAME = ModelHelper.CODELIST_NAME_PREFIX + "Participant identifier schemes";
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
  private boolean m_bRegistrable;

  @NonNull
  public ERowState getState ()
  {
    return m_eState;
  }

  @NonNull
  @Nonempty
  public String getUniqueKey ()
  {
    return m_sSchemeID + ':' + m_sISO6523;
  }

  public void checkConsistency ()
  {
    if (StringHelper.isEmpty (m_sSchemeID))
      throw new IllegalStateException ("Scheme ID is required");
    if (m_sSchemeID.indexOf (' ') >= 0)
      throw new IllegalStateException ("Scheme IDs are not supposed to contain spaces!");

    if (StringHelper.isEmpty (m_sISO6523))
      throw new IllegalStateException ("ISO 6523 code is required");
    if (!RegExHelper.stringMatchesPattern ("[0-9]{4}", m_sISO6523))
      throw new IllegalStateException ("The ISO 6523 code '" + m_sISO6523 + "' does not consist of 4 numbers");

    if (StringHelper.isEmpty (m_sCountry))
      throw new IllegalStateException ("Country is required");
    if (StringHelper.isEmpty (m_sSchemeName))
      throw new IllegalStateException ("Scheme Name is required");
    if (StringHelper.isEmpty (m_sInitialRelease))
      throw new IllegalStateException ("Initial Release is required");
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
    final IMicroElement ret = new MicroElement ("participant-identifier-scheme");
    ret.setAttribute (SCHEME_ID, m_sSchemeID);
    ret.setAttribute (ISO6523, m_sISO6523);
    ret.setAttribute (COUNTRY, m_sCountry);
    ret.setAttribute (SCHEME_NAME, m_sSchemeName);
    ret.setAttribute (ISSUING_AGENCY, m_sIssuingAgency);
    ret.setAttribute (INITIAL_RELEASE, m_sInitialRelease);
    ret.setAttribute (STATE, m_eState.getID ());
    if (StringHelper.isNotEmpty (m_sDeprecationRelease))
      ret.setAttribute (DEPRECATION_RELEASE, m_sDeprecationRelease);
    if (m_aRemovalDate != null)
      ret.setAttribute (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    if (StringHelper.isNotEmpty (m_sStructure))
      ret.addElement (STRUCTURE).addText (m_sStructure);
    if (StringHelper.isNotEmpty (m_sDisplay))
      ret.addElement (DISPLAY).addText (m_sDisplay);
    if (StringHelper.isNotEmpty (m_sExamples))
      ret.addElement (EXAMPLES).addText (m_sExamples);
    if (StringHelper.isNotEmpty (m_sValidationRules))
      ret.addElement (VALIDATION_RULES).addText (m_sValidationRules);
    if (StringHelper.isNotEmpty (m_sUsage))
      ret.addElement (USAGE).addText (m_sUsage);
    ret.addElement (REGISTRABLE).addText (Boolean.toString (m_bRegistrable));
    return ret;
  }

  @NonNull
  public IJsonObject getAsJson ()
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (SCHEME_ID, m_sSchemeID);
    ret.add (ISO6523, m_sISO6523);
    ret.add (COUNTRY, m_sCountry);
    ret.add (SCHEME_NAME, m_sSchemeName);
    if (StringHelper.isNotEmpty (m_sIssuingAgency))
      ret.add (ISSUING_AGENCY, m_sIssuingAgency);
    ret.add (INITIAL_RELEASE, m_sInitialRelease);
    ret.add (STATE, m_eState.getID ());
    if (StringHelper.isNotEmpty (m_sDeprecationRelease))
      ret.add (DEPRECATION_RELEASE, m_sDeprecationRelease);
    if (m_aRemovalDate != null)
      ret.add (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    if (StringHelper.isNotEmpty (m_sStructure))
      ret.add (STRUCTURE, m_sStructure);
    if (StringHelper.isNotEmpty (m_sDisplay))
      ret.add (DISPLAY, m_sDisplay);
    if (StringHelper.isNotEmpty (m_sExamples))
      ret.add (EXAMPLES, m_sExamples);
    if (StringHelper.isNotEmpty (m_sValidationRules))
      ret.add (VALIDATION_RULES, m_sValidationRules);
    if (StringHelper.isNotEmpty (m_sUsage))
      ret.add (USAGE, m_sUsage);
    ret.add (REGISTRABLE, m_bRegistrable);
    return ret;
  }

  public static void addGCColumns (@NonNull final CodeListDocument aCLDoc)
  {
    final ColumnSet aColumnSet = aCLDoc.getColumnSet ();
    GCHelper.addHeaderColumn (aColumnSet, SCHEME_ID, true, true, "Scheme ID", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, ISO6523, false, true, "ICD Value", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, COUNTRY, true, true, "Country Code", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, SCHEME_NAME, true, true, "Scheme Name", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet,
                              ISSUING_AGENCY,
                              false,
                              false,
                              "Issuing Organisation",
                              ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, INITIAL_RELEASE, false, true, "Initial Release", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, STATE, false, true, "State", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet,
                              DEPRECATION_RELEASE,
                              false,
                              false,
                              "Deprecation release",
                              ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, REMOVAL_DATE, false, false, "Removal date", ECodeListDataType.DATE);
    GCHelper.addHeaderColumn (aColumnSet, STRUCTURE, false, false, "Structure of Code", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, DISPLAY, false, false, "Display Requirements", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, EXAMPLES, false, false, "Peppol Examples", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, VALIDATION_RULES, false, false, "Validation Rules", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, USAGE, false, false, "Usage Notes", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, REGISTRABLE, false, false, "Registrable?", ECodeListDataType.BOOLEAN);
  }

  @NonNull
  public Row getAsGCRow (@NonNull final ColumnSet aColumnSet)
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
    ret.add (REGISTRABLE, m_bRegistrable);
    return ret;
  }

  @NonNull
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
    aRow.addCell ("Registrable?");
    return aRow;
  }

  @NonNull
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
    aRow.addAndReturnCell (HCExtHelper.nl2brList (m_sStructure)).addClass (ModelHelper.CSS_WIDE_COLUMN);
    aRow.addAndReturnCell (HCExtHelper.nl2brList (m_sDisplay)).addClass (ModelHelper.CSS_WIDE_COLUMN);
    aRow.addCell (m_sExamples);
    aRow.addCell (m_sValidationRules);
    aRow.addCell (m_sUsage);
    if (m_eState.isRemoved ())
      aRow.addClass (ModelHelper.CSS_TABLE_DANGER);
    else
      if (m_eState.isDeprecated () || m_eState.isScheduledForDeprecation ())
        aRow.addClass (ModelHelper.CSS_TABLE_WARNING);
    aRow.addCell (Boolean.toString (m_bRegistrable));
    return aRow;
  }

  @NonNull
  public static ParticipantIdentifierSchemeRow createV9 (@NonNull final String [] aRow)
  {
    final ParticipantIdentifierSchemeRow ret = new ParticipantIdentifierSchemeRow ();
    ret.m_sSchemeID = aRow[0];
    ret.m_sISO6523 = aRow[1];
    ret.m_sCountry = aRow[2];
    ret.m_sSchemeName = aRow[3];
    ret.m_sIssuingAgency = aRow[4];
    ret.m_sInitialRelease = aRow[5];
    ret.m_eState = ERowState.getFromIDOrThrow (aRow[6]);
    ret.m_sDeprecationRelease = getDeprecationReleaseOrDate (aRow[7]);
    ret.m_aRemovalDate = getLocalDateFromExcel (aRow[8]);
    ret.m_sStructure = aRow[9];
    ret.m_sDisplay = aRow[10];
    ret.m_sExamples = aRow[11];
    ret.m_sValidationRules = aRow[12];
    ret.m_sUsage = aRow[13];
    ret.m_bRegistrable = StringParser.parseBool (aRow[14]);
    return ret;
  }
}

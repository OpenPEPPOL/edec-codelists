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

import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.URLHelper;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
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
  private static final String SINCE = "since";
  private static final String DEPRECATED = "deprecated";
  private static final String DEPRECATED_SINCE = "deprecated-since";
  private static final String STRUCTURE = "structure";
  private static final String DISPLAY = "display";
  private static final String EXAMPLES = "examples";
  private static final String VALIDATION_RULES = "validation-rules";
  private static final String USAGE = "usage";

  public static final String CODE_LIST_NAME = "PeppolParticipantIdentifierSchemes";
  public static final URI CODE_LIST_URI = URLHelper.getAsURI ("urn:peppol.eu:names:identifier:participant-identifier-scheme");

  private String m_sSchemeID;
  private String m_sISO6523;
  private String m_sCountry;
  private String m_sSchemeName;
  private String m_sIssuingAgency;
  private String m_sSince;
  private boolean m_bDeprecated;
  private String m_sDeprecatedSince;
  private String m_sStructure;
  private String m_sDisplay;
  private String m_sExamples;
  private String m_sValidationRules;
  private String m_sUsage;

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
    if (StringHelper.hasNoText (m_sSince))
      throw new IllegalStateException ("Since is required");
    if (m_bDeprecated && StringHelper.hasNoText (m_sDeprecatedSince))
      throw new IllegalStateException ("Code list entry is deprecated but there is no deprecated-since entry");
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
    ret.setAttribute (SINCE, m_sSince);
    ret.setAttribute (DEPRECATED, m_bDeprecated);
    ret.setAttribute (DEPRECATED_SINCE, m_sDeprecatedSince);
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
    ret.add (ISSUING_AGENCY, m_sIssuingAgency);
    ret.add (SINCE, m_sSince);
    ret.add (DEPRECATED, m_bDeprecated);
    ret.add (DEPRECATED_SINCE, m_sDeprecatedSince);
    ret.add (STRUCTURE, m_sStructure);
    ret.add (DISPLAY, m_sDisplay);
    ret.add (EXAMPLES, m_sExamples);
    ret.add (VALIDATION_RULES, m_sValidationRules);
    ret.add (USAGE, m_sUsage);
    return ret;
  }

  public static void addColumns (@Nonnull final CodeListDocument aCLDoc)
  {
    final ColumnSet aColumnSet = aCLDoc.getColumnSet ();
    GCHelper.addHeaderColumn (aColumnSet, SCHEME_ID, true, true, "Scheme ID", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, ISO6523, false, true, "ICD Value", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, COUNTRY, true, true, "Country Code", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, SCHEME_NAME, true, true, "Scheme Name", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, ISSUING_AGENCY, false, false, "Issuing Organisation", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, SINCE, false, true, "Since", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, DEPRECATED, false, true, "Deprecated?", ECodeListDataType.BOOLEAN);
    GCHelper.addHeaderColumn (aColumnSet, DEPRECATED_SINCE, false, false, "Deprecated since", ECodeListDataType.STRING);
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
    ret.add (SINCE, m_sSince);
    ret.add (DEPRECATED, m_bDeprecated);
    ret.add (DEPRECATED_SINCE, m_sDeprecatedSince);
    ret.add (STRUCTURE, m_sStructure);
    ret.add (DISPLAY, m_sDisplay);
    ret.add (EXAMPLES, m_sExamples);
    ret.add (VALIDATION_RULES, m_sValidationRules);
    ret.add (USAGE, m_sUsage);
    return ret;
  }

  @Nonnull
  public static ParticipantIdentifierSchemeRow createV7 (@Nonnull final String [] aRow)
  {
    final ParticipantIdentifierSchemeRow ret = new ParticipantIdentifierSchemeRow ();
    ret.m_sSchemeID = aRow[0];
    ret.m_sISO6523 = aRow[1];
    ret.m_sCountry = aRow[2];
    ret.m_sSchemeName = aRow[3];
    ret.m_sIssuingAgency = aRow[4];
    ret.m_sSince = aRow[5];
    ret.m_bDeprecated = ModelHelper.parseDeprecated (aRow[6]);
    ret.m_sDeprecatedSince = aRow[7];
    ret.m_sStructure = aRow[8];
    ret.m_sDisplay = aRow[9];
    ret.m_sExamples = aRow[10];
    ret.m_sValidationRules = aRow[11];
    ret.m_sUsage = aRow[12];
    return ret;
  }
}

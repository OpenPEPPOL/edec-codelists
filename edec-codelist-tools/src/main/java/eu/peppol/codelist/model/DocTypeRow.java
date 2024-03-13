/*
 * Copyright (C) 2020-2024 OpenPeppol AISBL (www.peppol.eu)
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
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.datetime.PDTWebDateHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.url.URLHelper;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
import com.helger.html.hc.ext.HCExtHelper;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.peppolid.peppol.doctype.IPeppolDocumentTypeIdentifierParts;
import com.helger.peppolid.peppol.doctype.PeppolDocumentTypeIdentifierParts;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

import eu.peppol.codelist.field.ECodeListDataType;
import eu.peppol.codelist.gc.GCHelper;
import eu.peppol.codelist.gc.GCRowExt;

/**
 * Single row of a document type in a code list version independent format.
 *
 * @author Philip Helger
 */
public final class DocTypeRow extends AbstractModelRow
{
  private static final String NAME = "name";
  private static final String SCHEME = "scheme";
  private static final String VALUE = "value";
  // New in V8
  private static final String INITIAL_RELEASE = "initial-release";
  // New in V8
  private static final String STATE = "state";
  // New in V8
  private static final String DEPRECATION_RELEASE = "deprecation-release";
  // New in V8
  private static final String REMOVAL_DATE = "removal-date";
  private static final String COMMENT = "comment";
  private static final String ISSUED_BY_OPENPEPPOL = "issued-by-openpeppol";
  private static final String BIS_VERSION = "bis-version";
  private static final String DOMAIN_COMMUNITY = "domain-community";
  private static final String PROCESS_ID_ONE = "process-id";
  private static final String PROCESS_ID_MANY = "process-ids";

  public static final String CODE_LIST_NAME = ModelHelper.CODELIST_NAME_PREFIX + "Document types";
  public static final URI CODE_LIST_URI = URLHelper.getAsURI ("urn:peppol.eu:names:identifier:document-type");
  public static final String ROOT_ELEMENT_NAME = "document-types";

  private String m_sName;
  private String m_sScheme;
  private String m_sValue;
  private String m_sInitialRelease;
  private ERowState m_eState;
  private String m_sDeprecationRelease;
  private LocalDate m_aRemovalDate;
  private String m_sComment;
  private boolean m_bIssuedByOpenPeppol;
  private String m_sBISVersion;
  private String m_sDomainCommunity;
  private String m_sProcessIDs;
  private ICommonsList <IProcessIdentifier> m_aProcessIDs;

  @Nullable
  public Iterable <IProcessIdentifier> getAllProcessIDs ()
  {
    return m_aProcessIDs;
  }

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
    if (StringHelper.hasNoText (m_sName))
      throw new IllegalStateException ("Name is required");
    if (StringHelper.hasNoText (m_sScheme))
      throw new IllegalStateException ("Scheme is required");
    if (m_eState == null)
      throw new IllegalStateException ("State is required");
    if (StringHelper.hasNoText (m_sValue))
      throw new IllegalStateException ("Value is required");
    if (StringHelper.hasNoText (m_sInitialRelease))
      throw new IllegalStateException ("Initial Release is required");
    if (false)
      if (StringHelper.hasNoText (m_sDomainCommunity))
        throw new IllegalStateException ("DomainCommunity is required");
    if (CollectionHelper.isEmpty (m_aProcessIDs))
      throw new IllegalStateException ("ProcessID is required");

    if (!PeppolIdentifierFactory.INSTANCE.isDocumentTypeIdentifierSchemeValid (m_sScheme))
      throw new IllegalStateException ("Scheme does not match Peppol requirements");
    if (!PeppolIdentifierFactory.INSTANCE.isDocumentTypeIdentifierValueValid (m_sScheme, m_sValue))
      throw new IllegalStateException ("Value does not match Peppol requirements");
    final IPeppolDocumentTypeIdentifierParts aParts = PeppolDocumentTypeIdentifierParts.extractFromString (m_sValue);
    if (aParts == null)
      throw new IllegalStateException ("Value does not match detailed Peppol requirements");
    // Consistency check for UBL document types
    // Root NS: urn:oasis:names:specification:ubl:schema:xsd:OrderCancellation-2
    // Local name: OrderCancellation
    if (aParts.getRootNS ().endsWith ("-2"))
      if (m_eState.isActive () && !aParts.getRootNS ().endsWith (aParts.getLocalName () + "-2"))
        throw new IllegalStateException ("Value '" + m_sValue + "' seems to be inconsistent");

    if (PeppolIdentifierHelper.DOCUMENT_TYPE_SCHEME_PEPPOL_DOCTYPE_WILDCARD.equals (m_sScheme))
    {
      // Add additional wild card tests
      final String sCustomizationID = aParts.getCustomizationID ();

      // Check allowed root parts
      final boolean bIsBilling = sCustomizationID.startsWith ("urn:peppol:pint:billing-1");
      final boolean bIsSelfBilling = sCustomizationID.startsWith ("urn:peppol:pint:selfbilling-1");
      final boolean bIsNonTaxInvoice = sCustomizationID.startsWith ("urn:peppol:pint:nontaxinvoice-1");
      if (!bIsBilling && !bIsSelfBilling && !bIsNonTaxInvoice)
        throw new IllegalStateException ("The root part of the Customization ID '" +
                                         sCustomizationID +
                                         "' is not supported");

      // These are assumptions for now - lets see how long they hold true
      if (m_aProcessIDs.size () != 1)
        throw new IllegalStateException ("For wildcard identifiers, only 1 process ID is allowed");
      final String sProcessID = m_aProcessIDs.get (0).getURIEncoded ();

      if ((bIsBilling || bIsNonTaxInvoice) && !"cenbii-procid-ubl::urn:peppol:bis:billing".equals (sProcessID))
        throw new IllegalStateException ("For Billing Wildcard entries, the process ID '" + sProcessID + "' is wrong");
      if (bIsSelfBilling && !"cenbii-procid-ubl::urn:peppol:bis:selfbilling".equals (sProcessID))
        throw new IllegalStateException ("For Selfbilling Wildcard entries, the process ID '" +
                                         sProcessID +
                                         "' is wrong");
    }

    if (m_eState.isDeprecated () && StringHelper.hasNoText (m_sDeprecationRelease))
      throw new IllegalStateException ("Code list entry has state 'deprecated' but there is no Deprecation release set");
    if (m_eState.isRemoved () && m_aRemovalDate == null)
      throw new IllegalStateException ("Code list entry has state 'removed' but there is no Removal date set");
    if (m_bIssuedByOpenPeppol && StringHelper.hasNoText (m_sBISVersion))
    {
      // Exclusion for Reporting stuff
      if (!"OO".equals (m_sDomainCommunity))
        throw new IllegalStateException ("If issued by OpenPeppol, a BIS version is required");
    }
    if (StringHelper.hasText (m_sBISVersion) && !StringParser.isUnsignedInt (m_sBISVersion))
      throw new IllegalStateException ("Code list entry has an invalid BIS version number - must be numeric");
  }

  @Nonnull
  public IMicroElement getAsElement ()
  {
    final IMicroElement ret = new MicroElement ("document-type");
    ret.setAttribute (NAME, m_sName);
    ret.setAttribute (SCHEME, m_sScheme);
    ret.setAttribute (VALUE, m_sValue);
    ret.setAttribute (INITIAL_RELEASE, m_sInitialRelease);
    ret.setAttribute (STATE, m_eState.getID ());
    if (StringHelper.hasText (m_sDeprecationRelease))
      ret.setAttribute (DEPRECATION_RELEASE, m_sDeprecationRelease);
    if (m_aRemovalDate != null)
      ret.setAttribute (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    if (StringHelper.hasText (m_sComment))
      ret.appendElement (COMMENT).appendText (m_sComment);
    ret.setAttribute (ISSUED_BY_OPENPEPPOL, m_bIssuedByOpenPeppol);
    ret.setAttribute (BIS_VERSION, m_sBISVersion);
    if (StringHelper.hasText (m_sDomainCommunity))
      ret.setAttribute (DOMAIN_COMMUNITY, m_sDomainCommunity);
    for (final IProcessIdentifier aProcID : m_aProcessIDs)
    {
      ret.appendElement (PROCESS_ID_ONE)
         .setAttribute (SCHEME, aProcID.getScheme ())
         .setAttribute (VALUE, aProcID.getValue ());
    }
    return ret;
  }

  @Nonnull
  public IJsonObject getAsJson ()
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (NAME, m_sName);
    ret.add (SCHEME, m_sScheme);
    ret.add (VALUE, m_sValue);
    ret.add (INITIAL_RELEASE, m_sInitialRelease);
    ret.add (STATE, m_eState.getID ());
    if (StringHelper.hasText (m_sDeprecationRelease))
      ret.add (DEPRECATION_RELEASE, m_sDeprecationRelease);
    if (m_aRemovalDate != null)
      ret.add (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    if (StringHelper.hasText (m_sComment))
      ret.add (COMMENT, m_sComment);
    ret.add (ISSUED_BY_OPENPEPPOL, m_bIssuedByOpenPeppol);
    if (StringHelper.hasText (m_sBISVersion))
      ret.add (BIS_VERSION, m_sBISVersion);
    if (StringHelper.hasText (m_sDomainCommunity))
      ret.add (DOMAIN_COMMUNITY, m_sDomainCommunity);
    {
      final IJsonArray aProcIDs = new JsonArray ();
      for (final IProcessIdentifier aProcID : m_aProcessIDs)
        aProcIDs.add (new JsonObject ().add (SCHEME, aProcID.getScheme ()).add (VALUE, aProcID.getValue ()));
      ret.addJson (PROCESS_ID_MANY, aProcIDs);
    }
    return ret;
  }

  public static void addGCColumns (@Nonnull final CodeListDocument aCLDoc)
  {
    final ColumnSet aColumnSet = aCLDoc.getColumnSet ();
    GCHelper.addHeaderColumn (aColumnSet, NAME, false, true, "Name", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet,
                              SCHEME,
                              true,
                              true,
                              "Peppol Document Type Identifier Scheme",
                              ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet,
                              VALUE,
                              true,
                              true,
                              "Peppol Document Type Identifier Value",
                              ECodeListDataType.STRING);
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
    GCHelper.addHeaderColumn (aColumnSet,
                              ISSUED_BY_OPENPEPPOL,
                              false,
                              true,
                              "Issued by OpenPeppol?",
                              ECodeListDataType.BOOLEAN);
    GCHelper.addHeaderColumn (aColumnSet, BIS_VERSION, false, false, "BIS version", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, DOMAIN_COMMUNITY, false, true, "Domain Community", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet,
                              PROCESS_ID_MANY,
                              false,
                              true,
                              "Associated Process/Profile Identifier(s)",
                              ECodeListDataType.STRING);
  }

  @Nonnull
  public Row getAsGCRow (@Nonnull final ColumnSet aColumnSet)
  {
    // Create Genericode row
    final GCRowExt ret = new GCRowExt (aColumnSet);
    ret.add (NAME, m_sName);
    ret.add (SCHEME, m_sScheme);
    ret.add (VALUE, m_sValue);
    ret.add (INITIAL_RELEASE, m_sInitialRelease);
    ret.add (STATE, m_eState.getID ());
    ret.add (DEPRECATION_RELEASE, m_sDeprecationRelease);
    ret.add (REMOVAL_DATE, PDTWebDateHelper.getAsStringXSD (m_aRemovalDate));
    ret.add (COMMENT, m_sComment);
    ret.add (ISSUED_BY_OPENPEPPOL, m_bIssuedByOpenPeppol);
    ret.add (BIS_VERSION, m_sBISVersion);
    ret.add (DOMAIN_COMMUNITY, m_sDomainCommunity);
    ret.add (PROCESS_ID_MANY, m_sProcessIDs);
    return ret;
  }

  @Nonnull
  public static HCRow getAsHtmlTableHeaderRow ()
  {
    final HCRow aRow = new HCRow (true);
    aRow.addCell ("Profile name");
    aRow.addCell ("Peppol Document Type Identifier Scheme");
    aRow.addCell ("Peppol Document Type Identifier Value");
    aRow.addCell ("Initial Release");
    aRow.addCell ("State");
    aRow.addCell ("Deprecation release");
    aRow.addCell ("Removal date");
    aRow.addCell ("Comment");
    aRow.addCell ("Issued by OpenPeppol?");
    aRow.addCell ("BIS version");
    aRow.addCell ("Domain Community");
    aRow.addCell ("Associated Process/Profile Identifier(s)");
    return aRow;
  }

  @Nonnull
  public HCRow getAsHtmlTableBodyRow ()
  {
    final HCRow aRow = new HCRow ();
    aRow.addCell (m_sName);
    aRow.addCell (m_sScheme);
    aRow.addCell (m_sValue);
    aRow.addAndReturnCell (m_sInitialRelease).addClass (ModelHelper.CSS_TEXT_END);
    aRow.addCell (m_eState.getDisplayName ());
    aRow.addAndReturnCell (m_sDeprecationRelease).addClass (ModelHelper.CSS_TEXT_END);
    aRow.addAndReturnCell (PDTWebDateHelper.getAsStringXSD (m_aRemovalDate)).addClass (ModelHelper.CSS_TEXT_END);
    aRow.addCell (m_sComment);
    aRow.addCell (Boolean.toString (m_bIssuedByOpenPeppol));
    aRow.addAndReturnCell (m_sBISVersion).addClass (ModelHelper.CSS_TEXT_END);
    aRow.addCell (m_sDomainCommunity);
    aRow.addCell (HCExtHelper.nl2brList (m_sProcessIDs));
    if (m_eState.isRemoved ())
      aRow.addClass (ModelHelper.CSS_TABLE_DANGER);
    else
      if (m_eState.isDeprecated ())
        aRow.addClass (ModelHelper.CSS_TABLE_WARNING);
    return aRow;
  }

  @Nonnull
  public static DocTypeRow createV8 (@Nonnull final String [] aRow)
  {
    final DocTypeRow ret = new DocTypeRow ();
    ret.m_sName = aRow[0];
    if (StringHelper.hasNoText (ret.m_sName))
      throw new IllegalStateException ("Empty name is not allowed");
    ret.m_sScheme = aRow[1];
    ret.m_sValue = aRow[2];
    ret.m_sInitialRelease = aRow[3];
    ret.m_eState = ERowState.getFromIDOrThrow (aRow[4]);
    ret.m_sDeprecationRelease = aRow[5];
    ret.m_aRemovalDate = getLocalDateFromExcel (aRow[6]);
    ret.m_sComment = aRow[7];
    ret.m_bIssuedByOpenPeppol = ModelHelper.parseIssuedByOpenPeppol (aRow[8]);
    ret.m_sBISVersion = aRow[9];
    ret.m_sDomainCommunity = aRow[10];
    ret.m_sProcessIDs = aRow[11];
    ret.m_aProcessIDs = ModelHelper.getAllProcessIDsFromMultilineString (ret.m_sProcessIDs);
    return ret;
  }
}

/**
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
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.url.URLHelper;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.hc.ext.HCExtHelper;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
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
public final class DocTypeRow implements IModelRow
{
  private static final String NAME = "name";
  private static final String SCHEME = "scheme";
  private static final String VALUE = "value";
  private static final String SINCE = "since";
  private static final String DEPRECATED = "deprecated";
  private static final String DEPRECATED_SINCE = "deprecated-since";
  private static final String COMMENT = "comment";
  private static final String ISSUED_BY_OPENPEPPOL = "issued-by-openpeppol";
  private static final String BIS_VERSION = "bis-version";
  private static final String DOMAIN_COMMUNITY = "domain-community";
  private static final String PROCESS_ID_ONE = "process-id";
  private static final String PROCESS_ID_MANY = "process-ids";

  private static final Logger LOGGER = LoggerFactory.getLogger (DocTypeRow.class);

  public static final String CODE_LIST_NAME = "Peppol Code Lists - Document types";
  public static final URI CODE_LIST_URI = URLHelper.getAsURI ("urn:peppol.eu:names:identifier:document-type");
  public static final String ROOT_ELEMENT_NAME = "document-types";

  private String m_sName;
  private String m_sScheme;
  private String m_sValue;
  private String m_sSince;
  private boolean m_bDeprecated;
  private String m_sDeprecatedSince;
  private String m_sComment;
  private boolean m_bIssuedByOpenPeppol;
  private String m_sBISVersion;
  private String m_sDomainCommunity;
  private String m_sProcessIDs;
  private ICommonsList <IProcessIdentifier> m_aProcessIDs;

  public boolean isDeprecated ()
  {
    return m_bDeprecated;
  }

  @Nullable
  public Iterable <IProcessIdentifier> getAllProcessIDs ()
  {
    return m_aProcessIDs;
  }

  public void checkConsistency ()
  {
    if (StringHelper.hasNoText (m_sName))
      throw new IllegalStateException ("Name is required");
    if (StringHelper.hasNoText (m_sScheme))
      throw new IllegalStateException ("Scheme is required");
    if (StringHelper.hasNoText (m_sValue))
      throw new IllegalStateException ("Value is required");
    if (StringHelper.hasNoText (m_sSince))
      throw new IllegalStateException ("Since is required");
    if (false)
      if (StringHelper.hasNoText (m_sDomainCommunity))
        throw new IllegalStateException ("DomainCommunity is required");
    if (CollectionHelper.isEmpty (m_aProcessIDs))
      throw new IllegalStateException ("ProcessID is required");

    if (!PeppolIdentifierFactory.INSTANCE.isDocumentTypeIdentifierSchemeValid (m_sScheme))
      throw new IllegalStateException ("Scheme does not match Peppol requirements");
    if (!PeppolIdentifierFactory.INSTANCE.isDocumentTypeIdentifierValueValid (m_sValue))
      throw new IllegalStateException ("Value does not match Peppol requirements");
    final IPeppolDocumentTypeIdentifierParts aParts = PeppolDocumentTypeIdentifierParts.extractFromString (m_sValue);
    if (aParts == null)
      throw new IllegalStateException ("Value does not match detailed Peppol requirements");
    // Consistency check for UBL document types
    // Root NS: urn:oasis:names:specification:ubl:schema:xsd:OrderCancellation-2
    // Local name: OrderCancellation
    if (aParts.getRootNS ().endsWith ("-2"))
      if (!m_bDeprecated && !aParts.getRootNS ().endsWith (aParts.getLocalName () + "-2"))
        throw new IllegalStateException ("Value '" + m_sValue + "' seems to be inconsistent");

    if (m_bDeprecated && StringHelper.hasNoText (m_sDeprecatedSince))
      throw new IllegalStateException ("Code list entry is deprecated but there is no deprecated-since entry");
    if (m_bIssuedByOpenPeppol && StringHelper.hasNoText (m_sBISVersion))
      throw new IllegalStateException ("If issued by OpenPEPPOL, a BIS version is required");
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
    ret.setAttribute (SINCE, m_sSince);
    ret.setAttribute (DEPRECATED, m_bDeprecated);
    ret.setAttribute (DEPRECATED_SINCE, m_sDeprecatedSince);
    if (StringHelper.hasText (m_sComment))
      ret.appendElement (COMMENT).appendText (m_sComment);
    ret.setAttribute (ISSUED_BY_OPENPEPPOL, m_bIssuedByOpenPeppol);
    ret.setAttribute (BIS_VERSION, m_sBISVersion);
    if (StringHelper.hasText (m_sDomainCommunity))
      ret.setAttribute (DOMAIN_COMMUNITY, m_sDomainCommunity);
    for (final IProcessIdentifier aProcID : m_aProcessIDs)
    {
      ret.appendElement (PROCESS_ID_ONE).setAttribute (SCHEME, aProcID.getScheme ()).setAttribute (VALUE, aProcID.getValue ());
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
    ret.add (SINCE, m_sSince);
    ret.add (DEPRECATED, m_bDeprecated);
    if (StringHelper.hasText (m_sDeprecatedSince))
      ret.add (DEPRECATED_SINCE, m_sDeprecatedSince);
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
    GCHelper.addHeaderColumn (aColumnSet, SCHEME, true, true, "Peppol Document Type Identifier Scheme", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, VALUE, true, true, "Peppol Document Type Identifier Value", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, SINCE, false, true, "Since", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, DEPRECATED, false, true, "Deprecated?", ECodeListDataType.BOOLEAN);
    GCHelper.addHeaderColumn (aColumnSet, DEPRECATED_SINCE, false, false, "Deprecated since", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, COMMENT, false, false, "Comment", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, ISSUED_BY_OPENPEPPOL, false, true, "Issued by OpenPEPPOL?", ECodeListDataType.BOOLEAN);
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
    ret.add (SINCE, m_sSince);
    ret.add (DEPRECATED, m_bDeprecated);
    ret.add (DEPRECATED_SINCE, m_sDeprecatedSince);
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
    aRow.addCell ("Since");
    aRow.addCell ("Deprecated?");
    aRow.addCell ("Deprecated since");
    aRow.addCell ("Comment");
    aRow.addCell ("Issued by OpenPEPPOL?");
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
    aRow.addCell (m_sSince);
    aRow.addCell (Boolean.toString (m_bDeprecated));
    aRow.addCell (m_sDeprecatedSince);
    aRow.addCell (m_sComment);
    aRow.addCell (Boolean.toString (m_bIssuedByOpenPeppol));
    aRow.addCell (m_sBISVersion);
    aRow.addCell (m_sDomainCommunity);
    aRow.addCell (HCExtHelper.nl2brList (m_sProcessIDs));
    if (m_bDeprecated)
      aRow.addClass (DefaultCSSClassProvider.create ("table-warning"));
    return aRow;
  }

  @Nonnull
  public static DocTypeRow createV7 (@Nonnull final String [] aRow)
  {
    final DocTypeRow ret = new DocTypeRow ();
    ret.m_sName = aRow[0];
    if (StringHelper.hasNoText (ret.m_sName))
      throw new IllegalStateException ("Empty name is not allowed");
    ret.m_sScheme = aRow[1];
    ret.m_sValue = aRow[2];
    ret.m_sSince = aRow[3];
    ret.m_bDeprecated = ModelHelper.parseDeprecated (aRow[4]);
    ret.m_sDeprecatedSince = aRow[5];
    ret.m_sComment = aRow[6];
    ret.m_bIssuedByOpenPeppol = ModelHelper.parseIssuedByOpenPeppol (aRow[7]);
    ret.m_sBISVersion = aRow[8];
    ret.m_sDomainCommunity = aRow[9];
    ret.m_sProcessIDs = aRow[10];
    ret.m_aProcessIDs = ModelHelper.getAllProcessIDsFromMultilineString (ret.m_sProcessIDs);
    return ret;
  }
}

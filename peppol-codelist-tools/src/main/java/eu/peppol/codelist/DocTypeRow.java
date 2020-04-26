package eu.peppol.codelist;

import java.net.URI;

import javax.annotation.Nonnull;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.url.URLHelper;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

import eu.peppol.codelist.field.ECodeListDataType;

public final class DocTypeRow
{
  private static final String ID_NAME = "name";
  private static final String ID_SCHEME = "scheme";
  private static final String ID_VALUE = "value";
  private static final String SINCE = "since";
  private static final String DEPRECATED = "deprecated";
  private static final String DEPRECATED_SINCE = "deprecated-since";
  private static final String COMMENT = "comment";
  private static final String ISSUED_BY_OPENPEPPOL = "issued-by-openpeppol";
  private static final String BIS_VERSION = "bis-version";
  private static final String DOMAIN_COMMUNITY = "domain-community";
  private static final String PROCESS_ID_ONE = "process-id";
  private static final String PROCESS_ID_MANY = "process-ids";

  public static final String CODE_LIST_NAME = "PeppolDocumentTypes";
  public static final URI CODE_LIST_URI = URLHelper.getAsURI ("urn:peppol.eu:names:identifier:documenttypes");

  String m_sName;
  String m_sScheme;
  String m_sValue;
  String m_sSince;
  boolean m_bDeprecated;
  String m_sDeprecatedSince;
  String m_sComment;
  boolean m_bIssuedByOpenPeppol;
  String m_sBISVersion;
  String m_sDomainCommunity;
  private String m_sProcessIDs;
  ICommonsList <IProcessIdentifier> m_aProcessIDs;

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
    if (StringHelper.hasNoText (m_sDomainCommunity))
      throw new IllegalStateException ("DomainCommunity is required");
    if (CollectionHelper.isEmpty (m_aProcessIDs))
      throw new IllegalStateException ("ProcessID is required");

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
    ret.setAttribute (ID_NAME, m_sName);
    ret.setAttribute (ID_SCHEME, m_sScheme);
    ret.setAttribute (ID_VALUE, m_sValue);
    ret.setAttribute (SINCE, m_sSince);
    ret.setAttribute (DEPRECATED, m_bDeprecated);
    ret.setAttribute (DEPRECATED_SINCE, m_sDeprecatedSince);
    ret.setAttribute (COMMENT, m_sComment);
    ret.setAttribute (ISSUED_BY_OPENPEPPOL, m_bIssuedByOpenPeppol);
    ret.setAttribute (BIS_VERSION, m_sBISVersion);
    ret.setAttribute (DOMAIN_COMMUNITY, m_sDomainCommunity);
    for (final IProcessIdentifier aProcID : m_aProcessIDs)
    {
      ret.appendElement (PROCESS_ID_ONE)
         .setAttribute (ID_SCHEME, aProcID.getScheme ())
         .setAttribute (ID_VALUE, aProcID.getValue ());
    }
    return ret;
  }

  @Nonnull
  public IJsonObject getAsJson ()
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (ID_NAME, m_sName);
    ret.add (ID_SCHEME, m_sScheme);
    ret.add (ID_VALUE, m_sValue);
    ret.add (SINCE, m_sSince);
    ret.add (DEPRECATED, m_bDeprecated);
    ret.add (DEPRECATED_SINCE, m_sDeprecatedSince);
    ret.add (COMMENT, m_sComment);
    ret.add (ISSUED_BY_OPENPEPPOL, m_bIssuedByOpenPeppol);
    ret.add (BIS_VERSION, m_sBISVersion);
    ret.add (DOMAIN_COMMUNITY, m_sDomainCommunity);
    final IJsonArray aProcIDs = new JsonArray ();
    for (final IProcessIdentifier aProcID : m_aProcessIDs)
      aProcIDs.add (new JsonObject ().add (ID_SCHEME, aProcID.getScheme ()).add (ID_VALUE, aProcID.getValue ()));
    ret.add (PROCESS_ID_MANY, aProcIDs);
    return ret;
  }

  public static void addColumns (@Nonnull final CodeListDocument aCLDoc)
  {
    final ColumnSet aColumnSet = aCLDoc.getColumnSet ();
    GCHelper.addHeaderColumn (aColumnSet, ID_NAME, false, true, "Name", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet,
                              ID_SCHEME,
                              true,
                              true,
                              "Peppol Document Type Identifier Scheme",
                              ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet,
                              ID_VALUE,
                              true,
                              true,
                              "Peppol Document Type Identifier Value",
                              ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, SINCE, false, true, "Since", ECodeListDataType.STRING);
    GCHelper.addHeaderColumn (aColumnSet, DEPRECATED, false, true, "Deprecated?", ECodeListDataType.BOOLEAN);
    GCHelper.addHeaderColumn (aColumnSet, DEPRECATED_SINCE, false, false, "Deprecated since", ECodeListDataType.STRING);
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
    ret.add (ID_NAME, m_sName);
    ret.add (ID_SCHEME, m_sScheme);
    ret.add (ID_VALUE, m_sValue);
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
  static ICommonsList <IProcessIdentifier> getAllProcessIDsFromMultilineString (@Nonnull final String sProcessIDs)
  {
    final ICommonsList <IProcessIdentifier> ret = new CommonsArrayList <> ();
    for (final String s : StringHelper.getExploded ('\n', StringHelper.replaceAll (sProcessIDs, '\r', '\n')))
    {
      final String sProcessID = s.trim ();
      if (StringHelper.hasNoText (sProcessID))
        throw new IllegalStateException ("Found empty process ID in '" + sProcessIDs + "'");
      final IProcessIdentifier aProcID = PeppolIdentifierFactory.INSTANCE.parseProcessIdentifier (sProcessID);
      if (aProcID == null)
        throw new IllegalStateException ("Failed to parse process ID '" + sProcessID + "'");
      ret.add (aProcID);
    }
    if (ret.isEmpty ())
      throw new IllegalStateException ("Found no single process ID in '" + sProcessIDs + "'");
    return ret;
  }

  @Nonnull
  public static DocTypeRow createV7 (@Nonnull final String [] aRow)
  {
    final DocTypeRow ret = new DocTypeRow ();
    ret.m_sName = aRow[0];
    ret.m_sScheme = aRow[1];
    ret.m_sValue = aRow[2];
    ret.m_sSince = aRow[3];
    ret.m_bDeprecated = AbstractConverter.parseDeprecated (aRow[4]);
    ret.m_sDeprecatedSince = aRow[5];
    ret.m_sComment = aRow[6];
    ret.m_bIssuedByOpenPeppol = AbstractConverter.parseIssuedByOpenPEPPOL (aRow[7]);
    ret.m_sBISVersion = aRow[8];
    ret.m_sDomainCommunity = aRow[9];
    ret.m_sProcessIDs = aRow[10];
    ret.m_aProcessIDs = getAllProcessIDsFromMultilineString (ret.m_sProcessIDs);
    return ret;
  }
}

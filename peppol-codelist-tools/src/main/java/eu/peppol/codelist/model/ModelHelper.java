package eu.peppol.codelist.model;

import javax.annotation.Nonnull;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;

final class ModelHelper
{
  private static final boolean DEFAULT_DEPRECATED = false;
  private static final boolean DEFAULT_ISSUED_BY_OPENPEPPOL = false;

  private ModelHelper ()
  {}

  static boolean parseBoolean (final String s, final boolean bFallback)
  {
    if (StringHelper.hasText (s))
      return "1".equals (s) || "true".equalsIgnoreCase (s) || "yes".equalsIgnoreCase (s);
    return bFallback;
  }

  static boolean parseDeprecated (final String s)
  {
    return parseBoolean (s, DEFAULT_DEPRECATED);
  }

  static boolean parseIssuedByOpenPEPPOL (final String s)
  {
    return parseBoolean (s, DEFAULT_ISSUED_BY_OPENPEPPOL);
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
}

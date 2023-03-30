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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;

@Immutable
public final class ModelHelper
{
  // This has an impact on the created filename, so don't change
  public static final String CODELIST_NAME_PREFIX = "Peppol Code Lists - ";
  public static final ICSSClassProvider CSS_TABLE_DANGER = DefaultCSSClassProvider.create ("table-danger");
  public static final ICSSClassProvider CSS_TABLE_WARNING = DefaultCSSClassProvider.create ("table-warning");
  // Right align
  public static final ICSSClassProvider CSS_TEXT_END = DefaultCSSClassProvider.create ("text-end");

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

  static boolean parseIssuedByOpenPeppol (final String s)
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

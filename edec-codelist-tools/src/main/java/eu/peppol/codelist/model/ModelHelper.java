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

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringReplace;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;

import jakarta.annotation.Nonnull;

@Immutable
public final class ModelHelper
{
  // This has an impact on the created filename, so don't change
  public static final String CODELIST_NAME_PREFIX = "Peppol Code Lists - ";
  public static final ICSSClassProvider CSS_TABLE_DANGER = DefaultCSSClassProvider.create ("table-danger");
  public static final ICSSClassProvider CSS_TABLE_WARNING = DefaultCSSClassProvider.create ("table-warning");
  // Right align
  public static final ICSSClassProvider CSS_TEXT_END = DefaultCSSClassProvider.create ("text-end");
  public static final ICSSClassProvider CSS_WIDE_COLUMN = DefaultCSSClassProvider.create ("wide-column");

  private static final boolean DEFAULT_DEPRECATED = false;
  private static final boolean DEFAULT_ABSTRACT = false;
  private static final boolean DEFAULT_ISSUED_BY_OPENPEPPOL = false;

  private ModelHelper ()
  {}

  static boolean parseBoolean (final String s, final boolean bFallback)
  {
    if (StringHelper.isNotEmpty (s))
      return "1".equals (s) || "true".equalsIgnoreCase (s) || "yes".equalsIgnoreCase (s);
    return bFallback;
  }

  static boolean parseDeprecated (final String s)
  {
    return parseBoolean (s, DEFAULT_DEPRECATED);
  }

  static boolean parseAbstract (final String s)
  {
    return parseBoolean (s, DEFAULT_ABSTRACT);
  }

  static boolean parseIssuedByOpenPeppol (final String s)
  {
    return parseBoolean (s, DEFAULT_ISSUED_BY_OPENPEPPOL);
  }

  @Nonnull
  static ICommonsList <IProcessIdentifier> getAllProcessIDsFromMultilineString (@Nonnull final String sProcessIDs)
  {
    final ICommonsList <IProcessIdentifier> ret = new CommonsArrayList <> ();
    for (final String s : StringHelper.getExploded ('\n', StringReplace.replaceAll (sProcessIDs, '\r', '\n')))
    {
      final String sProcessID = s.trim ();
      if (StringHelper.isEmpty (sProcessID))
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

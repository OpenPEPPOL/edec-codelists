/*
 * Copyright (C) 2020-2022 OpenPeppol AISBL (www.peppol.eu)
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

import com.helger.commons.annotation.Nonempty;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.json.IJsonObject;
import com.helger.xml.microdom.IMicroElement;

/**
 * Base interface for a single item in a code list.
 *
 * @author Philip Helger
 */
public interface IModelRow
{

  /**
   * @return The state of this row. May not be <code>null</code>.
   */
  @Nonnull
  ERowState getState ();

  /**
   * @return The unique key of this particular row. If the key consists of
   *         multiple columns, the parts should just be aggregated and combined
   *         with the colon (:) character.
   */
  @Nonnull
  @Nonempty
  String getUniqueKey ();

  /**
   * Check the consistency of this row. Throws a runtime exception on error.
   */
  void checkConsistency ();

  /**
   * @return The representation of this code list row for the format XML. Never
   *         <code>null</code>
   */
  @Nonnull
  IMicroElement getAsElement ();

  /**
   * @return The representation of this code list row for the format JSON. Never
   *         <code>null</code>
   */
  @Nonnull
  IJsonObject getAsJson ();

  /**
   * @return The representation of this code list row for the format GeneriCode.
   *         Never <code>null</code>
   */
  @Nonnull
  Row getAsGCRow (@Nonnull ColumnSet aColumnSet);

  /**
   * @return The representation of this code list row for the format HTML. Never
   *         <code>null</code>
   */
  @Nonnull
  HCRow getAsHtmlTableBodyRow ();
}

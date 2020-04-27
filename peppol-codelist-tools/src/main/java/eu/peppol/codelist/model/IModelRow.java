package eu.peppol.codelist.model;

import javax.annotation.Nonnull;

import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
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
   * Check the consistency of this row. Throws a runtime exception on error.
   */
  void checkConsistency ();

  @Nonnull
  IMicroElement getAsElement ();

  @Nonnull
  IJsonObject getAsJson ();

  @Nonnull
  Row getAsGCRow (@Nonnull ColumnSet aColumnSet);
}

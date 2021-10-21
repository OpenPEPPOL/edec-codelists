package eu.peppol.codelist.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.name.IHasDisplayName;

/**
 * The "state" per row, introduced in V8 of the code list.
 *
 * @author Philip Helger
 */
public enum ERowState implements IHasID <String>, IHasDisplayName
{
  ACTIVE ("active", "Active"),
  DEPRECATED ("deprecated", "Deprecated"),
  REMOVED ("removed", "Removed");

  private final String m_sID;
  private final String m_sDisplayName;

  ERowState (@Nonnull @Nonempty final String sID, @Nonnull @Nonempty final String sDisplayName)
  {
    m_sID = sID;
    m_sDisplayName = sDisplayName;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sDisplayName;
  }

  public boolean isActive ()
  {
    return this == ACTIVE;
  }

  public boolean isDeprecated ()
  {
    return this == DEPRECATED;
  }

  public boolean isRemoved ()
  {
    return this == REMOVED;
  }

  @Nullable
  public static ERowState getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ERowState.class, sID);
  }

  @Nonnull
  public static ERowState getFromIDOrThrow (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrThrow (ERowState.class, sID);
  }
}

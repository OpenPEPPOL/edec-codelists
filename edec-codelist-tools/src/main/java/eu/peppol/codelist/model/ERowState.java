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
  SCHEDULED_FOR_DEPRECATION ("deprecation-scheduled", "Scheduled for Deprecation"),
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

  public boolean isScheduledForDeprecation ()
  {
    return this == SCHEDULED_FOR_DEPRECATION;
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

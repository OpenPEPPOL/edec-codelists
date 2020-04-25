package eu.peppol.codelist.field;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;

public enum ETransportProfilesField implements IHasCodeListField
{
  PROTOCOL ("protocol", false, true, ECodeListDataType.STRING),
  PROFILE_VERSION ("profile-version", false, true, ECodeListDataType.STRING),
  PROFILE_ID ("profile-id", true, true, ECodeListDataType.STRING),
  SINCE ("since", false, true, ECodeListDataType.STRING),
  DEPRECATED ("deprecated", false, true, ECodeListDataType.BOOLEAN),
  DEPRECATED_SINCE ("deprecated-since", false, false, ECodeListDataType.STRING);

  private final CodeListField m_aField;

  ETransportProfilesField (@Nonnull @Nonempty final String sColumnID,
                           final boolean bKeyColumn,
                           final boolean bRequired,
                           @Nonnull final ECodeListDataType eDataType)
  {
    m_aField = new CodeListField (sColumnID, bRequired, eDataType, bKeyColumn);
  }

  @Nonnull
  public CodeListField field ()
  {
    return m_aField;
  }
}

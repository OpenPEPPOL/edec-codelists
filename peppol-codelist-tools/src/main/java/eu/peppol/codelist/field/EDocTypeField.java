package eu.peppol.codelist.field;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;

public enum EDocTypeField implements IHasCodeListField
{
  NAME ("name", false, false, ECodeListDataType.STRING),
  SCHEME ("scheme", true, true, ECodeListDataType.STRING),
  ID ("id", true, true, ECodeListDataType.STRING),
  SINCE ("since", false, true, ECodeListDataType.STRING),
  DEPRECATED ("deprecated", false, true, ECodeListDataType.BOOLEAN),
  DEPRECATED_SINCE ("deprecated-since", false, false, ECodeListDataType.STRING),
  COMMENT ("comment", false, false, ECodeListDataType.STRING),
  ISSUED_BY_OPENPEPPOL ("issued-by-openpeppol", false, true, ECodeListDataType.BOOLEAN),
  BIS_VERSION ("bis-version", false, false, ECodeListDataType.INT),
  DOMAIN_COMMUNITY ("domain-community", false, true, ECodeListDataType.STRING),
  PROCESS_IDs ("process-ids", false, true, ECodeListDataType.STRING);

  private final CodeListField m_aField;

  EDocTypeField (@Nonnull @Nonempty final String sColumnID,
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

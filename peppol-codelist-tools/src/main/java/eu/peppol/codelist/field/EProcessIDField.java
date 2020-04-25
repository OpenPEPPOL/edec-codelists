package eu.peppol.codelist.field;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;

public enum EProcessIDField implements IHasCodeListField
{
  SCHEME ("scheme", true, true, ECodeListDataType.STRING),
  VALUE ("value", true, true, ECodeListDataType.STRING);

  private final CodeListField m_aField;

  EProcessIDField (@Nonnull @Nonempty final String sColumnID,
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

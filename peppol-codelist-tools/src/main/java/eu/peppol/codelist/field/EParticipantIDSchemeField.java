package eu.peppol.codelist.field;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;

public enum EParticipantIDSchemeField implements IHasCodeListField
{
  SCHEME_ID ("schemeid", true, true, ECodeListDataType.STRING),
  ISO6523 ("iso6523", false, true, ECodeListDataType.STRING),
  COUNTRY ("country", true, true, ECodeListDataType.STRING),
  SCHEME_NAME ("schemename", true, true, ECodeListDataType.STRING),
  ISSUING_AGENCY ("issuingagency", false, false, ECodeListDataType.STRING),
  SINCE ("since", false, true, ECodeListDataType.STRING),
  DEPRECATED ("deprecated", false, true, ECodeListDataType.BOOLEAN),
  DEPRECATED_SINCE ("deprecated-since", false, false, ECodeListDataType.STRING),
  STRUCTURE ("structure", false, false, ECodeListDataType.STRING),
  DISPLAY ("display", false, false, ECodeListDataType.STRING),
  EXAMPLES ("examples", false, false, ECodeListDataType.STRING),
  VALIDATION_RULES ("validation-rules", false, false, ECodeListDataType.STRING),
  USAGE ("usage", false, false, ECodeListDataType.STRING);

  private final CodeListField m_aField;

  EParticipantIDSchemeField (@Nonnull @Nonempty final String sColumnID,
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

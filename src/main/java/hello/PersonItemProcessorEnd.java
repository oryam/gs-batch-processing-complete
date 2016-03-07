package hello;

import static org.apache.commons.lang3.StringUtils.upperCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonItemProcessorEnd extends PersonItemProcessor {

  private static final Logger log = LoggerFactory.getLogger(PersonItemProcessorEnd.class);

  @Override
  public Person process(final Person person) throws Exception {
    Person transformedPerson = new Person();
    nullAwareBeanUtilsBean.copyProperties(transformedPerson, person);
    transformedPerson.setFirstName(upperCase(person.getFirstName()));
    transformedPerson.setLastName(upperCase(person.getLastName()));

    log.info("Converting (" + person + ") into (" + transformedPerson + ")");

    saveData(transformedPerson);

    return transformedPerson;
  }

  @Override
  protected void saveData(final Person transformedPerson) throws Exception {
    mergeData(transformedPerson);
  }

  private void mergeData(final Person transformedPerson) throws Exception {
    log.info(transformedPerson.toString());
    int personId = transformedPerson.getId();
    if (sharedData.containsKey(personId)) {
      Person person = sharedData.get(personId);
      log.info("found in data : " + person);
      nullAwareBeanUtilsBean.copyProperties(transformedPerson, person);
    }
    sharedData.remove(personId);
    log.info(transformedPerson.toString());
  }

}
